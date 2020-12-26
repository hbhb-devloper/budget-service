package com.hbhb.cw.budget.mapper;


import com.hbhb.cw.budget.model.BudgetProjectNotice;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeVO;
import com.hbhb.cw.budget.web.vo.WorkBenchAgendaVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BudgetProjectNoticeMapper extends BaseMapper<BudgetProjectNotice, Long> {
    List<BudgetProjectNoticeResVO> selectNoticeListByUserId(Integer userId);

    void updateNoticeStateById(@Param("id") Long id, @Param("state") Integer state);

    void updateByBudgetProjectId(@Param("projectId") Integer projectId);

    List<BudgetProjectNoticeResVO> selectNoticeListByCond(BudgetProjectNoticeVO bpnVo);

    int countListByCond(BudgetProjectNoticeVO bpnVo);

    Long selectCountByUserId(@Param("userId") Integer userId);

    List<WorkBenchAgendaVO> selectNoticeByUserId(Integer id);
}