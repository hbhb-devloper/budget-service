package com.hbhb.cw.budget.service;


import com.hbhb.cw.budget.model.BudgetProjectSplit;
import com.hbhb.cw.budget.model.BudgetProjectSplitApproved;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import java.util.List;

public interface BudgetProjectSplitService {

    /**
     * 新增分类预算
     */
    void andSaveBudgetProjectSplit(BudgetProjectSplitVO bpClassVo, UserInfo user);

    /**
     * 根据签报id获取分类预算信息
     */
    List<BudgetProjectSplitVO> getBudgetProjectSplitList(Integer projectId);

    /**
     * 根据id删除分类预算信息
     */
    void deleteBudgetProjectSplit(Integer id, UserInfo user);

    /**
     * 修改分类预算信息
     *
     * @param bpClassVo
     */

    void updateProjectSplit(BudgetProjectSplitVO bpClassVo, UserInfo user);

    /**
     * 跟据id查询分类预算详情
     */
    BudgetProjectSplitVO getProjectSplit(Integer id);

    /**
     * 根据签报id删除预算分类
     *
     * @param ProjectId
     */
    void deleteApprovedByProjectId(Integer ProjectId);

    /**
     * 批量新增快照
     *
     * @param list
     */
    void insertBatchApproved(List<BudgetProjectSplitApproved> list);

    /**
     * 通过projectId得到List<BudgetProjectSplit>
     *
     * @param projectId
     * @return
     */
    List<BudgetProjectSplit> getSplitByProjectId(Integer projectId);

    /**
     * 获取签报分类预算快照
     */
    List<BudgetProjectSplitVO> getBudgetApprovedSplitList(Integer projectId);
}
