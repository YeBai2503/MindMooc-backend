package com.mindmooc.business.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mindmooc.business.service.MindmapService;
import com.mindmooc.common.Result;
import com.mindmooc.dto.UpdateNodeRequest;
import com.mindmooc.vo.MindmapVO;
import com.mindmooc.vo.NodeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 思维导图控制类
 */
@RestController
@RequestMapping("/api/mindmaps")
@RequiredArgsConstructor
public class MindmapController {
    
    private final MindmapService mindmapService;
    
    /**
     * 根据ID获取思维导图
     */
    @GetMapping("/{mindmapId}")
    public Result<MindmapVO> getMindmapById(@PathVariable String mindmapId) {
        MindmapVO mindmap = mindmapService.getMindmapById(mindmapId);
        return Result.success(mindmap);
    }
    
    /**
     * 根据任务ID获取思维导图
     */
    @GetMapping("/task/{taskId}")
    public Result<MindmapVO> getMindmapByTaskId(@PathVariable String taskId) {
        MindmapVO mindmap = mindmapService.getMindmapByTaskId(taskId);
        return Result.success(mindmap);
    }
    
    /**
     * 获取用户的思维导图列表（分页）
     */
    @GetMapping("/my")
    public Result<IPage<MindmapVO>> getUserMindmaps(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<MindmapVO> mindmapPage = mindmapService.getUserMindmaps(userId, pageNum, pageSize);
        return Result.success(mindmapPage);
    }
    
    /**
     * 获取导图的节点列表（树形结构）
     */
    @GetMapping("/{mindmapId}/nodes")
    public Result<List<NodeVO>> getNodes(@PathVariable String mindmapId) {
        List<NodeVO> nodes = mindmapService.getNodesByMapId(mindmapId);
        return Result.success(nodes);
    }
    
    /**
     * 添加节点
     */
    @PostMapping("/{mindmapId}/nodes")
    public Result<NodeVO> addNode(
            @PathVariable String mindmapId,
            @Valid @RequestBody UpdateNodeRequest request) {
        NodeVO node = mindmapService.addNode(mindmapId, request);
        return Result.success("节点添加成功", node);
    }
    
    /**
     * 更新节点
     */
    @PutMapping("/nodes/{nodeId}")
    public Result<NodeVO> updateNode(
            @PathVariable String nodeId,
            @Valid @RequestBody UpdateNodeRequest request) {
        NodeVO node = mindmapService.updateNode(nodeId, request);
        return Result.success("节点更新成功", node);
    }
    
    /**
     * 删除节点
     */
    @DeleteMapping("/nodes/{nodeId}")
    public Result<Void> deleteNode(@PathVariable String nodeId) {
        mindmapService.deleteNode(nodeId);
        return Result.success("节点删除成功", null);
    }
    
    /**
     * 重新生成 Mermaid 代码
     */
    @PostMapping("/{mindmapId}/regenerate")
    public Result<String> regenerateMermaidCode(@PathVariable String mindmapId) {
        String mermaidCode = mindmapService.regenerateMermaidCode(mindmapId);
        return Result.success("生成成功", mermaidCode);
    }
}

