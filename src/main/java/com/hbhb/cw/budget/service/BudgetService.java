package com.hbhb.cw.budget.service;


import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.cw.budget.model.Budget;
import com.hbhb.cw.budget.model.BudgetHistory;
import com.hbhb.cw.budget.web.vo.BudgetAdjustVO;
import com.hbhb.cw.budget.web.vo.BudgetExportVO;
import com.hbhb.cw.budget.web.vo.BudgetImportVO;
import com.hbhb.cw.budget.web.vo.BudgetInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetReqVO;
import com.hbhb.cw.budget.web.vo.BudgetVO;
import com.hbhb.cw.systemcenter.vo.TreeSelectParentVO;

import java.math.BigDecimal;
import java.util.List;

public interface BudgetService {

    /**
     * 按条件查询预算列表（树形结构）
     */
    List<BudgetVO> getBudgetListByCond(BudgetReqVO cond);

    /**
     * 按条件查询预算列表（树形结构、KV）
     */
    List<TreeSelectParentVO> getTreeByCond(BudgetReqVO cond);

    /**
     * 获取预算导出列表
     */
    List<BudgetExportVO> getExportList(BudgetReqVO cond);

    /**
     * 获取所有项目类型列表
     */
    List<SelectVO> getProjectTypeList();

    /**
     * 获取预算详情
     */
    BudgetVO getInfoById(Long id);

    /**
     * 修改阀值
     */
    void updateThreshold(BudgetAdjustVO vo);

    /**
     * 获取预算的金额阀值
     */
    BigDecimal getBudgetThreshold(Long id);

    /**
     * 批量保存预算分解数据
     */
    void saveBudget(List<BudgetImportVO> dataList, List<String> headerList, String importDate);

    /**
     * 预算调整
     */
    List<BudgetHistory> adjustBudget(List<BudgetAdjustVO> vos);

    /**
     * 通过id修改budget的预算值
     */
    void updateBudget(Long budgetId, BigDecimal balance);

    /**
     * 新增一个项目
     *
     * @param budgetAddVO)
     */
    void addBudget(BudgetInfoVO budgetAddVO);

    /**
     * 通过itemId删除（budget）项目
     *
     * @param budgetId
     */
    void deleteByItemId(Long budgetId);

    /**
     * 修改项目/科目
     *
     * @param budgetInfoVO
     */
    void updateByUpdateVO(BudgetInfoVO budgetInfoVO);

    /**
     * 通过itemId得到类别详情
     *
     * @param itemId
     * @return
     */
    BudgetInfoVO updateById(Long itemId);

    /**
     * 通过科目id（itemId）是否有项目
     *
     * @param itemId
     * @return
     */
    Integer getBudgetCount(Long itemId);

    /**
     * 通过预算id得到预算编号
     *
     * @param budgetId
     * @return
     */
    String getNumByBudgetId(Long budgetId);

    /**
     * 通过id得到预算
     */
    Budget getBudgetById(Long id);

    /**
     * 判断表名是否正确
     */
    void judegFileName(String fileName);

    /**
     * 通过预算编号和年份得到预算id
     */
    Long getIdByNum(String budgetNum, String importDate);

    void check();
}