package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProjectFlowHistoryService;
import com.hbhb.cw.service.BudgetProjectFlowService;
import com.hbhb.cw.web.vo.BudgetProjectFlowHistoryVO;
import com.hbhb.cw.web.vo.BudgetProjectFlowInfoVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author yzc
 */
@Api(tags = "预算执行-项目审批流程相关")
@RestController
@RequestMapping("/budget/project/flow")
public class BudgetProjectFlowController {

    @Resource
    private BudgetProjectFlowService budgetProjectFlowService;
    @Resource
    private BudgetProjectFlowHistoryService budgetProjectFlowHistoryService;

    @ApiOperation(value = "获取项目签报的流程详情", notes = "查询该流程的所有节点信息")
    @GetMapping("/{projectId}")
    public List<BudgetProjectFlowInfoVO> getProjectFlow(
            @ApiParam(value = "项目签报id", required = true) @PathVariable Integer projectId,
            @ApiIgnore @CurrentUser LoginUser loginUser) {
        return budgetProjectFlowService.getBudgetProjectFlow(projectId, loginUser.getUser().getId());
    }

    @ApiOperation(value = "删除项目签报的流程节点", notes = "逻辑删除")
    @PutMapping("/{id}")
    public void updateProjectFlow(
            @ApiParam(value = "项目签报流程信息id", required = true) @PathVariable Long id) {
        budgetProjectFlowService.deleteProjectFlow(id);
    }

    @ApiOperation(value = "历史记录", notes = "逻辑删除")
    @GetMapping("/history/{projectId}")
    public List<BudgetProjectFlowHistoryVO> list(
            @ApiParam(value = "项目签报id", required = true) @PathVariable Long projectId) {
        return budgetProjectFlowHistoryService.getBudgetProjectFlowHistory(projectId);
    }

    @ApiOperation(value = "通过签报id得到项目签报流程信息id")
    @GetMapping("/isJoin/{projectId}")
    public List<Long> idList(
            @ApiParam(value = "项目签报id", required = true) @PathVariable Long projectId) {
        return budgetProjectFlowService.getIdListByProjectId(projectId);
    }
}
