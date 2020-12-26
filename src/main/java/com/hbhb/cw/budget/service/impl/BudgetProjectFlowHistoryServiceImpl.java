package com.hbhb.cw.budget.service.impl;

import com.hbhb.cw.common.AllName;
import com.hbhb.cw.mapper.BudgetProjectFlowHistoryMapper;
import com.hbhb.cw.model.BudgetProjectFlowHistory;
import com.hbhb.cw.rpc.UserApiExp;
import com.hbhb.cw.service.BudgetProjectFlowHistoryService;
import com.hbhb.cw.service.BudgetProjectService;
import com.hbhb.cw.utils.DateUtil;
import com.hbhb.cw.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.web.vo.BudgetProjectFlowHistoryVO;

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
        // userId <=> userName
        Map<Integer, String> userMap = userApi.getUserMapById();
        List<BudgetProjectFlowHistory> list = budgetProjectFlowHistoryMapper.selectByProjectId(Math.toIntExact(projectId));
        if (list == null || list.size() == 0) {
            return historyVos;
        }
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
                        .moder(budgetProject.getProjectName() + AllName.FLOW_NAME.getValue() +
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
                        .moder(budgetProject.getProjectName() + AllName.FLOW_NAME.value() +
                                DateUtil.dateToString(list.get(list.size() - 1).getUpdateTime()))
                        .build());
            }
        }
        return historyVos;
    }
}