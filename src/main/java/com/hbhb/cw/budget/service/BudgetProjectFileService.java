package com.hbhb.cw.budget.service;

import com.hbhb.cw.systemcenter.vo.UserInfo;

public interface BudgetProjectFileService {

    /**
     * 删除附件
     */
    void deleteProjectFile(Long fileId, UserInfo user);
}
