package com.hbhb.cw.budget.service.impl;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetProjectMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectSplitApprovedMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectSplitMapper;
import com.hbhb.cw.budget.model.BudgetProject;
import com.hbhb.cw.budget.model.BudgetProjectSplit;
import com.hbhb.cw.budget.model.BudgetProjectSplitApproved;
import com.hbhb.cw.budget.service.BudgetProjectSplitService;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitVO;
import com.hbhb.cw.flowcenter.enums.FlowState;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class BudgetProjectSplitServiceImpl implements BudgetProjectSplitService {

    @Resource
    private BudgetProjectMapper budgetProjectMapper;
    @Resource
    private BudgetProjectSplitMapper budgetProjectSplitMapper;
    @Resource
    private BudgetProjectSplitApprovedMapper budgetProjectSplitApprovedMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void andSaveBudgetProjectSplit(BudgetProjectSplitVO bpClassVo, UserInfo user) {

        BudgetProject bp = budgetProjectMapper.selectByPrimaryKey(bpClassVo.getProjectId());
        if (!bp.getCreateBy().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        BudgetProjectSplit bpClass = new BudgetProjectSplit();
        BeanUtils.copyProperties(bpClassVo, bpClass);
        bpClass.setCreateBy(user.getNickName());
        // 处理单价、总额格式

        if (bpClassVo.getPrice() != null) {
            bpClass.setPrice(new BigDecimal(bpClassVo.getPrice()));
        }
        bpClass.setCost(new BigDecimal(bpClassVo.getCost()));
        // 处理日期
        bpClass.setYears(bpClassVo.getYears());
        bpClass.setCreateTime(new Date());
        Long id = Long.valueOf(bpClassVo.getProjectId());
        // 通过签报id获取项目开始时间结束时间，判断用户所选时间是否在范围内
        BudgetProject budgetProject = budgetProjectMapper.selectBudgetProjectById(id);
        int startTime = Integer.parseInt(DateUtil.dateToStringY(budgetProject.getStartTime()));
        int endTime = Integer.parseInt(DateUtil.dateToStringY(budgetProject.getEndTime()));
        int count = budgetProjectSplitMapper.selectCondByProjectId(bpClassVo.getProjectId());
        // 若单个项目签报分类预算数量超过所跨年度则不允许添加
        if (count > (endTime - startTime)) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_SPLIT_EXCESS);
        }
        // 若用户所选年份不不为空则判断是否在签报期间
        if (bpClassVo.getYears() != null) {
            int year = Integer.parseInt(bpClassVo.getYears());
            if (year < startTime || year > endTime) {
                throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_NOT_AT_RISK);
            }
            // 跨年项目用户未选择年份
        } else if (endTime - startTime != 0) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_SPLIT_YEAR_NOT_NULL);
        }
        // 判断填写金额是否超过签报项目预算总额
        BigDecimal amount = budgetProject.getAmount();
        BigDecimal cost = budgetProjectSplitMapper.selectSumAmountByProjectId(id);
        if (cost == null) {
            cost = bpClass.getCost();
        } else {
            cost = cost.add(bpClass.getCost());
        }

        if (cost.compareTo(amount) > 0) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_SPLIT_COST_ERROR);
        }
        // 添加
        budgetProjectSplitMapper.insertSelective(bpClass);
    }

    @Override
    public List<BudgetProjectSplitVO> getBudgetProjectSplitList(Integer projectId) {
        return budgetProjectSplitMapper.selectSplitByProjectId(projectId);
    }

    @Override
    public void deleteBudgetProjectSplit(Integer id, UserInfo user) {
        BudgetProjectSplit bpSplit = budgetProjectSplitMapper.selectByPrimaryKey(id);
        if (!bpSplit.getCreateBy().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        budgetProjectSplitMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProjectSplit(BudgetProjectSplitVO bpClassVo, UserInfo user) {
        // 判断是否为项目签报人
        BudgetProjectSplit bpSplit = budgetProjectSplitMapper.selectByPrimaryKey(bpClassVo.getId());
        if (!bpSplit.getCreateBy().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        BudgetProjectSplit bpClass = new BudgetProjectSplit();
        BeanUtils.copyProperties(bpClassVo, bpClass);
        //处理单价、总额格式
        if (bpClassVo.getPrice() != null) {
            bpClass.setPrice(new BigDecimal(bpClassVo.getPrice()));
        }
        bpClass.setCost(new BigDecimal(bpClassVo.getCost()));
        // 判断修改总额是否超出签报总额
        Integer projectId = bpClass.getProjectId();
        BudgetProject budgetProject = budgetProjectMapper.selectByPrimaryKey(projectId);
        BigDecimal amount = budgetProject.getAmount();
        BigDecimal cost = new BigDecimal(bpClassVo.getCost());
        if (cost.compareTo(amount) > 0) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_SPLIT_COST_ERROR);
        }
        //处理日期
        bpClass.setYears(bpClassVo.getYears());
        budgetProjectSplitMapper.updateByPrimaryKeySelective(bpClass);
        // 判断项目签报审批状态若为审批通过则修改状态为调整中
        if (budgetProject.getState().equals(FlowState.APPROVED.value())) {
            budgetProjectMapper.updateStateById(projectId, FlowState.IN_ADJUST.value());
        }
    }

    @Override
    public BudgetProjectSplitVO getProjectSplit(Integer id) {
        return budgetProjectSplitMapper.selectBudgetProjectSplitById(id);
    }

    @Override
    public void deleteApprovedByProjectId(Integer projectId) {
        budgetProjectSplitApprovedMapper.deleteApprovedByProjectId(projectId);
    }

    @Override
    public void insertBatchApproved(List<BudgetProjectSplitApproved> list) {
        budgetProjectSplitApprovedMapper.insertBatchApproved(list);
    }

    @Override
    public List<BudgetProjectSplit> getSplitByProjectId(Integer projectId) {
        return budgetProjectSplitMapper.selectSplitAllByProjectId(projectId);
    }

    @Override
    public List<BudgetProjectSplitVO> getBudgetApprovedSplitList(Integer projectId) {
        return budgetProjectSplitApprovedMapper.selectApprovedByProjectId(projectId);
    }
}