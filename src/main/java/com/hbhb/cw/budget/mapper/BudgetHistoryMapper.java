package com.hbhb.cw.budget.mapper;

import com.hbhb.cw.model.BudgetHistory;
import com.hbhb.cw.web.vo.BudgetHistoryInfoVO;
import com.hbhb.cw.web.vo.BudgetHistoryVO;
import com.hbhb.cw.web.vo.BudgetReqVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetHistoryMapper extends BaseMapper<BudgetHistory, Long> {

    int insertBatch(List<BudgetHistory> budgetHistoryList);

    List<BudgetHistoryVO> selectTreeListByCond(@Param("cond") BudgetReqVO cond,
                                               @Param("list") List<Integer> unitIds);

    BudgetHistoryInfoVO selectDetailById(Long budgetId);
}