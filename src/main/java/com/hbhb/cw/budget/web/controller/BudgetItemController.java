package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.service.BudgetItemService;
import com.hbhb.cw.web.vo.BudgetItemVO;
import com.hbhb.cw.web.vo.SelectVO;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yzc
 */
@Api(tags = "预算执行-预算相关(预算科目)")
@RestController
@RequestMapping("/budget/item")
@Slf4j
public class BudgetItemController {

    @Resource
    private BudgetItemService budgetItemService;

    @ApiOperation(value = "通过预算科目id得到预算科目详情")
    @GetMapping("/{itemId}")
    public BudgetItemVO budgetItemById(
            @ApiParam(value = "预算科目表id", required = true) @PathVariable Long itemId) {
        return budgetItemService.getBudgetItemById(itemId);
    }

    @ApiOperation(value = "科目列表")
    @GetMapping("")
    public List<SelectVO> budgetItemList() {
        return budgetItemService.getBudgetItemList();
    }

    @ApiOperation(value = "新增预算可科目")
    @PostMapping("")
    public void saveBudgetItem(
            @RequestBody BudgetItemVO budgetItemVO) {
        budgetItemService.addBudgetItem(budgetItemVO);
    }

    @ApiOperation(value = "删除预算科目")
    @DeleteMapping("/{itemId}")
    public void remove(
            @ApiParam(value = "预算科目表id", required = true) @PathVariable Long itemId) {
        budgetItemService.deleteByItemId(itemId);
    }

    @ApiOperation(value = "修改预算科目")
    @PutMapping("")
    public void budgetUpdateItemVO(
            @RequestBody BudgetItemVO budgetItemVO) {
        budgetItemService.updateByBudgetItemVO(budgetItemVO);
    }

}
