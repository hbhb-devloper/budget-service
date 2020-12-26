package com.hbhb.cw.budget.service.impl;

import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.mapper.BudgetProjectFileMapper;
import com.hbhb.cw.model.BudgetProjectFile;
import com.hbhb.cw.rpc.FileApiExp;
import com.hbhb.cw.service.BudgetProjectFileService;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

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
            throw new BizException(BizStatus.BUDGET_PROJECT_INITIATOR_ERROR.getCode());
        }
        bpfMapper.deleteByFileId(fileId);
    }
}