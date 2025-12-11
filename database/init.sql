-- MindMOOC 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS mindmooc DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE mindmooc;

-- 设置默认存储引擎为 InnoDB，以支持事务和外键
SET default_storage_engine = INNODB;

-- 1. 用户表 (Users)
-- 重点：密码存储哈希值 (password_hash)
CREATE TABLE IF NOT EXISTS users (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL, -- 存储 bcrypt 或 Argon2 哈希值
    avatar_url VARCHAR(512), -- 头像url
    role TINYINT NOT NULL DEFAULT 0 COMMENT '0 = 普通用户, 1 = 管理员',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- 实现自动更新：首次创建时设置，后续每次 UPDATE 都会自动刷新
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 视频源表 (Videos)
-- 重点：file_hash 用于去重；original_ai_output 用于复用
CREATE TABLE IF NOT EXISTS videos (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    file_hash VARCHAR(64) UNIQUE NOT NULL, -- 例如 SHA-256 哈希值
    storage_url VARCHAR(512) UNIQUE NOT NULL, -- 文件在服务器/S3上的路径
    original_filename VARCHAR(255),
    video_title VARCHAR(255),
    duration INT NOT NULL, -- 视频时长 (秒)
    file_size BIGINT NOT NULL, -- 文件大小 (bytes)
    mime_type VARCHAR(50),
    -- JSONB 替换为 MySQL 的 JSON 类型
    original_ai_output JSON, -- 存储 AI 首次分析生成的原始节点结构 (JSON 格式)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频表';

-- 3. 任务表 (Tasks)
-- 记录用户发起的视频处理任务
CREATE TABLE IF NOT EXISTS tasks (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL, -- 谁发起的任务
    video_id CHAR(36) NOT NULL, -- 处理哪个视频
    status VARCHAR(50) NOT NULL, -- 'pending', 'processing', 'completed', 'failed'
    task_type VARCHAR(50) NOT NULL DEFAULT 'common',
    error_details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP NULL, -- 任务开始时间
    completed_at TIMESTAMP NULL, -- 任务完成时间

    -- 外键定义
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 4. 思维导图表 (Mindmaps)
-- 重点：user_id (所有者), video_id, task_id (创建来源), mermaid_cache (高性能缓存)
CREATE TABLE IF NOT EXISTS mindmaps (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36) NOT NULL, -- 导图的所有者 (方便直接查询)
    video_id CHAR(36) NOT NULL, -- 导图是关于哪个视频
    task_id CHAR(36) UNIQUE, -- 哪个任务生成了它 (1:1 关系)
    title VARCHAR(255) NOT NULL,
    summary TEXT, -- 视频概要
    mermaid_cache TEXT, -- 缓存的 Mermaid 代码 (由节点表生成)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 外键定义
    CONSTRAINT fk_maps_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_maps_video FOREIGN KEY (video_id) REFERENCES videos(id) ON DELETE RESTRICT,
    CONSTRAINT fk_maps_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='思维导图表';

-- 5. 导图节点表 (Mindmap_Nodes)
-- 核心：parent_id 实现树状结构
CREATE TABLE IF NOT EXISTS mindmap_nodes (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    map_id CHAR(36) NOT NULL, -- 属于哪个导图
    parent_id CHAR(36), -- 指向父节点 (自关联)，允许为空
    content TEXT NOT NULL,
    start_time INT, -- 视频起始时间 (秒)
    end_time INT, -- 视频结束时间 (秒)
    node_order INT NOT NULL DEFAULT 0, -- 同级节点(同一父节点)的顺序
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 外键定义
    CONSTRAINT fk_nodes_map FOREIGN KEY (map_id) REFERENCES mindmaps(id) ON DELETE CASCADE,
    CONSTRAINT fk_nodes_parent FOREIGN KEY (parent_id) REFERENCES mindmap_nodes(id) ON DELETE CASCADE,
    
    -- 节点顺序唯一约束：同一父节点下的子节点 node_order 不重复
    -- 注意：由于 parent_id 可能为 NULL（根节点），对根节点需要在应用层保证
    UNIQUE KEY idx_map_parent_order (map_id, parent_id, node_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='思维导图节点表';

-- 6. 反馈表 (Feedback)
CREATE TABLE IF NOT EXISTS feedback (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id CHAR(36), -- 允许匿名反馈
    type VARCHAR(50) NOT NULL, -- 'bug', 'suggestion', 'other'
    details TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'open',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 外键定义
    CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈表';

-- 创建索引以提高查询性能
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_videos_file_hash ON videos(file_hash);
CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_video_id ON tasks(video_id);
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_mindmaps_user_id ON mindmaps(user_id);
CREATE INDEX idx_mindmaps_video_id ON mindmaps(video_id);
CREATE INDEX idx_mindmaps_task_id ON mindmaps(task_id);
CREATE INDEX idx_nodes_map_id ON mindmap_nodes(map_id);
CREATE INDEX idx_nodes_parent_id ON mindmap_nodes(parent_id);
CREATE INDEX idx_feedback_user_id ON feedback(user_id);

-- 完成提示
SELECT '数据库初始化完成！' AS status;

