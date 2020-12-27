package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectSplitService;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.annotation.UserId;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "预算执行-项目签报分类预算")
@RestController
@RequestMapping("/project/split")
public class BudgetProjectSplitController {
    @Resource
    private BudgetProjectSplitService bpcService;
    @Resource
    private UserApiExp userApi;

    @Operation(summary = "根据签报id查找分类预算")
    @GetMapping("/list/{projectId}")
    public List<BudgetProjectSplitVO> getBudgetProjectSplitList(
            @Parameter(description = "签报id", required = true) @PathVariable Integer projectId) {
        return bpcService.getBudgetProjectSplitList(projectId);
    }


    @Operation(summary = "新增分类预算签报")
    @PostMapping("")
    public void addProjectSplit(@RequestBody BudgetProjectSplitVO bpClassVo,
                                @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        bpcService.andSaveBudgetProjectSplit(bpClassVo, user);
    }

    @Operation(summary = "删除分类预算")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProjectSplit(
            @Parameter(description = "id", required = true) @PathVariable Integer id,
            @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        bpcService.deleteBudgetProjectSplit(id, user);
    }

    @Operation(summary = "修改分类预算")
    @PutMapping("/update")
    private void updateProjectClass(@RequestBody BudgetProjectSplitVO bpClassVo,
                                    @Parameter(hidden = true) @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        bpcService.updateProjectSplit(bpClassVo, user);
    }

    @Operation(summary = "跟据id查询分类预算详情")
    @GetMapping("/info/{id}")
    private BudgetProjectSplitVO getBudgetProjectSplit(
            @Parameter(description = "id", required = true) @PathVariable Integer id) {
        return bpcService.getProjectSplit(id);
    }
}
