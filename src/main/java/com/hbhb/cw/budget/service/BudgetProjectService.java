package com.hbhb.cw.budget.service;


import com.hbhb.cw.budget.model.BudgetProject;
import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.web.vo.BudgetProgressResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailExportReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectInitVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectResVO;
import com.hbhb.cw.budget.web.vo.BudgetReqVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

public interface BudgetProjectService {

    /**
     * 判断是否有快照
     */
    boolean getBudgetApprovedState(Integer id);

    /**
     * 分页获取签报信息
     */
    Page<BudgetProjectResVO> getBudgetProjectPage(Integer pageNum, Integer pageSize, boolean pageable,
                                                  BudgetProjectReqVO cond);

    /**
     * 得到去年所有单个项目的状态统计
     */
    List<BudgetProgressResVO> getBudgetProgressByBudgetData(BudgetReqVO cond);

    /**
     * 通过签报id获取签报详情
     */
    BudgetProjectDetailVO getBudgetProject(Long id);

    /**
     * 新增签报
     */
    Integer addSaveBudgetProject(BudgetProjectDetailVO budgetProjectDetailVO, UserInfo userName);

    /**
     * 删除签报(只改变其删除状态并非清除数据)
     */
    void deleteBudgetProject(Integer id, UserInfo user);

    /**
     * 修改签报
     */
    void updateBudgetProject(BudgetProjectDetailVO budgetProjectDetailVO, UserInfo user);

    /**
     * 更新项目流程状态
     */
    void updateState(Integer projectId, Integer state);

    /**
     * 判断是否存在指定预算id的项目签报
     */
    boolean existByBudgetId(Long budgetId);

    /**
     * 导出签报列表
     */
    List<BudgetProjectExportVO> getExportList(BudgetProjectReqVO cond);

    /**
     * 发起审批
     */
    void toApprove(BudgetProjectInitVO approveVO);

    /**
     * 回滚签报
     */
    void revert(Integer id, Integer userId);

    /**
     * 判断该条签报是否正在使用某条流程
     */
    boolean isUseFlowId(Long flowId);

    /**
     * 签报详情导出至word格式
     */
    void export2Word(HttpServletResponse response, BudgetProjectDetailExportReqVO vo);

    /**
     * 通过budgetId和unitIds查找是否有签报
     */
    int countListByUnitIds(Long budgetId, List<Integer> unitIds);

    /**
     * 跨年项目签报审批通过后自动生成相同信息签报
     */
    List<Integer> addProject(Integer projectId);

    /**
     * 用来查询当年结转情况
     */
    List<BudgetProject> getJieZhuan(String year);

    /**
     * 批量修改签报
     */
    void updateBatch(List<BudgetProject> list);

    /**
     * 通过id得到签报全字段
     */
    BudgetProject getInfo(Integer id);

    /**
     * 通过签报id获取签报快照详情
     */
    BudgetProjectDetailVO getBudgetProjectApproved(Integer id);
}
