package com.hbhb.cw.budget.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.service.BudgetHistoryService;
import com.hbhb.cw.service.BudgetService;
import com.hbhb.cw.service.listener.BudgetListener;
import com.hbhb.cw.utils.DateUtil;
import com.hbhb.cw.utils.ExcelUtil;
import com.hbhb.cw.utils.RegexUtil;
import com.hbhb.cw.web.vo.BudgetHistoryExportVO;
import com.hbhb.cw.web.vo.BudgetHistoryInfoVO;
import com.hbhb.cw.web.vo.BudgetHistoryVO;
import com.hbhb.cw.web.vo.BudgetImportVO;
import com.hbhb.cw.web.vo.BudgetReqVO;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaokang
 * @since 2020-07-20
 */
@Api(tags = "预算执行-预算历史相关")
@RestController
@RequestMapping("/budget/history")
@Slf4j
public class BudgetHistoryController {

    @Resource
    private BudgetService budgetService;
    @Resource
    private BudgetHistoryService budgetHistoryService;

    @ApiOperation(value = "按条件获取中期预算调整列表", notes = "树形结构")
    @GetMapping("/list")
    public List<BudgetHistoryVO> getBudgetHistoryByCond(@ApiParam(value = "条件") BudgetReqVO cond) {
        if (cond.getUnitId() == null) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(cond.getImportDate())) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        return budgetHistoryService.getListByCond(cond);
    }

    @ApiOperation("获取预算历史详情")
    @GetMapping("/{budgetId}")
    public BudgetHistoryInfoVO getInfoById(
            @ApiParam(value = "预算id", required = true) @PathVariable Long budgetId) {
        return budgetHistoryService.getInfoById(budgetId);
    }

    @ApiOperation("预算历史模板导入")
    @PostMapping("/import")
    public void importBudgetBreak(MultipartFile file,
                                  @ApiParam(value = "导入年份（默认为当年）", required = true) @RequestParam String importDate) {
        long begin = System.currentTimeMillis();
        if (StringUtils.isEmpty(importDate)) {
            importDate = DateUtil.getCurrentYear();
        }
        if (!RegexUtil.isYear(importDate)) {
            throw new BizException(BizStatus.BUDGET_IMPORT_DATE_ERROR.getCode());
        }
        try {
            EasyExcel.read(file.getInputStream(), BudgetImportVO.class,
                    new BudgetListener(budgetService, importDate)).sheet().doRead();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BizException(BizStatus.BUDGET_DATA_IMPORT_ERROR.getCode());
        }
        log.info("预算历史模板导入结束，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
    }

    @ApiOperation("预算历史导出")
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
