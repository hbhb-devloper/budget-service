package com.hbhb.cw.budget.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.core.utils.RegexUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetService;
import com.hbhb.cw.budget.service.listener.BudgetListener;
import com.hbhb.cw.budget.web.vo.BudgetAdjustVO;
import com.hbhb.cw.budget.web.vo.BudgetExportVO;
import com.hbhb.cw.budget.web.vo.BudgetImportVO;
import com.hbhb.cw.budget.web.vo.BudgetInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetReqVO;
import com.hbhb.cw.budget.web.vo.BudgetVO;
import com.hbhb.cw.systemcenter.vo.TreeSelectParentVO;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


/**
 * @author xiaokang
 */
@Tag(name = "预算执行-预算相关")
@RestController
@RequestMapping("")
@Slf4j
public class BudgetController {

    @Resource
    private BudgetService budgetService;
    @Resource
    private UserApiExp userApi;

    @Operation(summary = "按条件获取预算列表 预算分解列表用，树形结构")
    @GetMapping("/list")
    public List<BudgetVO> getBudgetList(@Parameter(description = "条件") BudgetReqVO cond) {
        if (cond.getUnitId() == null) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(cond.getImportDate())) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        if (!RegexUtil.isYear(cond.getImportDate())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_IMPORT_DATE_ERROR);
        }

        int lastYear = Integer.parseInt(cond.getImportDate());
        cond.setLastYear(String.valueOf(lastYear - 1));
        return budgetService.getBudgetListByCond(cond);
    }

    @Operation(summary = "按条件获取预算列表 树形结构、KV")
    @GetMapping("/tree")
    public List<TreeSelectParentVO> getTreeListByUnit(
            @Parameter(description = "导入日期") @RequestParam(required = false) String importDate,
            @Parameter(description = "项目类别名称") @RequestParam(required = false) String projectItem) {
        if (StringUtils.isEmpty(importDate)) {
            importDate = DateUtil.getCurrentYear();
        }
        if (!RegexUtil.isYear(importDate)) {
            throw new BudgetException(BudgetErrorCode.BUDGET_IMPORT_DATE_ERROR);
        }
        return budgetService.getTreeByCond(BudgetReqVO.builder()
                .importDate(importDate)
                .projectItem(projectItem)
                .build());
    }

    @Operation(summary = "获取所有项目类型列表")
    @GetMapping("/project-type/list")
    public List<SelectVO> getProjectTypeList() {
        return budgetService.getProjectTypeList();
    }

    @Operation(summary = "获取预算详情")
    @GetMapping("/{id}")
    public BudgetVO getBudgetInfo(
            @Parameter(description = "预算id", required = true) @PathVariable Long id) {
        return budgetService.getInfoById(id);
    }

    @Operation(summary = "预算调整")
    @PutMapping("/adjust")
    public void updateBudget(
            @Parameter(description = "预算详情", required = true) @RequestBody BudgetAdjustVO vo) {
        budgetService.updateThreshold(vo);
    }

    @Operation(summary = "预算模板导入")
    @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public void importBudgetBreak(MultipartFile file,
                                  String importDate) {
        long begin = System.currentTimeMillis();
        String fileName = file.getOriginalFilename();
        budgetService.judegFileName(fileName);
        if (importDate == null) {
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
        log.info("预算分解模板导入结束，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
    }

    @Operation(summary = "预算导出")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @RequestBody BudgetReqVO cond) {
        if (cond.getUnitId() == null) {
            return;
        }
        if (StringUtils.isEmpty(cond.getImportDate())) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        if (!RegexUtil.isYear(cond.getImportDate())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_IMPORT_DATE_ERROR);
        }
        int lastYear = Integer.parseInt(cond.getImportDate());
        cond.setLastYear(String.valueOf(lastYear - 1));
        List<BudgetExportVO> list = budgetService.getExportList(cond);
        String fileName = ExcelUtil.encodingFileName(request, "预算分解");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetExportVO.class, list);
    }

    @Operation(summary = "新增预算")
    @PostMapping("")
    public void saveBudget(
            @RequestBody BudgetInfoVO budgetAddVO) {
        budgetService.addBudget(budgetAddVO);
    }

    @Operation(summary = "删除预算")
    @DeleteMapping("/{budgetId}")
    public void deleteBudget(
            @Parameter(description = "预算表id", required = true) @PathVariable Long budgetId) {
        budgetService.deleteByItemId(budgetId);
    }

    @Operation(summary = "修改预算")
    @PutMapping("")
    public void updateBudget(
            @RequestBody BudgetInfoVO budgetInfoVO) {
        budgetService.updateByUpdateVO(budgetInfoVO);
    }


    @Operation(summary = "通过预算id获取金额阀值")
    @GetMapping("/threshold/{id}")
    public BigDecimal getBudgetThreshold(
            @Parameter(description = "预算id", required = true) @PathVariable Long id) {
        return budgetService.getBudgetThreshold(id);
    }

    @Operation(summary = "修改项目签报预算编号")
    @PostMapping("/check")
    public void checkProject() {
        budgetService.check();
    }

    @Operation(summary = "修改归口单位预算编号")
    @PostMapping("/checkBelong")
    public void checkBelong() {
        budgetService.checkBelong();
    }
}
