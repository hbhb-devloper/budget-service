package com.hbhb.cw.budget.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.model.BudgetMonthCheck;
import com.hbhb.cw.budget.service.BudgetMapService;
import com.hbhb.cw.budget.service.listener.BudgetMonthCheckListener;
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
 * @author yzcyzc
 */
@Tag(name = "预算执行-月度考核情况")
@RestController
@RequestMapping("/month/check")
public class BudgetMonthCheckController {
    @Resource
    private BudgetMapService budgetMapService;

    @Operation(summary = "获取月度考核列表")
    @GetMapping("/list")
    public List<BudgetMonthCheck> getBudget() {
        return budgetMapService.getMonthCheckList();
    }

    @Operation(summary = "月度考核导入")
    @PostMapping("/import")
    public void importBudgetBreak(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), BudgetMonthCheck.class,
                    new BudgetMonthCheckListener(budgetMapService)).sheet().doRead();
        } catch (IOException e) {
            throw new BudgetException(BudgetErrorCode.BUDGET_MAP_DATA_IMPORT_ERROR);
        }
    }

    @Operation(summary = "导出月度考核列表")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response) {
        List<BudgetMonthCheck> list = budgetMapService.getMonthCheckList();
        String fileName = ExcelUtil.encodingFileName(request, "月度考核列表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetMonthCheck.class, list);
    }
}
