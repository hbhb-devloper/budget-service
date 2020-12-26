package com.hbhb.cw.budget.web.controller;


import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.model.BudgetMap;
import com.hbhb.cw.budget.service.BudgetMapService;
import com.hbhb.cw.budget.service.listener.BudgetMapListener;
import com.hbhb.cw.budget.web.vo.BudgetMapVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author ln
 */
@Tag(name = "预算执行-预算映射相关")
@RestController
@RequestMapping("/budget/map")
public class BudgetMapController {

    @Resource
    private BudgetMapService budgetMapService;

    @Operation(summary = "获取预算映射列表")
    @GetMapping("/list")
    public List<BudgetMapVO> getBudget() {
        return budgetMapService.getBudgetMapList();
    }

    @Operation(summary = "预算映射导入")
    @PostMapping("/import")
    public void importBudgetBreak(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), BudgetMap.class,
                    new BudgetMapListener(budgetMapService)).sheet().doRead();
        } catch (IOException e) {
            throw new BudgetException(BudgetErrorCode.BUDGET_MAP_DATA_IMPORT_ERROR);
        }
    }

    @Operation(summary = "导出预算映射列表")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response) {
        List<BudgetMapVO> list = budgetMapService.getBudgetMapList();
        String fileName = ExcelUtil.encodingFileName(request, "预算映射列表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetMapVO.class, list);
    }
}