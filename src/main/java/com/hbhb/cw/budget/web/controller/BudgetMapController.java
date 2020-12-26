package com.hbhb.cw.budget.web.controller;


import com.alibaba.excel.EasyExcel;
import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.model.BudgetMap;
import com.hbhb.cw.service.BudgetMapService;
import com.hbhb.cw.service.listener.BudgetMapListener;
import com.hbhb.cw.utils.ExcelUtil;
import com.hbhb.cw.web.vo.BudgetMapVO;

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
 * @author ln
 */
@Api(tags = "预算执行-预算映射相关")
@RestController
@RequestMapping("/budget/map")
public class BudgetMapController {

    @Resource
    private BudgetMapService budgetMapService;

    @ApiOperation("获取预算映射列表")
    @GetMapping("/list")
    public List<BudgetMapVO> getBudget() {
        return budgetMapService.getBudgetMapList();
    }

    @ApiOperation("预算映射导入")
    @PostMapping("/import")
    public void importBudgetBreak(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), BudgetMap.class,
                    new BudgetMapListener(budgetMapService)).sheet().doRead();
        } catch (IOException e) {
            throw new BizException(BizStatus.BUDGET_MAP_DATA_IMPORT_ERROR.getCode());
        }
    }

    @ApiOperation("导出预算映射列表")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response) {
        List<BudgetMapVO> list = budgetMapService.getBudgetMapList();
        String fileName = ExcelUtil.encodingFileName(request, "预算映射列表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetMapVO.class, list);
    }
}