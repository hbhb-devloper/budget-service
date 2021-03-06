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

        // ???????????????????????????
        Map<String, List<BudgetProgressResVO>> progressMap = progressList.stream()
                .collect(Collectors.groupingBy(BudgetProgressResVO::getBudgetItemName));

        // ????????????????????????
        BigDecimal balanceTotal = new BigDecimal(0);
        BigDecimal amountTotal = new BigDecimal(0);

        for (Map.Entry<String, List<BudgetProgressResVO>> entry : progressMap.entrySet()) {
            // ????????????
            BudgetProgressVO vo = BudgetProgressVO.builder()
                    .id(new Random().nextLong())
                    .itemName(entry.getKey())
                    .hasChildren(true)
                    .build();

            // ????????????
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
                    // ????????????
                    balanceTotal = balanceTotal.add(child.getBudgetBalance());
                    amountTotal = amountTotal.add(child.getAmount());
                }
                vo.setChildren(children);
            }
            result.add(vo);
        }

        // ????????????
        result.add(0, BudgetProgressVO.builder()
                .itemName("??????")
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
        // ?????????state???state = 31???state = 31???32
        if (cond.getState() == null || cond.getState().equals(FlowState.APPROVED.value())) {
            states.add(FlowState.APPROVED.value());
            states.add(FlowState.ADJUST_APPROVED.value());
        }
        // ??????state = 10 ?????? ???10???30???40???
        else if (cond.getState().equals(FlowState.NOT_APPROVED.value())) {
            states.add(FlowState.NOT_APPROVED.value());
            states.add(FlowState.APPROVE_REJECTED.value());
            states.add(FlowState.IN_ADJUST.value());
        }
        // ??????state = 20 ?????????20???50???
        else if (cond.getState().equals(FlowState.APPROVING.value())) {
            states.add(FlowState.APPROVING.value());
            states.add(FlowState.APPROVED_ADJUST.value());
        }
        // ???????????????????????????
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
        // ?????????state???state = 31???state = 31???32
        // ??????state ????????? 31??????32
        if (cond.getState() == null || cond.getState().equals(FlowState.APPROVED.value())) {
            List<BudgetProjectAmountVO> progressByBudgets = budgetProjectAgileService.getProgressByBudget(
                    unitId, budgetId, year);
            progress.addAll(progressByBudgets);
        }
        // ??????????????????????????????
        BudgetProjectAmountVO pro = new BudgetProjectAmountVO();
        BigDecimal bigDecimal = new BigDecimal(0);
        BigDecimal amount = new BigDecimal(0);
        // ??????????????????????????????????????????????????????list?????????
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
        pro.setProjectItemName("??????");
        progress.add(pro);
        return progress;
    }

    @Override
    public List<BudgetProgressExportVO> getExportList(BudgetProgressReqVO cond) {
        List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
        List<BudgetProgressResVO> progressList = budgetProjectMapper.selectProgress(cond, unitIds);

        // ????????????
        BigDecimal balanceTotal = new BigDecimal("0.00");
        BigDecimal amountTotal = new BigDecimal("0.00");
        for (BudgetProgressResVO vo : progressList) {
            balanceTotal = balanceTotal.add(vo.getBudgetBalance());
            amountTotal = amountTotal.add(vo.getAmount());
        }
        progressList.add(0, BudgetProgressResVO.builder()
                .budgetItemName("??????")
                .budgetBalance(balanceTotal)
                .amount(balanceTotal.compareTo(BigDecimal.ZERO) == 0 ? null : amountTotal)
                .surplus(calculateSurplus(amountTotal, balanceTotal))
                .percentage(calculatePercentage(amountTotal, balanceTotal))
                .build());

        // ????????????????????????
        for (BudgetProgressResVO vo : progressList) {
            if (vo.getBudgetBalance().compareTo(BigDecimal.ZERO) == 0) {
                vo.setAmount(null);
            }
            vo.setPercentage(calculatePercentage(vo.getAmount(), vo.getBudgetBalance()));
            vo.setSurplus(calculateSurplus(vo.getAmount(), vo.getBudgetBalance()));
        }

        // ???exportVO
        List<BudgetProgressExportVO> exports = BeanConverter.copyBeanList(progressList, BudgetProgressExportVO.class);
        for (int i = 0; i < exports.size(); i++) {
            exports.get(i).setLineNumber(i + 1);
        }
        return exports;
    }

    @Override
    public BudgetProgressDeclareVO getProgressByState(BudgetProgressReqVO cond) {
        Budget budget = budgetService.getBudgetById(cond.getBudgetId());
        String serialNum = budget.getBudgetNum()+cond.getYear();
        cond.setSerialNum(serialNum);
        if (cond.getBudgetId() == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NO_DATA);
        }
        BudgetProgressDeclareVO declareVO = new BudgetProgressDeclareVO();
        // ?????????????????????????????????
        List<BudgetProgressResVO> list = new ArrayList<>();
        // ????????????????????????????????????????????????
        BudgetData budgetData = budgetDataService.getDataByUnitIdAndBudgetIdByNum(cond.getUnitId(), cond.getSerialNum());
        if (budgetData == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NO_DATA);
        }
        // ???????????????????????????????????????????????????
        Integer underUnitId = budgetBelongMapper.selectUnderUnitIdByNum(cond.getSerialNum(), cond.getUnitId());
        cond.setUnitId(underUnitId);
        BudgetData underUnitData = budgetDataService.getDataByUnitIdAndBudgetIdByNum(cond.getUnitId(), cond.getSerialNum());
        // ????????????balance
        cond.setState(FlowState.NOT_APPROVED.value());
        BudgetProgressResVO byState1 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState1);
        // ?????????
        cond.setState(FlowState.IN_ADJUST.value());
        BudgetProgressResVO byState6 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState6);
        // ?????????
        cond.setState(FlowState.APPROVE_REJECTED.value());
        BudgetProgressResVO byState7 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState7);
        // ????????????balance
        cond.setState(FlowState.APPROVING.value());
        BudgetProgressResVO byState2 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState2);
        // ???????????????
        cond.setState(FlowState.APPROVED_ADJUST.value());
        BudgetProgressResVO byState5 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState5);
        //  ???????????????balance
        cond.setState(FlowState.APPROVED.value());
        BudgetProgressResVO byState3 = budgetProjectMapper.selectProgressByState(cond);
        list.add(byState3);
        // ??????????????????
        cond.setState(FlowState.ADJUST_APPROVED.value());
        BudgetProgressResVO byState4 = budgetProjectMapper.selectProgressByState(cond);
        // ?????????????????????
        BudgetProgressResVO byState8 = budgetProjectAgileService.getProgressByState(cond);
        list.add(byState4);
        if (underUnitData.getBalance()==null){
            underUnitData.setBudgetId(0L);
        }
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
        //  ???????????????
        for (BudgetProgressResVO budgetProgressResVO : list) {
            if (budgetProgressResVO != null) {
                BeanUtils.copyProperties(budgetProgressResVO, declareVO);
                declareVO.setRemark(budgetProgressResVO.getRemark());
            }
        }
        if (declareVO.getBudgetId() == null) {
            return declareVO;
        }
        // ???????????????3????????????
        BigDecimal bigDecimal1 = byState4.getAmount() == null ? new BigDecimal(0) : byState4.getAmount();
        BigDecimal bigDecimal2 = byState3.getAmount() == null ? new BigDecimal(0) : byState3.getAmount();
        BigDecimal bigDecimal = byState8.getAmount() == null ? new BigDecimal(0) : byState8.getAmount();
        declareVO.setDeclared(bigDecimal1.add(bigDecimal2).add(bigDecimal));
        // ????????????2????????????
        BigDecimal bigDecimal3 = byState2.getAmount() == null ? new BigDecimal(0) : byState2.getAmount();
        BigDecimal bigDecimal4 = byState5.getAmount() == null ? new BigDecimal(0) : byState5.getAmount();
        declareVO.setDeclaring(bigDecimal3.add(bigDecimal4));
        // ????????????3????????????
        BigDecimal bigDecimal5 = byState1.getAmount() == null ? new BigDecimal(0) : byState1.getAmount();
        BigDecimal bigDecimal6 = byState6.getAmount() == null ? new BigDecimal(0) : byState6.getAmount();
        BigDecimal bigDecimal7 = byState6.getAmount() == null ? new BigDecimal(0) : byState7.getAmount();
        declareVO.setDeclaration(bigDecimal5.add(bigDecimal6).add(bigDecimal7));
        // ??????
        BigDecimal surplus = calculateSurplus(bigDecimal2.add(bigDecimal3)
                .add(bigDecimal1.add(bigDecimal4).add(bigDecimal)), underUnitData.getBalance());
        declareVO.setSurplus(surplus);
        // ?????????????????????
        declareVO.setDeclaredPer(calculatePercentage(declareVO.getDeclared(), underUnitData.getBalance()));
        // ??????????????????
        declareVO.setDeclaringPer(calculatePercentage(declareVO.getDeclaring(), underUnitData.getBalance()));
        // ????????????????????????
        declareVO.setDeclarationPer(calculatePercentage(declareVO.getDeclaration(), underUnitData.getBalance()));
        return declareVO;
    }

    @Override
    public BudgetProjectAllVO getProjectInfo(String projectNum) {
        BudgetProjectAllVO detailVO = new BudgetProjectAllVO();
        // ???????????????????????????????????????18???
        if (projectNum.length() == 18) {
            BudgetProjectAgile infoByNum = budgetProjectAgileService.getInfoByNum(projectNum);
            BeanUtils.copyProperties(infoByNum, detailVO);
            detailVO.setVatRate(String.valueOf(infoByNum.getVatRate()));
        }
        // ????????????????????????????????????16???
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
     * ???????????????
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
     * ????????????
     */
    private BigDecimal calculateSurplus(BigDecimal dividend, BigDecimal divisor) {
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } else {
            return divisor.subtract(dividend);
        }
    }
}