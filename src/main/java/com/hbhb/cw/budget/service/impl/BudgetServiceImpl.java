package com.hbhb.cw.budget.service.impl;


import com.alibaba.excel.support.ExcelTypeEnum;
import com.github.promeg.pinyinhelper.Pinyin;
import com.hbhb.api.core.bean.SelectVO;
import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetBelongMapper;
import com.hbhb.cw.budget.mapper.BudgetDataMapper;
import com.hbhb.cw.budget.mapper.BudgetItemMapper;
import com.hbhb.cw.budget.mapper.BudgetMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectMapper;
import com.hbhb.cw.budget.model.Budget;
import com.hbhb.cw.budget.model.BudgetBelong;
import com.hbhb.cw.budget.model.BudgetData;
import com.hbhb.cw.budget.model.BudgetHistory;
import com.hbhb.cw.budget.model.BudgetItem;
import com.hbhb.cw.budget.model.BudgetProject;
import com.hbhb.cw.budget.rpc.UnitApiExp;
import com.hbhb.cw.budget.service.BudgetDataService;
import com.hbhb.cw.budget.service.BudgetHistoryService;
import com.hbhb.cw.budget.service.BudgetProjectService;
import com.hbhb.cw.budget.service.BudgetService;
import com.hbhb.cw.budget.web.vo.BudgetAdjustVO;
import com.hbhb.cw.budget.web.vo.BudgetExportVO;
import com.hbhb.cw.budget.web.vo.BudgetHistoryInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetImportVO;
import com.hbhb.cw.budget.web.vo.BudgetInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressResVO;
import com.hbhb.cw.budget.web.vo.BudgetReqVO;
import com.hbhb.cw.budget.web.vo.BudgetVO;
import com.hbhb.cw.systemcenter.vo.TreeSelectParentVO;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BudgetServiceImpl implements BudgetService {

    @Resource
    private BudgetMapper budgetMapper;
    @Resource
    private BudgetItemMapper budgetItemMapper;
    @Resource
    private BudgetDataMapper budgetDataMapper;
    @Resource
    private BudgetBelongMapper budgetBelongMapper;
    @Resource
    private BudgetDataService budgetDataService;
    @Resource
    private BudgetHistoryService budgetHistoryService;
    @Resource
    private BudgetProjectService budgetProjectService;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private BudgetProjectMapper budgetProjectMapper;

    @Override
    public List<BudgetVO> getBudgetListByCond(BudgetReqVO cond) {
        Map<String, BigDecimal> budgetNumAmountMap = new HashMap<>();
        List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
        List<BudgetVO> list = budgetMapper.selectTreeListByCond(cond, unitIds);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        // ???????????????????????????????????????
        String currentYear = DateUtil.getCurrentYear();
        Date year = DateUtil.stringToDate(currentYear + "-01-01 00:00:00");
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getUpdateTime() == null || list.get(i).getUpdateTime().getTime() < year.getTime()) {
                if (list.get(i).getChildren().get(0).getId() == null) {
                    list.remove(i);
                }
            }
        }

        // ?????????????????????
        List<BudgetProgressResVO> budgetDataList = budgetProjectService.getBudgetProgressByBudgetData(cond);
        for (BudgetProgressResVO progress : budgetDataList) {
            budgetNumAmountMap.put(progress.getBudgetNum(), progress.getAmount());
        }
        // ????????????????????????
        for (BudgetVO budgetVO : list) {
            for (BudgetVO child : budgetVO.getChildren()) {
                if (child.getLastYearBalance() == null) {
                    child.setLastYearBalance(new BigDecimal("0.0"));
                }
                BigDecimal bigDecimal = budgetNumAmountMap.get(child.getBudgetNum());
                if (bigDecimal == null) {
                    bigDecimal = new BigDecimal("0.0");
                }
                child.setLastYearFinishedBalance(bigDecimal);
            }
        }
        list.forEach(vo -> {
            vo.setId(vo.getId() << 10);
            vo.setHasChildren(true);
            // ??????????????????/?????????????????????
            BigDecimal lastYearBalanceTotal = new BigDecimal(0);
            BigDecimal balanceTotal = new BigDecimal(0);
            for (BudgetVO childVO : vo.getChildren()) {
                childVO.setHasChildren(false);
                lastYearBalanceTotal = lastYearBalanceTotal.add(childVO.getLastYearBalance());
                if (childVO.getBalance() != null) {
                    balanceTotal = balanceTotal.add(childVO.getBalance());
                }
            }
            vo.setLastYearBalance(lastYearBalanceTotal);
            vo.setBalance(balanceTotal);
        });
        return list;
    }

    @Override
    public List<TreeSelectParentVO> getTreeByCond(BudgetReqVO cond) {
        List<BudgetVO> list = budgetMapper.selectTreeByCond(cond);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        String currentYear = DateUtil.getCurrentYear();
        Date year = DateUtil.stringToDate(currentYear + "-01-01 00:00:00");
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getUpdateTime() == null || list.get(i).getUpdateTime().getTime() < year.getTime()) {
                if (list.get(i).getChildren().get(0).getId() == null) {
                    list.remove(i);
                }
            }
        }
        return list.stream().map(this::buildTreeSelectParentVO).collect(Collectors.toList());
    }

    @Override
    public List<BudgetExportVO> getExportList(BudgetReqVO cond) {
        List<BudgetVO> list = this.getBudgetListByCond(cond);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        // ????????????????????????
        return buildExportVO(list);
    }

    @Override
    public List<SelectVO> getProjectTypeList(String year) {
        List<SelectVO> list = new ArrayList<>();
        if (year==null){
            year = DateUtil.getCurrentYear();
        }
        List<Budget> budgets = budgetMapper.selectAllByYear(year);
        budgets.forEach(budget -> list.add(SelectVO.builder()
                .id(budget.getId())
                .label(budget.getBudgetNum() + "_" + budget.getProjectItem())
                .build()));
        return list;
    }

    @Override
    public BudgetVO getInfoById(Long id) {
        BudgetVO vo = new BudgetVO();
        Budget budget = budgetMapper.selectByPrimaryKey(id);
        if (budget == null) {
            BudgetHistoryInfoVO budgetHistory = budgetHistoryService.getInfoById(id);
            BeanConverter.copyProp(budgetHistory, vo);
            return vo;
        }
        BeanConverter.copyProp(budget, vo);
        vo.setLabel(budget.getProjectItem());
        vo.setBudgetItemId(budget.getBudgetItemId() << 10);
        return vo;
    }

    @Override
    public void updateThreshold(BudgetAdjustVO vo) {
        Budget budget = new Budget();
        budget.setId(vo.getBudgetId());
        budget.setThreshold(vo.getThreshold());
        budget.setRemark(vo.getRemark());
        budgetMapper.updateByPrimaryKeySelective(budget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBudget(List<BudgetImportVO> dataList, List<String> headerList,
                           String importDate) {
        Date now = new Date();
        // ??????????????????list
        List<Budget> insertBudgetList = new ArrayList<>();
        // ??????????????????list
        List<Budget> updateBudgetList = new ArrayList<>();
        // ??????????????????????????????list
        List<BudgetHistory> budgetHistoryList = new ArrayList<>();
        // ??????????????????????????????list
        List<BudgetData> budgetDataList = new ArrayList<>();
        // ????????????????????????????????????
        List<BudgetBelong> budgetBelongList = new ArrayList<>();
        // ??????????????????dataList
        List<Map<String, List<BudgetData>>> dataMapList = new ArrayList<>();
        // ??????????????????belongList
        List<Map<String, List<BudgetBelong>>> belongMapList = new ArrayList<>();
        // ??????????????????????????????budgetIds
        List<Long> budgetIds = new ArrayList<>();
        // budgetNum -> budgetData list
        Map<String, List<BudgetData>> budgetDataMap = new LinkedHashMap<>();
        // budgetBelong -> budgetBelong list
        Map<String, List<BudgetBelong>> budgetBelongMap = new HashMap<>();
        // budgetNum -> budgetNum???????????????????????????key???
        Map<String, String> booleanMap = new HashMap<>();
        // budgetNum -> Long(?????????????????????budgetId)   ????????????????????????????????????????????????????????????
        Map<String, Long> idByNumMap = new HashMap<>();

        // ????????????????????????????????????
        Map<String, Integer> unitMap = unitApi.getUnitMapByShortName();

        // ?????????????????????????????????
        List<SelectVO> budgetItemList = budgetItemMapper.selectAllItems();
        HashMap<String, Long> budgetItemMap = new HashMap<>();
        for (SelectVO selectVO : budgetItemList) {
            budgetItemMap.put(selectVO.getLabel(), selectVO.getId());
        }
//        Map<String, Long> budgetItemMap = budgetItemList.stream()
//                .collect(Collectors.toMap(SelectVO::getLabel, SelectVO::getId));

        // ????????????????????????????????????????????????????????????
        List<Budget> thisYearBudgetList = budgetMapper.selectAllByYear(DateUtil.getCurrentYear());
        Map<String, Budget> thisYearBudgetMap = thisYearBudgetList.stream()
                .collect(Collectors.toMap(Budget::getBudgetNum, Function.identity()));

        // ?????????????????????????????????????????????
        boolean flag = importDate.equals(DateUtil.getCurrentYear());

        // 1?????????map????????????num???vo??????
        // 2?????????????????????????????????????????????num??????????????????????????????budget????????????budgetBelong???budgetData??????list
        // 3???????????????budget????????????+???????????? budgetBelong????????????+????????????+???????????? budgetData????????????+????????????+????????????

        // ???????????????
        for (BudgetImportVO budgetImportVO : dataList) {
            String budgetItem = budgetImportVO.getBudgetItem();
            String budgetNum = budgetImportVO.getBudgetNum();
            Long budgetItemId;
            // ?????????????????????????????????????????????????????????????????????????????????id???
            if (budgetItemMap.containsKey(budgetItem) && flag) {
                budgetItemId = budgetItemMap.get(budgetItem);
            }
            // ???????????????????????????
            else {
                BudgetItem newBudgetItem = BudgetItem.builder()
                        .itemName(budgetItem)
                        .build();
                budgetItemMapper.insertSelective(newBudgetItem);
                if (newBudgetItem.getId() == null) {
                    budgetItemId = budgetItemMap.get(budgetItem);
                } else {
                    budgetItemMap.put(budgetItem, newBudgetItem.getId());
                    budgetItemId = newBudgetItem.getId();
                }
            }

            // ??????????????????????????????????????????????????????????????????????????????
            if (thisYearBudgetMap.containsKey(budgetNum) && flag) {
                Budget oldBudget = thisYearBudgetMap.get(budgetNum);
                // ????????????????????????????????????????????????????????????
                updateBudgetList.add(Budget.builder()
                        .id(oldBudget.getId())
                        .budgetItemId(budgetItemMap.get(budgetItem))
                        .projectItem(budgetImportVO.getProjectItem())
                        .balance(budgetImportVO.getBalance())
                        .budgetNum(budgetImportVO.getBudgetNum())
                        .importDate(importDate)
                        .updateTime(now)
                        .build());

                // ????????????????????????????????????data???
                List<BudgetData> tempList = new ArrayList<>();
                headerList.forEach(header -> {
                    // ???????????????????????????????????????
                    if (unitMap.containsKey(header)) {
                        // ????????????????????????????????????
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // ?????????null???????????????????????????????????????
                        if (balance != null) {
                            tempList.add(BudgetData.builder()
                                    .budgetId(oldBudget.getId())
                                    .unitId(unitMap.get(header))
                                    .balance(balance)
                                    .build());
                        }
                    }
                });

                List<BudgetBelong> belongList = new ArrayList<>();
                headerList.forEach(header -> {
                    // ???????????????????????????????????????
                    if (unitMap.containsKey(header)) {
                        // ????????????????????????????????????
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // ?????????null???????????????????????????????????????
                        if (balance != null) {
                            //??????????????????
                            if (balance.equals(BigDecimal.ZERO)) {
                                belongList.add(BudgetBelong.builder()
                                        .budgetId(oldBudget.getId())
                                        .unitId(unitMap.get(header))
                                        .underUnitId(unitMap.get(budgetImportVO.getUnitShortName()))
                                        .build());
                            } else {
                                belongList.add(BudgetBelong.builder()
                                        .budgetId(oldBudget.getId())
                                        .unitId(unitMap.get(header))
                                        .underUnitId(unitMap.get(header))
                                        .build());
                            }
                        }
                    }
                });
                budgetBelongList.addAll(belongList);
                budgetDataList.addAll(tempList);
            }
            // ?????????????????????????????????
            else {
                // ????????????????????????key
                String number = booleanMap.get(budgetNum);
                // ????????????????????????????????????????????????budgetBelong???budgetData?????????list
                if (number == null) {
                    booleanMap.put(budgetNum, budgetNum);
                }
                // ???????????????
                else {
                    // budgetNum -> budgetBelong
                    Map<String, List<BudgetBelong>> belongMap = new HashMap<>();
                    // budgetNum -> budgetData
                    Map<String, List<BudgetData>> dataMap = new HashMap<>();

                    List<BudgetData> tempList = new ArrayList<>();
                    headerList.forEach(header -> {
                        // ???????????????????????????????????????
                        if (unitMap.containsKey(header)) {
                            // ????????????????????????????????????
                            BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                            // ?????????null???????????????????????????????????????
                            if (balance != null) {
                                tempList.add(BudgetData.builder()
                                        .unitId(unitMap.get(header))
                                        .balance(balance)
                                        .build());
                            }
                        }
                    });
                    dataMap.put(budgetNum, tempList);
                    dataMapList.add(dataMap);

                    List<BudgetBelong> belongList = new ArrayList<>();
                    headerList.forEach(header -> {
                        // ???????????????????????????????????????
                        if (unitMap.containsKey(header)) {
                            // ????????????????????????????????????
                            BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                            // ?????????null???????????????????????????????????????
                            if (balance != null) {
                                //??????????????????
                                if (balance.equals(BigDecimal.ZERO)) {
                                    belongList.add(BudgetBelong.builder()
                                            .unitId(unitMap.get(header))
                                            .underUnitId(unitMap.get(budgetImportVO.getUnitShortName()))
                                            .build());
                                } else {
                                    belongList.add(BudgetBelong.builder()
                                            .unitId(unitMap.get(header))
                                            .underUnitId(unitMap.get(header))
                                            .build());
                                }
                            }
                        }
                    });
                    belongMap.put(budgetNum, belongList);
                    belongMapList.add(belongMap);
                    continue;
                }

                insertBudgetList.add(Budget.builder()
                        .budgetItemId(budgetItemId)
                        .projectItem(budgetImportVO.getProjectItem())
                        .budgetNum(budgetNum)
                        .balance(budgetImportVO.getBalance())
                        .lastYearBalance(new BigDecimal(BigInteger.ZERO))
                        .threshold(new BigDecimal(BigInteger.ZERO))
                        .importDate(importDate)
                        .remark("")
                        .build());

                // ????????????????????????
                List<BudgetData> tempList = new ArrayList<>();
                headerList.forEach(header -> {
                    // ???????????????????????????????????????
                    if (unitMap.containsKey(header)) {
                        // ????????????????????????????????????
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // ?????????null???????????????????????????????????????
                        if (balance != null) {
                            tempList.add(BudgetData.builder()
                                    .unitId(unitMap.get(header))
                                    .balance(balance)
                                    .build());
                        }
                    }
                });
                // ????????????????????????
                List<BudgetBelong> belongList = new ArrayList<>();
                headerList.forEach(header -> {
                    // ???????????????????????????????????????
                    if (unitMap.containsKey(header)) {
                        // ????????????????????????????????????
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // ?????????null???????????????????????????????????????
                        if (balance != null) {
                            if (balance.equals(BigDecimal.ZERO)) {
                                belongList.add(BudgetBelong.builder()
                                        .unitId(unitMap.get(header))
                                        .underUnitId(unitMap.get(budgetImportVO.getUnitShortName()))
                                        .build());
                            } else {
                                belongList.add(BudgetBelong.builder()
                                        .unitId(unitMap.get(header))
                                        .underUnitId(unitMap.get(header))
                                        .build());
                            }
                        }
                    }
                });
                budgetDataMap.put(budgetNum, tempList);
                budgetBelongMap.put(budgetNum, belongList);
            }
        }


        // ????????????
        if (!CollectionUtils.isEmpty(updateBudgetList)) {
            for (Budget budget : updateBudgetList) {
                budget.setSerialNum(budget.getBudgetNum()+importDate);
            }
            budgetMapper.updateBatch(updateBudgetList);
        }
        for (Budget budget : updateBudgetList) {
            budgetIds.add(budget.getId());
        }

        if (!CollectionUtils.isEmpty(insertBudgetList)) {
            for (Budget budget : insertBudgetList) {
                budget.setSerialNum(budget.getBudgetNum()+importDate);
            }
            budgetMapper.insertBatch(insertBudgetList);
        }

        for (Budget budget : insertBudgetList) {
            budgetIds.add(budget.getId());
        }

        List<Budget> budgets = budgetMapper.selectAllByYear(importDate);
        for (Budget budget : budgets) {
            idByNumMap.put(budget.getBudgetNum(), budget.getId());
        }

        // ??????budgetId????????????????????????data???
        for (Map<String, List<BudgetData>> stringListMap : dataMapList) {
            Set<String> strings = stringListMap.keySet();
            for (String string : strings) {
                Long aLong = idByNumMap.get(string);
                List<BudgetData> budgetDataList1 = stringListMap.get(string);
                for (BudgetData budgetData : budgetDataList1) {
                    budgetData.setBudgetId(aLong);
                }
                budgetDataList.addAll(budgetDataList1);
            }
        }

        // ??????budgetId????????????????????????belong???
        for (Map<String, List<BudgetBelong>> stringListMap : belongMapList) {
            Set<String> strings = stringListMap.keySet();
            for (String string : strings) {
                Long aLong = idByNumMap.get(string);
                List<BudgetBelong> budgetBelongList1 = stringListMap.get(string);
                for (BudgetBelong budgetBelong : budgetBelongList1) {
                    budgetBelong.setBudgetId(aLong);
                }
                budgetBelongList.addAll(budgetBelongList1);
            }
        }


        // ??????budgetId????????????data???
        insertBudgetList.forEach(newBudget -> {
            if (budgetDataMap.containsKey(newBudget.getBudgetNum())) {
                List<BudgetData> list = budgetDataMap.get(newBudget.getBudgetNum());
                list.forEach(budgetData -> budgetData.setBudgetId(newBudget.getId()));
                budgetDataList.addAll(list);
            }
        });

        // ??????budgetId????????????belong???
        insertBudgetList.forEach(newBudget -> {
            if (budgetBelongMap.containsKey(newBudget.getBudgetNum())) {
                List<BudgetBelong> list = budgetBelongMap.get(newBudget.getBudgetNum());
                list.forEach(budgetBelong -> budgetBelong.setBudgetId(newBudget.getId()));
                budgetBelongList.addAll(list);
            }
        });

        List<BudgetAdjustVO> vos = new ArrayList<>();
        List<BudgetHistory> adjust = new ArrayList<>();

        // ?????????budgetHistory
        for (int i = 0; i < budgetBelongList.size(); i++) {
            vos.add(BudgetAdjustVO.builder()
                    .budgetId(budgetBelongList.get(i).getBudgetId())
                    .deptId(budgetBelongList.get(i).getUnderUnitId())
                    .unitId(budgetBelongList.get(i).getUnitId())
                    .balance(budgetDataList.get(i).getBalance())
                    .build());
            int l = i + 1;
            if (l >= budgetBelongList.size()) {
                List<BudgetHistory> budgetHistories = adjustBudget(vos);
                adjust.addAll(budgetHistories);
                vos.clear();
            } else {
                if (!budgetBelongList.get(i).getBudgetId().equals(budgetBelongList.get(l).getBudgetId())) {
                    List<BudgetHistory> budgetHistories = adjustBudget(vos);
                    adjust.addAll(budgetHistories);
                    vos.clear();
                }
            }
        }
        // ??????????????????????????????
        budgetHistoryService.insertBatch(adjust);
        // ????????????????????????
        // ?????????????????????
        if (budgetIds.size() != 0) {
            budgetDataMapper.deleteBatchByBudgetId(budgetIds);
        }
        if (!CollectionUtils.isEmpty(budgetDataList)) {
            // ?????????UK(budgetId, unitId) ????????????????????????balance
            budgetDataMapper.insertBatch(budgetDataList);
        }
        // ?????????????????????
        if (budgetIds.size() != 0) {
            budgetBelongMapper.deleteBatchByBudgetId(budgetIds);
        }
        if (!CollectionUtils.isEmpty(budgetBelongList)) {
            // ?????????UK(budgetId, unitId) ????????????????????????underUnitId
            updateBudgetList.addAll(insertBudgetList);
            // budgetId => budgetNum+importDate
            Map<Long, String> budgetMap = new HashMap<>();
            for (Budget budget : updateBudgetList) {
                budgetMap.put(budget.getId(),budget.getBudgetNum()+importDate);
            }
            for (BudgetBelong budgetBelong : budgetBelongList) {
                budgetBelong.setSerialNum(budgetMap.get(budgetBelong.getBudgetId()));
            }
            budgetBelongMapper.insertBatch(budgetBelongList);
        }
        // ??????????????????
        if (!CollectionUtils.isEmpty(budgetHistoryList)) {
            budgetHistoryService.insertBatch(budgetHistoryList);
        }

        Map<String, Long> map = new HashMap<>();
        for (Budget budget : insertBudgetList) {
            map.put(budget.getImportDate() + budget.getBudgetNum(), budget.getId());
        }
        List<BudgetProject> budgetProjects = budgetProjectService.getJieZhuan(importDate);
        ArrayList<BudgetProject> budgetProjectList = new ArrayList<>();
        if (budgetProjects != null && budgetProjects.size() != 0 && !flag) {
            for (BudgetProject budgetProject : budgetProjects) {
                Date createTime = budgetProject.getCreateTime();
                String s = DateUtil.dateToString(createTime);
                String year = s.substring(0, 4);
                String projectNum = budgetProject.getProjectNum();
                String substring = projectNum.substring(0, projectNum.length() - 8);
                if (map.get(year + substring) == null) {
                    continue;
                }
                budgetProject.setBudgetId(map.get(year + substring));
                budgetProjectList.add(budgetProject);
            }
            budgetProjectService.updateBatch(budgetProjectList);
        }
        System.gc();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BudgetHistory> adjustBudget(List<BudgetAdjustVO> vos) {
        // BudgetAdjustVO???  unitId=>balance
        Map<Integer, BigDecimal> voMap = new HashMap<>();
        // ????????????????????????????????????????????????
        ArrayList<BudgetHistory> budgetHistories = new ArrayList<>();
        // ?????????????????????????????????????????????id
        List<Integer> dataUnitIds = new ArrayList<>();
        // ?????????????????????????????????????????????id
        List<Integer> voUnitIds = new ArrayList<>();
        // ??????adjustList???budgetId???underUnitId??????
        Long budgetId = vos.get(0).getBudgetId();
        Integer underUnitId = vos.get(0).getDeptId();
        for (BudgetAdjustVO vo : vos) {
            voMap.put(vo.getUnitId(), vo.getBalance());
            voUnitIds.add(vo.getUnitId());
        }
        List<Integer> unitIds = budgetBelongMapper.selectUnitIdByUnderUnitId(budgetId, underUnitId);
        List<BudgetData> dataList = budgetDataService.getListByUnitIds(budgetId, unitIds);
        // ??????????????????????????????
        if (dataList == null || dataList.size() == 0) {
            for (Integer voUnitId : voUnitIds) {
                budgetHistories.add(BudgetHistory.builder()
                        .budgetId(budgetId)
                        .unitId(voUnitId)
                        .oldValue(null)
                        .newValue(voMap.get(voUnitId))
                        .difValue(voMap.get(voUnitId))
                        .build());
            }
        } else {
            // ???????????????
            for (BudgetData budgetData : dataList) {
                BigDecimal newValue = voMap.get(budgetData.getUnitId());
                BigDecimal subtract = new BigDecimal("0.0");
                // ???????????????????????????????????????
                // ?????????
                if (newValue != null) {
                    subtract = newValue.subtract(budgetData.getBalance());
                } else {
                    subtract = subtract.subtract(budgetData.getBalance());
                }
                budgetHistories.add(BudgetHistory.builder()
                        .budgetId(budgetId)
                        .unitId(budgetData.getUnitId())
                        .oldValue(budgetData.getBalance())
                        .newValue(newValue)
                        .difValue(subtract)
                        .build());
                dataUnitIds.add(budgetData.getUnitId());
            }
            // ?????????????????????????????????
            for (Integer voUnitId : voUnitIds) {
                if (!dataUnitIds.contains(voUnitId)) {
                    budgetHistories.add(BudgetHistory.builder()
                            .budgetId(budgetId)
                            .unitId(voUnitId)
                            .oldValue(null)
                            .newValue(voMap.get(voUnitId))
                            .difValue(voMap.get(voUnitId))
                            .build());
                }
            }
        }
        return budgetHistories;

    }

    @Override
    public void updateBudget(Long budgetId, BigDecimal balance) {
        budgetMapper.upBudgetByBudgetId(budgetId, balance);
    }

    @Override
    public void addBudget(BudgetInfoVO budgetAddVO) {
        // ??????budgetNum?????????????????????????????????????????????
        String lastYear = DateUtil.getLastYear();
        BigDecimal lastYearBalance = budgetMapper
                .selectBalanceByDate(lastYear, budgetAddVO.getBudgetNum());
        if (lastYearBalance != null) {
            budgetAddVO.setLastYearBalance(lastYearBalance);
        }
        budgetAddVO.setBudgetItemId(budgetAddVO.getBudgetItemId() >> 10);
        // ??????
        Budget budget = new Budget();
        budget.setBudgetItemId(budgetAddVO.getBudgetItemId());
        budget.setBudgetNum(budgetAddVO.getBudgetNum());
        budget.setThreshold(budgetAddVO.getThreshold());
        budget.setProjectItem(budgetAddVO.getProjectItem());
        budget.setLastYearBalance(budgetAddVO.getLastYearBalance());
        budget.setRemark(budgetAddVO.getRemark());
        budget.setImportDate(budgetAddVO.getImportDate());
        budgetMapper.insertSelective(budget);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByItemId(Long budgetId) {
        // ??????
        // ??????????????????????????????????????????????????????
        if (budgetProjectService.existByBudgetId(budgetId)) {
            throw new BudgetException(BudgetErrorCode.THE_PROJECT_IS_CONNECTED);
        }
        // ????????????
        budgetMapper.deleteByPrimaryKey(budgetId);
        // ???????????????
        budgetDataMapper.deleteByBudgetId(budgetId);
        budgetBelongMapper.deleteByBudgetId(budgetId);
    }

    @Override
    public void updateByUpdateVO(BudgetInfoVO budgetInfoVO) {
        Budget budget = new Budget();
        BeanUtils.copyProperties(budgetInfoVO, budget);
        budget.setBudgetItemId(budget.getBudgetItemId() >> 10);
        budgetMapper.updateByPrimaryKeySelective(budget);
    }

    @Override
    public BudgetInfoVO updateById(Long budgetId) {
        Budget budget = budgetMapper.selectByPrimaryKey(budgetId);
        BudgetInfoVO budgetUpdateVO = new BudgetInfoVO();
        BeanUtils.copyProperties(budget, budgetUpdateVO);
        return budgetUpdateVO;
    }

    /**
     * budgetVO ??? treeSelectParentVO
     */
    private TreeSelectParentVO buildTreeSelectParentVO(BudgetVO budgetVO) {
        TreeSelectParentVO vo = new TreeSelectParentVO();
        vo.setId(Math.toIntExact(budgetVO.getId()) << 10);
        vo.setLabel(budgetVO.getItemName());
        vo.setIsParent(true);
        if (!CollectionUtils.isEmpty(budgetVO.getChildren())) {
            List<TreeSelectParentVO> children = new ArrayList<>();
            // ????????????????????????children-??????????????????
            for (BudgetVO child : budgetVO.getChildren()) {
                if (child.getId() == null) {
                    continue;
                }
                TreeSelectParentVO treeSelectParentVO = new TreeSelectParentVO();
                treeSelectParentVO.setId(Math.toIntExact(child.getId()));
                treeSelectParentVO.setLabel(child.getItemName());
                treeSelectParentVO.setIsParent(false);
                children.add(treeSelectParentVO);

            }
            vo.setChildren(children);
        }
        return vo;
    }

    /**
     * ??????????????????????????????????????????
     */
    private BigDecimal getBudgetBalance(String headerValue, BudgetImportVO budgetImportVO) {
        if (StringUtils.isEmpty(headerValue)) {
            return null;
        }
        // ???????????????????????????????????????
        String filed = Pinyin.toPinyin(headerValue, "").toLowerCase();
        try {
            PropertyDescriptor pd = new PropertyDescriptor(filed, BudgetImportVO.class);
            return (BigDecimal) pd.getReadMethod().invoke(budgetImportVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BudgetException(BudgetErrorCode.BUDGET_DATA_PARSE_ERROR);
        }
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????
     */
    private List<BudgetExportVO> buildExportVO(List<BudgetVO> list) {
        List<BudgetExportVO> result = new ArrayList<>();
        // excel??????
        AtomicLong lineNumber = new AtomicLong(1);

        for (BudgetVO budget : list) {
            // ??????????????????
            BudgetExportVO vo = new BudgetExportVO();
            vo.setLineNumber(lineNumber.getAndIncrement());
            vo.setItemName(budget.getItemName());
            vo.setMeasurement("??????");
            vo.setRemark(budget.getRemark());
            // ?????????????????????
            if (!CollectionUtils.isEmpty(budget.getChildren())) {
                for (BudgetVO child : budget.getChildren()) {
                    result.add(BudgetExportVO.builder()
                            .lineNumber(lineNumber.getAndIncrement())
                            .itemName("----" + child.getItemName())
                            .measurement("??????")
                            .lastYearBalance(child.getLastYearBalance())
                            .lastYearFinishedBalance(child.getLastYearFinishedBalance())
                            .thisYearBalance(child.getBalance())
                            .remark(child.getRemark())
                            .build());
                }
            }
            vo.setLastYearBalance(budget.getLastYearBalance());
            vo.setThisYearBalance(budget.getBalance());
            vo.setLastYearFinishedBalance(budget.getLastYearFinishedBalance());
            result.add(vo);
        }
        // ???lineNumber??????
        result.sort(Comparator.comparing(BudgetExportVO::getLineNumber));
        return result;
    }

    @Override
    public BigDecimal getBudgetThreshold(Long id) {
        return budgetMapper.selectThreshold(id);
    }

    @Override
    public Integer getBudgetCount(Long itemId) {
        return budgetMapper.selectBudgetByItemId(itemId);
    }

    @Override
    public String getNumByBudgetId(Long budgetId) {
        return budgetMapper.selectByBudgetId(budgetId);
    }

    @Override
    public Budget getBudgetById(Long id) {
        return budgetMapper.selectByPrimaryKey(id);
    }

    @Override
    public void judegFileName(String fileName) {
        int i = fileName.lastIndexOf(".");
        String name = fileName.substring(i);
        if (!(ExcelTypeEnum.XLS.getValue().equals(name) || ExcelTypeEnum.XLSX.getValue().equals(name))) {
            throw new BudgetException(BudgetErrorCode.BUDGET_DATA_NAME_ERROR);
        }
    }

    @Override
    public Long getIdByNum(String budgetNum, String importDate) {
        return budgetMapper.selectIdByNum(budgetNum, importDate);
    }

    @Override
    public void check() {
        // ??????????????????
        List<BudgetProject> budgetProjects = budgetProjectMapper.selectAll();
        // ??????????????????????????????????????????????????????????????????????????????
        for (BudgetProject budgetProject : budgetProjects) {
            String projectNum = budgetProject.getProjectNum();
            String substring = projectNum.substring(0, projectNum.length() - 8);
            String year = DateUtil.dateToString(budgetProject.getCreateTime(), "yyyy");
            String budgetNum = substring+year;
            budgetProject.setSerialNum(budgetNum);
        }
        // ??????
        budgetProjectMapper.updateBatchById(budgetProjects);
    }

    @Override
    public void checkBelong() {
        // ????????????????????????
        List<BudgetBelong> budgetBelongs = budgetBelongMapper.selectAll();
        // ??????????????????
        List<Budget> budgets = budgetMapper.selectAll();
        // id => budgetNum
        Map<Long, String> budgetMap = new HashMap<>();
        for (Budget budget : budgets) {
            budgetMap.put(budget.getId(),budget.getBudgetNum()+budget.getImportDate());
        }
        // ??????????????????????????????????????????????????????????????????????????????
        for (BudgetBelong budgetBelong : budgetBelongs) {
            budgetBelong.setSerialNum(budgetMap.get(budgetBelong.getBudgetId()));
        }
        // ??????
        budgetBelongMapper.batchUpdate(budgetBelongs);
    }

    @Override
    public void checkBudget() {
        // ??????????????????
        List<Budget> budgets = budgetMapper.selectAll();
        for (Budget budget : budgets) {
           budget.setSerialNum(budget.getBudgetNum()+budget.getImportDate());
        }
        // ??????
        budgetMapper.updateBatch(budgets);
    }
}