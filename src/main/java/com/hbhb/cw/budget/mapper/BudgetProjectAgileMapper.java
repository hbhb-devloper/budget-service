package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProjectAgile;
import com.hbhb.cw.budget.web.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectAgileMapper extends BaseMapper<BudgetProjectAgile, Long> {

    List<BudgetProjectAgileVO> selectAgileList(@Param("cond") BudgetProjectAgileReqVO cond);

    int countAgileList(@Param("cond") BudgetProjectAgileReqVO cond);

    List<BudgetProjectAgileVO> selectParentAgileList(@Param("cond") BudgetProjectAgileReqVO cond, @Param("list") List<Integer> list);

    int countParentAgileList(@Param("cond") BudgetProjectAgileReqVO cond, @Param("list") List<Integer> list);

    Integer selectTypeCountByBudgetId(@Param("budgetId") Long budgetId,
                                      @Param("unitId") Integer unitId,
                                      @Param("createTime") String createTime);

    BudgetProjectAgileInfoVO selectAgileInfoById(@Param("id") Long id);

    int updateDeleteById(@Param("id") Long id);

    List<BudgetProjectAmountVO> selectProgressByBudget(@Param("unitId") Integer unitId,
                                                       @Param("budgetId") Long budgetId,
                                                       @Param("year") String year);

    BudgetProgressResVO selectProgressByState(BudgetProgressReqVO cond);

    BudgetProjectAgile selectInfoByNum(@Param("projectNum") String projectNum);
}