package com.mindmooc.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindmooc.business.mapper.MindmapMapper;
import com.mindmooc.business.mapper.MindmapNodeMapper;
import com.mindmooc.business.mapper.TaskMapper;
import com.mindmooc.business.mapper.VideoMapper;
import com.mindmooc.business.utils.BeanCopyUtil;
import com.mindmooc.common.BusinessException;
import com.mindmooc.common.ResultCode;
import com.mindmooc.dto.AiCallbackRequest;
import com.mindmooc.dto.UpdateNodeRequest;
import com.mindmooc.entity.Mindmap;
import com.mindmooc.entity.MindmapNode;
import com.mindmooc.entity.Task;
import com.mindmooc.entity.Video;
import com.mindmooc.vo.MindmapVO;
import com.mindmooc.vo.NodeVO;
import com.mindmooc.vo.VideoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 思维导图服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MindmapService {
    
    private final MindmapMapper mindmapMapper;
    private final MindmapNodeMapper mindmapNodeMapper;
    private final TaskMapper taskMapper;
    private final VideoMapper videoMapper;
    
    /**
     * 给AI 回调结果创建思维导图
     */
    @Transactional(rollbackFor = Exception.class)
    public void createMindmapFromAiResult(AiCallbackRequest request) {
        // 获取任务信息
        Task task = taskMapper.selectById(request.getTaskId());
        if (task == null) {
            throw new BusinessException(ResultCode.TASK_NOT_FOUND);
        }
        
        // 获取视频信息
        Video video = videoMapper.selectById(task.getVideoId());
        if (video == null) {
            throw new BusinessException(ResultCode.VIDEO_NOT_FOUND);
        }
        
        // 创建思维导图记录
        Mindmap mindmap = new Mindmap();
        mindmap.setUserId(task.getUserId());
        mindmap.setVideoId(task.getVideoId());
        mindmap.setTaskId(task.getId());
        mindmap.setTitle(video.getVideoTitle());
        mindmap.setSummary(request.getSummary());
        mindmap.setMermaidCache(request.getMermaidCode());
        mindmap.setCreatedAt(LocalDateTime.now());
        mindmap.setUpdatedAt(LocalDateTime.now());
        
        mindmapMapper.insert(mindmap);
        
        // 更新视频的AI 原始输出
        if (request.getOriginalOutput() != null) {
            video.setOriginalAiOutput(request.getOriginalOutput());
            videoMapper.updateById(video);
        }
        
        log.info("思维导图创建成功: {}", mindmap.getId());
    }
    
    /**
     * 根据ID获取思维导图
     */
    public MindmapVO getMindmapById(String mindmapId) {
        Mindmap mindmap = mindmapMapper.selectById(mindmapId);
        if (mindmap == null) {
            throw new BusinessException(ResultCode.MINDMAP_NOT_FOUND);
        }
        
        MindmapVO mindmapVO = BeanCopyUtil.copy(mindmap, MindmapVO.class);
        mindmapVO.setMermaidCode(mindmap.getMermaidCache());
        
        // 加载视频信息
        Video video = videoMapper.selectById(mindmap.getVideoId());
        if (video != null) {
            mindmapVO.setVideo(BeanCopyUtil.copy(video, VideoVO.class));
        }
        
        // 加载节点信息
        List<NodeVO> nodes = getNodesByMapId(mindmapId);
        mindmapVO.setNodes(nodes);
        
        return mindmapVO;
    }
    
    /**
     * 根据任务ID获取思维导图
     */
    public MindmapVO getMindmapByTaskId(String taskId) {
        LambdaQueryWrapper<Mindmap> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Mindmap::getTaskId, taskId);
        Mindmap mindmap = mindmapMapper.selectOne(wrapper);
        
        if (mindmap == null) {
            throw new BusinessException(ResultCode.MINDMAP_NOT_FOUND);
        }
        
        return getMindmapById(mindmap.getId());
    }
    
    /**
     * 获取用户的思维导图列表（分页）
     */
    public IPage<MindmapVO> getUserMindmaps(String userId, int pageNum, int pageSize) {
        Page<Mindmap> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Mindmap> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Mindmap::getUserId, userId)
               .orderByDesc(Mindmap::getCreatedAt);
        
        IPage<Mindmap> mindmapPage = mindmapMapper.selectPage(page, wrapper);
        
        // 转换成VO
        Page<MindmapVO> voPage = new Page<>(mindmapPage.getCurrent(), mindmapPage.getSize(), mindmapPage.getTotal());
        List<MindmapVO> mindmapVOList = BeanCopyUtil.copyList(mindmapPage.getRecords(), MindmapVO.class);
        
        // 加载视频信息
        for (int i = 0; i < mindmapVOList.size(); i++) {
            Mindmap mindmap = mindmapPage.getRecords().get(i);
            mindmapVOList.get(i).setMermaidCode(mindmap.getMermaidCache());
            
            Video video = videoMapper.selectById(mindmap.getVideoId());
            if (video != null) {
                mindmapVOList.get(i).setVideo(BeanCopyUtil.copy(video, VideoVO.class));
            }
        }
        
        voPage.setRecords(mindmapVOList);
        
        return voPage;
    }
    
    /**
     * 获取导图的所有节点（树形结构）
     */
    public List<NodeVO> getNodesByMapId(String mapId) {
        LambdaQueryWrapper<MindmapNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MindmapNode::getMapId, mapId)
               .orderByAsc(MindmapNode::getNodeOrder);
        
        List<MindmapNode> nodes = mindmapNodeMapper.selectList(wrapper);
        List<NodeVO> nodeVOList = BeanCopyUtil.copyList(nodes, NodeVO.class);
        
        // 构建树形结构
        return buildNodeTree(nodeVOList);
    }
    
    /**
     * 构建节点树形结构
     */
    private List<NodeVO> buildNodeTree(List<NodeVO> allNodes) {
        Map<String, NodeVO> nodeMap = new HashMap<>();
        List<NodeVO> rootNodes = new ArrayList<>();
        
        // 先将所有节点放进Map
        for (NodeVO node : allNodes) {
            node.setChildren(new ArrayList<>());
            nodeMap.put(node.getId(), node);
        }
        
        // 构建树形结构
        for (NodeVO node : allNodes) {
            if (node.getParentId() == null || node.getParentId().isEmpty()) {
                // 根节点
                rootNodes.add(node);
            } else {
                // 子节点
                NodeVO parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            }
        }
        
        return rootNodes;
    }
    
    /**
     * 添加节点
     */
    @Transactional(rollbackFor = Exception.class)
    public NodeVO addNode(String mapId, UpdateNodeRequest request) {
        // 验证导图是否存在
        Mindmap mindmap = mindmapMapper.selectById(mapId);
        if (mindmap == null) {
            throw new BusinessException(ResultCode.MINDMAP_NOT_FOUND);
        }
        
        // 检查同一父节点下是否已存在相同的 node_order（应用层保证唯一性）
        Integer nodeOrder = request.getNodeOrder() != null ? request.getNodeOrder() : 0;
        LambdaQueryWrapper<MindmapNode> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(MindmapNode::getMapId, mapId);
        
        // 处理 parent_id 为空的情况（根节点）
        if (request.getParentId() == null || request.getParentId().isEmpty()) {
            checkWrapper.isNull(MindmapNode::getParentId);
        } else {
            checkWrapper.eq(MindmapNode::getParentId, request.getParentId());
        }
        
        checkWrapper.eq(MindmapNode::getNodeOrder, nodeOrder);
        
        if (mindmapNodeMapper.selectCount(checkWrapper) > 0) {
            throw new BusinessException(400, "同一层级已存在相同顺序的节点，请使用不同的 node_order");
        }
        
        // 创建节点
        MindmapNode node = new MindmapNode();
        node.setMapId(mapId);
        node.setParentId(request.getParentId());
        node.setContent(request.getContent());
        node.setStartTime(request.getStartTime());
        node.setEndTime(request.getEndTime());
        node.setNodeOrder(nodeOrder);
        node.setCreatedAt(LocalDateTime.now());
        node.setUpdatedAt(LocalDateTime.now());
        
        mindmapNodeMapper.insert(node);
        
        // 更新导图的更新时间和清除缓存
        mindmap.setUpdatedAt(LocalDateTime.now());
        mindmap.setMermaidCache(null); // 清除缓存，需要重新生成
        mindmapMapper.updateById(mindmap);
        
        log.info("节点添加成功: {}", node.getId());
        return BeanCopyUtil.copy(node, NodeVO.class);
    }
    
    /**
     * 更新节点
     */
    @Transactional(rollbackFor = Exception.class)
    public NodeVO updateNode(String nodeId, UpdateNodeRequest request) {
        MindmapNode node = mindmapNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException(ResultCode.NODE_NOT_FOUND);
        }
        
        // 更新节点信息
        if (request.getContent() != null) {
            node.setContent(request.getContent());
        }
        if (request.getParentId() != null) {
            node.setParentId(request.getParentId());
        }
        if (request.getStartTime() != null) {
            node.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            node.setEndTime(request.getEndTime());
        }
        if (request.getNodeOrder() != null) {
            node.setNodeOrder(request.getNodeOrder());
        }
        node.setUpdatedAt(LocalDateTime.now());
        
        mindmapNodeMapper.updateById(node);
        
        // 更新导图的更新时间和清除缓存
        Mindmap mindmap = mindmapMapper.selectById(node.getMapId());
        if (mindmap != null) {
            mindmap.setUpdatedAt(LocalDateTime.now());
            mindmap.setMermaidCache(null);
            mindmapMapper.updateById(mindmap);
        }
        
        log.info("节点更新成功: {}", nodeId);
        return BeanCopyUtil.copy(node, NodeVO.class);
    }
    
    /**
     * 删除节点
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteNode(String nodeId) {
        MindmapNode node = mindmapNodeMapper.selectById(nodeId);
        if (node == null) {
            throw new BusinessException(ResultCode.NODE_NOT_FOUND);
        }
        
        // 删除节点及其所有子节点
        deleteNodeRecursively(nodeId);
        
        // 更新导图的更新时间和清除缓存
        Mindmap mindmap = mindmapMapper.selectById(node.getMapId());
        if (mindmap != null) {
            mindmap.setUpdatedAt(LocalDateTime.now());
            mindmap.setMermaidCache(null);
            mindmapMapper.updateById(mindmap);
        }
        
        log.info("节点删除成功: {}", nodeId);
    }
    
    /**
     * 递归删除节点及其子节点
     */
    private void deleteNodeRecursively(String nodeId) {
        // 查找所有子节点
        LambdaQueryWrapper<MindmapNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MindmapNode::getParentId, nodeId);
        List<MindmapNode> children = mindmapNodeMapper.selectList(wrapper);
        
        // 递归删除子节点
        for (MindmapNode child : children) {
            deleteNodeRecursively(child.getId());
        }
        
        // 删除当前节点
        mindmapNodeMapper.deleteById(nodeId);
    }
    
    /**
     * 重新生成 Mermaid 代码
     */
    @Transactional(rollbackFor = Exception.class)
    public String regenerateMermaidCode(String mapId) {
        List<NodeVO> nodes = getNodesByMapId(mapId);
        
        // 生成 Mermaid 代码
        StringBuilder mermaidCode = new StringBuilder("graph TD\n");
        generateMermaidCodeRecursively(nodes, mermaidCode);
        
        String code = mermaidCode.toString();
        
        // 更新缓存
        Mindmap mindmap = mindmapMapper.selectById(mapId);
        if (mindmap != null) {
            mindmap.setMermaidCache(code);
            mindmap.setUpdatedAt(LocalDateTime.now());
            mindmapMapper.updateById(mindmap);
        }
        
        return code;
    }
    
    /**
     * 递归生成 Mermaid 代码
     */
    private void generateMermaidCodeRecursively(List<NodeVO> nodes, StringBuilder sb) {
        for (NodeVO node : nodes) {
            String nodeId = node.getId().replace("-", "");
            String content = node.getContent().replace("\"", "'");
            
            if (node.getParentId() != null && !node.getParentId().isEmpty()) {
                String parentId = node.getParentId().replace("-", "");
                sb.append("    ").append(parentId).append("[\"").append(content).append("\"]\n");
                sb.append("    ").append(parentId).append(" --> ").append(nodeId).append("\n");
            } else {
                sb.append("    ").append(nodeId).append("[\"").append(content).append("\"]\n");
            }
            
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                generateMermaidCodeRecursively(node.getChildren(), sb);
            }
        }
    }
}

