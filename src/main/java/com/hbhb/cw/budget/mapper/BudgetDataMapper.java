package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetData;
import com.hbhb.cw.budget.web.vo.BudgetBelongVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetDataMapper extends BaseMapper<BudgetData, Long> {

    List<BudgetData> selectByBelongVO(BudgetBelongVO budgetBelongVO);

    BigDecimal selectSumByBudgetId(@Param("budgetId") Long budgetId);

    void insertBatch(@Param("list") List<BudgetData> budgetDataList);

    void deleteBatch(@Param("budgetId") Long budgetId, @Param("list") List<Integer> unitIds);

    BudgetData getDataByUnitIdAndBudgetId(@Param("unitId") Integer unitId,
                                          @Param("budgetId") Long budgetId);

    int deleteByBudgetId(@Param("budgetId") Long budgetId);

    int deleteBatchByBudgetId(@Param("list") List<Long> list);

    List<BudgetData> selectListByUnitIds(@Param("budgetId") Long budgetId, @Param("list") List<Integer> unitIds);
}

