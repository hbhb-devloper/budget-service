package com.hbhb.cw.budget.mapper;

import com.hbhb.cw.model.Budget;
import com.hbhb.cw.web.vo.BudgetReqVO;
import com.hbhb.cw.web.vo.BudgetVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetMapper extends BaseMapper<Budget, Long> {

    int insertBatch(List<Budget> list);

    int updateBatch(List<Budget> list);

    List<BudgetVO> selectTreeListByCond(@Param("cond") BudgetReqVO cond,
                                        @Param("list") List<Integer> unitIds);

    int upBudgetByBudgetId(@Param("budgetId") Long budgetId, @Param("balance") BigDecimal balance);

    List<Budget> selectAllByYear(String year);

    BigDecimal selectBalanceByDate(@Param("date") String date,
                                   @Param("budgetNum") String budgetNum);

    Long selectIdByDate(@Param("date") String date,
                        @Param("budgetNum") String budgetNum);

    String selectByBudgetId(@Param("budgetId") Long budgetId);

    BigDecimal selectThreshold(@Param("id") Long id);

    Integer selectBudgetByItemId(@Param("itemId") Long id);

    Long selectIdByNum(@Param("budgetNum") String budgetNum, @Param("importDate") String importDate);
}