package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProjectFlowService;
import com.hbhb.cw.service.BudgetProjectService;
import com.hbhb.cw.service.BudgetProjectSplitService;
import com.hbhb.cw.web.vo.BudgetProjectApprovedFlowInfoVO;
import com.hbhb.cw.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.web.vo.BudgetProjectSplitVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
 * @since 2020-11-12
 */
@Api(tags = "预算执行-快照相关")
@RestController
@RequestMapping("/budget/approved")
public class BudgetApprovedController {

    @Resource
    private BudgetProjectFlowService budgetProjectFlowService;
    @Resource
    private BudgetProjectSplitService budgetProjectSplitService;
    @Resource
    private BudgetProjectService budgetProjectService;

    @ApiOperation("快照流程展示")
    @GetMapping("/{projectId}")
    public List<BudgetProjectApprovedFlowInfoVO> getProjectApproved(@PathVariable Integer projectId,
                                                                    @ApiIgnore @CurrentUser LoginUser loginUser) {
        return budgetProjectFlowService.getBudgetApprovedFlow(projectId, loginUser.getUser().getId());
    }

    @ApiOperation("根据签报id查找分类预算快照")
    @GetMapping("/list/{projectId}")
    public List<BudgetProjectSplitVO> getBudgetProjectSplitApprovedList(
            @ApiParam(value = "签报id", required = true) @PathVariable Integer projectId) {
        return budgetProjectSplitService.getBudgetApprovedSplitList(projectId);
    }

    @ApiOperation("根据签报id查找签报快照")
    @GetMapping("/info/{id}")
    public BudgetProjectDetailVO getBudgetProjectApprovedList(@PathVariable Integer id) {
        return budgetProjectService.getBudgetProjectApproved(id);
    }

    @ApiOperation("判断是否有快照")
    @GetMapping("/state/{id}")
    public boolean getBudgetApprovedState(@PathVariable Integer id) {
        return budgetProjectService.getBudgetApprovedState(id);
    }

}
