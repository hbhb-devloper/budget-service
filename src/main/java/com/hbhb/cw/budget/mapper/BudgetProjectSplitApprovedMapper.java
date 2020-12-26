package com.hbhb.cw.budget.mapper;

import com.hbhb.cw.model.BudgetProjectSplitApproved;
import com.hbhb.cw.web.vo.BudgetProjectSplitVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yzc
 */
@Mapper
public interface BudgetProjectSplitApprovedMapper extends BaseMapper<BudgetProjectSplitApproved,Long> {
    int deleteApprovedByProjectId(@Param("projectId")Integer projectId);

    List<BudgetProjectSplitApproved> selectSplitApprovedByProjectId(@Param("projectId")Integer projectId);

    int insertBatchApproved(@Param("list") List<BudgetProjectSplitApproved> list);

    List<BudgetProjectSplitVO> selectApprovedByProjectId(@Param("projectId") Integer projectId);
}