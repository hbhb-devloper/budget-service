package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.service.BudgetDataService;
import com.hbhb.cw.web.vo.BudgetBelongVO;
import com.hbhb.cw.web.vo.BudgetDataResVO;
import com.hbhb.cw.web.vo.BudgetDataVO;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "预算执行-预算数据相关")
@RestController
@RequestMapping("/budget/data")
public class BudgetDataController {

    @Resource
    private BudgetDataService budgetDataService;

    @ApiOperation("查询项目类别管理")
    @GetMapping("/list")
    public List<BudgetDataResVO> list(
            @ApiParam(value = "预算id和归口单位id", required = true) BudgetBelongVO budgetBelongVO) {
        return budgetDataService.listByBudgetId(budgetBelongVO);
    }

    @ApiOperation("调整项目类别管理")
    @PutMapping("")
    public void updateBudgetDate(
            @ApiParam(value = "budgetData集合", required = true) @RequestBody List<BudgetDataVO> list) {
        budgetDataService.updateBudgetDate(list);
    }
}
