package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.rpc.FileApiExp;
import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProjectAgileService;
import com.hbhb.cw.utils.DateUtil;
import com.hbhb.cw.utils.ExcelUtil;
import com.hbhb.cw.web.vo.BudgetAgileAddVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileExportVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileInfoVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileReqVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileVO;
import com.hbhb.springboot.web.view.Page;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author yzc
 * @since 2020-09-23
 */
@Api(tags = "预算执行-日常性签报")
@RestController
@RequestMapping("/budget/agile")
public class BudgetProjectAgileController {

    @Resource
    private BudgetProjectAgileService budgetProjectAgileService;
    @Resource
    private FileApiExp fileApi;

    @ApiOperation("按条件获取项目签报列表（分页）")
    @GetMapping("/list")
    public Page<BudgetProjectAgileVO> getBudgetProjectList(
            @ApiParam(value = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @ApiParam(value = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            BudgetProjectAgileReqVO cond) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        if (cond.getUnitId() == null) {
            return new Page<>(new ArrayList<>(), 0L);
        }
        cond.setImportDate(DateUtil.getCurrentYear());
        return budgetProjectAgileService.getBudgetAgileList(pageNum, pageSize, cond);
    }

    @ApiOperation("根据签报id获取签报详情")
    @GetMapping("/info/{id}")
    public BudgetProjectAgileInfoVO getByBudgetAgile(@PathVariable Long id) {
        return budgetProjectAgileService.getBudgetAgile(id);
    }

    @ApiOperation("新增常用性签报")
    @PostMapping("")
    public void addBudgetProject(@RequestBody BudgetAgileAddVO cond,
                                 @ApiIgnore @CurrentUser LoginUser loginUser) {
        cond.setImportDate(DateUtil.getCurrentYear());
        budgetProjectAgileService.addSaveBudgetAgile(cond, loginUser.getUser());
    }


    @ApiOperation("日常性签报导出")
    @PostMapping("/export/subsidy")
    public void exportSubsidy(HttpServletRequest request, HttpServletResponse response,
                              @RequestBody BudgetProjectAgileReqVO cond,
                              @ApiIgnore @CurrentUser LoginUser loginUser) {
        if (cond.getUnitId() == null) {
            cond.setUnitId(loginUser.getUser().getUnitId());
        }
        cond.setImportDate(DateUtil.getCurrentYear());
        List<BudgetProjectAgileExportVO> list = budgetProjectAgileService.getDetailsExportByCond(cond);
        String fileName = ExcelUtil.encodingFileName(request, "日常性费用导出模板");
        ExcelUtil.export2WebWithTemplate(response, fileName, "表单",
                fileApi.getTemplatePath() + File.separator + "日常性费用导出模板.xlsx", list);
    }

    @ApiOperation("删除常用性签报")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProject(@PathVariable Long id,
                                    @ApiIgnore @CurrentUser LoginUser loginUser) {
        budgetProjectAgileService.deleteBudgetProject(id, loginUser.getUser());
    }

    @ApiOperation("删除文件")
    @DeleteMapping("/delete/file/{fileId}")
    public void deleteAgileFile(@ApiParam(value = "fileId", required = true)
                                @PathVariable Long fileId,
                                @CurrentUser @ApiIgnore LoginUser loginUser) {
        if (fileId == null) {
            throw new BizException(BizStatus.BUDGET_PROJECT_FILE_DELETE_SUCCESSFUL.getCode());
        }
        budgetProjectAgileService.deleteAgileFile(fileId, loginUser.getUser());
    }

    @ApiOperation(value = "导出word", notes = "导出常用性项目签报详情至word格式")
    @PostMapping("/info/export")
    public void export(HttpServletResponse response, @RequestBody BudgetProjectAgileInfoVO vo) {
        budgetProjectAgileService.export2Word(response, vo);
    }
}
