package com.hbhb.cw.budget.service.impl;


import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetProjectFileMapper;
import com.hbhb.cw.budget.model.BudgetProjectFile;
import com.hbhb.cw.budget.rpc.FileApiExp;
import com.hbhb.cw.budget.service.BudgetProjectFileService;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class BudgetProjectFileServiceImpl implements BudgetProjectFileService {

    @Resource
    private BudgetProjectFileMapper bpfMapper;
    @Resource
    private FileApiExp fileApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectFile(Long fileId, UserInfo user) {
        BudgetProjectFile budgetProjectFile = bpfMapper.selectProjectFileByFileId(fileId);
        if (budgetProjectFile == null) {
            fileApi.deleteFile(fileId);
        }
        if (budgetProjectFile != null && !budgetProjectFile.getAuthor().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        bpfMapper.deleteByFileId(fileId);
    }
}