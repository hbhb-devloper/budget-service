package com.hbhb.cw.budget.service.impl;

import com.github.pagehelper.PageHelper;
import com.hbhb.cw.budget.mapper.BudgetProjectNoticeMapper;
import com.hbhb.cw.budget.model.BudgetProjectNotice;
import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.rpc.DictApiExp;
import com.hbhb.cw.budget.service.BudgetProjectNoticeService;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeVO;
import com.hbhb.cw.budget.web.vo.WorkBenchAgendaVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.vo.DictVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BudgetProjectNoticeServiceImpl implements BudgetProjectNoticeService {

    @Resource
    private BudgetProjectNoticeMapper noticeMapper;
    @Resource
    private DictApiExp dictApi;

    @Override
    public void andSaveBudgetProjectNotice(BudgetProjectNoticeReqVO budgetProjectNoticeReqVO) {
        BudgetProjectNotice notice = new BudgetProjectNotice();
        BeanUtils.copyProperties(budgetProjectNoticeReqVO, notice);
        notice.setCreateTime(new Date());
        noticeMapper.insertSelective(notice);
    }

    @Override
    public Page<BudgetProjectNoticeResVO> getBudgetProjectNoticeList(BudgetProjectNoticeVO bpnVo,
                                                                     Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        List<BudgetProjectNoticeResVO> list = noticeMapper.selectNoticeListByCond(bpnVo);
        // 获取项目状态字典
        List<DictVO> stateList = dictApi.getDict(
                TypeCode.BUDGET.value(), DictCode.BUDGET_PROJECT_STATUS.value());

        Map<String, String> stateMap = stateList.stream().collect(
                Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        if (list == null || list.size() == 0) {
            return new Page<>();
        }
        // 组装状态名称
        list.forEach(item -> item.setStateLabel(stateMap.get(item.getState().toString())));
        int count = noticeMapper.countListByCond(bpnVo);
        return new Page<>(list, (long) count);
    }

    @Override
    public void deleteBudgetProjectNotice(Long id) {
        noticeMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void updateBudgetProjectNoticeState(Long id) {
        // 设置提醒状态为已读(0-未读,1-已读)
        Integer state = 1;
        noticeMapper.updateNoticeStateById(id, state);
    }

    @Override
    public void updateByBudgetProjectId(Integer projectId) {
        noticeMapper.updateByBudgetProjectId(projectId);
    }

    @Override
    public Long getNoticeAccount(Integer userId) {
        return noticeMapper.selectCountByUserId(userId);
    }

    @Override
    public List<WorkBenchAgendaVO> getBudgetNoticeList(Integer userId) {
        return noticeMapper.selectNoticeByUserId(userId);
    }
}