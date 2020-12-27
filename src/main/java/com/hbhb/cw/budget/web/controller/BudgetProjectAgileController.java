package com.hbhb.cw.budget.web.controller;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.rpc.FileApiExp;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectAgileService;
import com.hbhb.cw.budget.web.vo.BudgetAgileAddVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAgileVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * @author yzc
 * @since 2020-09-23
 */
@Tag(name = "预算执行-日常性签报")
@RestController
@RequestMapping("/agile")
public class BudgetProjectAgileController {

    @Resource
    private BudgetProjectAgileService budgetProjectAgileService;
    @Resource
    private FileApiExp fileApi;
    @Resource
    private UserApiExp userApi;

    @Operation(summary = "按条件获取项目签报列表（分页）")
    @GetMapping("/list")
    public Page<BudgetProjectAgileVO> getBudgetProjectList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            BudgetProjectAgileReqVO cond) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        if (cond.getUnitId() == null) {
            return new Page<>(new ArrayList<>(), 0L);
        }
        cond.setImportDate(DateUtil.getCurrentYear());
        return budgetProjectAgileService.getBudgetAgileList(pageNum, pageSize, cond);
    }

    @Operation(summary = "根据签报id获取签报详情")
    @GetMapping("/info/{id}")
    public BudgetProjectAgileInfoVO getByBudgetAgile(@PathVariable Long id) {
        return budgetProjectAgileService.getBudgetAgile(id);
    }

    @Operation(summary = "新增常用性签报")
    @PostMapping("")
    public void addBudgetProject(@RequestBody BudgetAgileAddVO cond,
                                 @Parameter(hidden = true) @UserId Integer userId) {
        cond.setImportDate(DateUtil.getCurrentYear());
        UserInfo user = userApi.getUserInfoById(userId);
        budgetProjectAgileService.addSaveBudgetAgile(cond, user);
    }


    @Operation(summary = "日常性签报导出")
    @PostMapping("/export/subsidy")
    public void exportSubsidy(HttpServletRequest request, HttpServletResponse response,
                              @RequestBody BudgetProjectAgileReqVO cond,
                              @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (cond.getUnitId() == null) {
            cond.setUnitId(user.getUnitId());
        }
        cond.setImportDate(DateUtil.getCurrentYear());
        List<BudgetProjectAgileExportVO> list = budgetProjectAgileService.getDetailsExportByCond(cond);
        String fileName = ExcelUtil.encodingFileName(request, "日常性费用导出模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "表单",
                fileApi.getTemplatePath() + File.separator + "日常性费用导出模板.xlsx", list);
    }

    @Operation(summary = "删除常用性签报")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProject(@PathVariable Long id,
                                    @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        budgetProjectAgileService.deleteBudgetProject(id, user);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete/file/{fileId}")
    public void deleteAgileFile(@Parameter(description = "fileId", required = true)
                                @PathVariable Long fileId,
                                @Parameter(hidden = true) @UserId Integer userId) {
        if (fileId == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_FILE_DELETE_SUCCESSFUL);
        }
        UserInfo user = userApi.getUserInfoById(userId);
        budgetProjectAgileService.deleteAgileFile(fileId, user);
    }

    @Operation(summary = "导出word 导出常用性项目签报详情至word格式")
    @PostMapping("/info/export")
    public void export(HttpServletResponse response, @RequestBody BudgetProjectAgileInfoVO vo) {
        budgetProjectAgileService.export2Word(response, vo);
    }
}
