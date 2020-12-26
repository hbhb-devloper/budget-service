package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProjectNoticeService;
import com.hbhb.cw.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.web.vo.BudgetProjectNoticeResVO;
import com.hbhb.cw.web.vo.BudgetProjectNoticeVO;
import com.hbhb.springboot.web.view.Page;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "预算执行-工作台提醒")
@RestController
@RequestMapping("/notice")
public class BudgetProjectNoticeController {

    @Resource
    private BudgetProjectNoticeService budgetProjectNoticeService;


    @ApiOperation("新增提醒")
    @PostMapping("")
    public void addBudgetProjectNotice(
            @RequestBody BudgetProjectNoticeReqVO budgetProjectNoticeReqVO) {
        budgetProjectNoticeService.andSaveBudgetProjectNotice(budgetProjectNoticeReqVO);
    }

    @ApiOperation("根据接收人id查询提醒列表")
    @GetMapping("/list")
    public Page<BudgetProjectNoticeResVO> getBudgetProjectNoticeList(
            @ApiIgnore @CurrentUser LoginUser loginUser,
            @ApiParam(value = "筛选条件") BudgetProjectNoticeVO bpNoticeVo,
            @ApiParam(value = "页码，默认为1") @RequestParam(required = false) Integer pageNum,
            @ApiParam(value = "每页数量，默认为10") @RequestParam(required = false) Integer pageSize) {
        Integer userId = loginUser.getUser().getId();
        bpNoticeVo.setUserId(userId);
        pageNum = pageNum == null ? 1 : pageNum;
        pageSize = pageSize == null ? 10 : pageSize;
        return budgetProjectNoticeService.getBudgetProjectNoticeList(bpNoticeVo, pageNum, pageSize);

    }

    @ApiOperation("删除提醒")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProjectNotice(@PathVariable Long id) {
        budgetProjectNoticeService.deleteBudgetProjectNotice(id);
    }

    @ApiOperation("修改提醒状态")
    @PutMapping("/update/{id}")
    public void updateBudgetProjectNoticeState(@PathVariable Long id) {
        budgetProjectNoticeService.updateBudgetProjectNoticeState(id);
    }
}
