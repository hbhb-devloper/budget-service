package com.hbhb.cw.budget.service;

import com.hbhb.cw.model.BudgetProjectFlowHistory;
import com.hbhb.cw.web.vo.BudgetProjectFlowHistoryVO;

import java.util.List;

public interface BudgetProjectFlowHistoryService {

    /**
     * 保存历史流程 -> mongo
     */
    void saveBudgetProjectFlowHistory(List<BudgetProjectFlowHistory> list);

    /**
     * 获取预历史流程列表 <- mongo
     */
    List<BudgetProjectFlowHistoryVO> getBudgetProjectFlowHistory(Long projectId);
}
