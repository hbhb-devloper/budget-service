package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProjectFlowApproved;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectFlowApprovedMapper extends BaseMapper<BudgetProjectFlowApproved, Long> {

    int insertBatch(@Param("list") List<BudgetProjectFlowApproved> list);

    List<BudgetProjectFlowApproved> selectByProjectId(@Param("projectId") Long projectId);

    List<BudgetProjectFlowVO> selectApprovedByProjectId(@Param("projectId") Long projectId);

    int deleteByProjectId(@Param("projectId") Long projectId);
}