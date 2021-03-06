package com.hbhb.cw.budget.service.impl;


import com.hbhb.core.bean.BeanConverter;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.cw.budget.enums.OperationType;
import com.hbhb.cw.budget.exception.BudgetException;
import com.hbhb.cw.budget.mapper.BudgetProjectApprovedMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectFlowApprovedMapper;
import com.hbhb.cw.budget.mapper.BudgetProjectFlowMapper;
import com.hbhb.cw.budget.model.BudgetProject;
import com.hbhb.cw.budget.model.BudgetProjectApproved;
import com.hbhb.cw.budget.model.BudgetProjectFlow;
import com.hbhb.cw.budget.model.BudgetProjectFlowApproved;
import com.hbhb.cw.budget.model.BudgetProjectSplit;
import com.hbhb.cw.budget.model.BudgetProjectSplitApproved;
import com.hbhb.cw.budget.rpc.FlowApiExp;
import com.hbhb.cw.budget.rpc.FlowNodeApiExp;
import com.hbhb.cw.budget.rpc.FlowNodePropApiExp;
import com.hbhb.cw.budget.rpc.FlowNoticeApiExp;
import com.hbhb.cw.budget.rpc.FlowRoleUserApiExp;
import com.hbhb.cw.budget.rpc.FlowTypeApiExp;
import com.hbhb.cw.budget.rpc.UserApiExp;
import com.hbhb.cw.budget.service.BudgetProjectFlowService;
import com.hbhb.cw.budget.service.BudgetProjectNoticeService;
import com.hbhb.cw.budget.service.BudgetProjectService;
import com.hbhb.cw.budget.service.BudgetProjectSplitService;
import com.hbhb.cw.budget.service.MailService;
import com.hbhb.cw.budget.web.vo.BudgetProjectApproveVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectApprovedFlowInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectDetailVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowApproverVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowInfoVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowNodeVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowOperationVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowSuggestionVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectFlowVO;
import com.hbhb.cw.budget.web.vo.BudgetProjectNoticeReqVO;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeState;
import com.hbhb.cw.flowcenter.enums.FlowNodeNoticeTemp;
import com.hbhb.cw.flowcenter.model.Flow;
import com.hbhb.cw.flowcenter.vo.FlowRoleResVO;
import com.hbhb.cw.flowcenter.vo.NodeOperationReqVO;
import com.hbhb.cw.systemcenter.vo.UserInfo;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import static com.hbhb.cw.flowcenter.enums.FlowState.ADJUST_APPROVED;
import static com.hbhb.cw.flowcenter.enums.FlowState.APPROVED;
import static com.hbhb.cw.flowcenter.enums.FlowState.APPROVE_REJECTED;

@Service
public class BudgetProjectFlowServiceImpl implements BudgetProjectFlowService {

    @Resource
    private BudgetProjectFlowMapper budgetProjectFlowMapper;
    @Resource
    private BudgetProjectApprovedMapper budgetProjectApprovedMapper;
    @Resource
    private BudgetProjectFlowApprovedMapper budgetProjectFlowApprovedMapper;
    @Resource
    private BudgetProjectService budgetProjectService;
    @Resource
    private BudgetProjectSplitService budgetProjectSplitService;
    @Resource
    private BudgetProjectNoticeService budgetProjectNoticeService;
    @Resource
    private FlowNodeApiExp nodeApi;
    @Resource
    private FlowRoleUserApiExp roleUserApi;
    @Resource
    private FlowApiExp flowApi;
    @Resource
    private FlowNodePropApiExp propApi;
    @Resource
    private FlowNoticeApiExp noticeApi;
    @Resource
    private MailService mailService;
    @Resource
    private UserApiExp userApi;

    @Resource
    private FlowTypeApiExp typeApi;

    @Value("${mail.enable}")
    private Boolean mailEnable;

    @Override
    public List<BudgetProjectFlowInfoVO> getBudgetProjectFlow(Integer projectId, Integer userId) {
        List<BudgetProjectFlowInfoVO> list = new ArrayList<>();
        String flowName;
        // ???????????????????????????
        List<BudgetProjectFlowVO> flowNodes = budgetProjectFlowMapper.selectByProjectId(projectId);
        // ??????projectId??????flowId
        BudgetProject info = budgetProjectService.getInfo(projectId);
        if (info.getFlowId() != null) {
            // ??????flowId??????????????????
            Flow flow = flowApi.getFlowById(info.getFlowId());
            // ????????????id????????????????????????
            flowName = flow.getFlowName();
        } else {
            // ????????????id????????????????????????
            flowName = flowApi.getNameByNodeId(flowNodes.get(0).getFlowNodeId());

        }
        // ????????????id??????????????????
        BudgetProject project = budgetProjectService.getInfo(projectId);
        // ??????????????????
        String projectFlowName = project.getProjectName() + flowName;
        // ??????userId??????nickName
        UserInfo userInfo = userApi.getUserInfoById(userId);
        // ???????????????????????????????????????????????????
        for (int i = 0; i < flowNodes.size(); i++) {
            if (flowNodes.get(i).getOperation() == null
                    || !flowNodes.get(flowNodes.size() - 1).getOperation().equals(OperationType.UN_EXECUTED.value())) {
                for (BudgetProjectFlowVO flowNode : flowNodes) {
                    BudgetProjectFlowInfoVO result = new BudgetProjectFlowInfoVO();
                    BeanConverter.copyProp(flowNode, result);
                    result.setApproverRole(flowNode.getRoleDesc());
                    result.setApprover(BudgetProjectFlowApproverVO.builder()
                            .value(flowNode.getApprover()).readOnly(true).build());
                    result.setOperation(BudgetProjectFlowOperationVO.builder()
                            .value(flowNode.getOperation()).hidden(true).build());
                    result.setSuggestion(BudgetProjectFlowSuggestionVO.builder()
                            .value(flowNode.getSuggestion()).readOnly(true).build());
                    result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(),
                            Math.toIntExact(flowNode.getProjectId())));
                    result.setProjectFlowName(projectFlowName);
                    list.add(result);
                }
                return list;
            }
        }
        Map<String, BudgetProjectFlowVO> flowNodeMap = flowNodes.stream().collect(
                Collectors.toMap(BudgetProjectFlowVO::getFlowNodeId, Function.identity()));
        // ??????userId????????????????????????????????????
        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
        // 1.????????????????????????????????????<currentNode>
        // 2.?????????<loginUser>?????????<currentNode>????????????
        //   2-1.???????????????????????????????????????????????????
        //   2-2.???????????????????????????????????????????????????
        //      a.??????????????????????????????????????????????????????????????????<operation>?????????<suggestion>
        //      b.?????????????????????????????????????????????
        //        ???????????????????????????<operation>?????????<suggestion>
        //        ????????????????????????<approver>

        // 1.????????????????????????????????????
        List<NodeOperationReqVO> voList = new ArrayList<>();
        flowNodes.forEach(flowNode -> voList.add(NodeOperationReqVO.builder()
                .flowNodeId(flowNode.getFlowNodeId())
                .operation(flowNode.getOperation())
                .build()));
        // ????????????id
        String currentNodeId = getCurrentNode(voList);
        if (!StringUtils.isEmpty(currentNodeId)) {
            BudgetProjectFlowVO currentNode = flowNodeMap.get(currentNodeId);
            // 2.???????????????????????????????????????
            if (!userId.equals(currentNode.getApprover())) {
                // 2-1.???????????????????????????????????????????????????
                flowNodes.forEach(flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 0, projectFlowName)));
            } else {
                // 2-2-a.????????????????????????????????????
                if (flowRoleIds.contains(currentNode.getAssigner())) {
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 2, projectFlowName)));
                } else {
                    // 2-2-b.???????????????????????????????????????
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 1, projectFlowName)));
                }
            }
        }
        // ??????????????????????????????????????????????????????????????????????????????????????????
        for (BudgetProjectFlowInfoVO budgetProjectFlowInfoVO : list) {
            if (userId.equals(budgetProjectFlowInfoVO.getApprover().getValue())) {
                ArrayList<Integer> userIds = new ArrayList<>();
                List<FlowRoleResVO> approverSelect = budgetProjectFlowInfoVO.getApproverSelect();
                for (FlowRoleResVO flowRoleResVO : approverSelect) {
                    userIds.add(flowRoleResVO.getUserId());
                }
                if (!userIds.contains(userId)) {
                    FlowRoleResVO flowRoleResVO = new FlowRoleResVO();
                    flowRoleResVO.setUserId(userId);
                    flowRoleResVO.setNickName(userInfo.getNickName());
                    approverSelect.add(flowRoleResVO);
                    budgetProjectFlowInfoVO.setApproverSelect(approverSelect);
                }
            }
        }
        // ?????????????????????????????????????????????????????????
        for (BudgetProjectFlowInfoVO infoVO : list) {
            if (!infoVO.getFlowNodeId().equals(currentNodeId)
                    && !infoVO.getOperation().getValue().equals(OperationType.UN_EXECUTED.value())) {
                infoVO.setApprover(BudgetProjectFlowApproverVO.builder()
                        .value(infoVO.getApprover().getValue()).readOnly(true).build());
                infoVO.setOperation(BudgetProjectFlowOperationVO.builder()
                        .value(infoVO.getOperation().getValue()).hidden(true).build());
                infoVO.setSuggestion(BudgetProjectFlowSuggestionVO.builder()
                        .value(infoVO.getSuggestion().getValue()).readOnly(true).build());
                infoVO.setApproverSelect(getApproverSelectList(infoVO.getFlowNodeId(),
                        Math.toIntExact(infoVO.getProjectId())));
                infoVO.setProjectFlowName(projectFlowName);
            } else {
                break;
            }
        }
        return list;
    }

    @Override
    public void deleteProjectFlow(Long id) {
        budgetProjectFlowMapper.updateByPrimaryKeySelective(BudgetProjectFlow.builder()
                .id(id).isDelete(true).build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(BudgetProjectApproveVO approveVO, Integer userId) {
        // ????????????????????????
        BudgetProjectFlow currentFlow = budgetProjectFlowMapper
                .selectByPrimaryKey(approveVO.getId());
        // ??????????????????????????????
        if (!currentFlow.getUserId().equals(userId)) {
            throw new BudgetException(BudgetErrorCode.LOCK_OF_APPROVAL_ROLE);
        }


        // ????????????id
        Long projectId = currentFlow.getProjectId();
        // ??????????????????
        List<BudgetProjectFlowApproved> approveds = budgetProjectFlowApprovedMapper.selectByProjectId(projectId);
        // ????????????id
        String currentFlowNodeId = currentFlow.getFlowNodeId();
        // ???????????????
        List<BudgetProjectFlowNodeVO> approvers = approveVO.getApprovers();
        // ????????????id
        List<String> flowNodes = approvers.stream().map(
                BudgetProjectFlowNodeVO::getFlowNodeId).collect(Collectors.toList());
        // ????????????????????????id
        // flowNodeId => userId
        Map<String, Integer> approverMap = new HashMap<>();
        for (BudgetProjectFlowNodeVO approver : approvers) {
            approverMap.put(approver.getFlowNodeId(), approver.getUserId());
        }
        Integer operation = null;
        Integer FlowState = null;
        List<Integer> projectIds = new ArrayList<>();
        // ??????
        if (approveVO.getOperation().equals(OperationType.AGREE.value())) {
            operation = OperationType.AGREE.value();
            // ???????????????????????????????????????????????????????????????????????????
            if (isLastNode(currentFlowNodeId, flowNodes)) {
                // ?????????????????????????????????
                if (budgetProjectApprovedMapper.selectByProjectId(Math.toIntExact(projectId)) != null) {
                    FlowState = ADJUST_APPROVED.value();
                } else {
                    FlowState = APPROVED.value();
                    // 1.?????????????????????id?????? 2?????????id?????????????????????????????????????????????????????????????????????????????????????????????31??? 3?????????????????????
                    projectIds = budgetProjectService.addProject(Math.toIntExact(projectId));
                    if (approveds == null || approveds.size() == 0) {
                        if (projectIds != null && projectIds.size() != 0) {
                            // ????????????
                            for (Integer id : projectIds) {
                                insertProjectApprove(id);
                            }
                        }
                    }
                }
                if (approveds == null || approveds.size() == 0) {
                    // ???????????????
                    insertProjectApprove(Math.toIntExact(projectId));
                }
            } else {
                // ?????????????????????????????????
                List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
                // ???????????????????????????????????????????????????????????????????????????????????????????????????
                if (flowRoleIds.contains(currentFlow.getAssigner())) {
                    // ????????????????????????????????????
                    if (!isAllApproverAssigned(approvers)) {
                        throw new BudgetException(BudgetErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                    // ????????????????????????
                    budgetProjectFlowMapper.updateBatchByNodeId(approvers, projectId);
                }
                // ??????????????????????????????????????????????????????????????????
                else {
                    // ????????????????????????????????????????????????
                    Integer nextApprover = approverMap
                            .get(getNextNode(currentFlowNodeId, flowNodes));
                    if (nextApprover == null) {
                        throw new BudgetException(BudgetErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                }
            }
        }
        // ??????
        else if (approveVO.getOperation().equals(OperationType.REJECT.value())) {
            operation = OperationType.REJECT.value();
            FlowState = APPROVE_REJECTED.value();
        }

        // ????????????????????????????????????????????????????????????
        budgetProjectNoticeService.updateByBudgetProjectId(Math.toIntExact(projectId));

        // ????????????
        assert operation != null;
        toInform(operation, approvers, userId, projectId, currentFlowNodeId, flowNodes,
                approverMap, approveVO.getSuggestion());

        // ????????????????????????
        budgetProjectFlowMapper.updateByPrimaryKeySelective(BudgetProjectFlow.builder()
                .operation(operation)
                .suggestion(approveVO.getSuggestion())
                .updateTime(new Date())
                .id(approveVO.getId())
                .build());

        // ???????????????????????????
        if (FlowState != null) {
            budgetProjectService.updateState(Math.toIntExact(projectId), FlowState);
        }

        // ??????????????????????????????????????????
        if (isLastNode(currentFlowNodeId, flowNodes) && operation.equals(OperationType.AGREE.value())) {
            List<BudgetProjectFlow> budgetProjectFlows = budgetProjectFlowMapper
                    .selectAllByProjectId(Math.toIntExact(projectId));
            List<BudgetProjectFlowApproved> budgetProjectFlowApproveds = BeanConverter
                    .copyBeanList(budgetProjectFlows, BudgetProjectFlowApproved.class);
            for (int i = 0; i < budgetProjectFlowApproveds.size(); i++) {
                budgetProjectFlowApproveds.get(i).setId(null);
                budgetProjectFlowApproveds.get(i).setProjectId(projectId);
                budgetProjectFlowApproveds.get(i).setBudgetProjectFlowId(budgetProjectFlows.get(i).getId());
            }
            // ??????????????????
            if (approveds == null || approveds.size() == 0) {
                // ????????????????????????
                budgetProjectFlowApprovedMapper.insertBatch(budgetProjectFlowApproveds);
            }
            List<BudgetProjectFlow> projectFlows = budgetProjectFlowMapper
                    .selectAllByProjectId(Math.toIntExact(currentFlow.getProjectId()));
            assert projectIds != null;
            insertProjectFlow(projectFlows, projectIds);
        }
    }

    @Override
    public List<Long> getIdListByProjectId(Long projectId) {
        return budgetProjectFlowMapper.selectIdByProjectId(Math.toIntExact(projectId));
    }

    @Override
    public void insertBatch(List<BudgetProjectFlow> list) {
        budgetProjectFlowMapper.insertBatch(list);
    }

    @Override
    public List<BudgetProjectFlow> getAllByProjectId(Integer projectId) {
        return budgetProjectFlowMapper.selectAllByProjectId(projectId);
    }

    @Override
    public String getInform(String flowNodeId, Integer state) {
        return noticeApi.getInform(flowNodeId, state);

    }

    @Override
    public void deleteBatch(List<Long> list) {
        budgetProjectFlowMapper.deleteBatch(list);
    }

    @Override
    public List<BudgetProjectApprovedFlowInfoVO> getBudgetApprovedFlow(Integer projectId, Integer userId) {
        List<BudgetProjectApprovedFlowInfoVO> list = new ArrayList<>();
        String flowName;
        // ???????????????????????????
        List<BudgetProjectFlowVO> flowNodes = budgetProjectFlowApprovedMapper
                .selectApprovedByProjectId(Long.valueOf(projectId));
        if (flowNodes == null || flowNodes.size() == 0) {
            return list;
        }
        // ??????projectId??????flowId
        BudgetProject info = budgetProjectService.getInfo(projectId);
        if (info.getFlowId() != null) {
            // ??????flowId??????????????????
            Flow flow = flowApi.getFlowById(info.getFlowId());
            // ????????????id????????????????????????
            flowName = flow.getFlowName();
        } else {
            // ????????????id????????????????????????
            flowName = flowApi.getNameByNodeId(flowNodes.get(0).getFlowNodeId());

        }
        // ????????????id????????????????????????
        BudgetProjectApproved budgetApproved = budgetProjectApprovedMapper.selectByProjectId(projectId);
        if (budgetApproved == null) {
            return list;
        }
        // ??????????????????
        String projectFlowName = budgetApproved.getProjectName() + flowName;
        // ??????????????????
        for (BudgetProjectFlowVO flowNode : flowNodes) {
            BudgetProjectApprovedFlowInfoVO result = new BudgetProjectApprovedFlowInfoVO();
            BeanConverter.copyProp(flowNode, result);
            result.setApproverRole(flowNode.getRoleDesc());
            result.setRoleUserId(flowNode.getApprover());
            result.setOperation(BudgetProjectFlowOperationVO.builder()
                    .value(flowNode.getOperation()).hidden(true).build());
            result.setSuggestion(flowNode.getSuggestion());
            result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(),
                    Math.toIntExact(flowNode.getProjectId())));
            result.setProjectFlowName(projectFlowName);
            list.add(result);
        }
        return list;
    }


    /**
     * ?????????????????????????????????????????????
     */
    private boolean isAllApproverAssigned(List<BudgetProjectFlowNodeVO> approvers) {
        for (BudgetProjectFlowNodeVO approver : approvers) {
            if (approver.getUserId() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * ????????????????????????
     */
    private BudgetProjectFlowInfoVO buildFlowNode(BudgetProjectFlowVO flowNode,
                                                  String currentNodeId,
                                                  Integer type,
                                                  String projectFlowName) {
        BudgetProjectFlowInfoVO result = new BudgetProjectFlowInfoVO();
        BeanConverter.copyProp(flowNode, result);

        // ???????????????????????????
        boolean isCurrentNode = currentNodeId.equals(flowNode.getFlowNodeId());
        boolean approverReadOnly;
        boolean operationHidden;
        boolean suggestionReadOnly;
        switch (type) {
            // ????????????
            case 1:
                approverReadOnly = true;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                break;
            // ???????????????????????????
            case 2:
                approverReadOnly = isCurrentNode;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                break;
            // ????????????
            default:
                approverReadOnly = true;
                operationHidden = true;
                suggestionReadOnly = true;
        }
        result.setApprover(BudgetProjectFlowApproverVO.builder()
                .value(flowNode.getApprover()).readOnly(approverReadOnly).build());
        result.setOperation(BudgetProjectFlowOperationVO.builder()
                .value(flowNode.getOperation()).hidden(operationHidden).build());
        result.setSuggestion(BudgetProjectFlowSuggestionVO.builder()
                .value(flowNode.getSuggestion()).readOnly(suggestionReadOnly).build());
        result.setApproverSelect(getApproverSelectList(flowNode.getFlowNodeId(),
                Math.toIntExact(flowNode.getProjectId())));
        result.setProjectFlowName(projectFlowName);
        result.setApproverRole(flowNode.getRoleDesc());
        return result;
    }

    /**
     * ???????????????????????????
     */
    private List<FlowRoleResVO> getApproverSelectList(String flowNodeId, Integer projectId) {
        return budgetProjectFlowMapper.selectNodeByNodeId(flowNodeId, projectId);
    }

    /**
     * ?????????????????????????????????id
     *
     * @param list ?????????
     * @return ???????????????id
     */
    private String getCurrentNode(List<NodeOperationReqVO> list) {
        // ????????????operation??????????????????????????????????????????
        for (NodeOperationReqVO vo : list) {
            if (OperationType.UN_EXECUTED.value().equals(vo.getOperation())) {
                return vo.getFlowNodeId();
            }
        }
        return null;
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    private boolean isLastNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (CollectionUtils.isEmpty(flowNodeIds)) {
            return false;
        }
        return currentFlowNodeId.equals(flowNodeIds.get(flowNodeIds.size() - 1));
    }

    /**
     * ????????????????????????????????????
     */
    private String getNextNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (!flowNodeIds.contains(currentFlowNodeId) || isLastNode(currentFlowNodeId,
                flowNodeIds)) {
            return null;
        }
        return flowNodeIds.get(flowNodeIds.indexOf(currentFlowNodeId) + 1);
    }

    /**
     * ???????????????
     *
     * @param operation         ???????????????1??????0?????????
     * @param approvers         ???????????????
     * @param userId            ??????id
     * @param projectId         ??????id
     * @param currentFlowNodeId ???????????????id
     * @param flowNodes         ???????????????id
     * @param approverMap       flowNodeId => userId
     * @param suggestion        ??????
     */
    private void toInform(Integer operation, List<BudgetProjectFlowNodeVO> approvers,
                          Integer userId, Long projectId, String currentFlowNodeId, List<String> flowNodes,
                          Map<String, Integer> approverMap, String suggestion) {
        // ??????flowNodeId??????????????????id
        Long flowTypeId = typeApi.getTypeIdByNode(flowNodes.get(0));
        BudgetProjectDetailVO budgetProject = budgetProjectService.getBudgetProject(projectId);
        // ????????????
        String projectName = budgetProject.getProjectName();
        // ????????????
        String projectNum = budgetProject.getProjectNum();
        // ??????????????????
        UserInfo user = userApi.getUserInfoById(userId);
        // ????????????(??????)
        if (operation.equals(OperationType.AGREE.value())) {
            // ???????????????????????????
            String inform;
            // ?????????????????????????????????????????????????????????
            if (!approvers.get(approvers.size() - 1).getUserId().equals(userId)) {
                inform = getInform(currentFlowNodeId, FlowNodeNoticeState.DEFAULT_REMINDER.value());
                if (inform == null) {
                    return;
                }
                inform = inform.replace(FlowNodeNoticeTemp.TITLE.value(), projectNum + "_" + projectName);
                // ????????????????????????
                Integer nextApprover = approverMap.get(getNextNode(currentFlowNodeId, flowNodes));
                budgetProjectNoticeService.andSaveBudgetProjectNotice(
                        BudgetProjectNoticeReqVO.builder().projectId(Math.toIntExact(projectId))
                                .receiver(nextApprover)
                                .promoter(userId)
                                .content(inform)
                                .flowTypeId(flowTypeId)
                                .build());
                // ????????????
                if (mailEnable) {
                    //  ???????????????????????????
                    UserInfo nextApproverInfo = userApi.getUserInfoById(nextApprover);
                    // ????????????
                    String info = projectNum + "_" + projectName;
                    mailService.postMail(nextApproverInfo.getEmail(), nextApproverInfo.getNickName(), info);
                }
            }
            inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.COMPLETE_REMINDER.value());
            if (inform == null) {
                return;
            }
            inform = inform.replace(FlowNodeNoticeTemp.TITLE.value(), projectNum + "_" + projectName);
            inform = inform.replace(FlowNodeNoticeTemp.APPROVE.value(), user.getNickName());
            // ???????????????
            budgetProjectNoticeService.andSaveBudgetProjectNotice(
                    BudgetProjectNoticeReqVO.builder().projectId(Math.toIntExact(projectId))
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .build());
        }
        // ??????
        else {
            String inform = getInform(currentFlowNodeId,
                    FlowNodeNoticeState.REJECT_REMINDER.value());
            if (inform == null) {
                return;
            }
            String replace = inform.replace(FlowNodeNoticeTemp.TITLE.value(), projectNum + "_" + projectName);
            inform = replace.replace(FlowNodeNoticeTemp.APPROVE.value(), user.getNickName());
            inform = inform.replace(FlowNodeNoticeTemp.CAUSE.value(), suggestion);
            budgetProjectNoticeService.andSaveBudgetProjectNotice(
                    BudgetProjectNoticeReqVO.builder().projectId(Math.toIntExact(projectId))
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .build());
        }
    }

    /**
     * ???????????????????????????
     */
    private void insertProjectFlow(List<BudgetProjectFlow> list, List<Integer> projectList) {
        for (Integer projectId : projectList) {
            for (BudgetProjectFlow budgetProjectFlow : list) {
                budgetProjectFlow.setProjectId(Long.valueOf(projectId));
                budgetProjectFlow.setId(null);
            }
            budgetProjectFlowMapper.insertBatchHaveSuggestion(list);
            List<BudgetProjectFlowApproved> budgetProjectFlowApproveds = BeanConverter
                    .copyBeanList(list, BudgetProjectFlowApproved.class);
            for (int i = 0; i < budgetProjectFlowApproveds.size(); i++) {
                budgetProjectFlowApproveds.get(i).setId(null);
                budgetProjectFlowApproveds.get(i).setBudgetProjectFlowId(list.get(i).getId());
            }
            List<BudgetProjectFlowApproved> approveds = budgetProjectFlowApprovedMapper.selectByProjectId(Long.valueOf(projectId));
            // ??????????????????
            if (approveds == null || approveds.size() == 0) {
                // ????????????
                budgetProjectFlowApprovedMapper.insertBatch(budgetProjectFlowApproveds);
            }
        }
    }

    /**
     * ?????????????????????????????????
     */
    private void insertProjectApprove(Integer projectId) {
        // ?????????????????????
        budgetProjectApprovedMapper.deleteByProjectId(Math.toIntExact(projectId));
        // ??????????????????
        BudgetProject budgetProject = budgetProjectService.getInfo(projectId);
        BudgetProjectApproved budgetProjectApproved = new BudgetProjectApproved();
        BeanUtils.copyProperties(budgetProject, budgetProjectApproved);
        budgetProjectApproved.setId(null);
        budgetProjectApproved.setProjectId(budgetProject.getId());
        budgetProjectApproved.setState(APPROVED.value());
        budgetProjectApproved.setTaixIncloudAmount(budgetProject.getTaxIncludeAmount());
        budgetProjectApproved.setFlowId(Math.toIntExact(budgetProject.getFlowId()));
        budgetProjectApprovedMapper.insertSelective(budgetProjectApproved);
        // ???????????????????????????????????????
        budgetProjectSplitService.deleteApprovedByProjectId(projectId);
        // ????????????????????????
        List<BudgetProjectSplit> budgetProjectSplitList = budgetProjectSplitService.getSplitByProjectId(projectId);
        List<BudgetProjectSplitApproved> splitApproveds = BeanConverter
                .copyBeanList(budgetProjectSplitList, BudgetProjectSplitApproved.class);
        for (BudgetProjectSplitApproved splitApproved : splitApproveds) {
            splitApproved.setId(null);
        }
        budgetProjectSplitService.insertBatchApproved(splitApproveds);
    }
}