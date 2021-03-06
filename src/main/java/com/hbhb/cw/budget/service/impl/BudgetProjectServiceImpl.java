package com.hbhb.cw.budget.service.impl;

import com.google.common.collect.Lists;

import com.github.pagehelper.PageHelper;
import com.hbhb.core.bean.BeanConverter;
import com.hbhb.core.utils.DateUtil;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.enums.EnableCond;
import com.hbhb.cw.budget.enums.OperationType;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetBelongMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectApprovedMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectFileMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectFlowApprovedMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectSplitApprovedMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectSplitMapper;
import com.hbhb.cw.budget.model.Budget;
import com.hbhb.cw.budget.model.BudgetData;
import com.hbhb.cw.budget.model.BudgetProject;
import com.hbhb.cw.budget.model.BudgetProjectApproved;
import com.hbhb.cw.budget.model.BudgetProjectFile;
import com.hbhb.cw.budget.model.BudgetProjectFlow;
import com.hbhb.cw.budget.model.BudgetProjectFlowApproved;
import com.hbhb.cw.budget.model.BudgetProjectFlowHistory;
import com.hbhb.cw.budget.model.BudgetProjectSplit;
import com.hbhb.cw.budget.model.BudgetProjectSplitApproved;
import com.hbhb.cw.budget.model.Page;
import com.hbhb.cw.budget.rpc.DictApiExp;
import com.hbhb.cw.budget.rpc.FileApiExp;
import com.hbhb.cw.budget.rpc.FlowApiExp;
import com.hbhb.cw.budget.rpc.FlowNodeApiExp;
import com.hbhb.cw.budget.rpc.FlowNodePropApiExp;
import com.hbhb.cw.budget.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.budget.rpc.UnitApiExp;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetDataService;
import com.hbhb.cw.budget.service.BudgetProgressService;
import com.hbhb.cw.budget.service.BudgetProjectFlowHistoryService;
import com.hbhb.cw.budget.service.BudgetProjectFlowService;
import com.hbhb.cw.budget.service.BudgetProjectNoticeService;
import com.hbhb.cw.budget.service.BudgetProjectService;
import com.hbhb.cw.budget.service.BudgetProjectSplitService;
import com.hbhb.cw.budget.service.BudgetService;
import com.hbhb.cw.budget.web.vo.BudgetProgressDeclareVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProgressResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailExportReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFileExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFileVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectInitVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectReqVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectResVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitExportVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectSplitVO;
import com.hbhb.cw.budget.web.vo.BudgetReqVO;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeState;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeTemp;
import com.hbhb.cw.flowcenter.enums.FlowState;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowNodePropVO;
import com.hbhb.cw.systemcenter.enums.DictCode;
import com.hbhb.cw.systemcenter.enums.TypeCode;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.web.util.FileUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BudgetProjectServiceImpl implements BudgetProjectService {

    @Resource
    private BudgetBelongMapper budgetBelongMapper;
    @Resource
    private BudgetProjectMapper budgetProjectMapper;
    @Resource
    private BudgetProjectFileMapper budgetProjectFileMapper;
    @Resource
    private BudgetProjectSplitMapper budgetProjectSplitMapper;
    @Resource
    private BudgetProjectApprovedMapper budgetProjectApprovedMapper;
    @Resource
    private BudgetProjectFlowApprovedMapper budgetProjectFlowApprovedMapper;
    @Resource
    private BudgetProjectSplitApprovedMapper budgetProjectSplitApprovedMapper;
    @Resource
    private BudgetService budgetService;
    @Resource
    private BudgetDataService budgetDataService;
    @Resource
    private BudgetProgressService budgetProgressService;
    @Resource
    private BudgetProjectFlowService budgetProjectFlowService;
    @Resource
    private BudgetProjectSplitService budgetProjectSplitService;
    @Resource
    private BudgetProjectNoticeService budgetProjectNoticeService;
    @Resource
    private BudgetProjectFlowHistoryService budgetProjectFlowHistoryService;
    @Resource
    private FlowNodeApiExp nodeApi;
    @Resource
    private FlowRoleUserApiExp roleUserApi;
    @Resource
    private FlowApiExp flowApi;
    @Resource
    private FlowNodePropApiExp propApi;


    @Resource
    private UserApiExp userApi;
    @Resource
    private UnitApiExp unitApi;
    @Resource
    private DictApiExp dictApi;
    @Resource
    private FileApiExp fileApi;

    @Override
    public boolean getBudgetApprovedState(Integer id) {
        BudgetProjectApproved budgetProjectApproved = budgetProjectApprovedMapper.selectByProjectId(id);
        BudgetProject project = budgetProjectMapper.selectByPrimaryKey(id);
        return budgetProjectApproved != null && !FlowState.APPROVED.value().equals(project.getState());
    }

    @Override
    public Page<BudgetProjectResVO> getBudgetProjectPage(Integer pageNum, Integer pageSize, boolean pageable,
                                                         BudgetProjectReqVO cond) {
        // ????????????????????????
        List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
        // ????????????map
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // ????????????????????????
        List<DictVO> stateList = dictApi.getDict(TypeCode.BUDGET.value(), DictCode.BUDGET_PROJECT_STATUS.value());
        Map<String, String> stateMap = stateList.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));

        if (pageable) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<BudgetProjectResVO> list = budgetProjectMapper.selectListByCond(cond, unitIds);
        int count = budgetProjectMapper.countListByCond(cond, unitIds);

        // ????????????
        list.forEach(item -> {
            item.setStateLabel(stateMap.get(item.getState().toString()));
            item.setProjectTypeName(item.getBudgetNum() + "_" + item.getProjectTypeName());
            item.setUnitName(unitMap.get(item.getUnitId()));
        });
        return new Page<>(list, (long) count);
    }

    @Override
    public List<BudgetProgressResVO> getBudgetProgressByBudgetData(BudgetReqVO cond) {
        BudgetReqVO budgetReqVO = BeanConverter.copyBean(cond, BudgetReqVO.class);
        if (UnitEnum.HANGZHOU.value().equals(cond.getUnitId())){
            budgetReqVO.setUnitId(null);
        }
        return budgetProjectMapper.selectBudgetProgressByBudgetData(cond);
    }

    @Override
    public BudgetProjectDetailVO getBudgetProject(Long id) {
        BudgetProjectDetailVO detailVO = budgetProjectMapper.selectProjectById(id);
        List<DictVO> rateList = dictApi.getDict(
                TypeCode.BUDGET.value(), DictCode.BUDGET_PROJECT_VAT_RATES.value());
        Map<String, String> rateMap = rateList.stream().collect(
                Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        detailVO.setVatRate(rateMap.get(detailVO.getVatRate()));
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addSaveBudgetProject(BudgetProjectDetailVO vo, UserInfo user) {
        // ????????????????????????????????????????????????
        BudgetData budgetData = budgetDataService
                .getDataByUnitIdAndBudgetId(user.getUnitId(), vo.getBudgetId());
        if (budgetData == null) {
            throw new BudgetException(BudgetErrorCode.BUDGET_NOT_ADD_PROJECT);
        }
        BudgetProject budgetProject = new BudgetProject();
        BeanUtils.copyProperties(vo, budgetProject);
        budgetProject.setBudgetId(vo.getBudgetId());
        budgetProject.setUnitId(Long.valueOf(user.getUnitId()));
        // ????????????=????????????+????????????+????????????+4???????????????
        String budgetNum = budgetService.getNumByBudgetId(vo.getBudgetId());
        // ????????????
        Unit unit = unitApi.getUnitInfo(user.getUnitId());
        String abbr = unit.getAbbr();
        // ????????????
        String date = (DateUtil.dateToStringY(new Date())).substring(2, 4);
        // ??????????????????
        Integer count = budgetProjectMapper.selectTypeCountByBudgetId(vo.getBudgetId(), user.getUnitId(), DateUtil.dateToStringY(new Date()));
        if (count == null) {
            count = 0;
            String counts = String.format("%0" + 4 + "d", (count + 1));
            budgetProject.setProjectNum(budgetNum + abbr + date + counts);
        } else {
            String counts = String.format("%0" + 4 + "d", (count + 1));
            budgetProject.setProjectNum(budgetNum + abbr + date + counts);
        }
        // ?????? ??????????????????.amount?????????????????????.availableAmount?????????????????????.taxIncludeAmount?????????????????????.cost???
        // ????????????.vatRate?????????????????????.vatAmount,??????(String-->BigDecimal)
        budgetProject.setSerialNum(budgetNum + DateUtil.dateToStringY(new Date()));
        budgetProject.setAmount(new BigDecimal(vo.getAmount()));
        budgetProject.setAvailableAmount(new BigDecimal(vo.getAvailableAmount()));
        budgetProject.setTaxIncludeAmount(new BigDecimal(vo.getTaxIncludeAmount()));
        budgetProject.setCost(new BigDecimal(vo.getCost()));
        budgetProject.setVatRate(new BigDecimal(vo.getVatRate()));
        budgetProject.setVatAmount(new BigDecimal(vo.getVatAmount()));
        // ?????? ??????????????????????????????????????????????????????<String-->Date>
        String startTime = vo.getStartTime();
        String endTime = vo.getEndTime();
        if (DateUtil.isExpiredYMD(startTime, endTime)) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_TIME_ERROR);
        }
        budgetProject.setStartTime(DateUtil.string2DateYMD(startTime));
        budgetProject.setEndTime(DateUtil.string2DateYMD(endTime));
        budgetProject.setCreateTime(new Date());
        // ???????????????
        budgetProject.setCreateBy(user.getNickName());
        // ????????????????????????-10???????????????
        budgetProject.setState(FlowState.NOT_APPROVED.value());
        budgetProjectMapper.insertSelective(budgetProject);
        List<BudgetProjectFileVO> budgetProjectFileVOS = vo.getFiles();
        if (budgetProjectFileVOS.size() != 0) {
            List<BudgetProjectFile> bPFs = new ArrayList<>();
            for (BudgetProjectFileVO bpfVo : budgetProjectFileVOS) {
                BudgetProjectFile budgetProjectFile = new BudgetProjectFile();
                budgetProjectFile.setAuthor(user.getNickName());
                budgetProjectFile.setCreateTime((new Date()));
                budgetProjectFile.setIsApproved(bpfVo.getIsApproved());
                budgetProjectFile.setRequired(bpfVo.getRequired());
                budgetProjectFile.setProjectId(Long.valueOf(budgetProject.getId()));
                budgetProjectFile.setFileId(bpfVo.getFileId());
                bPFs.add(budgetProjectFile);
            }
            budgetProjectFileMapper.insertBudgetProjectFiles(bPFs);
        }
        return budgetProject.getId();
    }

    @Override
    public void deleteBudgetProject(Integer id, UserInfo user) {
        // ?????????????????????????????????????????????
        BudgetProject budgetProject = budgetProjectMapper.selectByPrimaryKey(id);
        if (!budgetProject.getCreateBy().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        budgetProjectMapper.updateDeleteFlagById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBudgetProject(BudgetProjectDetailVO budgetProjectDetailVO, UserInfo user) {
        // ?????????????????????????????????????????????
        BudgetProject bp = budgetProjectMapper
                .selectByPrimaryKey(Math.toIntExact(budgetProjectDetailVO.getId()));
        if (!bp.getCreateBy().equals(user.getNickName())) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_INITIATOR_ERROR);
        }
        BudgetProject budgetProject = new BudgetProject();
        BeanUtils.copyProperties(budgetProjectDetailVO, budgetProject);
        budgetProject.setId(Math.toIntExact(budgetProjectDetailVO.getId()));
        // ?????? ??????????????????.amount?????????????????????.availableAmount?????????????????????.taxIncludeAmount?????????????????????.cost???
        // ????????????.vatRate?????????????????????.vatAmount,??????(String-->BigDecimal)
        budgetProject.setAmount(new BigDecimal(budgetProjectDetailVO.getAmount()));
        budgetProject
                .setAvailableAmount(new BigDecimal(budgetProjectDetailVO.getAvailableAmount()));
        budgetProject
                .setTaxIncludeAmount(new BigDecimal(budgetProjectDetailVO.getTaxIncludeAmount()));
        budgetProject.setCost(new BigDecimal(budgetProjectDetailVO.getCost()));
        budgetProject.setVatRate(new BigDecimal(budgetProjectDetailVO.getVatRate()));
        budgetProject.setVatAmount(new BigDecimal(budgetProjectDetailVO.getVatAmount()));
        // ?????? ??????????????????????????????????????????????????????<String-->Date>
        String startTime = budgetProjectDetailVO.getStartTime();
        String endTime = budgetProjectDetailVO.getEndTime();
        if (DateUtil.isExpiredYMD(startTime, endTime)) {
            throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_TIME_ERROR);
        }
        budgetProject.setStartTime(DateUtil.string2DateYMD(startTime));
        budgetProject.setEndTime(DateUtil.string2DateYMD(endTime));

        // ???????????????????????????
        if (budgetProjectDetailVO.getState() != null &&
                budgetProjectDetailVO.getState().equals(FlowState.APPROVED.value())) {
            budgetProject.setState(FlowState.IN_ADJUST.value());
        }
        //??????
        budgetProjectMapper.updateByPrimaryKeySelective(budgetProject);
        //??????????????????
        List<Long> files = budgetProjectFileMapper.selectIdListByProjectId(budgetProjectDetailVO.getId());
        List<BudgetProjectFileVO> budgetProjectFileVOS = budgetProjectDetailVO.getFiles();
        if (budgetProjectFileVOS.size() != 0) {
            List<BudgetProjectFile> bpfs = new ArrayList<>();
            for (BudgetProjectFileVO bpfVo : budgetProjectFileVOS) {
                // ?????????????????????????????????
                if (!files.contains(bpfVo.getFileId())) {
                    BudgetProjectFile budgetProjectFile = new BudgetProjectFile();
                    budgetProjectFile.setAuthor(user.getNickName());
                    budgetProjectFile.setCreateTime((new Date()));
                    budgetProjectFile.setIsApproved(bpfVo.getIsApproved());
                    budgetProjectFile.setRequired(bpfVo.getRequired());
                    budgetProjectFile.setProjectId(Long.valueOf(budgetProject.getId()));
                    budgetProjectFile.setFileId(bpfVo.getFileId());
                    bpfs.add(budgetProjectFile);
                }
            }
            if (bpfs.size() != 0) {
                budgetProjectFileMapper.insertBudgetProjectFiles(bpfs);
            }
        }
    }

    @Override
    public void updateState(Integer projectId, Integer state) {
        budgetProjectMapper.updateByPrimaryKeySelective(BudgetProject.builder()
                .id(projectId)
                .state(state)
                .updateTime(new Date())
                .build());
    }

    @Override
    public boolean existByBudgetId(Long budgetId) {
        return budgetProjectMapper.countByBudgetId(budgetId) > 0;
    }

    @Override
    public List<BudgetProjectExportVO> getExportList(BudgetProjectReqVO cond) {
        Page<BudgetProjectResVO> page = this.getBudgetProjectPage(
                null, null, false, cond);
        return Optional.ofNullable(page.getList())
                .orElse(new ArrayList<>())
                .stream()
                .map(vo -> BeanConverter.convert(vo, BudgetProjectExportVO.class))
                .collect(Collectors.toList());
    }

    /**
     * 1.??????????????????????????? 2.????????????????????????????????????id 3.?????????????????????????????? 4.?????????????????? 5.???????????? 6.??????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprove(BudgetProjectInitVO initVO) {
        Integer projectId = initVO.getProjectId();
        // 1.???????????????????????????
        // 1-1.??????????????????????????? ??????????????????????????????
        List<BudgetProjectSplitVO> splitList = budgetProjectSplitService
                .getBudgetProjectSplitList(projectId);
        if (splitList == null || splitList.size() == 0) {
            throw new BudgetException(BudgetErrorCode.CATEGORY_BUDGET_NOT_WRITE);
        }
        // ?????????????????????????????????
        BudgetProject budgetProject = budgetProjectMapper
                .selectBudgetProjectById(Long.valueOf(projectId));
        // 1-2.???????????????????????????????????????????????????????????????
        UserInfo user = userApi.getUserInfoById(initVO.getUserId());
        if (!user.getNickName().equals(budgetProject.getCreateBy())) {
            throw new BudgetException(BudgetErrorCode.LACK_OF_FLOW_ROLE);
        }
        // 1-3.????????????id???????????????????????????????????????????????????????????????????????????
        int startTime = Integer.parseInt(DateUtil.dateToStringY(budgetProject.getStartTime()));
        int endTime = Integer.parseInt(DateUtil.dateToStringY(budgetProject.getEndTime()));
        List<Integer> years = new ArrayList<>();
        // ??????????????????????????????
        if (endTime - startTime != 0) {
            for (BudgetProjectSplitVO vo : splitList) {
                if (vo.getYears() == null || "".equals(vo.getYears())) {
                    throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_SPLIT_YEAR_ERROR);
                }
                years.add(Integer.valueOf(vo.getYears()));
            }
            List<Integer> year = new ArrayList<>();
            for (int i = startTime; i <= endTime; i++) {
                year.add(i);
            }
            if (!year.equals(years)) {
                throw new BudgetException(BudgetErrorCode.BUDGET_PROJECT_SPLIT_YEAR_ERROR);
            }
        }
        // ??????????????????????????????
        Long budgetId = budgetProject.getBudgetId();
        Long unitId = budgetProject.getUnitId();
        // 1-4.??????????????????,?????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ????????????id?????????????????????
        Budget budget = budgetService.getBudgetById(budgetId);
        // ??????????????????????????????????????????id
        Integer underUnitId = budgetBelongMapper
                .selectUnderUnitId(budgetId, Math.toIntExact(unitId));
        String newUnitId;
        if (underUnitId == null) {
            throw new BudgetException(BudgetErrorCode.COST_EXCEED_SURPLUS);
        }
        // ?????????????????????????????????
        if (underUnitId != Math.toIntExact(unitId)) {
            // ????????????????????????????????????????????????
            newUnitId = underUnitId.toString();
        } else {
            newUnitId = unitId.toString();
        }
        BudgetProgressDeclareVO progressByState = budgetProgressService
                .getProgressByState(BudgetProgressReqVO.builder()
                        .budgetId(budgetId)
                        .unitId(Integer.valueOf(newUnitId))
                        .year(budget.getImportDate())
                        .build());
        // ?????????????????????0
        progressByState.setSurplus(progressByState.getSurplus() == null ? new BigDecimal("0.0") : progressByState.getSurplus());
        // ???????????????????????????
        if (budgetProject.getCost().compareTo(progressByState.getSurplus()) > 0) {
            throw new BudgetException(BudgetErrorCode.COST_EXCEED_SURPLUS);
        }

        // 2.????????????????????????????????????id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId(), unitId, underUnitId);

        // ??????i??????id????????????????????????
        // ????????????id????????????????????????
        List<FlowNodePropVO> flowProps = propApi.getNodeProps(flowId);
        // 3.??????????????????????????????
        boolean hasAccess = hasAccess2Approve(flowProps, initVO.getUserId(),
                Math.toIntExact(unitId));
        if (!hasAccess) {
            throw new BudgetException(BudgetErrorCode.LACK_OF_FLOW_ROLE);
        }
        // 4.??????????????????
        syncBudgetProjectFlow(flowProps, projectId, underUnitId, initVO.getUserId(), budgetProject.getCost(),
                Math.toIntExact(budgetProject.getUnitId()));
        budgetProject.setFlowId(flowId);
        budgetProject.setId(projectId);
        budgetProjectMapper.updateByPrimaryKeySelective(budgetProject);
        // 5.????????????
        // ??????????????????
        String inform = budgetProjectFlowService.getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // ??????????????????
        inform = inform.replace(FlowNodeNoticeTemp.TITLE.value()
                , budgetProject.getProjectNum() + "_" + budgetProject.getProjectName());
        // ????????????????????????
        budgetProjectNoticeService.andSaveBudgetProjectNotice(
                BudgetProjectNoticeReqVO.builder()
                        .projectId(Math.toIntExact(projectId))
                        .receiver(initVO.getUserId())
                        .promoter(initVO.getUserId())
                        .content(inform)
                        .flowTypeId(initVO.getFlowTypeId())
                        .build());
        // 6.??????????????????
        // ??????????????????????????????
        // ?????????????????????????????????
        List<BudgetProjectFlowApproved> projectFlowApproves = budgetProjectFlowApprovedMapper
                .selectByProjectId(Long.valueOf(projectId));
        // ??????????????????????????????????????????
        if (projectFlowApproves == null || projectFlowApproves.size() == 0) {
            budgetProjectMapper.updateStateById(projectId, FlowState.APPROVED_ADJUST.value());
        }
        budgetProjectMapper.updateStateById(projectId, FlowState.APPROVING.value());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revert(Integer id, Integer userId) {
        // ??????????????????
        List<BudgetProjectFlow> list = new ArrayList<>();
        // ??????id?????????????????????
        BudgetProject project = budgetProjectMapper.selectByPrimaryKey(id);
        UserInfo userInfo = userApi.getUserInfoById(userId);
        // ??????id???????????????????????????
        List<Long> budgetProjectFlowIds = budgetProjectFlowService
                .getIdListByProjectId(Long.valueOf(id));
        if (!project.getCreateBy().equals(userInfo.getNickName())) {
            throw new BudgetException(BudgetErrorCode.LACK_OF_REVERT_ROLE);
        }
        // ??????id??????????????????????????????
        List<BudgetProjectFlowApproved> projectFlowApproves = budgetProjectFlowApprovedMapper
                .selectByProjectId(Long.valueOf(id));
        // ??????id????????????????????????
        BudgetProjectApproved projectApproved = budgetProjectApprovedMapper
                .selectByProjectId(id);
        if (projectApproved == null) {
            throw new BudgetException(BudgetErrorCode.LACK_OF_REVERT);
        }

        // ????????????
        BudgetProject budgetProject = new BudgetProject();
        BeanUtils.copyProperties(projectApproved, budgetProject);
        budgetProject.setId(projectApproved.getProjectId());
        // ????????????????????????????????????
        budgetProject.setState(FlowState.APPROVED.value());
        budgetProject.setTaxIncludeAmount(projectApproved.getTaixIncloudAmount());
        // ??????????????????
        List<BudgetProjectFlow> budgetProjectFlows = BeanConverter
                .copyBeanList(projectFlowApproves, BudgetProjectFlow.class);
        for (int i = 0; i < budgetProjectFlows.size(); i++) {
            budgetProjectFlows.get(i).setId(projectFlowApproves.get(i).getBudgetProjectFlowId());
        }
        List<BudgetProjectFlow> budgetProjectFlowList = budgetProjectFlowService
                .getAllByProjectId(id);
        // ????????????????????????????????????????????????????????????
        for (BudgetProjectFlow budgetProjectFlow : budgetProjectFlowList) {
            if (budgetProjectFlow.getOperation() != 2) {
                list.add(budgetProjectFlow);
            }
        }
        List<BudgetProjectFlowHistory> histories = BeanConverter
                .copyBeanList(list, BudgetProjectFlowHistory.class);
        List<BudgetProjectSplitApproved> splitApproveds = budgetProjectSplitApprovedMapper.selectSplitApprovedByProjectId(id);
        List<BudgetProjectSplit> budgetProjectSplits = BeanConverter
                .copyBeanList(splitApproveds, BudgetProjectSplit.class);
        //  ??????????????????
        budgetProjectFlowHistoryService.saveBudgetProjectFlowHistory(histories);
        // ????????????id??????
        budgetProjectMapper.updateByPrimaryKeySelective(budgetProject);
        // ??????????????????
        budgetProjectSplitMapper.deleteByProjectId(id);
        // ????????????????????????
        budgetProjectSplitMapper.insertBatch(budgetProjectSplits);
        // ??????
        budgetProjectFlowService.deleteBatch(budgetProjectFlowIds);
        // ????????????
        budgetProjectFlowService.insertBatch(budgetProjectFlows);
    }

    @Override
    public boolean isUseFlowId(Long flowId) {
        int count = budgetProjectMapper.countFlowId(flowId);
        return count != 0;
    }

    @Override
    public void export2Word(HttpServletResponse response, BudgetProjectDetailExportReqVO vo) {
        BudgetProjectDetailExportVO result = new BudgetProjectDetailExportVO();

        // ????????????
        List<BudgetProjectFlowInfoVO> flows = vo.getFlows();
        List<BudgetProjectFlowExportVO> exportFlows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(flows)) {
            flows.forEach(flow -> {
                BudgetProjectFlowExportVO exportFlow = new BudgetProjectFlowExportVO();
                BeanConverter.copyProp(flow, exportFlow);
                exportFlow.setApproverRole(Optional.ofNullable(flow.getApproverRole()).orElse(""));
                exportFlow.setNickName(Optional.ofNullable(flow.getNickName()).orElse(""));
                exportFlow.setUpdateTime(Optional.ofNullable(flow.getUpdateTime()).orElse(""));
                exportFlow.setSuggestion(Optional.ofNullable(flow.getSuggestion().getValue()).orElse(""));
                exportFlows.add(exportFlow);
            });
        }
        result.setFlows(Lists.partition(exportFlows, 2));

        // ????????????
        BudgetProgressDeclareVO declare = vo.getStatistics().get(0);
        result.setBudgetBalance(declare.getBudgetBalance() == null ? "" : declare.getBudgetBalance().toString());
        result.setSurplus(declare.getSurplus() == null ? "" : declare.getSurplus().toString());
        result.setDeclared(declare.getDeclared() == null ? "" : declare.getDeclared().toString());
        result.setDeclaredPer(declare.getDeclaredPer() == null ? "" : declare.getDeclaredPer().toString());
        result.setDeclaring(declare.getDeclaring() == null ? "" : declare.getDeclaring().toString());
        result.setDeclaringPer(declare.getDeclaringPer() == null ? "" : declare.getDeclaringPer().toString());
        result.setDeclaration(declare.getDeclaration() == null ? "" : declare.getDeclaration().toString());
        result.setDeclarationPer(declare.getDeclarationPer() == null ? "" : declare.getDeclarationPer().toString());

        // ????????????
        BudgetProjectDetailVO detail = vo.getDetail();
        // ???????????????????????????????????????
        detail.setIntroduction(transferBreak(detail.getIntroduction()));
        detail.setDetail(transferBreak(detail.getDetail()));
        detail.setTarget(transferBreak(detail.getTarget()));
        detail.setRemark(transferBreak(detail.getRemark()));
        BeanConverter.copyProp(detail, result);

        List<BudgetProjectFileVO> files = detail.getFiles();
        List<BudgetProjectFileExportVO> exportFiles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(files)) {
            files.forEach(file -> {
                BudgetProjectFileExportVO exportFile = new BudgetProjectFileExportVO();
                BeanConverter.copyProp(file, exportFile);
                exportFiles.add(exportFile);
            });
        }
        result.setFiles(exportFiles);

        // ????????????
        List<BudgetProjectSplitVO> splits = vo.getSplits();
        List<BudgetProjectSplitExportVO> exportSplits = new ArrayList<>();
        AtomicInteger splitIndex = new AtomicInteger(1);
        if (!CollectionUtils.isEmpty(splits)) {
            splits.forEach(split -> {
                BudgetProjectSplitExportVO exportSplit = new BudgetProjectSplitExportVO();
                BeanConverter.copyProp(split, exportSplit);
                exportSplit.setIndex(splitIndex.getAndAdd(1));
                exportSplit.setPrice(split.getPrice() == null ? "" : split.getPrice());
                exportSplit.setAmount(split.getAmount() == null ? 0 : split.getAmount());
                exportSplit.setExplains(split.getExplains() == null ? "" : split.getExplains());
                exportSplits.add(exportSplit);
            });
        }
        result.setSplits(exportSplits);

        String path = fileApi.getPath() + File.separator + result.getProjectName() + ".doc";
        // ??????????????????
        fileApi.fillTemplate(result, "????????????????????????.ftl", path);
        // ????????????
        FileUtil.download(response, path, true);
    }

    /**
     * ???????????????
     */
    private String transferBreak(String target) {
        return target.replaceAll("\n", "<w:p></w:p>");
    }

    @Override
    public int countListByUnitIds(Long budgetId, List<Integer> unitIds) {
        return budgetProjectMapper.countListByUnitIds(budgetId, unitIds);
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> addProject(Integer projectId) {
        // ???????????????????????????????????????id
        ArrayList<Integer> projectIds = new ArrayList<>();
        // ????????????????????????
        List<BudgetProjectSplitVO> splitList
                = budgetProjectSplitService.getBudgetProjectSplitList(projectId);
        // ??????????????????
        BudgetProject project = budgetProjectMapper.selectByPrimaryKey(projectId);
        // ????????????????????????
        project.setState(FlowState.APPROVED.value());

        BudgetProjectDetailVO detailVO = budgetProjectMapper.selectProjectById(Long.valueOf(projectId));
        for (BudgetProjectSplitVO list : splitList) {
            String startTime = (detailVO.getStartTime().substring(0, 4));
            if (list.getYears() != null && !"".equals(list.getYears()) && !startTime.equals(list.getYears())) {
                // ????????????????????????????????????
                BigDecimal taxIncludeAmount = new BigDecimal(list.getCost());
                BigDecimal vatRate = project.getVatRate();
                BigDecimal a = BigDecimal.valueOf(1);
                BigDecimal cost = taxIncludeAmount.divide(a.add(vatRate), BigDecimal.ROUND_HALF_UP);
                BudgetProject budgetProject = new BudgetProject();
                BeanUtils.copyProperties(project, budgetProject);
                // ??????????????????
                budgetProject.setTaxIncludeAmount(new BigDecimal(list.getCost()));
                // ??????????????????
                budgetProject.setCost(cost);
                // ??????????????????
                budgetProject.setVatAmount(taxIncludeAmount.subtract(cost));
                // ??????????????????
                Date createTime = DateUtil.string2DateY(list.getYears());
                budgetProject.setCreateTime(createTime);
                // ????????????=????????????+????????????+????????????+4???????????????
                String budgetNum = budgetService.getNumByBudgetId(budgetProject.getBudgetId());
                // ????????????
                Unit unit = unitApi.getUnitInfo(Math.toIntExact(budgetProject.getUnitId()));
                String abbr = unit.getAbbr();
                // ????????????
                String date = (list.getYears().substring(2));
                // ??????????????????
                Integer count = budgetProjectMapper.selectTypeCountByBudgetId(budgetProject.getBudgetId(),
                        Math.toIntExact(budgetProject.getUnitId()), DateUtil.dateToStringY(createTime));
                if (count == null) {
                    count = 0;
                    String counts = String.format("%0" + 4 + "d", (count + 1));
                    budgetProject.setProjectNum(budgetNum + abbr + date + counts);
                } else {
                    String counts = String.format("%0" + 4 + "d", (count + 1));
                    budgetProject.setProjectNum(budgetNum + abbr + date + counts);
                }
                budgetProject.setSerialNum(budgetNum + list.getYears());
                // ??????????????????
                budgetProject.setOrigin("??????");
                // ??????id??????
                budgetProject.setId(null);
                budgetProjectMapper.insertSelective(budgetProject);
                Integer id = budgetProject.getId();
                // ??????????????????
                List<BudgetProjectFile> files = new ArrayList<>();
                List<BudgetProjectFileVO> file = detailVO.getFiles();
                if (file.size() != 0) {
                    for (BudgetProjectFileVO vo : file) {
                        BudgetProjectFile budgetProjectFile = new BudgetProjectFile();
                        BeanUtils.copyProperties(vo, budgetProjectFile);
                        budgetProjectFile.setProjectId(Long.valueOf(id));
                        budgetProjectFile.setCreateTime(new Date());
                        budgetProjectFile.setId(null);
                        files.add(budgetProjectFile);
                    }
                    budgetProjectFileMapper.insertBudgetProjectFiles(files);
                }
                // ?????????????????????
                List<BudgetProjectSplit> budgetProjectSplits = new ArrayList<>();
                for (BudgetProjectSplitVO list1 : splitList) {
                    BudgetProjectSplit split = new BudgetProjectSplit();
                    BeanUtils.copyProperties(list1, split);
                    split.setProjectId(id);
                    if (list1.getPrice() != null) {
                        split.setPrice(new BigDecimal(list1.getPrice()));
                    }
                    split.setCost(new BigDecimal(list1.getCost()));
                    split.setYears(list1.getYears());
                    split.setCreateTime(DateUtil.string2DateY(list1.getYears()));
                    split.setId(null);
                    budgetProjectSplits.add(split);
                }
                budgetProjectSplitMapper.insertBatch(budgetProjectSplits);
                projectIds.add(id);
            }
        }

        return projectIds;
    }

    @Override
    public List<BudgetProject> getJieZhuan(String year) {
        return budgetProjectMapper.selectJieZhuan(year);
    }

    @Override
    public void updateBatch(List<BudgetProject> list) {
        budgetProjectMapper.updateBatchById(list);
    }

    @Override
    public BudgetProject getInfo(Integer id) {
        return budgetProjectMapper.selectByPrimaryKey(id);
    }

    @Override
    public BudgetProjectDetailVO getBudgetProjectApproved(Integer id) {
        BudgetProject project = budgetProjectMapper.selectByPrimaryKey(id);
        BudgetProjectDetailVO detailVO = budgetProjectApprovedMapper
                .selectProjectApprovedByProjectId(Long.valueOf(id));
        detailVO.setUnitId(Math.toIntExact(project.getUnitId()));
        detailVO.setBudgetId(project.getBudgetId());
        List<DictVO> rateList = dictApi.getDict(
                TypeCode.BUDGET.value(), DictCode.BUDGET_PROJECT_VAT_RATES.value());
        Map<String, String> rateMap = rateList.stream().collect(
                Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        detailVO.setVatRate(rateMap.get(detailVO.getVatRate()));
        return detailVO;
    }

    /**
     * ????????????????????????????????????id
     */
    private Long getRelatedFlow(Long flowTypeId, Long unitId, Integer underUnitId) {
        // ?????????????????? => ??????id
        Map<Long, Long> flowMap = new HashMap<>();
        List<Flow> flowList = flowApi.getFlowsByTypeId(flowTypeId);
        // ????????????????????????????????????????????????????????????????????????????????????????????????
        if (flowList.size() == 0) {
            throw new BudgetException(BudgetErrorCode.NOT_EXIST_FLOW);
        } else if (flowList.size() > 2) {
            throw new BudgetException(BudgetErrorCode.EXCEED_LIMIT_FLOW);
        }
        flowList.forEach(flow ->
                flowMap.put(nodeApi.getNodeNum(flow.getId()), flow.getId()));

        Long flowNodeCount;
        // ??????????????????????????????
        if (underUnitId == Math.toIntExact(unitId)) {
            flowNodeCount = 2L;
        }
        // ??????????????????????????????
        else {
            flowNodeCount = 3L;
        }
        // ?????????????????????????????????id
        Long flowId = flowMap.get(flowNodeCount);
        // ????????????????????????????????????????????????????????????????????????
        if (flowId == null) {
            throw new BudgetException(BudgetErrorCode.LACK_OF_FLOW);
        }
        return flowId;
    }

    /**
     * ???????????????????????????????????????????????????
     */
    private boolean hasAccess2Approve(List<FlowNodePropVO> flowProps, Integer userId, Integer unitId) {
        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
        // ?????????????????????
        FlowNodePropVO firstNodeProp = flowProps.get(0);
        // ????????????????????????
        // ???????????????????????????????????????????????????????????????????????????
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        }
        // ????????????????????????????????????????????????????????????
        else {
            // ??????????????????????????????
            if (UnitEnum.isHangzhou(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // ???????????????????????????
            else if (UnitEnum.isBenbu(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApi.getSubUnit(UnitEnum.BENBU.value());
                return unitIds.contains(unitId) && flowRoleIds
                        .contains(firstNodeProp.getFlowRoleId());
            }
            // ???????????????????????????
            else {
                // ?????????????????????????????????????????????????????????????????????
                return unitId.equals(firstNodeProp.getUnitId())
                        && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    /**
     * ??????????????????
     */
    private void syncBudgetProjectFlow(List<FlowNodePropVO> flowProps, Integer projectId,
                                       Integer underUnitId, Integer userId, BigDecimal cost, Integer unitId) {
        // ??????????????????list
        List<BudgetProjectFlow> list = new ArrayList<>();
        // ???????????????????????????list
        List<BudgetProjectFlow> projectFlowList = new ArrayList<>();
        // ?????????????????????????????????
        for (FlowNodePropVO flowPropVO : flowProps) {
            if (flowPropVO.getIsJoin() == null || flowPropVO.getControlAccess() == null) {
                throw new BudgetException(BudgetErrorCode.LACK_OF_NODE_PROP);
            }
        }
        // ????????????/??????
        for (FlowNodePropVO flowProp : flowProps) {
            if (flowProp.getAmount() != null) {
                int amount = flowProp.getAmount().intValue();
                Integer enableCond = flowProp.getEnableCond();
                if (EnableCond.GREATER_THAN_OR_EQUAL.value().equals(enableCond) && cost.intValue() < amount) {
                    throw new BudgetException(BudgetErrorCode.LACK_OF_NODE_PROP);
                } else if (EnableCond.GREATER_THAN.value().equals(enableCond) && cost.intValue() <= amount) {
                    throw new BudgetException(BudgetErrorCode.LACK_OF_NODE_PROP);
                } else if (EnableCond.EQUAL_TO.value().equals(enableCond) && cost.intValue() != amount) {
                    throw new BudgetException(BudgetErrorCode.LACK_OF_NODE_PROP);
                } else if (EnableCond.LESS_THAN.value().equals(enableCond) && cost.intValue() >= amount) {
                    throw new BudgetException(BudgetErrorCode.LACK_OF_NODE_PROP);
                } else if (EnableCond.LESS_THAN_OR_EQUAL.value().equals(enableCond) && cost.intValue() > amount) {
                    throw new BudgetException(BudgetErrorCode.LACK_OF_NODE_PROP);
                }
            }
        }
        // ???????????????????????????????????????????????????????????????????????????
        if (flowProps.get(0).getUserId() == null) {
            flowProps.get(0).setUserId(userId);
        }
        // ??????????????????id??????????????????????????????????????????
        List<BudgetProjectFlow> budgetProjectFlows = budgetProjectFlowService
                .getAllByProjectId(projectId);
        // ??????????????????????????????????????????????????????????????????????????????
        for (BudgetProjectFlow budgetProjectFlow : budgetProjectFlows) {
            if (budgetProjectFlow.getOperation() != 2) {
                list.add(budgetProjectFlow);
            }
        }
        List<BudgetProjectFlowHistory> histories = BeanConverter
                .copyBeanList(list, BudgetProjectFlowHistory.class);
        // ??????????????????
        budgetProjectFlowHistoryService.saveBudgetProjectFlowHistory(histories);
        // ????????????????????????????????????????????????
        budgetProjectMapper.deleteByProjectId(projectId);
        for (FlowNodePropVO flowPropVO : flowProps) {
            projectFlowList.add(BudgetProjectFlow.builder()
                    .flowNodeId(flowPropVO.getFlowNodeId())
                    .projectId(Long.valueOf(projectId))
                    .userId(flowPropVO.getUserId())
                    .flowRoleId(flowPropVO.getFlowRoleId())
                    .underUnitId(unitId)
                    .roleDesc(flowPropVO.getRoleDesc())
                    .controlAccess(flowPropVO.getControlAccess())
                    .isJoin(flowPropVO.getIsJoin())
                    .assigner(flowPropVO.getAssigner())
                    .operation(OperationType.UN_EXECUTED.value())
                    .build());
        }
        if (projectFlowList.size() == 3) {
            projectFlowList.get(2).setUnderUnitId(underUnitId);
        }
        budgetProjectFlowService.insertBatch(projectFlowList);
    }
}