package com.hbhb.cw.budget.web.controller;

import com.alibaba.excel.EasyExcel;
import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetService;
import com.hbhb.cw.service.listener.BudgetListener;
import com.hbhb.cw.systemcenter.vo.TreeSelectParentVO;
import com.hbhb.cw.utils.DateUtil;
import com.hbhb.cw.utils.ExcelUtil;
import com.hbhb.cw.utils.RegexUtil;
import com.hbhb.cw.web.vo.BudgetAdjustVO;
import com.hbhb.cw.web.vo.BudgetExportVO;
import com.hbhb.cw.web.vo.BudgetImportVO;
import com.hbhb.cw.web.vo.BudgetInfoVO;
import com.hbhb.cw.web.vo.BudgetReqVO;
import com.hbhb.cw.web.vo.BudgetVO;
import com.hbhb.cw.web.vo.SelectVO;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author xiaokang
 */
@Api(tags = "预算执行-预算相关")
@RestController
@RequestMapping("/budget")
@Slf4j
public class BudgetController {

    @Resource
    private BudgetService budgetService;

    @ApiOperation(value = "按条件获取预算列表", notes = "预算分解列表用，树形结构")
    @GetMapping("/list")
    public List<BudgetVO> getBudgetList(@ApiParam(value = "条件") BudgetReqVO cond) {
        if (cond.getUnitId() == null) {
            return new ArrayList<>();
        }
        if (StringUtils.isEmpty(cond.getImportDate())) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        if (!RegexUtil.isYear(cond.getImportDate())) {
            throw new BizException(BizStatus.BUDGET_IMPORT_DATE_ERROR.getCode());
        }

        int lastYear = Integer.parseInt(cond.getImportDate());
        cond.setLastYear(String.valueOf(lastYear - 1));
        return budgetService.getBudgetListByCond(cond);
    }

    @ApiOperation(value = "按条件获取预算列表", notes = "树形结构、KV")
    @GetMapping("/tree")
    public List<TreeSelectParentVO> getTreeListByUnit(
            @ApiParam(value = "单位id") @RequestParam(required = false) Integer unitId,
            @ApiParam(value = "导入日期") @RequestParam(required = false) String importDate,
            @ApiParam(value = "项目类别名称") @RequestParam(required = false) String projectItem) {
        if (StringUtils.isEmpty(importDate)) {
            importDate = DateUtil.getCurrentYear();
        }
        if (!RegexUtil.isYear(importDate)) {
            throw new BizException(BizStatus.BUDGET_IMPORT_DATE_ERROR.getCode());
        }
        return budgetService.getTreeByCond(BudgetReqVO.builder()
                .unitId(unitId)
                .importDate(importDate)
                .projectItem(projectItem)
                .build());
    }

    @ApiOperation("获取所有项目类型列表")
    @GetMapping("/project-type/list")
    public List<SelectVO> getProjectTypeList() {
        return budgetService.getProjectTypeList();
    }

    @ApiOperation("获取预算详情")
    @GetMapping("/{id}")
    public BudgetVO getBudgetInfo(
            @ApiParam(value = "预算id", required = true) @PathVariable Long id) {
        return budgetService.getInfoById(id);
    }

    @ApiOperation("预算调整")
    @PutMapping("/adjust")
    public void updateBudget(
            @ApiParam(value = "预算详情", required = true) @RequestBody BudgetAdjustVO vo) {
        budgetService.updateThreshold(vo);
    }

    @ApiOperation("预算模板导入")
    @PostMapping("/import")
    public void importBudgetBreak(MultipartFile file,
                                  String importDate) {
        long begin = System.currentTimeMillis();
        String fileName = file.getOriginalFilename();
        budgetService.judegFileName(fileName);
        if (importDate == null) {
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
        log.info("预算分解模板导入结束，总共耗时：" + (System.currentTimeMillis() - begin) / 1000 + "s");
    }

    @ApiOperation("预算导出")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @RequestBody BudgetReqVO cond,
                       @ApiIgnore @CurrentUser LoginUser loginUser) {
        if (cond.getUnitId() == null) {
            cond.setUnitId(loginUser.getUser().getUnitId());
        }
        if (cond.getImportDate() == null) {
            cond.setImportDate(DateUtil.getCurrentYear());
        }
        List<BudgetExportVO> list = budgetService.getExportList(cond);
        String fileName = ExcelUtil.encodingFileName(request, "预算分解");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetExportVO.class, list);
    }

    @ApiOperation(value = "新增预算")
    @PostMapping("")
    public void saveBudget(
            @RequestBody BudgetInfoVO budgetAddVO) {
        budgetService.addBudget(budgetAddVO);
    }

    @ApiOperation(value = "删除预算")
    @DeleteMapping("/{budgetId}")
    public void deleteBudget(
            @ApiParam(value = "预算表id", required = true) @PathVariable Long budgetId) {
        budgetService.deleteByItemId(budgetId);
    }

    @ApiOperation(value = "修改预算")
    @PutMapping("")
    public void updateBudget(
            @RequestBody BudgetInfoVO budgetInfoVO) {
        budgetService.updateByUpdateVO(budgetInfoVO);
    }


    @ApiOperation("通过预算id获取金额阀值")
    @GetMapping("/threshold/{id}")
    public BigDecimal getBudgetThreshold(
            @ApiParam(value = "预算id", required = true) @PathVariable Long id) {
        return budgetService.getBudgetThreshold(id);
    }
}
