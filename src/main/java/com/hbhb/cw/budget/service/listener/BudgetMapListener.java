package com.hbhb.cw.budget.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hbhb.cw.budget.model.BudgetMap;
import com.hbhb.cw.budget.service.BudgetMapService;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings({"rawtypes"})
public class BudgetMapListener extends AnalysisEventListener {

    private static final int BATCH_COUNT = 100;
    private final List<BudgetMap> dataList = new ArrayList<>();
    private final BudgetMapService budgetMapService;

    public BudgetMapListener(BudgetMapService budgetMapService) {
        this.budgetMapService = budgetMapService;
    }

    @Override
    public void invoke(Object object, AnalysisContext context) {
        dataList.add((BudgetMap) object);
        if (dataList.size() >= BATCH_COUNT) {
            saveData();
            dataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        dataList.clear();
    }

    private void saveData() {
        budgetMapService.saveBudgetMap(dataList);
    }
}