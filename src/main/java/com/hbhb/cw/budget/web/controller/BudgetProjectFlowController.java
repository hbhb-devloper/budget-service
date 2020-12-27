package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectFlowHistoryService;
import com.hbhb.cw.budget.service.BudgetProjectFlowService;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowHistoryVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowInfoVO;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * @author yzc
 */
@Tag(name = "预算执行-项目审批流程相关")
@RestController
@RequestMapping("/project/flow")
public class BudgetProjectFlowController {

    @Resource
    private BudgetProjectFlowService budgetProjectFlowService;
    @Resource
    private BudgetProjectFlowHistoryService budgetProjectFlowHistoryService;
    @Resource
    private UserApiExp userApi;

    @Operation(summary = "获取项目签报的流程详情:查询该流程的所有节点信息")
    @GetMapping("/{projectId}")
    public List<BudgetProjectFlowInfoVO> getProjectFlow(
            @Parameter(description = "项目签报id", required = true) @PathVariable Integer projectId,
            @Parameter(hidden = true) @UserId Integer userId) {
        return budgetProjectFlowService.getBudgetProjectFlow(projectId, userId);
    }

    @Operation(summary = "删除项目签报的流程节点:逻辑删除")
    @PutMapping("/{id}")
    public void updateProjectFlow(
            @Parameter(description = "项目签报流程信息id", required = true) @PathVariable Long id) {
        budgetProjectFlowService.deleteProjectFlow(id);
    }

    @Operation(summary = "历史记录 逻辑删除")
    @GetMapping("/history/{projectId}")
    public List<BudgetProjectFlowHistoryVO> list(
            @Parameter(description = "项目签报id", required = true) @PathVariable Long projectId) {
        return budgetProjectFlowHistoryService.getBudgetProjectFlowHistory(projectId);
    }

    @Operation(summary = "通过签报id得到项目签报流程信息id")
    @GetMapping("/isJoin/{projectId}")
    public List<Long> idList(
            @Parameter(description = "项目签报id", required = true) @PathVariable Long projectId) {
        return budgetProjectFlowService.getIdListByProjectId(projectId);
    }
}
