package com.hbhb.cw.budget.service;

import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeVO;
import com.hbhb.cw.budget.web.vo.WorkBenchAgendaVO;

import java.util.List;

public interface BudgetProjectNoticeService {

    /**
     * 新增提醒
     */
    void andSaveBudgetProjectNotice(BudgetProjectNoticeReqVO budgetProjectNoticeReqVO);

    /**
     * 根据条件筛选提醒信息列表
     */
    Page<BudgetProjectNoticeResVO> getBudgetProjectNoticeList(BudgetProjectNoticeVO bpNoticeVo,
                                                              Integer pageNum, Integer pageSize);

    /**
     * 删除提醒
     */
    void deleteBudgetProjectNotice(Long id);

    /**
     * 修改提醒状态
     */
    void updateBudgetProjectNoticeState(Long id);

    /**
     * 通过签报项目Id把该签报下的所有提醒改成已读
     */
    void updateByBudgetProjectId(Integer projectId);

    /**
     * 跟据用户id统计提醒数量
     */
    Long getNoticeAccount(Integer id);

    /**
     * 跟据登录用户查看提醒信息
     */
    List<WorkBenchAgendaVO> getBudgetNoticeList(Integer userId);

}
