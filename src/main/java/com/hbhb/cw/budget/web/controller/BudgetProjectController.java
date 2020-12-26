package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.common.ProjectState;
import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProjectFlowService;
import com.hbhb.cw.service.BudgetProjectService;
import com.hbhb.cw.utils.DateUtil;
import com.hbhb.cw.utils.ExcelUtil;
import com.hbhb.cw.web.vo.BudgetProjectApproveVO;
import com.hbhb.cw.web.vo.BudgetProjectDetailExportReqVO;
import com.hbhb.cw.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.web.vo.BudgetProjectExportVO;
import com.hbhb.cw.web.vo.BudgetProjectInitVO;
import com.hbhb.cw.web.vo.BudgetProjectReqVO;
import com.hbhb.cw.web.vo.BudgetProjectResVO;
import com.hbhb.springboot.web.view.Page;

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
 * @Author:WXG
 * @Date:2020/7/8
 */
@Api(tags = "预算执行-项目签报相关")
@RestController
@RequestMapping("/budget/project")
public class BudgetProjectController {

    @Resource
    private BudgetProjectService budgetProjectService;
    @Resource
    private BudgetProjectFlowService budgetProjectFlowService;

    @ApiOperation("按条件获取项目签报列表（分页）")
    @GetMapping("/list")
    public Page<BudgetProjectResVO> getBudgetProjectList(
            @ApiParam(value = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @ApiParam(value = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
            BudgetProjectReqVO cond) {
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 20 : pageSize;
        if (cond.getUnitId() == null) {
            return new Page<>(new ArrayList<>(), 0L);
        }
        if (StringUtils.isEmpty(cond.getProjectYear())) {
            cond.setProjectYear(DateUtil.getCurrentYear());
        }
        return budgetProjectService.getBudgetProjectPage(pageNum, pageSize, true, cond);
    }

    @ApiOperation("根据签报id获取签报详情")
    @GetMapping("/info/{id}")
    public BudgetProjectDetailVO getByBudgetProject(@PathVariable Long id) {
        return budgetProjectService.getBudgetProject(id);
    }

    @ApiOperation("新增签报")
    @PostMapping("")
    public Integer addBudgetProject(@RequestBody BudgetProjectDetailVO budgetProjectDetailVO,
                                    @ApiIgnore @CurrentUser LoginUser loginUser) {
        return budgetProjectService.addSaveBudgetProject(budgetProjectDetailVO, loginUser.getUser());
    }

    @ApiOperation("删除签报")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProject(@PathVariable Integer id,
                                    @ApiIgnore @CurrentUser LoginUser loginUser) {
        budgetProjectService.deleteBudgetProject(id, loginUser.getUser());
    }

    @ApiOperation("修改签报")
    @PutMapping("/update")
    public void updateBudgetProject(@RequestBody BudgetProjectDetailVO budgetProjectDetailVO,
                                    @ApiIgnore @CurrentUser LoginUser loginUser) {
        budgetProjectService.updateBudgetProject(budgetProjectDetailVO, loginUser.getUser());
    }

    @ApiOperation("签报导出")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @ApiParam(value = "需导出的签报条件") @RequestBody BudgetProjectReqVO cond,
                       @ApiIgnore @CurrentUser LoginUser loginUser) {
        if (cond.getUnitId() == null) {
            cond.setUnitId(loginUser.getUser().getUnitId());
        }
        if (cond.getProjectYear() == null) {
            cond.setProjectYear(DateUtil.getCurrentYear());
        }
        List<BudgetProjectExportVO> list = budgetProjectService.getExportList(cond);
        String fileName = ExcelUtil.encodingFileName(request, "签报列表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetProjectExportVO.class, list);
    }

    @ApiOperation(value = "发起审批", notes = "项目签报发起审批")
    @PostMapping("/to-approve")
    public void toApprove(@RequestBody BudgetProjectInitVO initVO,
                          @ApiIgnore @CurrentUser LoginUser loginUser) {
        initVO.setUserId(loginUser.getUser().getId());
        budgetProjectService.toApprove(initVO);
    }

    @ApiOperation(value = "审批", notes = "项目签报节点流转")
    @PostMapping("/approve")
    public void approve(@RequestBody BudgetProjectApproveVO approveVO,
                        @ApiIgnore @CurrentUser LoginUser loginUser) {
        budgetProjectFlowService.approve(approveVO, loginUser.getUser().getId());
    }

    @ApiOperation(value = "审批回滚", notes = "回滚")
    @PostMapping("/revert")
    public Integer revert(@RequestBody Integer id,
                          @ApiIgnore @CurrentUser LoginUser loginUser) {
        budgetProjectService.revert(id, loginUser.getUser().getId());
        return ProjectState.APPROVED.value();
    }

    @ApiOperation(value = "导出word", notes = "导出项目签报详情至word格式")
    @PostMapping("/info/export")
    public void export(HttpServletResponse response, @RequestBody BudgetProjectDetailExportReqVO vo) {
        budgetProjectService.export2Word(response, vo);
    }
}

