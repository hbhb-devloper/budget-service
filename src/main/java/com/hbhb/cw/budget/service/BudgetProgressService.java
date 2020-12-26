package com.hbhb.cw.budget.service;


import com.hbhb.cw.budget.web.vo.*;

import java.util.List;

public interface BudgetProgressService {

    /**
     * 按条件项目进度列表
     */
    List<BudgetProgressVO> getBudgetProgressList(BudgetProgressReqVO vo);

    /**
     * 通过unitId和budgetId查询该立项值下的所有签报
     */
    List<BudgetProjectAmountVO> getProgressByBudgetId(BudgetFlowStateVO vo);

    /**
     * 导出excel表
     */
    List<BudgetProgressExportVO> getExportList(BudgetProgressReqVO cond);

    /**
     * 通过budgetId得到改项目统计
     */
    BudgetProgressDeclareVO getProgressByState(BudgetProgressReqVO cond);

    /**
     * 通过签报编号得到相应签报详情
     */
    BudgetProjectAllVO getProjectInfo(String projectNum);

}
