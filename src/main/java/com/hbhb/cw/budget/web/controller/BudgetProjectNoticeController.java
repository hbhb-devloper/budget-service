package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.service.BudgetProjectNoticeService;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeVO;
import com.hbhb.cw.budget.web.vo.WorkBenchAgendaVO;
import com.hbhb.cw.budgetservice.api.NoticeApi;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "预算执行-工作台")
@RestController
@RequestMapping("/notice")
public class BudgetProjectNoticeController implements NoticeApi {

    @Resource
    private BudgetProjectNoticeService budgetProjectNoticeService;

    @Operation(summary = "根据接收人id查询提醒列表")
    @GetMapping("/list")
    public Page<BudgetProjectNoticeResVO> getBudgetProjectNoticeList(
            @Parameter(hidden = true) @UserId Integer userId,
            @Parameter(description = "筛选条件") BudgetProjectNoticeVO bpNoticeVo,
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize) {
        bpNoticeVo.setUserId(userId);
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return budgetProjectNoticeService.getBudgetProjectNoticeList(bpNoticeVo, pageNum, pageSize);
    }

    @Operation(summary = "获取登录用户的待办提醒")
    @GetMapping("/summary")
    public List<WorkBenchAgendaVO> getBudgetNoticeList(@Parameter(hidden = true) @UserId Integer userId) {
        return budgetProjectNoticeService.getBudgetNoticeList(userId);
    }

    @Operation(summary = "新增提醒")
    @PostMapping("")
    public void addBudgetProjectNotice(
            @RequestBody BudgetProjectNoticeReqVO budgetProjectNoticeReqVO) {
        budgetProjectNoticeService.andSaveBudgetProjectNotice(budgetProjectNoticeReqVO);
    }

    @Operation(summary = "删除提醒")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProjectNotice(@PathVariable Long id) {
        budgetProjectNoticeService.deleteBudgetProjectNotice(id);
    }

    @Operation(summary = "更新提醒消息为已读")
    @PutMapping("/{id}")
    public void updateBudgetProjectNoticeState(@PathVariable Long id) {
        budgetProjectNoticeService.updateBudgetProjectNoticeState(id);
    }

    @Override
    @Operation(summary = "提醒数量")
    public Long countNotice(Integer userId) {
        return budgetProjectNoticeService.getNoticeAccount(userId);
    }
}
