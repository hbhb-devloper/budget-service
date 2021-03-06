package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.service.BudgetDataService;
import com.hbhb.cw.budget.web.vo.BudgetBelongVO;
import com.hbhb.cw.budget.web.vo.BudgetDataResVO;
import com.hbhb.cw.budget.web.vo.BudgetDataVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "预算执行-预算数据相关")
@RestController
@RequestMapping("/data")
public class BudgetDataController {

    @Resource
    private BudgetDataService budgetDataService;

    @Operation(summary = "查询项目类别管理")
    @GetMapping("/list")
    public List<BudgetDataResVO> list(
            @Parameter(description = "预算id和归口单位id", required = true) BudgetBelongVO budgetBelongVO) {
        return budgetDataService.listByBudgetId(budgetBelongVO);
    }

    @Operation(summary = "调整项目类别管理")
    @PutMapping("")
    public void updateBudgetDate(
            @Parameter(description = "budgetData集合", required = true) @RequestBody List<BudgetDataVO> list) {
        budgetDataService.updateBudgetDate(list);

    }
}
