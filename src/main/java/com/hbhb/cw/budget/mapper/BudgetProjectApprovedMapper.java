package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProjectApproved;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BudgetProjectApprovedMapper extends BaseMapper<BudgetProjectApproved, Long> {
    int deleteByProjectId(@Param("projectId") Integer projectId);

    BudgetProjectApproved selectByProjectId(@Param("projectId") Integer projectId);

    BudgetProjectDetailVO selectProjectApprovedByProjectId(@Param("id") Long id);

}