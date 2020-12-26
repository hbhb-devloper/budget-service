package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProgressService;
import com.hbhb.cw.utils.DateUtil;
import com.hbhb.cw.utils.ExcelUtil;
import com.hbhb.cw.web.vo.BudgetProgressDeclareVO;
import com.hbhb.cw.web.vo.BudgetProgressExportVO;
import com.hbhb.cw.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.web.vo.BudgetProgressVO;
import com.hbhb.cw.web.vo.BudgetProjectAllVO;
import com.hbhb.cw.web.vo.BudgetProjectAmountVO;
import com.hbhb.cw.web.vo.BudgetProjectStateVO;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "预算执行-预算目标与进度")
@RestController
@RequestMapping("/budget/progress")
public class BudgetProgressController {

    @Resource
    private BudgetProgressService budgetProgressService;

    @ApiOperation("按条件查询项目类别列表")
    @GetMapping("/list")
    public List<BudgetProgressVO> getBudgetProgressList(BudgetProgressReqVO cond) {
        if (cond.getUnitId() == null) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(DateUtil.getCurrentYear());
        }
        return budgetProgressService.getBudgetProgressList(cond);
    }

    @ApiOperation("按立项值查询项目签报列表")
    @GetMapping("/project/list")
    public List<BudgetProjectAmountVO> getProjectAmount(BudgetProjectStateVO cond) {
        if (cond.getUnitId() == null) {
            return new ArrayList<>();
        }
        if (cond.getImportDate() == null) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        // todo 此处有bug
        return budgetProgressService.getProgressByBudgetId(cond);
    }

    @ApiOperation("按预算id查询项目统计")
    @GetMapping("/project/statistics")
    public BudgetProgressDeclareVO getProgressByBudgetId(
            @ApiParam(value = "项目id", required = true) @RequestParam Long budgetId,
            @ApiParam(value = "单位id") @RequestParam(required = false) Integer unitId,
            @ApiParam(value = "时间") @RequestParam(required = false) String importDate,
            @ApiIgnore @CurrentUser LoginUser loginUser) {
        if (unitId == null) {
            unitId = loginUser.getUser().getUnitId();
        }
        if (importDate == null) {
            importDate = DateUtil.getCurrentYear();
        }
        return budgetProgressService.getProgressByState(
                BudgetProgressReqVO.builder().unitId(unitId).year(importDate)
                        .budgetId(budgetId).build());

    }

    @ApiOperation("按签报编号查询签报")
    @GetMapping("/project/{number}")
    public BudgetProjectAllVO getProgressByBudgetId(
            @ApiParam(value = "签报编号", required = true) @PathVariable String number) {
        return budgetProgressService.getProjectInfo(number);
    }

    @ApiOperation("导出进度表")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @ApiParam(value = "按条件查询") @RequestBody BudgetProgressReqVO cond,
                       @ApiIgnore @CurrentUser LoginUser loginUser) {
        if (cond.getUnitId() == null) {
            cond.setUnitId(loginUser.getUser().getUnitId());
        }
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(DateUtil.getCurrentYear());
        }
        List<BudgetProgressExportVO> list = budgetProgressService.getExportList(cond);
        String fileName = ExcelUtil.encodingFileName(request, "预算目标与进度表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetProgressExportVO.class, list);
    }

}