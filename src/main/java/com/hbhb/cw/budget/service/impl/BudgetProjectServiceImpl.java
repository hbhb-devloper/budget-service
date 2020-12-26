package com.hbhb.cw.budget.service.impl;

import com.google.common.collect.Lists;

import com.github.pagehelper.PageHelper;
import com.hbhb.cw.common.AllName;
import com.hbhb.cw.common.DictCode;
import com.hbhb.cw.common.DictType;
import com.hbhb.cw.common.EnableCond;
import com.hbhb.cw.common.FlowNodeNoticeState;
import com.hbhb.cw.common.OperationType;
import com.hbhb.cw.common.ProjectState;
import com.hbhb.cw.common.exception.BizException;
import com.hbhb.cw.common.exception.BizStatus;
import com.hbhb.cw.mapper.BudgetBelongMapper;
import com.hbhb.cw.mapper.BudgetProjectApprovedMapper;
import com.hbhb.cw.mapper.BudgetProjectFileMapper;
import com.hbhb.cw.mapper.BudgetProjectFlowApprovedMapper;
import com.hbhb.cw.mapper.BudgetProjectMapper;
import com.hbhb.cw.mapper.BudgetProjectSplitApprovedMapper;
import com.hbhb.cw.mapper.BudgetProjectSplitMapper;
import com.hbhb.cw.model.Budget;
import com.hbhb.cw.model.BudgetData;
import com.hbhb.cw.model.BudgetProject;
import com.hbhb.cw.model.BudgetProjectApproved;
import com.hbhb.cw.model.BudgetProjectFile;
import com.hbhb.cw.model.BudgetProjectFlow;
import com.hbhb.cw.model.BudgetProjectFlowApproved;
import com.hbhb.cw.model.BudgetProjectFlowHistory;
import com.hbhb.cw.model.BudgetProjectSplit;
import com.hbhb.cw.model.BudgetProjectSplitApproved;
import com.hbhb.cw.model.Flow;
import com.hbhb.cw.rpc.DictApiExp;
import com.hbhb.cw.rpc.FileApiExp;
import com.hbhb.cw.rpc.UnitApiExp;
import com.hbhb.cw.rpc.UserApiExp;
import com.hbhb.cw.service.BudgetDataService;
import com.hbhb.cw.service.BudgetProgressService;
import com.hbhb.cw.service.BudgetProjectFlowHistoryService;
import com.hbhb.cw.service.BudgetProjectFlowService;
import com.hbhb.cw.service.BudgetProjectNoticeService;
import com.hbhb.cw.service.BudgetProjectService;
import com.hbhb.cw.service.BudgetProjectSplitService;
import com.hbhb.cw.service.BudgetService;
import com.hbhb.cw.service.FlowNodeService;
import com.hbhb.cw.service.FlowRoleUserService;
import com.hbhb.cw.service.FlowService;
import com.hbhb.cw.systemcenter.enums.UnitEnum;
import com.hbhb.cw.systemcenter.model.Unit;
import com.hbhb.cw.systemcenter.vo.DictVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;
import com.hbhb.cw.utils.BeanConverter;
import com.hbhb.cw.utils.DateUtil;
import com.hbhb.cw.web.vo.BudgetProgressDeclareVO;
import com.hbhb.cw.web.vo.BudgetProgressReqVO;
import com.hbhb.cw.web.vo.BudgetProgressResVO;
import com.hbhb.cw.web.vo.BudgetProjectDetailExportReqVO;
import com.hbhb.cw.web.vo.BudgetProjectDetailExportVO;
import com.hbhb.cw.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.web.vo.BudgetProjectExportVO;
import com.hbhb.cw.web.vo.BudgetProjectFileExportVO;
import com.hbhb.cw.web.vo.BudgetProjectFileVO;
import com.hbhb.cw.web.vo.BudgetProjectFlowExportVO;
import com.hbhb.cw.web.vo.BudgetProjectFlowInfoVO;
import com.hbhb.cw.web.vo.BudgetProjectInitVO;
import com.hbhb.cw.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.web.vo.BudgetProjectReqVO;
import com.hbhb.cw.web.vo.BudgetProjectResVO;
import com.hbhb.cw.web.vo.BudgetProjectSplitExportVO;
import com.hbhb.cw.web.vo.BudgetProjectSplitVO;
import com.hbhb.cw.web.vo.BudgetReqVO;
import com.hbhb.cw.web.vo.FlowPropVO;
import com.hbhb.springboot.web.view.Page;

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
    private FlowService flowService;
    @Resource
    private FlowNodeService flowNodeService;
    @Resource
    private FlowRoleUserService flowRoleUserService;
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
        return budgetProjectApproved != null && !ProjectState.APPROVED.value().equals(project.getState());
    }

    @Override
    public Page<BudgetProjectResVO> getBudgetProjectPage(Integer pageNum, Integer pageSize, boolean pageable,
                                                         BudgetProjectReqVO cond) {
        // 获取所有下属单位
        List<Integer> unitIds = unitApi.getSubUnit(cond.getUnitId());
        // 获取单位map
        Map<Integer, String> unitMap = unitApi.getUnitMapById();
        // 获取项目状态字典
        List<DictVO> stateList = dictApi.getDict(DictType.BUDGET.value(), DictCode.BUDGET_PROJECT_STATUS.value());
        Map<String, String> stateMap = stateList.stream().collect(Collectors.toMap(DictVO::getValue, DictVO::getLabel));

        if (pageable) {
            PageHelper.startPage(pageNum, pageSize);
        }
        List<BudgetProjectResVO> list = budgetProjectMapper.selectListByCond(cond, unitIds);
        int count = budgetProjectMapper.countListByCond(cond);

        // 组装数据
        list.forEach(item -> {
            item.setStateLabel(stateMap.get(item.getState().toString()));
            item.setProjectTypeName(item.getBudgetNum() + "_" + item.getProjectTypeName());
            item.setUnitName(unitMap.get(item.getUnitId()));
        });
        return new Page<>(list, (long) count);
    }

    @Override
    public List<BudgetProgressResVO> getBudgetProgressByBudgetData(BudgetReqVO cond) {
        return budgetProjectMapper.selectBudgetProgressByBudgetData(cond);
    }

    @Override
    public BudgetProjectDetailVO getBudgetProject(Long id) {
        BudgetProjectDetailVO detailVO = budgetProjectMapper.selectProjectById(id);
        List<DictVO> rateList = dictApi.getDict(
                DictType.BUDGET.value(), DictCode.BUDGET_PROJECT_VAT_RATES.value());
        Map<String, String> rateMap = rateList.stream().collect(
                Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        detailVO.setVatRate(rateMap.get(detailVO.getVatRate()));
        return detailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addSaveBudgetProject(BudgetProjectDetailVO vo, UserInfo user) {
        // 校验是否可以发起该项目类型下签报
        BudgetData budgetData = budgetDataService
                .getDataByUnitIdAndBudgetId(user.getUnitId(), vo.getBudgetId());
        if (budgetData == null) {
            throw new BizException(BizStatus.BUDGET_NOT_ADD_PROJECT.getCode());
        }
        BudgetProject budgetProject = new BudgetProject();
        BeanUtils.copyProperties(vo, budgetProject);
        budgetProject.setBudgetId(vo.getBudgetId());
        budgetProject.setUnitId(Long.valueOf(user.getUnitId()));
        // 项目编号=预算编号+单位简称+两位年份+4位增长编号
        String budgetNum = budgetService.getNumByBudgetId(vo.getBudgetId());
        // 单位简称
        Unit unit = unitApi.getUnitInfo(user.getUnitId());
        String abbr = unit.getAbbr();
        // 两位年份
        String date = (DateUtil.dateToStringY(new Date())).substring(2, 4);
        // 四位增长编号
        Integer count = budgetProjectMapper.selectTypeCountByBudgetId(vo.getBudgetId(), user.getUnitId(), DateUtil.dateToStringY(new Date()));
        if (count == null) {
            count = 0;
            String counts = String.format("%0" + 4 + "d", (count + 1));
            budgetProject.setProjectNum(budgetNum + abbr + date + counts);
        } else {
            String counts = String.format("%0" + 4 + "d", (count + 1));
            budgetProject.setProjectNum(budgetNum + abbr + date + counts);
        }
        // 处理 项目预算总额.amount，可供分配预算.availableAmount、本年价税合计.taxIncludeAmount、本年项目成本.cost、
        // 增值税率.vatRate、本年增值税金.vatAmount,格式(String-->BigDecimal)
        budgetProject.setAmount(new BigDecimal(vo.getAmount()));
        budgetProject.setAvailableAmount(new BigDecimal(vo.getAvailableAmount()));
        budgetProject.setTaxIncludeAmount(new BigDecimal(vo.getTaxIncludeAmount()));
        budgetProject.setCost(new BigDecimal(vo.getCost()));
        budgetProject.setVatRate(new BigDecimal(vo.getVatRate()));
        budgetProject.setVatAmount(new BigDecimal(vo.getVatAmount()));
        // 处理 项目开始时间、结束时间、创建时间格式<String-->Date>
        String startTime = vo.getStartTime();
        String endTime = vo.getEndTime();
        if (DateUtil.isExpiredYMD(startTime, endTime)) {
            throw new BizException(BizStatus.BUDGET_PROJECT_TIME_ERROR.getCode());
        }
        budgetProject.setStartTime(DateUtil.string2DateYMD(startTime));
        budgetProject.setEndTime(DateUtil.string2DateYMD(endTime));
        budgetProject.setCreateTime(new Date());
        // 设置创建人
        budgetProject.setCreateBy(user.getNickName());
        // 设置默认创建状态-10未开始审批
        budgetProject.setState(ProjectState.NOT_APPROVED.value());
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
        // 判断登录用户是否有权限进行操作
        BudgetProject budgetProject = budgetProjectMapper.selectByPrimaryKey(id);
        if (!budgetProject.getCreateBy().equals(user.getNickName())) {
            throw new BizException(BizStatus.BUDGET_PROJECT_INITIATOR_ERROR.getCode());
        }
        budgetProjectMapper.updateDeleteFlagById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBudgetProject(BudgetProjectDetailVO budgetProjectDetailVO, UserInfo user) {
        // 判断登录用户是否有权限进行操作
        BudgetProject bp = budgetProjectMapper
                .selectByPrimaryKey(Math.toIntExact(budgetProjectDetailVO.getId()));
        if (!bp.getCreateBy().equals(user.getNickName())) {
            throw new BizException(BizStatus.BUDGET_PROJECT_INITIATOR_ERROR.getCode());
        }
        BudgetProject budgetProject = new BudgetProject();
        BeanUtils.copyProperties(budgetProjectDetailVO, budgetProject);
        budgetProject.setId(Math.toIntExact(budgetProjectDetailVO.getId()));
        // 处理 项目预算总额.amount，可供分配预算.availableAmount、本年价税合计.taxIncludeAmount、本年项目成本.cost、
        // 增值税率.vatRate、本年增值税金.vatAmount,格式(String-->BigDecimal)
        budgetProject.setAmount(new BigDecimal(budgetProjectDetailVO.getAmount()));
        budgetProject
                .setAvailableAmount(new BigDecimal(budgetProjectDetailVO.getAvailableAmount()));
        budgetProject
                .setTaxIncludeAmount(new BigDecimal(budgetProjectDetailVO.getTaxIncludeAmount()));
        budgetProject.setCost(new BigDecimal(budgetProjectDetailVO.getCost()));
        budgetProject.setVatRate(new BigDecimal(budgetProjectDetailVO.getVatRate()));
        // 处理 项目开始时间、结束时间、创建时间格式<String-->Date>
        String startTime = budgetProjectDetailVO.getStartTime();
        String endTime = budgetProjectDetailVO.getEndTime();
        if (DateUtil.isExpiredYMD(startTime, endTime)) {
            throw new BizException(BizStatus.BUDGET_PROJECT_TIME_ERROR.getCode());
        }
        budgetProject.setStartTime(DateUtil.string2DateYMD(startTime));
        budgetProject.setEndTime(DateUtil.string2DateYMD(endTime));

        // 判断是修改还是调整
        if (budgetProjectDetailVO.getState() != null &&
                budgetProjectDetailVO.getState().equals(ProjectState.APPROVED.value())) {
            budgetProject.setState(ProjectState.IN_ADJUST.value());
        }
        //修改
        budgetProjectMapper.updateByPrimaryKeySelective(budgetProject);
        //重新增加文件
        List<Long> files = budgetProjectFileMapper.selectIdListByProjectId(budgetProjectDetailVO.getId());
        List<BudgetProjectFileVO> budgetProjectFileVOS = budgetProjectDetailVO.getFiles();
        if (budgetProjectFileVOS.size() != 0) {
            List<BudgetProjectFile> bpfs = new ArrayList<>();
            for (BudgetProjectFileVO bpfVo : budgetProjectFileVOS) {
                // 判断是否已经存在该附件
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
     * 1.判断是否能发起签报 2.获取项目签报所对应的流程id 3.校验用户发起审批权限 4.同步节点属性 5.推送提醒 6.更新签报状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toApprove(BudgetProjectInitVO initVO) {
        Integer projectId = initVO.getProjectId();
        // 1.判断是否能发起签报
        // 1-1.判断是否有分类预算 如果不存在则无法发起
        List<BudgetProjectSplitVO> splitList = budgetProjectSplitService
                .getBudgetProjectSplitList(projectId);
        if (splitList == null || splitList.size() == 0) {
            throw new BizException(BizStatus.CATEGORY_BUDGET_NOT_WRITE.getCode());
        }
        // 得到索要发起的签报详情
        BudgetProject budgetProject = budgetProjectMapper
                .selectBudgetProjectById(Long.valueOf(projectId));
        // 1-2.判断登录用户和发起签报用户是否为同一个用户
        UserInfo user = userApi.getUserInfoById(initVO.getUserId());
        if (!user.getNickName().equals(budgetProject.getCreateBy())) {
            throw new BizException(BizStatus.LACK_OF_FLOW_ROLE.getCode());
        }
        // 1-3.通过签报id获取项目开始时间结束时间，判断用户是否添加分类预算
        int startTime = Integer.parseInt(DateUtil.dateToStringY(budgetProject.getStartTime()));
        int endTime = Integer.parseInt(DateUtil.dateToStringY(budgetProject.getEndTime()));
        List<Integer> years = new ArrayList<>();
        // 判断跨年项目分类预算
        if (endTime - startTime != 0) {
            for (BudgetProjectSplitVO vo : splitList) {
                if (vo.getYears() == null || "".equals(vo.getYears())) {
                    throw new BizException(BizStatus.BUDGET_PROJECT_SPLIT_YEAR_ERROR.getCode());
                }
                years.add(Integer.valueOf(vo.getYears()));
            }
            List<Integer> year = new ArrayList<>();
            for (int i = startTime; i <= endTime; i++) {
                year.add(i);
            }
            if (!year.equals(years)) {
                throw new BizException(BizStatus.BUDGET_PROJECT_SPLIT_YEAR_ERROR.getCode());
            }
        }
        // 查询当前项目签报详情
        Long budgetId = budgetProject.getBudgetId();
        Long unitId = budgetProject.getUnitId();
        // 1-4.判断审批通过,调整审批通过和审批中金额和当前金额是否超过改项目类别归口单位的预算金额
        // 通过预算id判断该签报年份
        Budget budget = budgetService.getBudgetById(budgetId);
        // 查询当前单位所对应的归口单位id
        Integer underUnitId = budgetBelongMapper
                .selectUnderUnitId(budgetId, Math.toIntExact(unitId));
        String newUnitId;
        if (underUnitId == null) {
            throw new BizException(BizStatus.COST_EXCEED_SURPLUS.getCode());
        }
        // 判断是否归口与其他单位
        if (underUnitId != Math.toIntExact(unitId)) {
            // 如果归口于其他单位则统计归口单位
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
        // 归口单位预算为0
        progressByState.setSurplus(progressByState.getSurplus() == null ? new BigDecimal("0.0") : progressByState.getSurplus());
        // 判断是否能发起审批
        if (budgetProject.getCost().compareTo(progressByState.getSurplus()) > 0) {
            throw new BizException(BizStatus.COST_EXCEED_SURPLUS.getCode());
        }

        // 2.获取项目签报所对应的流程id
        Long flowId = getRelatedFlow(initVO.getFlowTypeId(), unitId, underUnitId);

        // 通过i流程id得到流程节点属性
        List<FlowPropVO> flowProps = flowService.getFlowProp(flowId);
        // 3.校验用户发起审批权限
        boolean hasAccess = hasAccess2Approve(flowProps, initVO.getUserId(),
                Math.toIntExact(unitId));
        if (!hasAccess) {
            throw new BizException(BizStatus.LACK_OF_FLOW_ROLE.getCode());
        }
        // 4.同步节点属性
        syncBudgetProjectFlow(flowProps, projectId, underUnitId, initVO.getUserId(), budgetProject.getCost(),
                Math.toIntExact(budgetProject.getUnitId()));
        budgetProject.setFlowId(flowId);
        budgetProject.setId(projectId);
        budgetProjectMapper.updateByPrimaryKeySelective(budgetProject);
        // 5.推送提醒
        // 得到推送模板
        String inform = budgetProjectFlowService.getInform(flowProps.get(0).getFlowNodeId()
                , FlowNodeNoticeState.DEFAULT_REMINDER.value());
        if (inform == null) {
            return;
        }
        // 修改推送模板
        inform = inform.replace(AllName.TITLE.getValue()
                , budgetProject.getProjectNum() + "_" + budgetProject.getProjectName());
        // 推送消息给发起人
        budgetProjectNoticeService.andSaveBudgetProjectNotice(
                BudgetProjectNoticeReqVO.builder()
                        .projectId(Math.toIntExact(projectId))
                        .receiver(initVO.getUserId())
                        .promoter(initVO.getUserId())
                        .content(inform)
                        .flowTypeId(initVO.getFlowTypeId())
                        .build());
        // 6.更新签报状态
        // 判断是否为第一次调整
        // 判断是否有该项目的快照
        List<BudgetProjectFlowApproved> projectFlowApproves = budgetProjectFlowApprovedMapper
                .selectByProjectId(Long.valueOf(projectId));
        // 若存在则表示为第二次发起审批
        if (projectFlowApproves == null || projectFlowApproves.size() == 0) {
            budgetProjectMapper.updateStateById(projectId, ProjectState.APPROVED_ADJUST.value());
        }
        budgetProjectMapper.updateStateById(projectId, ProjectState.APPROVING.value());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void revert(Integer id, Integer userId) {
        // 暂存历史数据
        List<BudgetProjectFlow> list = new ArrayList<>();
        // 通过id得到该项目签报
        BudgetProject project = budgetProjectMapper.selectByPrimaryKey(id);
        UserInfo userInfo = userApi.getUserInfoById(userId);
        // 通过id得到该项目流程属性
        List<Long> budgetProjectFlowIds = budgetProjectFlowService
                .getIdListByProjectId(Long.valueOf(id));
        if (!project.getCreateBy().equals(userInfo.getNickName())) {
            throw new BizException(BizStatus.LACK_OF_REVERT_ROLE.getCode());
        }
        // 通过id得到签报流程信息快照
        List<BudgetProjectFlowApproved> projectFlowApproves = budgetProjectFlowApprovedMapper
                .selectByProjectId(Long.valueOf(id));
        // 通过id得到项目签报快照
        BudgetProjectApproved projectApproved = budgetProjectApprovedMapper
                .selectByProjectId(id);
        if (projectApproved == null) {
            throw new BizException(BizStatus.LACK_OF_REVERT.getCode());
        }

        // 签报赋值
        BudgetProject budgetProject = new BudgetProject();
        BeanUtils.copyProperties(projectApproved, budgetProject);
        budgetProject.setId(projectApproved.getProjectId());
        // 回滚后状态一定为审批通过
        budgetProject.setState(ProjectState.APPROVED.value());
        budgetProject.setTaxIncludeAmount(projectApproved.getTaixIncloudAmount());
        // 流程信息赋值
        List<BudgetProjectFlow> budgetProjectFlows = BeanConverter
                .copyBeanList(projectFlowApproves, BudgetProjectFlow.class);
        for (int i = 0; i < budgetProjectFlows.size(); i++) {
            budgetProjectFlows.get(i).setId(projectFlowApproves.get(i).getBudgetProjectFlowId());
        }
        List<BudgetProjectFlow> budgetProjectFlowList = budgetProjectFlowService
                .getAllByProjectId(id);
        // 若有存在不等于二的则把原数据放入历史表中
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
        //  添加历史数据
        budgetProjectFlowHistoryService.saveBudgetProjectFlowHistory(histories);
        // 通过签报id修改
        budgetProjectMapper.updateByPrimaryKeySelective(budgetProject);
        // 删除分类预算
        budgetProjectSplitMapper.deleteByProjectId(id);
        // 批量流程信息修改
        budgetProjectSplitMapper.insertBatch(budgetProjectSplits);
        // 删除
        budgetProjectFlowService.deleteBatch(budgetProjectFlowIds);
        // 批量新增
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

        // 流程节点
        List<BudgetProjectFlowInfoVO> flows = vo.getFlows();
        List<BudgetProjectFlowExportVO> exportFlows = new ArrayList<>();
        if (!CollectionUtils.isEmpty(flows)) {
            flows.forEach(flow -> {
                BudgetProjectFlowExportVO exportFlow = new BudgetProjectFlowExportVO();
                BeanConverter.copyProp(flow, exportFlow);
                exportFlow.setSuggestion(flow.getSuggestion().getValue());
                exportFlows.add(exportFlow);
            });
        }
        result.setFlows(Lists.partition(exportFlows, 2));

        // 统计信息
        BudgetProgressDeclareVO declare = vo.getStatistics().get(0);
        result.setBudgetBalance(declare.getBudgetBalance() == null ? "" : declare.getBudgetBalance().toString());
        result.setSurplus(declare.getSurplus() == null ? "" : declare.getSurplus().toString());
        result.setDeclared(declare.getDeclared() == null ? "" : declare.getDeclared().toString());
        result.setDeclaredPer(declare.getDeclaredPer() == null ? "" : declare.getDeclaredPer().toString());
        result.setDeclaring(declare.getDeclaring() == null ? "" : declare.getDeclaring().toString());
        result.setDeclaringPer(declare.getDeclaringPer() == null ? "" : declare.getDeclaringPer().toString());
        result.setDeclaration(declare.getDeclaration() == null ? "" : declare.getDeclaration().toString());
        result.setDeclarationPer(declare.getDeclarationPer() == null ? "" : declare.getDeclarationPer().toString());

        // 详细信息
        BudgetProjectDetailVO detail = vo.getDetail();
        // 对文本域中的回车符进行转换
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

        // 分类预算
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
        // 生成填充文件
        fileApi.fillTemplate(result, "项目签报导出模板.ftl", path);
        // 下载文件
        fileApi.download(response, path, true);
    }

    /**
     * 换行符转换
     */
    private String transferBreak(String target) {
        return target.replaceAll("\n", "<w:p></w:p>");
    }

    @Override
    public int countListByUnitIds(Long budgetId, List<Integer> unitIds) {
        return budgetProjectMapper.countListByUnitIds(budgetId, unitIds);
    }

    /**
     * 审批通过后根据分类预算生成后几年签报
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Integer> addProject(Integer projectId) {
        // 用来存储跨年时分出来的签报id
        ArrayList<Integer> projectIds = new ArrayList<>();
        // 获取分类预算信息
        List<BudgetProjectSplitVO> splitList
                = budgetProjectSplitService.getBudgetProjectSplitList(projectId);
        // 获取签报详情
        BudgetProject project = budgetProjectMapper.selectByPrimaryKey(projectId);
        // 设置状态为已通过
        project.setState(ProjectState.APPROVED.value());

        BudgetProjectDetailVO detailVO = budgetProjectMapper.selectProjectById(Long.valueOf(projectId));
        for (BudgetProjectSplitVO list : splitList) {
            String startTime = (detailVO.getStartTime().substring(0, 4));
            if (list.getYears() != null && !"".equals(list.getYears()) && !startTime.equals(list.getYears())) {
                // 跟据分类预算信息新增签报
                BigDecimal taxIncludeAmount = new BigDecimal(list.getCost());
                BigDecimal vatRate = project.getVatRate();
                BigDecimal a = BigDecimal.valueOf(1);
                BigDecimal cost = taxIncludeAmount.divide(a.add(vatRate), BigDecimal.ROUND_HALF_UP);
                BudgetProject budgetProject = new BudgetProject();
                BeanUtils.copyProperties(project, budgetProject);
                // 本年价税合计
                budgetProject.setTaxIncludeAmount(new BigDecimal(list.getCost()));
                // 本年项目成本
                budgetProject.setCost(cost);
                // 本年增值税金
                budgetProject.setVatAmount(taxIncludeAmount.subtract(cost));
                // 设置创建时间
                Date createTime = DateUtil.string2DateY(list.getYears());
                budgetProject.setCreateTime(createTime);
                // 项目编号=预算编号+单位简称+两位年份+4位增长编号
                String budgetNum = budgetService.getNumByBudgetId(budgetProject.getBudgetId());
                // 单位简称
                Unit unit = unitApi.getUnitInfo(Math.toIntExact(budgetProject.getUnitId()));
                String abbr = unit.getAbbr();
                // 两位年份
                String date = (list.getYears().substring(2));
                // 四位增长编号
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
                // 设置项目来源
                budgetProject.setOrigin("结转");
                // 签报id置空
                budgetProject.setId(null);
                budgetProjectMapper.insertSelective(budgetProject);
                Integer id = budgetProject.getId();
                // 生成新的附件
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
                // 生成新分类预算
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
                DictType.BUDGET.value(), DictCode.BUDGET_PROJECT_VAT_RATES.value());
        Map<String, String> rateMap = rateList.stream().collect(
                Collectors.toMap(DictVO::getValue, DictVO::getLabel));
        detailVO.setVatRate(rateMap.get(detailVO.getVatRate()));
        return detailVO;
    }

    /**
     * 获取项目签报所对应的流程id
     */
    private Long getRelatedFlow(Long flowTypeId, Long unitId, Integer underUnitId) {
        // 流程节点数量 => 流程id
        Map<Integer, Long> flowMap = new HashMap<>();
        List<Flow> flowList = flowService.getFlowsByTypeId(flowTypeId);
        // 流程有效性校验（项目签报类型下，每个单位只能是有一条或两条流程）
        if (flowList.size() == 0) {
            throw new BizException(BizStatus.NOT_EXIST_FLOW.getCode());
        } else if (flowList.size() > 2) {
            throw new BizException(BizStatus.EXCEED_LIMIT_FLOW.getCode());
        }
        flowList.forEach(flow ->
                flowMap.put(flowNodeService.countFlowNode(flow.getId()), flow.getId()));

        int flowNodeCount;
        // 自归口（两节点流程）
        if (underUnitId == Math.toIntExact(unitId)) {
            flowNodeCount = 2;
        }
        // 被归口（三节点流程）
        else {
            flowNodeCount = 3;
        }
        // 当前项目签报关联的流程id
        Long flowId = flowMap.get(flowNodeCount);
        // 校验流程是否匹配，如果没有匹配的流程，则抛出提示
        if (flowId == null) {
            throw new BizException(BizStatus.LACK_OF_FLOW.getCode());
        }
        return flowId;
    }

    /**
     * 判断当前用户是否有权限发起审批权限
     */
    private boolean hasAccess2Approve(List<FlowPropVO> flowProps, Integer userId, Integer unitId) {
        List<Long> flowRoleIds = flowRoleUserService.getFlowRoleIdByUserId(userId);
        // 第一个节点属性
        FlowPropVO firstNodeProp = flowProps.get(0);
        // 判断是有默认用户
        // 如果设定了默认用户，且为当前登录用户，则有发起权限
        if (firstNodeProp.getUserId() != null) {
            return firstNodeProp.getUserId().equals(userId);
        }
        // 如果没有设定默认用户，则通过流程角色判断
        else {
            // 如果角色范围为全杭州
            if (UnitEnum.isHangzhou(firstNodeProp.getUnitId()) || firstNodeProp.getUnitId().equals(0)) {
                return flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
            // 如果角色范围为本部
            else if (UnitEnum.isBenbu(firstNodeProp.getUnitId())) {
                List<Integer> unitIds = unitApi.getSubUnit(UnitEnum.BENBU.value());
                return unitIds.contains(unitId) && flowRoleIds
                        .contains(firstNodeProp.getFlowRoleId());
            }
            // 如果为确定某个单位
            else {
                // 必须单位和流程角色都匹配，才可判定为有发起权限
                return unitId.equals(firstNodeProp.getUnitId())
                        && flowRoleIds.contains(firstNodeProp.getFlowRoleId());
            }
        }
    }

    /**
     * 同步节点属性
     */
    private void syncBudgetProjectFlow(List<FlowPropVO> flowProps, Integer projectId,
                                       Integer underUnitId, Integer userId, BigDecimal cost, Integer unitId) {
        // 暂存历史数据list
        List<BudgetProjectFlow> list = new ArrayList<>();
        // 用来存储同步节点的list
        List<BudgetProjectFlow> projectFlowList = new ArrayList<>();
        // 判断节点是否有保存属性
        for (FlowPropVO flowPropVO : flowProps) {
            if (flowPropVO.getIsJoin() == null || flowPropVO.getControlAccess() == null) {
                throw new BizException(BizStatus.LACK_OF_NODE_PROP.getCode());
            }
        }
        // 签报阀值/条件
        for (FlowPropVO flowProp : flowProps) {
            Integer amount = flowProp.getAmount();
            if (amount != null) {
                Integer enableCond = flowProp.getEnableCond();
                if (EnableCond.GREATER_THAN_OR_EQUAL.value().equals(enableCond) && cost.intValue() < amount) {
                    throw new BizException(BizStatus.LACK_OF_NODE_PROP.getCode());
                } else if (EnableCond.GREATER_THAN.value().equals(enableCond) && cost.intValue() <= amount) {
                    throw new BizException(BizStatus.LACK_OF_NODE_PROP.getCode());
                } else if (EnableCond.EQUAL_TO.value().equals(enableCond) && cost.intValue() != amount) {
                    throw new BizException(BizStatus.LACK_OF_NODE_PROP.getCode());
                } else if (EnableCond.LESS_THAN.value().equals(enableCond) && cost.intValue() >= amount) {
                    throw new BizException(BizStatus.LACK_OF_NODE_PROP.getCode());
                } else if (EnableCond.LESS_THAN_OR_EQUAL.value().equals(enableCond) && cost.intValue() > amount) {
                    throw new BizException(BizStatus.LACK_OF_NODE_PROP.getCode());
                }
            }
        }
        // 判断第一个节点是否有默认用户，如果没有则为当前用户
        if (flowProps.get(0).getUserId() == null) {
            flowProps.get(0).setUserId(userId);
        }
        // 通过项目签报id查询流程信息是否为第一次申请
        List<BudgetProjectFlow> budgetProjectFlows = budgetProjectFlowService
                .getAllByProjectId(projectId);
        // 若有存在不等于二的则不为第一次，把原数据放入历史表中
        for (BudgetProjectFlow budgetProjectFlow : budgetProjectFlows) {
            if (budgetProjectFlow.getOperation() != 2) {
                list.add(budgetProjectFlow);
            }
        }
        List<BudgetProjectFlowHistory> histories = BeanConverter
                .copyBeanList(list, BudgetProjectFlowHistory.class);
        // 添加历史数据
        budgetProjectFlowHistoryService.saveBudgetProjectFlowHistory(histories);
        // 所以需要先清空节点，再同步节点。
        budgetProjectMapper.deleteByProjectId(projectId);
        for (FlowPropVO flowPropVO : flowProps) {
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