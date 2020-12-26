package com.hbhb.cw.budget.service;

import com.hbhb.cw.model.BudgetData;
import com.hbhb.cw.web.vo.BudgetBelongVO;
import com.hbhb.cw.web.vo.BudgetDataResVO;
import com.hbhb.cw.web.vo.BudgetDataVO;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetDataService {

    /**
     * 通过budgetId分页查询budgetData
     */
    List<BudgetDataResVO> listByBudgetId(BudgetBelongVO budgetBelongVO);

    /**
     * 调整项目类别管理
     */
    void updateBudgetDate(List<BudgetDataVO> list);

    /**
     * 通过budgetId得到该项目预算总和
     *
     * @param BudgetId
     * @return
     */
    BigDecimal getBalanceByBudgetId(Long BudgetId);

    /**
     * 通过budgetId和unitId得到预算值
     *
     * @param unitId
     * @param budgetId
     * @return
     */
    BudgetData getDataByUnitIdAndBudgetId(Integer unitId, Long budgetId);

    /**
     * 通过budgetId和unitIds得到budgetData列表
     */
    List<BudgetData> getListByUnitIds(Long budgetId, List<Integer> unitIds);
}
