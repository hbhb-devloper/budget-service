package com.hbhb.cw.budget.web.controller;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProgressService;
import com.hbhb.cw.budget.web.vo.*;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Tag(name = "预算执行-预算目标与进度")
@RestController
@RequestMapping("/budget/progress")
public class BudgetProgressController {

    @Resource
    private BudgetProgressService budgetProgressService;

    @Resource
    private UserApiExp userApi;

    @Operation(summary = "按条件查询项目类别列表")
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

    @Operation(summary = "按立项值查询项目签报列表")
    @GetMapping("/project/list")
    public List<BudgetProjectAmountVO> getProjectAmount(BudgetFlowStateVO cond) {
        if (cond.getUnitId() == null) {
            return new ArrayList<>();
        }
        if (cond.getImportDate() == null) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        // todo 此处有bug
        return budgetProgressService.getProgressByBudgetId(cond);
    }

    @Operation(summary = "按预算id查询项目统计")
    @GetMapping("/project/statistics")
    public BudgetProgressDeclareVO getProgressByBudgetId(
            @Parameter(description = "项目id", required = true) @RequestParam Long budgetId,
            @Parameter(description = "单位id") @RequestParam(required = false) Integer unitId,
            @Parameter(description = "时间") @RequestParam(required = false) String importDate,
            @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (unitId == null) {
            unitId = user.getUnitId();
        }
        if (importDate == null) {
            importDate = DateUtil.getCurrentYear();
        }
        return budgetProgressService.getProgressByState(
                BudgetProgressReqVO.builder().unitId(unitId).year(importDate)
                        .budgetId(budgetId).build());

    }

    @Operation(summary = "按签报编号查询签报")
    @GetMapping("/project/{number}")
    public BudgetProjectAllVO getProgressByBudgetId(
            @Parameter(description = "签报编号", required = true) @PathVariable String number) {
        return budgetProgressService.getProjectInfo(number);
    }

    @Operation(summary = "导出进度表")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @Parameter(description = "按条件查询") @RequestBody BudgetProgressReqVO cond,
                       @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (cond.getUnitId() == null) {
            cond.setUnitId(user.getUnitId());
        }
        if (StringUtils.isEmpty(cond.getYear())) {
            cond.setYear(DateUtil.getCurrentYear());
        }
        List<BudgetProgressExportVO> list = budgetProgressService.getExportList(cond);
        String fileName = ExcelUtil.encodingFileName(request, "预算目标与进度表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetProgressExportVO.class, list);
    }

}