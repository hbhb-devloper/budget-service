package com.hbhb.cw.budget.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.model.BudgetMonthCheck;
import com.hbhb.cw.service.BudgetMapService;
import com.hbhb.cw.service.listener.BudgetMonthCheckListener;
import com.hbhb.cw.utils.ExcelUtil;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author yzcyzc
 */
@Api(tags = "预算执行-月度考核情况")
@RestController
@RequestMapping("/month/check")
public class BudgetMonthCheckController {
    @Resource
    private BudgetMapService budgetMapService;

    @ApiOperation("获取月度考核列表")
    @GetMapping("/list")
    public List<BudgetMonthCheck> getBudget() {
        return budgetMapService.getMonthCheckList();
    }

    @ApiOperation("月度考核导入")
    @PostMapping("/import")
    public void importBudgetBreak(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), BudgetMonthCheck.class,
                    new BudgetMonthCheckListener(budgetMapService)).sheet().doRead();
        } catch (IOException e) {
            throw new BizException(BizStatus.BUDGET_MAP_DATA_IMPORT_ERROR.getCode());
        }
    }

    @ApiOperation("导出月度考核列表")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response) {
        List<BudgetMonthCheck> list = budgetMapService.getMonthCheckList();
        String fileName = ExcelUtil.encodingFileName(request, "月度考核列表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetMonthCheck.class, list);
    }
}
