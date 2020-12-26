package com.hbhb.cw.budget.mapper;

import com.hbhb.cw.model.BudgetProjectFlow;
import com.hbhb.cw.web.vo.BudgetProjectFlowNodeVO;
import com.hbhb.cw.web.vo.BudgetProjectFlowVO;
import com.hbhb.cw.web.vo.FlowRoleResVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectFlowMapper extends BaseMapper<BudgetProjectFlow, Long> {

    int insertBatch(@Param("list") List<BudgetProjectFlow> list);

    int insertBatchHaveSuggestion(@Param("list") List<BudgetProjectFlow> list);

    List<BudgetProjectFlowVO> selectByProjectId(@Param("projectId") Integer projectId);

    List<Long> selectIdByProjectId(@Param("projectId") Integer projectId);

    int updateBatchByNodeId(@Param("list") List<BudgetProjectFlowNodeVO> list,@Param("projectId") Long projectId);

    List<FlowRoleResVO> selectNodeByNodeId(@Param("flowNodeId") String flowNodeId,
                                           @Param("projectId") Integer projectId);

    List<BudgetProjectFlow> selectAllByProjectId(@Param("projectId") Integer projectId);

    int deleteBatch(@Param("list") List<Long> list);
}