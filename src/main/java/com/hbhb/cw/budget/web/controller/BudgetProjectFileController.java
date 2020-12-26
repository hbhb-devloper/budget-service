package com.hbhb.cw.budget.web.controller;

import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.security.CurrentUser;
import com.hbhb.cw.security.LoginUser;
import com.hbhb.cw.service.BudgetProjectFileService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "预算执行-项目签报附件相关")
@RestController
@RequestMapping("/budget/project/file")
public class BudgetProjectFileController {
    @Resource
    private BudgetProjectFileService fileService;

    @ApiOperation("删除文件")
    @DeleteMapping("/delete/{fileId}")
    public void deleteProjectFile(@ApiParam(value = "fileId", required = true)
                                  @PathVariable Long fileId,
                                  @CurrentUser @ApiIgnore LoginUser loginUser) {
        if (fileId == null) {
            throw new BizException(BizStatus.BUDGET_PROJECT_FILE_DELETE_SUCCESSFUL.getCode());
        }
        fileService.deleteProjectFile(fileId, loginUser.getUser());
    }


}
