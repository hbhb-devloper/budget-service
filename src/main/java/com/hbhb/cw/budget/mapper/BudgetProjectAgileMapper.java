package com.hbhb.cw.budget.mapper;

import com.hbhb.cw.model.BudgetProjectAgile;
import com.hbhb.cw.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.web.vo.BudgetProgressResVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileInfoVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileReqVO;
import com.hbhb.cw.web.vo.BudgetProjectAgileVO;
import com.hbhb.cw.web.vo.BudgetProjectAmountVO;

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
                                                       @Param(value = "budgetId") Long budgetId,
                                                       @Param(value = "year") String year);

    BudgetProgressResVO selectProgressByState(BudgetProgressReqVO cond);

    BudgetProjectAgile selectInfoByNum(@Param("projectNum") String projectNum);
}