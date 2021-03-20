package com.hbhb.cw.budget.service;


import com.hbhb.cw.budget.model.BudgetProjectFlow;
import com.hbhb.cw.budget.web.vo.BudgetProjectApproveVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectApprovedFlowInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowInfoVO;

import java.util.List;

public interface BudgetProjectFlowService {

    /**
     * 获取签报审批节点列表
     */
    List<BudgetProjectFlowInfoVO> getBudgetProjectFlow(Integer projectId, Integer userId);

    /**
     * 逻辑删除
     */
    void deleteProjectFlow(Long id);

    /**
     * 审批节点
     */
    void approve(BudgetProjectApproveVO budgetProjectApproveVO, Integer userId);

    /**
     * 自定义流程
     */
    List<Long> getIdListByProjectId(Long projectId);


    /**
     * 同步节点信息
     *
     * @param list
     */
    void insertBatch(List<BudgetProjectFlow> list);

    /**
     * 通过签报id得到所有流程信息节点
     *
     * @param projectId
     * @return
     */
    List<BudgetProjectFlow> getAllByProjectId(Integer projectId);

    /**
     * 提醒信息
     */
    String getInform(String flowNodeId, Integer state);

    /**
     * 批量删除历史流程信息
     */
    void deleteBatch(List<Long> list);

    /**
     * 获取签报快照审批节点列表
     */
    List<BudgetProjectApprovedFlowInfoVO> getBudgetApprovedFlow(Integer projectId, Integer userId);
}
