package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProjectFlowHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectFlowHistoryMapper extends BaseMapper<BudgetProjectFlowHistory, Long> {
    void insertBatch(@Param("list") List<BudgetProjectFlowHistory> budgetDataList);

    List<BudgetProjectFlowHistory> selectByProjectId(Integer projectId);
}