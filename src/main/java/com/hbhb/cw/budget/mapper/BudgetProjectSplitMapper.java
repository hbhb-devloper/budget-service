package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProjectSplit;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BudgetProjectSplitMapper extends BaseMapper<BudgetProjectSplit, Integer> {


    List<BudgetProjectSplitVO> selectSplitByProjectId(@Param("projectId") Integer projectId);

    BudgetProjectSplitVO selectBudgetProjectSplitById(@Param("id") Integer id);


    int selectCondByProjectId(@Param("projectId") Integer projectId);

    BigDecimal selectSumAmountByProjectId(Long id);

    void insertBatch(List<BudgetProjectSplit> budgetProjectSplits);

    int deleteByProjectId(@Param("projectId") Integer projectId);

    List<BudgetProjectSplit> selectSplitAllByProjectId(@Param("projectId") Integer projectId);
}