package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.service.BudgetMaintainService;
import com.hbhb.cw.budget.web.vo.BudgetProjectResVO;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 * @since 2021-02-19
 */
@Tag(name = "预算执行-预算维护(管理员页面)")
@RestController
@RequestMapping("/maintain")
@Slf4j
public class BudgetMaintainController {

    @Resource
    private BudgetMaintainService budgetMaintainService;

    @Operation(summary = "通过项目编号获得项目签报（分页）")
    @GetMapping("/list")
    public Page<BudgetProjectResVO> getBudgetProjectList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            String projectNum) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        return budgetMaintainService.getPageByProjectNum(pageNum, pageSize, true, projectNum);
    }

    @Operation(summary = "删除签报")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProject(@PathVariable Integer id) {
        budgetMaintainService.deleteBudgetProject(id);
    }
}
