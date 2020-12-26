package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProjectSplitService;
import com.hbhb.cw.web.vo.BudgetProjectSplitVO;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "预算执行-项目签报分类预算")
@RestController
@RequestMapping("/budget/project/split")
public class BudgetProjectSplitController {
    @Resource
    private BudgetProjectSplitService bpcService;

    @ApiOperation("根据签报id查找分类预算")
    @GetMapping("/list/{projectId}")
    public List<BudgetProjectSplitVO> getBudgetProjectSplitList(
            @ApiParam(value = "签报id", required = true) @PathVariable Integer projectId) {
        return bpcService.getBudgetProjectSplitList(projectId);
    }


    @ApiOperation("新增分类预算签报")
    @PostMapping("")
    public void addProjectSplit(@RequestBody BudgetProjectSplitVO bpClassVo,
                                @ApiIgnore @CurrentUser LoginUser loginUser) {
        bpcService.andSaveBudgetProjectSplit(bpClassVo, loginUser.getUser());
    }

    @ApiOperation("删除分类预算")
    @DeleteMapping("/delete/{id}")
    public void deleteBudgetProjectSplit(
            @ApiParam(value = "id", required = true) @PathVariable Integer id,
            @ApiIgnore @CurrentUser LoginUser loginUser) {
        bpcService.deleteBudgetProjectSplit(id,loginUser.getUser());
    }

    @ApiOperation("修改分类预算")
    @PutMapping("/update")
    private void updateProjectClass(@RequestBody BudgetProjectSplitVO bpClassVo,
                                    @ApiIgnore @CurrentUser LoginUser loginUser) {
        bpcService.updateProjectSplit(bpClassVo,loginUser.getUser());
    }

    @ApiOperation("跟据id查询分类预算详情")
    @GetMapping("/info/{id}")
    private BudgetProjectSplitVO getBudgetProjectSplit(
            @ApiParam(value = "id", required = true) @PathVariable Integer id) {
        return bpcService.getProjectSplit(id);
    }
}
