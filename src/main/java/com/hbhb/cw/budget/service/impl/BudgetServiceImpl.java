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
import com.hbhb.cw.systemcenter.enums.UnitEnum;
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

    @Override
    public List<BudgetVO> getBudgetListByCond(BudgetReqVO cond) {
        Map<String, BigDecimal> budgetNumAmountMap = new HashMap<>();
        List<BudgetVO> list = new ArrayList<>();
        if (UnitEnum.HANGZHOU.value().equals(cond.getUnitId())){
             list = budgetMapper.selectTreeByCond(cond);
        }else {
            List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
            list = budgetMapper.selectTreeListByCond(cond, unitIds);
        }
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        // 删除科目更新时间未在本年的
        String currentYear = DateUtil.getCurrentYear();
        Date year = DateUtil.stringToDate(currentYear+"-01-01 00:00:00");
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).getUpdateTime() == null || list.get(i).getUpdateTime().getTime() < year.getTime()) {
                if (list.get(i).getChildren().get(0).getId() == null) {
                    list.remove(i);
                }
            }
        }

        // 得到去年完成值
        List<BudgetProgressResVO> budgetDataList = budgetProjectService.getBudgetProgressByBudgetData(cond);
        for (BudgetProgressResVO progress : budgetDataList) {
            budgetNumAmountMap.put(progress.getBudgetNum(), progress.getAmount());
        }
        // 把去年完成值附上
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
            // 统计子类去年/今年预算值合计
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
    public List<TreeSelectParentVO>  getTreeByCond(BudgetReqVO cond) {
        List<BudgetVO> list = budgetMapper.selectTreeByCond(cond);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        String currentYear = DateUtil.getCurrentYear();
        Date year = DateUtil.stringToDate(currentYear+"-01-01 00:00:00");
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
        // 组装导出列表字段
        return buildExportVO(list);
    }

    @Override
    public List<SelectVO> getProjectTypeList() {
        List<SelectVO> list = new ArrayList<>();
        List<Budget> budgets = budgetMapper.selectAllByYear(DateUtil.getCurrentYear());
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
        // 待更新的预算list
        List<Budget> insertBudgetList = new ArrayList<>();
        // 待新增的预算list
        List<Budget> updateBudgetList = new ArrayList<>();
        // 待新增的预算修改历史list
        List<BudgetHistory> budgetHistoryList = new ArrayList<>();
        // 待新增的预算关联数据list
        List<BudgetData> budgetDataList = new ArrayList<>();
        // 待新增的预算归口关系数据
        List<BudgetBelong> budgetBelongList = new ArrayList<>();
        // 特殊情况下的dataList
        List<Map<String, List<BudgetData>>> dataMapList = new ArrayList<>();
        // 特殊情况下的belongList
        List<Map<String, List<BudgetBelong>>> belongMapList = new ArrayList<>();
        // 用来存储这一年的所有budgetIds
        List<Long> budgetIds = new ArrayList<>();
        // budgetNum -> budgetData list
        Map<String, List<BudgetData>> budgetDataMap = new LinkedHashMap<>();
        // budgetBelong -> budgetBelong list
        Map<String, List<BudgetBelong>> budgetBelongMap = new HashMap<>();
        // budgetNum -> budgetNum（用来判断出现重复key）
        Map<String, String> booleanMap = new HashMap<>();
        // budgetNum -> Long(特殊情况下存储budgetId)   特殊（一个项目存在多个归口部门的情况下）
        Map<String, Long> idByNumMap = new HashMap<>();

        // 获取所有的单位缩写名列表
        Map<String, Integer> unitMap = unitApi.getUnitMapByShortName();

        // 获取所有的预算科目列表
        List<SelectVO> budgetItemList = budgetItemMapper.selectAllItems();
        HashMap<String, Long> budgetItemMap = new HashMap<>();
        for (SelectVO selectVO : budgetItemList) {
            budgetItemMap.put(selectVO.getLabel(), selectVO.getId());
        }
//        Map<String, Long> budgetItemMap = budgetItemList.stream()
//                .collect(Collectors.toMap(SelectVO::getLabel, SelectVO::getId));

        // 获取当年所有的预算列表，并按预算编号分组
        List<Budget> thisYearBudgetList = budgetMapper.selectAllByYear(DateUtil.getCurrentYear());
        Map<String, Budget> thisYearBudgetMap = thisYearBudgetList.stream()
                .collect(Collectors.toMap(Budget::getBudgetNum, Function.identity()));

        // 设置标识用来判断是否为本年导入
        boolean flag = importDate.equals(DateUtil.getCurrentYear());

        // 1，新建map用来存储num和vo对象
        // 2，在新增的时候判断是否有重复的num，如果存在则不添加到budget但是改变budgetBelong和budgetData放入list
        // 3，新增时为budget（修改）+（新增） budgetBelong（修改）+（新增）+（特殊） budgetData（修改）+（新增）+（特殊）

        // 分解元数据
        for (BudgetImportVO budgetImportVO : dataList) {
            String budgetItem = budgetImportVO.getBudgetItem();
            String budgetNum = budgetImportVO.getBudgetNum();
            Long budgetItemId;
            // 判断预算科目是否存在，如果已存在且是本年数据，则直接取id值
            if (budgetItemMap.containsKey(budgetItem) && flag) {
                budgetItemId = budgetItemMap.get(budgetItem);
            }
            // 如果不存在，则新增
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

            // 根据预算编号判断，如果该条预算已存在，则直接更新即可
            if (thisYearBudgetMap.containsKey(budgetNum) && flag) {
                Budget oldBudget = thisYearBudgetMap.get(budgetNum);
                // 此时预算科目必存在，故此处不需要再做校验
                updateBudgetList.add(Budget.builder()
                        .id(oldBudget.getId())
                        .budgetItemId(budgetItemMap.get(budgetItem))
                        .projectItem(budgetImportVO.getProjectItem())
                        .balance(budgetImportVO.getBalance())
                        .importDate(importDate)
                        .updateTime(now)
                        .build());

                // 组装预算关联数据（修改的data）
                List<BudgetData> tempList = new ArrayList<>();
                headerList.forEach(header -> {
                    // 遍历表头，匹配单位缩写名称
                    if (unitMap.containsKey(header)) {
                        // 通过反射获取对应的金额值
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // 如果为null，则表示没有对口关系，跳过
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
                    // 遍历表头，匹配单位缩写名称
                    if (unitMap.containsKey(header)) {
                        // 通过反射获取对应的金额值
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // 如果为null，则表示没有对口关系，跳过
                        if (balance != null) {
                            //判断归口关系
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
            // 如果预算不存在，则新增
            else {
                // 判断是否有重复的key
                String number = booleanMap.get(budgetNum);
                // 如果不存在（不重复）则赋值，改变budgetBelong和budgetData并放入list
                if (number == null) {
                    booleanMap.put(budgetNum, budgetNum);
                }
                // 如果重复则
                else {
                    // budgetNum -> budgetBelong
                    Map<String, List<BudgetBelong>> belongMap = new HashMap<>();
                    // budgetNum -> budgetData
                    Map<String, List<BudgetData>> dataMap = new HashMap<>();

                    List<BudgetData> tempList = new ArrayList<>();
                    headerList.forEach(header -> {
                        // 遍历表头，匹配单位缩写名称
                        if (unitMap.containsKey(header)) {
                            // 通过反射获取对应的金额值
                            BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                            // 如果为null，则表示没有对口关系，跳过
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
                        // 遍历表头，匹配单位缩写名称
                        if (unitMap.containsKey(header)) {
                            // 通过反射获取对应的金额值
                            BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                            // 如果为null，则表示没有对口关系，跳过
                            if (balance != null) {
                                //判断归口关系
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

                // 组装预算关联数据
                List<BudgetData> tempList = new ArrayList<>();
                headerList.forEach(header -> {
                    // 遍历表头，匹配单位缩写名称
                    if (unitMap.containsKey(header)) {
                        // 通过反射获取对应的金额值
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // 如果为null，则表示没有对口关系，跳过
                        if (balance != null) {
                            tempList.add(BudgetData.builder()
                                    .unitId(unitMap.get(header))
                                    .balance(balance)
                                    .build());
                        }
                    }
                });
                // 组装预算归口关系
                List<BudgetBelong> belongList = new ArrayList<>();
                headerList.forEach(header -> {
                    // 遍历表头，匹配单位缩写名称
                    if (unitMap.containsKey(header)) {
                        // 通过反射获取对应的金额值
                        BigDecimal balance = getBudgetBalance(header, budgetImportVO);
                        // 如果为null，则表示没有对口关系，跳过
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


        // 处理预算
        if (!CollectionUtils.isEmpty(updateBudgetList)) {
            budgetMapper.updateBatch(updateBudgetList);
        }
        for (Budget budget : updateBudgetList) {
            budgetIds.add(budget.getId());
        }

        if (!CollectionUtils.isEmpty(insertBudgetList)) {
            budgetMapper.insertBatch(insertBudgetList);
        }
        for (Budget budget : insertBudgetList) {
            budgetIds.add(budget.getId());
        }

        List<Budget> budgets = budgetMapper.selectAllByYear(importDate);
        for (Budget budget : budgets) {
            idByNumMap.put(budget.getBudgetNum(), budget.getId());
        }

        // 设置budgetId（特殊）（新增的data）
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

        // 设置budgetId（特殊）（新增的belong）
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


        // 设置budgetId（新增的data）
        insertBudgetList.forEach(newBudget -> {
            if (budgetDataMap.containsKey(newBudget.getBudgetNum())) {
                List<BudgetData> list = budgetDataMap.get(newBudget.getBudgetNum());
                list.forEach(budgetData -> budgetData.setBudgetId(newBudget.getId()));
                budgetDataList.addAll(list);
            }
        });

        // 设置budgetId（新增的belong）
        insertBudgetList.forEach(newBudget -> {
            if (budgetBelongMap.containsKey(newBudget.getBudgetNum())) {
                List<BudgetBelong> list = budgetBelongMap.get(newBudget.getBudgetNum());
                list.forEach(budgetBelong -> budgetBelong.setBudgetId(newBudget.getId()));
                budgetBelongList.addAll(list);
            }
        });

        List<BudgetAdjustVO> vos = new ArrayList<>();
        List<BudgetHistory> adjust = new ArrayList<>();

        // 新增的budgetHistory
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
        // 批量导入中期预算调整
        budgetHistoryService.insertBatch(adjust);
        // 处理预算关联数据
        // 先删除，再新增
        if (budgetIds.size() != 0) {
            budgetDataMapper.deleteBatchByBudgetId(budgetIds);
        }
        if (!CollectionUtils.isEmpty(budgetDataList)) {
            // 设置了UK(budgetId, unitId) 如果重复，则更新balance
            budgetDataMapper.insertBatch(budgetDataList);
        }
        // 先删除，再新增
        if (budgetIds.size() != 0) {
            budgetBelongMapper.deleteBatchByBudgetId(budgetIds);
        }
        if (!CollectionUtils.isEmpty(budgetBelongList)) {
            // 设置了UK(budgetId, unitId) 如果重复，则更新underUnitId
            budgetBelongMapper.insertBatch(budgetBelongList);
        }
        // 处理预算历史
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
        // BudgetAdjustVO下  unitId=>balance
        Map<Integer, BigDecimal> voMap = new HashMap<>();
        // 用来存储历史数据（中期调整数据）
        ArrayList<BudgetHistory> budgetHistories = new ArrayList<>();
        // 用来存储旧一代的预算数据的单位id
        List<Integer> dataUnitIds = new ArrayList<>();
        // 用来存储新一代的预算数据的单位id
        List<Integer> voUnitIds = new ArrayList<>();
        // 每个adjustList的budgetId和underUnitId固定
        Long budgetId = vos.get(0).getBudgetId();
        Integer underUnitId = vos.get(0).getDeptId();
        for (BudgetAdjustVO vo : vos) {
            voMap.put(vo.getUnitId(), vo.getBalance());
            voUnitIds.add(vo.getUnitId());
        }
        List<Integer> unitIds = budgetBelongMapper.selectUnitIdByUnderUnitId(budgetId, underUnitId);
        List<BudgetData> dataList = budgetDataService.getListByUnitIds(budgetId, unitIds);
        // 判断是否存在预算数据
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
            // 旧一代数据
            for (BudgetData budgetData : dataList) {
                BigDecimal newValue = voMap.get(budgetData.getUnitId());
                BigDecimal subtract = new BigDecimal("0.0");
                // 判断新数据已经没有了的单位
                // 还存在
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
            // 判断老数据不包含的单位
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
        // 通过budgetNum和年限查询去年预算表得到预算值
        String lastYear = DateUtil.getLastYear();
        BigDecimal lastYearBalance = budgetMapper
                .selectBalanceByDate(lastYear, budgetAddVO.getBudgetNum());
        if (lastYearBalance != null) {
            budgetAddVO.setLastYearBalance(lastYearBalance);
        }
        budgetAddVO.setBudgetItemId(budgetAddVO.getBudgetItemId() >> 10);
        // 增加
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
        // 删除
        // 判断签报是否被用过，如果用过不予删除
        if (budgetProjectService.existByBudgetId(budgetId)) {
            throw new BudgetException(BudgetErrorCode.THE_PROJECT_IS_CONNECTED);
        }
        // 删除主表
        budgetMapper.deleteByPrimaryKey(budgetId);
        // 删除关联表
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
     * budgetVO 转 treeSelectParentVO
     */
    private TreeSelectParentVO buildTreeSelectParentVO(BudgetVO budgetVO) {
        TreeSelectParentVO vo = new TreeSelectParentVO();
        vo.setId(Math.toIntExact(budgetVO.getId()) << 10);
        vo.setLabel(budgetVO.getItemName());
        vo.setIsParent(true);
        if (!CollectionUtils.isEmpty(budgetVO.getChildren())) {
            List<TreeSelectParentVO> children = new ArrayList<>();
            // 组装预算科目子类children-项目类别名称
            for (BudgetVO child : budgetVO.getChildren()) {
                if (child.getId()==null){
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
     * 通过表头名称获取对应的属性值
     */
    private BigDecimal getBudgetBalance(String headerValue, BudgetImportVO budgetImportVO) {
        if (StringUtils.isEmpty(headerValue)) {
            return null;
        }
        // 表头名称转拼音，对应属性名
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
     * 组装预算导出字段（仅限两层结构。预算类别目前只有两层）
     */
    private List<BudgetExportVO> buildExportVO(List<BudgetVO> list) {
        List<BudgetExportVO> result = new ArrayList<>();
        // excel行号
        AtomicLong lineNumber = new AtomicLong(1);

        for (BudgetVO budget : list) {
            // 组装预算科目
            BudgetExportVO vo = new BudgetExportVO();
            vo.setLineNumber(lineNumber.getAndIncrement());
            vo.setItemName(budget.getItemName());
            vo.setMeasurement("万元");
            vo.setRemark(budget.getRemark());
            // 预算合计值
            BigDecimal lastYearBalanceTotal = new BigDecimal(0);
            BigDecimal thisYearBalanceTotal = new BigDecimal(0);

            // 判断是否有子类
            if (!CollectionUtils.isEmpty(budget.getChildren())) {
                for (BudgetVO child : budget.getChildren()) {
                    result.add(BudgetExportVO.builder()
                            .lineNumber(lineNumber.getAndIncrement())
                            .itemName("----" + child.getItemName())
                            .measurement("万元")
                            .lastYearBalance(child.getLastYearBalance())
                            .thisYearBalance(child.getBalance())
                            .remark(child.getRemark())
                            .build());

                    // 统计子类去年/今年预算值
                    if (child.getLastYearBalance() != null) {
                        lastYearBalanceTotal = lastYearBalanceTotal.add(child.getLastYearBalance());
                    }
                    if (child.getBalance() != null) {
                        thisYearBalanceTotal = thisYearBalanceTotal.add(child.getBalance());
                    }
                }
            }
            vo.setLastYearBalance(lastYearBalanceTotal);
            vo.setThisYearBalance(thisYearBalanceTotal);
            result.add(vo);
        }
        // 按lineNumber排序
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
}