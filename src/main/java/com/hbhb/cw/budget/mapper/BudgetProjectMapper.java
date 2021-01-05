package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProject;
import com.hbhb.cw.budget.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAmountVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectResVO;
import com.hbhb.cw.budget.web.vo.BudgetReqVO;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectMapper extends BaseMapper<BudgetProject, Integer> {

    List<BudgetProjectResVO> selectListByCond(@Param("cond") BudgetProjectReqVO cond,
                                              @Param("list") List<Integer> unitIds);

    int countListByCond(@Param("cond") BudgetProjectReqVO cond, @Param("list") List<Integer> unitIds);

    List<BudgetProgressResVO> selectProgress(@Param("cond") BudgetProgressReqVO cond,
                                             @Param("list") List<Integer> unitIds);

    List<BudgetProjectAmountVO> selectProgressByBudget(@Param("unitId") Integer unitId,
                                                       @Param("budgetNum") String budgetNum,
                                                       @Param("year") String year,
                                                       @Param("list") List<Integer> states);

    BudgetProjectDetailVO selectProjectById(@Param("id") Long id);

    BudgetProgressResVO selectProgressByState(BudgetProgressReqVO cond);

    List<BudgetProgressResVO> selectBudgetProgressByBudgetData(BudgetReqVO cond);

    Integer selectTypeCountByBudgetId(@Param("budgetId") Long budgetId,
                                      @Param("unitId") Integer unitId,
                                      @Param("createTime") String createTime);

    BudgetProject selectInfoByNum(@Param("projectNum") String projectNum);

    void updateDeleteFlagById(@Param("id") Integer id);

    long countByBudgetId(@Param("budgetId") Long budgetId);

    List<BudgetProjectExportVO> selectListByIds(@Param("ids") List<Long> ids);

    Long selectBudgetIdById(@Param("projectId") Long projectId);


    BudgetProject selectBudgetProjectById(@Param("id") Long id);

    void updateStateById(@Param("id") Integer id, @Param("state") Integer state);

    int deleteByProjectId(Integer projectId);

    int countFlowId(@Param("flowId") Long flowId);

    int countListByUnitIds(@Param("budgetId") Long budgetId, @Param("list") List<Integer> unitIds);

    List<BudgetProject> selectJieZhuan(@Param("year") String year);

    int updateBatchById(@Param("list") List<BudgetProject> list);

    List<BudgetProject> selectAll();
}