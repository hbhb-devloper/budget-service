package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.service.BudgetProjectFlowService;
import com.hbhb.cw.budget.service.BudgetProjectService;
import com.hbhb.cw.budget.service.BudgetProjectSplitService;
import com.hbhb.cw.budget.web.vo.BudgetProjectApprovedFlowInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitVO;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;



/**
 * @author yzc
 * @since 2020-11-12
 */
@Tag(name = "预算执行-快照相关")
@RestController
@RequestMapping("/budget/approved")
public class BudgetApprovedController {

    @Resource
    private BudgetProjectFlowService budgetProjectFlowService;
    @Resource
    private BudgetProjectSplitService budgetProjectSplitService;
    @Resource
    private BudgetProjectService budgetProjectService;

    @Operation(summary = "快照流程展示")
    @GetMapping("/{projectId}")
    public List<BudgetProjectApprovedFlowInfoVO> getProjectApproved(@PathVariable Integer projectId,
                                                                    @UserId Integer userId) {
        return budgetProjectFlowService.getBudgetApprovedFlow(projectId, userId);
    }

    @Operation(summary = "根据签报id查找分类预算快照")
    @GetMapping("/list/{projectId}")
    public List<BudgetProjectSplitVO> getBudgetProjectSplitApprovedList(
            @Parameter(description = "签报id", required = true) @PathVariable Integer projectId) {
        return budgetProjectSplitService.getBudgetApprovedSplitList(projectId);
    }

    @Operation(summary = "根据签报id查找签报快照")
    @GetMapping("/info/{id}")
    public BudgetProjectDetailVO getBudgetProjectApprovedList(@PathVariable Integer id) {
        return budgetProjectService.getBudgetProjectApproved(id);
    }

    @Operation(summary = "判断是否有快照")
    @GetMapping("/state/{id}")
    public boolean getBudgetApprovedState(@PathVariable Integer id) {
        return budgetProjectService.getBudgetApprovedState(id);
    }

}
