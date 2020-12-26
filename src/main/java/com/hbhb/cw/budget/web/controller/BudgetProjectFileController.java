package com.hbhb.cw.budget.web.controller;


import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectFileService;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.annotation.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Tag(name = "预算执行-项目签报附件相关")
@RestController
@RequestMapping("/budget/project/file")
public class BudgetProjectFileController {
    @Resource
    private BudgetProjectFileService fileService;
    @Resource
    private UserApiExp userApi;

    @Operation(summary = "删除文件")
    @DeleteMapping("/delete/{fileId}")
    public void deleteProjectFile(@Parameter(description = "fileId", required = true)
                                  @PathVariable Long fileId,
                                  @UserId Integer userId) {
        if (fileId == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_FILE_DELETE_SUCCESSFUL);
        }
        UserInfo user = userApi.getUserInfoById(userId);
        fileService.deleteProjectFile(fileId, user);
    }


}
