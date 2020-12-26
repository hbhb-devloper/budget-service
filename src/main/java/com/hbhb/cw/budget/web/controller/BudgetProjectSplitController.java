package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectSplitService;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


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
                                @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        bpcService.andSaveBudgetProjectSplit(bpClassVo, user);
    }

    @Operation(summary = "删除分类预算")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProjectSplit(
            @Parameter(description = "id", required = true) @PathVariable Integer id,
            @UserId Integer userId) {
        UserInfo user = userApi.getUserInfoById(userId);
        bpcService.deleteBudgetProjectSplit(id, user);
    }

    @Operation(summary = "修改分类预算")
    @PutMapping("/update")
    private void updateProjectClass(@RequestBody BudgetProjectSplitVO bpClassVo,
                                    @UserId Integer userId) {
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
