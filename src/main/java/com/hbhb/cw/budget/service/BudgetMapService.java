package com.hbhb.cw.budget.service;

import com.hbhb.cw.model.BudgetMap;
import com.hbhb.cw.model.BudgetMonthCheck;
import com.hbhb.cw.web.vo.BudgetMapVO;

import java.util.List;

/**
 * @author xiaokang
 * @since 2020-07-20
 */
public interface BudgetMapService {

    /**
     * 获取预算映射列表 <- mongo
     */
    List<BudgetMapVO> getBudgetMapList();

    /**
     * 保存预算映射 -> mongo
     */
    void saveBudgetMap(List<BudgetMap> list);

    /**
     * 获取月度考核列表 <- mongo
     */
    List<BudgetMonthCheck> getMonthCheckList();

    /**
     * 保存月度考核 -> mongo
     */
    void saveMonthCheck(List<BudgetMonthCheck> list);
}
