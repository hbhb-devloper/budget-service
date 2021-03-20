package com.hbhb.cw.budget.service.impl;


import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.budget.mapper.BudgetProjectFlowHistoryMapper;
import com.hbhb.cw.budget.model.BudgetProjectFlowHistory;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectFlowHistoryService;
import com.hbhb.cw.budget.service.BudgetProjectService;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowHistoryVO;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

@Service
public class BudgetProjectFlowHistoryServiceImpl implements BudgetProjectFlowHistoryService {

    @Resource
    private BudgetProjectFlowHistoryMapper budgetProjectFlowHistoryMapper;
    @Resource
    private BudgetProjectService budgetProjectService;
    @Resource
    private UserApiExp userApi;

    @Override
    public void saveBudgetProjectFlowHistory(List<BudgetProjectFlowHistory> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        budgetProjectFlowHistoryMapper.insertBatch(list);
    }

    @Override
    public List<BudgetProjectFlowHistoryVO> getBudgetProjectFlowHistory(Long projectId) {
        List<BudgetProjectFlowHistoryVO> historyVos = new ArrayList<>();

        List<BudgetProjectFlowHistory> list = budgetProjectFlowHistoryMapper.selectByProjectId(Math.toIntExact(projectId));

        if (list == null || list.size() == 0) {
            return historyVos;
        }
        List<Integer> userIds = new ArrayList<>();
        list.forEach(item -> userIds.add(item.getUserId()));
        // userId <=> userName
        Map<Integer, String> userMap = userApi.getUserMapById(userIds);
        BudgetProjectDetailVO budgetProject = budgetProjectService.getBudgetProject(projectId);

        for (int i = 0; i < list.size(); i++) {
            String date = DateUtil.dateToString(list.get(i).getUpdateTime());
            // 判断是否审批到该节点
            if (list.get(i).getSuggestion() == null || "".equals(list.get(i).getSuggestion())) {
                continue;
            }
            if (i == 0) {
                historyVos.add(BudgetProjectFlowHistoryVO.builder()
                        .arrivalTime(DateUtil.dateToString(list.get(i).getCreateTime()))
                        .sendingTime(date)
                        .nickName(userMap.get(list.get(i).getUserId()))
                        .operation(list.get(i).getOperation())
                        .suggestion(list.get(i).getSuggestion())
                        .roleDesc(list.get(i).getRoleDesc())
                        .moder(budgetProject.getProjectName() + "流程" +
                                DateUtil.dateToString(list.get(list.size() - 1).getUpdateTime()))
                        .build());
            } else {
                historyVos.add(BudgetProjectFlowHistoryVO.builder()
                        .arrivalTime(DateUtil.dateToString(list.get(i - 1).getUpdateTime()))
                        .sendingTime(date)
                        .nickName(userMap.get(list.get(i).getUserId()))
                        .operation(list.get(i).getOperation())
                        .suggestion(list.get(i).getSuggestion())
                        .roleDesc(list.get(i).getRoleDesc())
                        .moder(budgetProject.getProjectName() + "流程" +
                                DateUtil.dateToString(list.get(list.size() - 1).getUpdateTime()))
                        .build());
            }
        }
        return historyVos;
    }
}