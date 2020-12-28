package com.hbhb.cw.budget.service.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hbhb.cw.budget.model.BudgetMonthCheck;
import com.hbhb.cw.budget.service.BudgetMapService;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yzcyzc
 */
@Slf4j
@SuppressWarnings({"rawtypes"})
public class BudgetMonthCheckListener extends AnalysisEventListener {
    private static final int BATCH_COUNT = 100;
    private final List<BudgetMonthCheck> dataList = new ArrayList<>();
    private final BudgetMapService budgetMapService;

    public BudgetMonthCheckListener(BudgetMapService budgetMapService) {
        this.budgetMapService = budgetMapService;
    }

    @Override
    public void invoke(Object object, AnalysisContext context) {
        dataList.add((BudgetMonthCheck) object);
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
        budgetMapService.saveMonthCheck(dataList);
    }
}
