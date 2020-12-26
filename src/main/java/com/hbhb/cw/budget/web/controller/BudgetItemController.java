package com.hbhb.cw.budget.web.controller;


import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.cw.budget.service.BudgetItemService;
import com.hbhb.cw.budget.web.vo.BudgetItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yzc
 */
@Tag(name = "预算执行-预算相关(预算科目)")
@RestController
@RequestMapping("/item")
@Slf4j
public class BudgetItemController {

    @Resource
    private BudgetItemService budgetItemService;

    @Operation(summary = "通过预算科目id得到预算科目详情")
    @GetMapping("/{itemId}")
    public BudgetItemVO budgetItemById(
            @Parameter(description = "预算科目表id", required = true) @PathVariable Long itemId) {
        return budgetItemService.getBudgetItemById(itemId);
    }

    @Operation(summary = "科目列表")
    @GetMapping("")
    public List<SelectVO> budgetItemList() {
        return budgetItemService.getBudgetItemList();
    }

    @Operation(summary = "新增预算可科目")
    @PostMapping("")
    public void saveBudgetItem(
            @RequestBody BudgetItemVO budgetItemVO) {
        budgetItemService.addBudgetItem(budgetItemVO);
    }

    @Operation(summary = "删除预算科目")
    @DeleteMapping("/{itemId}")
    public void remove(
            @Parameter(description = "预算科目表id", required = true) @PathVariable Long itemId) {
        budgetItemService.deleteByItemId(itemId);
    }

    @Operation(summary = "修改预算科目")
    @PutMapping("")
    public void budgetUpdateItemVO(
            @RequestBody BudgetItemVO budgetItemVO) {
        budgetItemService.updateByBudgetItemVO(budgetItemVO);
    }

}
