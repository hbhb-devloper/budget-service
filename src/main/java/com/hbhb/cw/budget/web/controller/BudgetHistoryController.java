package com.hbhb.cw.budget.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.core.utils.RegexUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.service.BudgetHistoryService;
import com.hbhb.cw.budget.service.BudgetService;
import com.hbhb.cw.budget.service.listener.BudgetListener;
import com.hbhb.cw.budget.web.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaokang
 * @since 2020-07-20
 */
@Tag(name = "预算执行-预算历史相关")
@RestController
@RequestMapping("/budget/history")
@Slf4j
public class BudgetHistoryController {

    @Resource
    private BudgetService budgetService;
    @Resource
    private BudgetHistoryService budgetHistoryService;

    @Operation(summary = "按条件获取中期预算调整列表 树形结构")
    @GetMapping("/list")
    public List<BudgetHistoryVO> getBudgetHistoryByCond(@Parameter(description = "条件") BudgetReqVO cond) {
        if (cond.getUnitId() == null) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(cond.getImportDate())) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        return budgetHistoryService.getListByCond(cond);
    }

    @Operation(summary = "获取预算历史详情")
    @GetMapping("/{budgetId}")
    public BudgetHistoryInfoVO getInfoById(
            @Parameter(description = "预算id", required = true) @PathVariable Long budgetId) {
        return budgetHistoryService.getInfoById(budgetId);
    }

    @Operation(summary = "预算历史模板导入")
    @PostMapping("/import")
    public void importBudgetBreak(MultipartFile file,
                                  @Parameter(description = "导入年份（默认为当年）", required = true) @RequestParam String importDate) {
        long begin = System.currentTimeMillis();
        if (StringUtils.isEmpty(importDate)) {
            importDate = DateUtil.getCurrentYear();
        }
        if (!RegexUtil.isYear(importDate)) {
            throw new BudgetException(BudgetErrorCode.BUDGET_IMPORT_DATE_ERROR);
        }
        try {
            EasyExcel.read(file.getInputStream(), BudgetImportVO.class,
                    new BudgetListener(budgetService, importDate)).sheet().doRead();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BudgetException(BudgetErrorCode.BUDGET_DATA_IMPORT_ERROR);
        }
        log.info("预算历史模板导入结束，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
    }

    @Operation(summary = "预算历史导出")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @RequestBody BudgetReqVO vo) {
        if (vo.getImportDate() == null) {
            vo.setImportDate(DateUtil.getCurrentYear());
        }
        List<BudgetHistoryExportVO> list = budgetHistoryService.getExportList(vo);
        String fileName = ExcelUtil.encodingFileName(request, "中期预算调整");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetHistoryExportVO.class, list);
    }
}
