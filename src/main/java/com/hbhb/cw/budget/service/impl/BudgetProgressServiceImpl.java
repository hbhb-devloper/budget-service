package com.hbhb.cw.budget.service.impl;


import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetBelongMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectMapper;
import com.hbhb.cw.budget.model.Budget;
import com.hbhb.cw.budget.model.BudgetData;
import com.hbhb.cw.budget.model.BudgetProject;
import com.hbhb.cw.budget.model.BudgetProjectAgile;
import com.hbhb.cw.budget.rpc.UnitApiExp;
import com.hbhb.cw.budget.service.BudgetDataService;
import com.hbhb.cw.budget.service.BudgetProgressService;
import com.hbhb.cw.budget.service.BudgetProjectAgileService;
import com.hbhb.cw.budget.service.BudgetService;
import com.hbhb.cw.budget.web.vo.BudgetFlowStateVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressDeclareVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressResVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAllVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectAmountVO;
import com.hbhb.cw.flowcenter.enums.FlowState;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Resource;

@Service
public class BudgetProgressServiceImpl implements BudgetProgressService {

    @Resource
    private BudgetBelongMapper budgetBelongMapper;
    @Resource
    private BudgetProjectMapper budgetProjectMapper;
    @Resource
    private BudgetDataService budgetDataService;
    @Resource
    private BudgetProjectAgileService budgetProjectAgileService;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private BudgetService budgetService;

    @Override
    public List<BudgetProgressVO> getBudgetProgressList(BudgetProgressReqVO cond) {
        List<BudgetProgressVO> result = new ArrayList<>();

        List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
        List<BudgetProgressResVO> progressList = budgetProjectMapper.selectProgress(cond, unitIds);

        // 按预算科目名称分组
        Map<String, List<BudgetProgressResVO>> progressMap = progressList.stream()
                .collect(Collectors.groupingBy(BudgetProgressResVO::getBudgetItemName));

        // 所有预算进度合计
        BigDecimal balanceTotal = new BigDecimal(0);
        BigDecimal amountTotal = new BigDecimal(0);

        for (Map.Entry<String, List<BudgetProgressResVO>> entry : progressMap.entrySet()) {
            // 组装父类
            BudgetProgressVO vo = BudgetProgressVO.builder()
                    .id(new Random().nextLong())
                    .itemName(entry.getKey())
                    .hasChildren(true)
                    .build();

            // 组装子类
            if (!CollectionUtils.isEmpty(entry.getValue())) {
                List<BudgetProgressVO> children = new ArrayList<>();
                List<BudgetProgressResVO> bpChildList = entry.getValue();
                for (BudgetProgressResVO child : bpChildList) {
                    children.add(BudgetProgressVO.builder()
                            .id(child.getBudgetId())
                            .itemName(child.getProjectItemName())
                            .balance(child.getBudgetBalance())
                            .amount(child.getBudgetBalance().compareTo(BigDecimal.ZERO) == 0 ? null : child.getAmount())
                            .surplus(calculateSurplus(child.getAmount(), child.getBudgetBalance()))
                            .percentage(calculatePercentage(child.getAmount(), child.getBudgetBalance()))
                            .hasChildren(false)
                            .build());
                    // 计算合计
                    balanceTotal = balanceTotal.add(child.getBudgetBalance());
                    amountTotal = amountTotal.add(child.getAmount());
                }
                vo.setChildren(children);
            }
            result.add(vo);
        }

        // 加入合计
        result.add(0, BudgetProgressVO.builder()
                .itemName("合计")
                .balance(balanceTotal)
                .amount(balanceTotal.compareTo(BigDecimal.ZERO) == 0 ? null : amountTotal)
                .surplus(calculateSurplus(amountTotal, balanceTotal))
                .percentage(calculatePercentage(amountTotal, balanceTotal))
                .build());
        return result;
    }

    @Override
    public List<BudgetProjectAmountVO> getProgressByBudgetId(BudgetFlowStateVO cond) {
        Budget budget = budgetService.getBudgetById(cond.getBudgetId());
        if (cond.getYear()==null){
            cond.setYear(cond.getImportDate());
        }
        String serialNum = budget.getBudgetNum()+cond.getYear();
        cond.setSerialNum(serialNum);
        List<Integer> states = new ArrayList<>();
        // 如果无state或state = 31则state = 31，32
        if (cond.getState() == null || cond.getState().equals(FlowState.APPROVED.value())) {
            states.add(FlowState.APPROVED.value());
            states.add(FlowState.ADJUST_APPROVED.value());
        }
        // 如果state = 10 则为 （10，30，40）
        else if (cond.getState().equals(FlowState.NOT_APPROVED.value())) {
            states.add(FlowState.NOT_APPROVED.value());
            states.add(FlowState.APPROVE_REJECTED.value());
            states.add(FlowState.IN_ADJUST.value());
        }
        // 如果state = 20 则为（20，50）
        else if (cond.getState().equals(FlowState.APPROVING.value())) {
            states.add(FlowState.APPROVING.value());
            states.add(FlowState.APPROVED_ADJUST.value());
        }
        // 判断是否有归口关系
        Integer unitId = cond.getUnitId();
        Long budgetId = cond.getBudgetId();
        String year = cond.getYear();
        Integer underUnitId = budgetBelongMapper.selectUnderUnitIdByNum(serialNum, unitId);
        if (underUnitId == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NO_DATA);
        }
        cond.setUnitId(underUnitId);
        List<BudgetProjectAmountVO> progress = budgetProjectMapper.selectProgressByBudget(
                unitId, serialNum, year, states);
        // 如果无state或state = 31则state = 31，32
        // 判断state 是否为 31或者32
        if (cond.getState() == null || cond.getState().equals(FlowState.APPROVED.value())) {
            List<BudgetProjectAmountVO> progressByBudgets = budgetProjectAgileService.getProgressByBudget(
                    unitId, budgetId, year);
            progress.addAll(progressByBudgets);
        }
        // 新建对象用来存储合计
        BudgetProjectAmountVO pro = new BudgetProjectAmountVO();
        BigDecimal bigDecimal = new BigDecimal(0);
        BigDecimal amount = new BigDecimal(0);
        // 遍历判断是否有立项值，如果有则添加到list，返回
        if (progress.size() != 0) {
            for (BudgetProjectAmountVO budgetProjectReqVO : progress) {
                if (budgetProjectReqVO != null) {
                    if (budgetProjectReqVO.getAmount() == null) {
                        amount = new BigDecimal(0);
                    } else {
                        amount = budgetProjectReqVO.getAmount();
                    }
                }
                bigDecimal = bigDecimal.add(amount);
            }
        }
        pro.setAmount(bigDecimal);
        pro.setId(0L);
        pro.setProjectItemName("合计");
        progress.add(pro);
        return progress;
    }

    @Override
    public List<BudgetProgressExportVO> getExportList(BudgetProgressReqVO cond) {
        List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
        List<BudgetProgressResVO> progressList = budgetProjectMapper.selectProgress(cond, unitIds);

        // 计算合计
        BigDecimal balanceTotal = new BigDecimal("0.00");
        BigDecimal amountTotal = new BigDecimal("0.00");
        for (BudgetProgressResVO vo : progressList) {
            balanceTotal = balanceTotal.add(vo.getBudgetBalance());
            amountTotal = amountTotal.add(vo.getAmount());
        }
        progressList.add(0, BudgetProgressResVO.builder()
                .budgetItemName("合计")
                .budgetBalance(balanceTotal)
                .amount(balanceTotal.compareTo(BigDecimal.ZERO) == 0 ? null : amountTotal)
                .surplus(calculateSurplus(amountTotal, balanceTotal))
                .percentage(calculatePercentage(amountTotal, balanceTotal))
                .build());

        // 计算百分比和结余
        for (BudgetProgressResVO vo : progressList) {
            if (vo.getBudgetBalance().compareTo(BigDecimal.ZERO) == 0) {
                vo.setAmount(null);
            }
            vo.setPercentage(calculatePercentage(vo.getAmount(), vo.getBudgetBalance()));
            vo.setSurplus(calculateSurplus(vo.getAmount(), vo.getBudgetBalance()));
        }

        // 转exportVO
        List<BudgetProgressExportVO> exports = BeanConverter.copyBeanList(progressList, BudgetProgressExportVO.class);
        for (int i = 0; i < exports.size(); i++) {
            exports.get(i).setLineNumber(i + 1);
        }
        return exports;
    }

    @Override
    public BudgetProgressDeclareVO getProgressByState(BudgetProgressReqVO cond) {
        cond.setUnitId(109);
        Budget budget = budgetService.getBudgetById(cond.getBudgetId());
        String serialNum = budget.getBudgetNum()+cond.getYear();
        cond.setSerialNum(serialNum);
        if (cond.getBudgetId() == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NO_DATA);
        }
        BudgetProgressDeclareVO declareVO = new BudgetProgressDeclareVO();
        // 用来存储不同状态的进度
        List<BudgetProgressResVO> list = new ArrayList<>();
        // 校验是否可以发起该项目类型下签报
        BudgetData budgetData = budgetDataService.getDataByUnitIdAndBudgetIdByNum(cond.getUnitId(), cond.getSerialNum());
        if (budgetData == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NO_DATA);
        }
        // 如果归口于其他单位则已归口单位统计
        Integer underUnitId = budgetBelongMapper.selectUnderUnitIdByNum(cond.getSerialNum(), cond.getUnitId());
        if (underUnitId == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NO_DATA);
        }
        cond.setUnitId(underUnitId);
        BudgetData underUnitData = budgetDataService.getDataByUnitIdAndBudgetIdByNum(cond.getUnitId(), cond.getSerialNum());
        // 未审批的balance
        cond.setState(FlowState.NOT_APPROVED.value());
        BudgetProgressResVO byState1 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState1);
        // 调整中
        cond.setState(FlowState.IN_ADJUST.value());
        BudgetProgressResVO byState6 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState6);
        // 被拒绝
        cond.setState(FlowState.APPROVE_REJECTED.value());
        BudgetProgressResVO byState7 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState7);
        // 审批中的balance
        cond.setState(FlowState.APPROVING.value());
        BudgetProgressResVO byState2 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState2);
        // 审批调整中
        cond.setState(FlowState.APPROVED_ADJUST.value());
        BudgetProgressResVO byState5 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState5);
        //  审批通过的balance
        cond.setState(FlowState.APPROVED.value());
        BudgetProgressResVO byState3 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState3);
        // 调整审批通过
        cond.setState(FlowState.ADJUST_APPROVED.value());
        BudgetProgressResVO byState4 = budgetProjectMapper.selectProgressByState(cond);
        // 日常性费用签报
        BudgetProgressResVO byState8 = budgetProjectAgileService.getProgressByState(cond);
        list.add(byState4);
        if (underUnitData.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            byState1.setAmount(null);
            byState2.setAmount(null);
            byState3.setAmount(null);
            byState4.setAmount(null);
            byState5.setAmount(null);
            byState6.setAmount(null);
            byState7.setAmount(null);
            byState8.setAmount(null);
        }
        //  整合在一起
        for (BudgetProgressResVO budgetProgressResVO : list) {
            if (budgetProgressResVO != null) {
                BeanUtils.copyProperties(budgetProgressResVO, declareVO);
                declareVO.setRemark(budgetProgressResVO.getRemark());
            }
        }
        if (declareVO.getBudgetId() == null) {
            return declareVO;
        }
        // 审批通过（3种情况）
        BigDecimal bigDecimal1 = byState4.getAmount() == null ? new BigDecimal(0) : byState4.getAmount();
        BigDecimal bigDecimal2 = byState3.getAmount() == null ? new BigDecimal(0) : byState3.getAmount();
        BigDecimal bigDecimal = byState8.getAmount() == null ? new BigDecimal(0) : byState8.getAmount();
        declareVO.setDeclared(bigDecimal1.add(bigDecimal2).add(bigDecimal));
        // 审批中（2种情况）
        BigDecimal bigDecimal3 = byState2.getAmount() == null ? new BigDecimal(0) : byState2.getAmount();
        BigDecimal bigDecimal4 = byState5.getAmount() == null ? new BigDecimal(0) : byState5.getAmount();
        declareVO.setDeclaring(bigDecimal3.add(bigDecimal4));
        // 未审批（3种情况）
        BigDecimal bigDecimal5 = byState1.getAmount() == null ? new BigDecimal(0) : byState1.getAmount();
        BigDecimal bigDecimal6 = byState6.getAmount() == null ? new BigDecimal(0) : byState6.getAmount();
        BigDecimal bigDecimal7 = byState6.getAmount() == null ? new BigDecimal(0) : byState7.getAmount();
        declareVO.setDeclaration(bigDecimal5.add(bigDecimal6).add(bigDecimal7));
        // 结余
        BigDecimal surplus = calculateSurplus(bigDecimal2.add(bigDecimal3)
                .add(bigDecimal1.add(bigDecimal4).add(bigDecimal)), underUnitData.getBalance());
        declareVO.setSurplus(surplus);
        // 审批通过百分比
        declareVO.setDeclaredPer(calculatePercentage(declareVO.getDeclared(), underUnitData.getBalance()));
        // 审批中百分比
        declareVO.setDeclaringPer(calculatePercentage(declareVO.getDeclaring(), underUnitData.getBalance()));
        // 未开始审批百分比
        declareVO.setDeclarationPer(calculatePercentage(declareVO.getDeclaration(), underUnitData.getBalance()));
        return declareVO;
    }

    @Override
    public BudgetProjectAllVO getProjectInfo(String projectNum) {
        BudgetProjectAllVO detailVO = new BudgetProjectAllVO();
        // 日常性费用签报的项目编号为18位
        if (projectNum.length() == 18) {
            BudgetProjectAgile infoByNum = budgetProjectAgileService.getInfoByNum(projectNum);
            BeanUtils.copyProperties(infoByNum, detailVO);
            detailVO.setVatRate(String.valueOf(infoByNum.getVatRate()));
        }
        // 预算项目签报的项目编号为16位
        else {
            BudgetProject infoByNum = budgetProjectMapper.selectInfoByNum(projectNum);
            BeanUtils.copyProperties(infoByNum, detailVO);
            detailVO.setStartTime(DateUtil.dateToString(infoByNum.getStartTime()));
            detailVO.setEndTime(DateUtil.dateToString(infoByNum.getEndTime()));
            detailVO.setVatRate(String.valueOf(infoByNum.getVatRate()));
        }
        return detailVO;
    }


    /**
     * 计算百分比
     */
    private BigDecimal calculatePercentage(BigDecimal dividend, BigDecimal divisor) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } else if (dividend.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal bigDecimal = new BigDecimal(100);
        return dividend.divide(divisor, 6, BigDecimal.ROUND_HALF_UP).multiply(bigDecimal);
    }

    /**
     * 计算结余
     */
    private BigDecimal calculateSurplus(BigDecimal dividend, BigDecimal divisor) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } else {
            return divisor.subtract(dividend);
        }
    }
}