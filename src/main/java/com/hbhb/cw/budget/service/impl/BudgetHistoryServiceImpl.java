package com.hbhb.cw.budget.service.impl;

import com.hbhb.cw.mapper.BudgetHistoryMapper;
import com.hbhb.cw.model.BudgetHistory;
import com.hbhb.cw.rpc.UnitApiExp;
import com.hbhb.cw.service.BudgetHistoryService;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.web.vo.BudgetHistoryExportVO;
import com.hbhb.cw.web.vo.BudgetHistoryInfoVO;
import com.hbhb.cw.web.vo.BudgetHistoryVO;
import com.hbhb.cw.web.vo.BudgetReqVO;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

/**
 * @author xiaokang
 * @since 2020-07-20
 */
@Service
public class BudgetHistoryServiceImpl implements BudgetHistoryService {

    @Resource
    private BudgetHistoryMapper budgetHistoryMapper;
    @Resource
    private UnitApiExp unitApi;

    @Override
    public List<BudgetHistoryVO> getListByCond(BudgetReqVO cond) {
        List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
        List<BudgetHistoryVO> list = budgetHistoryMapper.selectTreeListByCond(cond, unitIds);

        Optional.ofNullable(list)
                .orElse(new ArrayList<>())
                .forEach(vo -> {
                    vo.setBudgetId(vo.getBudgetId() << 10);
                    vo.setHasChildren(true);
                    vo.getChildren().forEach(child -> child.setHasChildren(false));
                });
        return list;
    }

    @Override
    public List<BudgetHistoryExportVO> getExportList(BudgetReqVO cond) {
        List<BudgetHistoryVO> list = this.getListByCond(cond);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        Unit unit = unitApi.getUnitInfo(cond.getUnitId());
        // 组装导出列表字段
        return buildExportVO(list, unit.getUnitName());
    }

    @Override
    public BudgetHistoryInfoVO getInfoById(Long budgetId) {
        return budgetHistoryMapper.selectDetailById(budgetId);
    }

    @Override
    public void insertBatch(List<BudgetHistory> list) {
        budgetHistoryMapper.insertBatch(list);
    }

    /**
     * 组装预算历史导出字段
     */
    private List<BudgetHistoryExportVO> buildExportVO(List<BudgetHistoryVO> list, String unitName) {
        List<BudgetHistoryExportVO> result = new ArrayList<>();
        // excel行号
        AtomicLong lineNumber = new AtomicLong(1);

        for (BudgetHistoryVO historyVO : list) {
            result.add(BudgetHistoryExportVO.builder()
                    .lineNumber(lineNumber.getAndIncrement())
                    .unitName(unitName)
                    .itemName(historyVO.getBudgetName())
                    .measurement("万元")
                    .build());
            historyVO.getChildren().forEach(child -> result.add(BudgetHistoryExportVO.builder()
                    .lineNumber(lineNumber.getAndIncrement())
                    .unitName(unitName)
                    .itemName("----" + child.getBudgetName())
                    .measurement("万元")
                    .oldValue(child.getOldValue())
                    .newValue(child.getNewValue())
                    .difValue(child.getDifValue())
                    .remark(child.getRemark())
                    .build()));
        }
        // 按lineNumber排序
        result.sort(Comparator.comparing(BudgetHistoryExportVO::getLineNumber));
        return result;
    }
}