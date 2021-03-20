package com.hbhb.cw.budget.service;


import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.cw.budget.web.vo.BudgetItemVO;

import java.util.List;

/**
 * @author yzc
 */
public interface BudgetItemService {

    /**
     * 通过id查询科目详情
     *
     * @param budgetItemId
     * @return
     */
    BudgetItemVO getBudgetItemById(Long budgetItemId);

    /**
     * 新增科目
     *
     * @param budgetItemVO
     */
    void addBudgetItem(BudgetItemVO budgetItemVO);

    /**
     * 通过id删除科目
     *
     * @param itemId
     */
    void deleteByItemId(Long itemId);

    /**
     * 修改科目
     *
     * @param budgetItemVO
     */
    void updateByBudgetItemVO(BudgetItemVO budgetItemVO);

    /**
     * 得到科目list
     * @return
     */
    List<SelectVO> getBudgetItemList();
}
