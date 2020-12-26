package com.hbhb.cw.budget.service.impl;

import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.mapper.BudgetBelongMapper;
import com.hbhb.cw.mapper.BudgetDataMapper;
import com.hbhb.cw.model.BudgetBelong;
import com.hbhb.cw.model.BudgetData;
import com.hbhb.cw.model.BudgetHistory;
import com.hbhb.cw.rpc.UnitApiExp;
import com.hbhb.cw.service.BudgetDataService;
import com.hbhb.cw.service.BudgetHistoryService;
import com.hbhb.cw.service.BudgetService;
import com.hbhb.cw.utils.BeanConverter;
import com.hbhb.cw.web.vo.BudgetAdjustVO;
import com.hbhb.cw.web.vo.BudgetBelongVO;
import com.hbhb.cw.web.vo.BudgetDataResVO;
import com.hbhb.cw.web.vo.BudgetDataVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

@Service
public class BudgetDataServiceImpl implements BudgetDataService {

    @Resource
    private BudgetDataMapper budgetDataMapper;
    @Resource
    private BudgetBelongMapper budgetBelongMapper;
    @Resource
    private BudgetService budgetService;
    @Resource
    private BudgetHistoryService budgetHistoryService;
    @Resource
    private UnitApiExp unitApi;

    /**
     * 通过budgetId分页查询budgetData
     */
    @Override
    public List<BudgetDataResVO> listByBudgetId(BudgetBelongVO budgetBelongVO) {
        List<BudgetData> budgetDataList = budgetDataMapper.selectByBelongVO(budgetBelongVO);
        Integer underUnitId = budgetBelongMapper.selectUnderUnitId(budgetBelongVO.getBudgetId(), budgetBelongVO.getDeptId());
        List<Long> longs = budgetBelongMapper.selectIdByUnderUnitId(budgetBelongVO.getBudgetId(), budgetBelongVO.getDeptId());
        List<BudgetDataResVO> budgetDataRes = BeanConverter.copyBeanList(budgetDataList, BudgetDataResVO.class);
        // 有单位归口于该单位，但是该单位无预算值
        if (underUnitId == null && longs.size() != 0) {
            String unitName = unitApi.getUnitInfo(budgetBelongVO.getDeptId()).getUnitName();
            for (BudgetDataResVO budgetData : budgetDataRes) {
                budgetData.setUnderUnitId(budgetBelongVO.getDeptId());
                budgetData.setUnderUnitName(unitName);
                budgetData.setFlag(1);
            }
            return budgetDataRes;

        }
        // 该单位有归口单位，但是无预算值（标识归口于其他单位）
        else if (underUnitId != null && longs.size() == 0) {
            String unitName = unitApi.getUnitInfo(underUnitId).getUnitName();
            BudgetDataResVO budgetDataRe = new BudgetDataResVO();
            budgetDataRe.setUnderUnitId(underUnitId);
            budgetDataRe.setUnderUnitName(unitName);
            budgetDataRes.add(budgetDataRe);
            return budgetDataRes;
        }
        // 该单位有归口单位，且有预算值
        else if (underUnitId != null) {
            String unitName = unitApi.getUnitInfo(underUnitId).getUnitName();
            // 归口单位等于本身单位，则自归口，不是则归口于其他单位
            if (underUnitId.equals(budgetBelongVO.getDeptId())) {
                for (BudgetDataResVO budgetData : budgetDataRes) {
                    budgetData.setFlag(1);
                }
            }
            for (BudgetDataResVO budgetData : budgetDataRes) {
                budgetData.setUnderUnitId(underUnitId);
                budgetData.setUnderUnitName(unitName);
            }
            return budgetDataRes;
        }
        throw new BizException(BizStatus.BUDGET_NO_DATA.getCode());
    }

    /**
     * 调整项目类别管理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBudgetDate(List<BudgetDataVO> list) {
        for (BudgetDataVO budgetDataVO : list) {
            if (budgetDataVO.getBalance() != null && budgetDataVO.getBalance().intValue() < 0) {
                throw new BizException(BizStatus.BUDGET_DATA_NOT_NEGATIVE.getCode());
            }
        }
        // 得到预算id
        Long budgetId = list.get(0).getBudgetId();
        // 得到归口单位id
        Integer underUnitId = list.get(0).getDeptId();
        // 用来批量新增预算数据
        List<BudgetData> budgetDataList = BeanConverter
                .copyBeanList(list, BudgetData.class);
        List<BudgetBelong> belongList = BeanConverter
                .copyBeanList(list, BudgetBelong.class);
        for (BudgetBelong budgetBelong : belongList) {
            budgetBelong.setUnderUnitId(underUnitId);
        }
        List<Long> longs = budgetBelongMapper
                .selectIdByUnderUnitId(budgetId, underUnitId);
        // 用来处理历史数据
        List<BudgetAdjustVO> adjustS = BeanConverter
                .copyBeanList(list, BudgetAdjustVO.class);
        List<BudgetHistory> budgetHistories = budgetService.adjustBudget(adjustS);
        budgetHistoryService.insertBatch(budgetHistories);
        // 通过预算id和归口单位id得到单位列表
        List<Integer> unitIds = budgetBelongMapper.selectUnitIdByUnderUnitId(budgetId, underUnitId);
        if (unitIds != null && unitIds.size() != 0) {
            budgetDataMapper.deleteBatch(budgetId, unitIds);
        }
        // 通过budgetBelongId删除所有budgetBelong
        if (longs.size() != 0) {
            budgetBelongMapper.deleteBatch(longs);
        }
        if (list.get(0).getUnitId() != null) {
            budgetDataMapper.insertBatch(budgetDataList);
            budgetBelongMapper.insertBatch(belongList);
        }

        BigDecimal total = budgetDataMapper.selectSumByBudgetId(budgetId);
        if (total == null) {
            total = new BigDecimal("0.0");
        }
        //修改这个项目的预算值
        budgetService.updateBudget(budgetId, total);
    }

    @Override
    public BigDecimal getBalanceByBudgetId(Long budgetId) {
        return budgetDataMapper.selectSumByBudgetId(budgetId);
    }

    @Override
    public BudgetData getDataByUnitIdAndBudgetId(Integer unitId, Long budgetId) {
        return budgetDataMapper.getDataByUnitIdAndBudgetId(unitId, budgetId);
    }


    @Override
    public List<BudgetData> getListByUnitIds(Long budgetId, List<Integer> unitIds) {
        unitIds.add(-1);
        return budgetDataMapper.selectListByUnitIds(budgetId, unitIds);
    }
}