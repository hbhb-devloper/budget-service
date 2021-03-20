package com.hbhb.cw.budget.service;

import com.hbhb.cw.budget.model.BudgetHistory;
import com.hbhb.cw.budget.web.vo.BudgetHistoryExportVO;
import com.hbhb.cw.budget.web.vo.BudgetHistoryInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetHistoryVO;
import com.hbhb.cw.budget.web.vo.BudgetReqVO;

import java.util.List;

/**
 * @author xiaokang
 * @since 2020-07-20
 */
public interface BudgetHistoryService {

    /**
     * 按条件查询预算历史列表（树形结构）
     */
    List<BudgetHistoryVO> getListByCond(BudgetReqVO cond);

    /**
     * 获取预算导出列表
     */
    List<BudgetHistoryExportVO> getExportList(BudgetReqVO cond);

    /**
     * 获取预算历史详情
     */
    BudgetHistoryInfoVO getInfoById(Long budgetId);

    /**
     * 批量保存预算历史
     */
    void insertBatch(List<BudgetHistory> list);
}
