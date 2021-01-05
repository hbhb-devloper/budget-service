package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetBelong;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author yzc
 */
@Mapper
public interface BudgetBelongMapper extends BaseMapper<BudgetBelong, Long> {

    int deleteBatch(@Param("list") List<Long> list);

    List<Long> selectIdByUnderUnitId(@Param("budgetId") Long budgetId,
                                     @Param("underUnitId") Integer underUnitId);

    List<Integer> selectUnitIdByUnderUnitId(@Param("budgetId") Long budgetId,
                                            @Param("underUnitId") Integer underUnitId);

    int insertBatch(@Param("list") List<BudgetBelong> belongList);

    int deleteByBudgetId(@Param("budgetId") Long budgetId);

    int deleteBatchByBudgetId(@Param("list") List<Long> list);

    Integer selectUnderUnitId(@Param("budgetId") Long budgetId,
                              @Param("unitId") Integer unitId);

    Integer selectUnderUnitIdByNum(@Param("budgetNum") String budgetNum,
                              @Param("unitId") Integer unitId);

    List<BudgetBelong> selectAll();

    void batchUpdate(@Param("list") List<BudgetBelong> list);
}