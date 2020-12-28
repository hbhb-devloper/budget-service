package com.hbhb.cw.budget.web.controller;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.core.utils.ExcelUtil;
import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectFlowService;
import com.hbhb.cw.budget.service.BudgetProjectService;
import com.hbhb.cw.budget.web.vo.BudgetProjectApproveVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailExportReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectInitVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectResVO;
import com.hbhb.cw.flowcenter.enums.FlowState;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.annotation.UserId;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "预算执行-项目签报相关")
@RestController
@RequestMapping("/project")
public class BudgetProjectController {

    @Resource
    private BudgetProjectService budgetProjectService;
    @Resource
    private BudgetProjectFlowService budgetProjectFlowService;
    @Resource
    private UserApiExp userApi;

    @Operation(summary = "按条件获取项目签报列表（分页）")
    @GetMapping("/list")
    public Page<BudgetProjectResVO> getBudgetProjectList(
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize,
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

    @Operation(summary = "根据签报id获取签报详情")
    @GetMapping("/info/{id}")
    public BudgetProjectDetailVO getByBudgetProject(@PathVariable Long id) {
        return budgetProjectService.getBudgetProject(id);
    }

    @Operation(summary = "新增签报")
    @PostMapping("")
    public Integer addBudgetProject(@RequestBody BudgetProjectDetailVO budgetProjectDetailVO,
                                    @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        return budgetProjectService.addSaveBudgetProject(budgetProjectDetailVO, user);
    }

    @Operation(summary = "删除签报")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProject(@PathVariable Integer id,
                                    @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        budgetProjectService.deleteBudgetProject(id, user);
    }

    @Operation(summary = "修改签报")
    @PutMapping("/update")
    public void updateBudgetProject(@RequestBody BudgetProjectDetailVO budgetProjectDetailVO,
                                    @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        budgetProjectService.updateBudgetProject(budgetProjectDetailVO, user);
    }

    @Operation(summary = "签报导出")
    @PostMapping("/export")
    public void export(HttpServletRequest request, HttpServletResponse response,
                       @Parameter(description = "需导出的签报条件") @RequestBody BudgetProjectReqVO cond,
                       @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        if (cond.getUnitId() == null) {
            cond.setUnitId(user.getUnitId());
        }
        if (cond.getProjectYear() == null) {
            cond.setProjectYear(DateUtil.getCurrentYear());
        }
        List<BudgetProjectExportVO> list = budgetProjectService.getExportList(cond);
        String fileName = ExcelUtil.encodingFileName(request, "签报列表");
        ExcelUtil.export2Web(response, fileName, fileName, BudgetProjectExportVO.class, list);
    }

    @Operation(summary = "发起审批:项目签报发起审批")
    @PostMapping("/to-approve")
    public void toApprove(@RequestBody BudgetProjectInitVO initVO,
                          @Parameter(hidden = true) @UserId Integer userId) {

        initVO.setUserId(userId);
        budgetProjectService.toApprove(initVO);
    }

    @Operation(summary = "审批 项目签报节点流转")
    @PostMapping("/approve")
    public void approve(@RequestBody BudgetProjectApproveVO approveVO,
                        @Parameter(hidden = true) @UserId Integer userId) {
        budgetProjectFlowService.approve(approveVO, userId);
    }

    @Operation(summary = "审批回滚 :回滚")
    @PostMapping("/revert")
    public Integer revert(@RequestBody Integer id,
                          @Parameter(hidden = true) @UserId Integer userId) {
        budgetProjectService.revert(id, userId);
        return FlowState.APPROVED.value();
    }

    @Operation(summary = "导出word 导出项目签报详情至word格式")
    @PostMapping("/info/export")
    public String export(HttpServletResponse response, @RequestBody BudgetProjectDetailExportReqVO vo) {
        return budgetProjectService.export2Word(response, vo);
    }
}

