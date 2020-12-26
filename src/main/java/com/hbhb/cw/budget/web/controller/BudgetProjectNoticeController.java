package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.service.BudgetProjectNoticeService;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeVO;
import com.hbhb.cw.budget.web.vo.WorkBenchAgendaVO;
import com.hbhb.cw.budgetservice.api.NoticeApi;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Tag(name = "预算执行-工作台提醒")
@RestController
@RequestMapping("/notice")
public class BudgetProjectNoticeController implements NoticeApi {

    @Resource
    private BudgetProjectNoticeService budgetProjectNoticeService;


    @Operation(summary = "新增提醒")
    @PostMapping("")
    public void addBudgetProjectNotice(
            @RequestBody BudgetProjectNoticeReqVO budgetProjectNoticeReqVO) {
        budgetProjectNoticeService.andSaveBudgetProjectNotice(budgetProjectNoticeReqVO);
    }

    @Operation(summary = "根据接收人id查询提醒列表")
    @GetMapping("/list")
    public Page<BudgetProjectNoticeResVO> getBudgetProjectNoticeList(
            @UserId Integer userId,
            @Parameter(description = "筛选条件") BudgetProjectNoticeVO bpNoticeVo,
            @Parameter(description = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @Parameter(description = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize) {
        bpNoticeVo.setUserId(userId);
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return budgetProjectNoticeService.getBudgetProjectNoticeList(bpNoticeVo, pageNum, pageSize);

    }

    @Operation(summary = "删除提醒")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProjectNotice(@PathVariable Long id) {
        budgetProjectNoticeService.deleteBudgetProjectNotice(id);
    }

    @Operation(summary = "修改提醒状态")
    @PutMapping("/update/{id}")
    public void updateBudgetProjectNoticeState(@PathVariable Long id) {
        budgetProjectNoticeService.updateBudgetProjectNoticeState(id);
    }

    @Operation(summary = "待办内容")
    @GetMapping("/content")
    public List<WorkBenchAgendaVO> getBudgetNoticeList(@UserId Integer userId) {
        return budgetProjectNoticeService.getBudgetNoticeList(userId);
    }

    @Override
    @Operation(summary = "提醒数量")
    public Long countNotice(Integer userId) {
        return budgetProjectNoticeService.getNoticeAccount(userId);
    }
}
