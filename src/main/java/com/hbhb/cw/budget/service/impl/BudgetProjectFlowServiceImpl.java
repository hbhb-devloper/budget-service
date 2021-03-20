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
        // 查询流程的所有节点
        List<BudgetProjectFlowVO> flowNodes = budgetProjectFlowMapper.selectByProjectId(projectId);
        // 通过projectId得到flowId
        BudgetProject info = budgetProjectService.getInfo(projectId);
        if (info.getFlowId() != null) {
            // 通过flowId得到流程名称
            Flow flow = flowApi.getFlowById(info.getFlowId());
            // 通过节点id得到流程类型名称
            flowName = flow.getFlowName();
        } else {
            // 通过节点id得到流程类型名称
            flowName = flowApi.getNameByNodeId(flowNodes.get(0).getFlowNodeId());

        }
        // 通过签报id得到签报名称
        BudgetProject project = budgetProjectService.getInfo(projectId);
        // 签报流程名称
        String projectFlowName = project.getProjectName() + flowName;
        // 通过userId得到nickName
        UserInfo userInfo = userApi.getUserInfoById(userId);
        // 如果流程已结束，参与流程的角色登入
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
        // 通过userId得到该用户的所有流程角色
        List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
        // 1.先获取流程流转的当前节点<currentNode>
        // 2.再判断<loginUser>是否为<currentNode>的审批人
        //   2-1.如果不是，则所有节点信息全部为只读
        //   2-2.如果是，则判断是否为该流程的分配者
        //      a.如果不是分配者，则只能编辑当前节点的按钮操作<operation>和意见<suggestion>
        //      b.如果是分配者，则可以编辑以下：
        //        当前节点的按钮操作<operation>和意见<suggestion>
        //        其他节点的审批人<approver>

        // 1.先获取流程流转的当前节点
        List<NodeOperationReqVO> voList = new ArrayList<>();
        flowNodes.forEach(flowNode -> voList.add(NodeOperationReqVO.builder()
                .flowNodeId(flowNode.getFlowNodeId())
                .operation(flowNode.getOperation())
                .build()));
        // 当前节点id
        String currentNodeId = getCurrentNode(voList);
        if (!StringUtils.isEmpty(currentNodeId)) {
            BudgetProjectFlowVO currentNode = flowNodeMap.get(currentNodeId);
            // 2.判断登录用户是否为该审批人
            if (!userId.equals(currentNode.getApprover())) {
                // 2-1.如果不是，则所有节点信息全部为只读
                flowNodes.forEach(flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 0, projectFlowName)));
            } else {
                // 2-2-a.如果是审批人，且为分配者
                if (flowRoleIds.contains(currentNode.getAssigner())) {
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 2, projectFlowName)));
                } else {
                    // 2-2-b.如果是审批人，但不是分配者
                    flowNodes.forEach(
                            flowNode -> list.add(buildFlowNode(flowNode, currentNodeId, 1, projectFlowName)));
                }
            }
        }
        // 如果是审批人，但是默认用户下拉框没有该用户的时候，加上该用户
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
        // 审批未结束，具有节点审批人权限的人登入
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
        // 查询当前节点信息
        BudgetProjectFlow currentFlow = budgetProjectFlowMapper
                .selectByPrimaryKey(approveVO.getId());
        // 校验审批人是否为本人
        if (!currentFlow.getUserId().equals(userId)) {
            throw new BudgetException(BudgetErrorCode.LOCK_OF_APPROVAL_ROLE);
        }


        // 项目签报id
        Long projectId = currentFlow.getProjectId();
        // 判断签报状态
        List<BudgetProjectFlowApproved> approveds = budgetProjectFlowApprovedMapper.selectByProjectId(projectId);
        // 当前节点id
        String currentFlowNodeId = currentFlow.getFlowNodeId();
        // 所有审批人
        List<BudgetProjectFlowNodeVO> approvers = approveVO.getApprovers();
        // 所有节点id
        List<String> flowNodes = approvers.stream().map(
                BudgetProjectFlowNodeVO::getFlowNodeId).collect(Collectors.toList());
        // 节点所对应的用户id
        // flowNodeId => userId
        Map<String, Integer> approverMap = new HashMap<>();
        for (BudgetProjectFlowNodeVO approver : approvers) {
            approverMap.put(approver.getFlowNodeId(), approver.getUserId());
        }
        Integer operation = null;
        Integer FlowState = null;
        List<Integer> projectIds = new ArrayList<>();
        // 同意
        if (approveVO.getOperation().equals(OperationType.AGREE.value())) {
            operation = OperationType.AGREE.value();
            // 判断是否为最后一个节点，如果是，则更新项目流程状态
            if (isLastNode(currentFlowNodeId, flowNodes)) {
                // 判断是否为调整后的审批
                if (budgetProjectApprovedMapper.selectByProjectId(Math.toIntExact(projectId)) != null) {
                    FlowState = ADJUST_APPROVED.value();
                } else {
                    FlowState = APPROVED.value();
                    // 1.得到新增签报的id列表 2，如果id列表有值，则新增流程信息列表，列表内容和当前的列表一致（状态为31） 3，同时新增快照
                    projectIds = budgetProjectService.addProject(Math.toIntExact(projectId));
                    if (approveds == null || approveds.size() == 0) {
                        if (projectIds != null && projectIds.size() != 0) {
                            // 新建快照
                            for (Integer id : projectIds) {
                                insertProjectApprove(id);
                            }
                        }
                    }
                }
                if (approveds == null || approveds.size() == 0) {
                    // 添加到快照
                    insertProjectApprove(Math.toIntExact(projectId));
                }
            } else {
                // 获取用户的所有流程角色
                List<Long> flowRoleIds = roleUserApi.getRoleIdByUserId(userId);
                // 判断当前用户是否为分配者。如果是分配者，则判断是否已指定所有审批人
                if (flowRoleIds.contains(currentFlow.getAssigner())) {
                    // 判断是否所有审批人已指定
                    if (!isAllApproverAssigned(approvers)) {
                        throw new BudgetException(BudgetErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                    // 更新各节点审批人
                    budgetProjectFlowMapper.updateBatchByNodeId(approvers, projectId);
                }
                // 如果不是分配者，则判断是否已指定下一个审批人
                else {
                    // 校验下一个节点的审批人是否已指定
                    Integer nextApprover = approverMap
                            .get(getNextNode(currentFlowNodeId, flowNodes));
                    if (nextApprover == null) {
                        throw new BudgetException(BudgetErrorCode.NOT_ALL_APPROVERS_ASSIGNED);
                    }
                }
            }
        }
        // 拒绝
        else if (approveVO.getOperation().equals(OperationType.REJECT.value())) {
            operation = OperationType.REJECT.value();
            FlowState = APPROVE_REJECTED.value();
        }

        // 同意或者拒绝后对该签报的代办提醒进行删除
        budgetProjectNoticeService.updateByBudgetProjectId(Math.toIntExact(projectId));

        // 推送提醒
        assert operation != null;
        toInform(operation, approvers, userId, projectId, currentFlowNodeId, flowNodes,
                approverMap, approveVO.getSuggestion());

        // 更新项目节点信息
        budgetProjectFlowMapper.updateByPrimaryKeySelective(BudgetProjectFlow.builder()
                .operation(operation)
                .suggestion(approveVO.getSuggestion())
                .updateTime(new Date())
                .id(approveVO.getId())
                .build());

        // 更新项目的流程状态
        if (FlowState != null) {
            budgetProjectService.updateState(Math.toIntExact(projectId), FlowState);
        }

        // 判断是否为最后一个节点且同意
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
            // 判断签报状态
            if (approveds == null || approveds.size() == 0) {
                // 添加流程信息快照
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
        // 查询流程的所有节点
        List<BudgetProjectFlowVO> flowNodes = budgetProjectFlowApprovedMapper
                .selectApprovedByProjectId(Long.valueOf(projectId));
        if (flowNodes == null || flowNodes.size() == 0) {
            return list;
        }
        // 通过projectId得到flowId
        BudgetProject info = budgetProjectService.getInfo(projectId);
        if (info.getFlowId() != null) {
            // 通过flowId得到流程名称
            Flow flow = flowApi.getFlowById(info.getFlowId());
            // 通过节点id得到流程类型名称
            flowName = flow.getFlowName();
        } else {
            // 通过节点id得到流程类型名称
            flowName = flowApi.getNameByNodeId(flowNodes.get(0).getFlowNodeId());

        }
        // 通过签报id得到签报快照名称
        BudgetProjectApproved budgetApproved = budgetProjectApprovedMapper.selectByProjectId(projectId);
        if (budgetApproved == null) {
            return list;
        }
        // 签报流程名称
        String projectFlowName = budgetApproved.getProjectName() + flowName;
        // 流程信息赋值
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
     * 判断是否所有节点的审批人已指定
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
     * 组装流程节点属性
     */
    private BudgetProjectFlowInfoVO buildFlowNode(BudgetProjectFlowVO flowNode,
                                                  String currentNodeId,
                                                  Integer type,
                                                  String projectFlowName) {
        BudgetProjectFlowInfoVO result = new BudgetProjectFlowInfoVO();
        BeanConverter.copyProp(flowNode, result);

        // 判断是否为当前节点
        boolean isCurrentNode = currentNodeId.equals(flowNode.getFlowNodeId());
        boolean approverReadOnly;
        boolean operationHidden;
        boolean suggestionReadOnly;
        switch (type) {
            // 审批节点
            case 1:
                approverReadOnly = true;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                break;
            // 审批节点（分配者）
            case 2:
                approverReadOnly = isCurrentNode;
                operationHidden = !isCurrentNode;
                suggestionReadOnly = !isCurrentNode;
                break;
            // 默认只读
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
     * 查询审批人下拉框值
     */
    private List<FlowRoleResVO> getApproverSelectList(String flowNodeId, Integer projectId) {
        return budgetProjectFlowMapper.selectNodeByNodeId(flowNodeId, projectId);
    }

    /**
     * 获取流程流转的当前节点id
     *
     * @param list 已排序
     * @return 当前节点的id
     */
    private String getCurrentNode(List<NodeOperationReqVO> list) {
        // 通过检查operation状态来确定流程流传到哪个节点
        for (NodeOperationReqVO vo : list) {
            if (OperationType.UN_EXECUTED.value().equals(vo.getOperation())) {
                return vo.getFlowNodeId();
            }
        }
        return null;
    }

    /**
     * 判断当前节点是否为流程中的最后一个节点
     */
    private boolean isLastNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (CollectionUtils.isEmpty(flowNodeIds)) {
            return false;
        }
        return currentFlowNodeId.equals(flowNodeIds.get(flowNodeIds.size() - 1));
    }

    /**
     * 获取当前节点的下一个节点
     */
    private String getNextNode(String currentFlowNodeId, List<String> flowNodeIds) {
        if (!flowNodeIds.contains(currentFlowNodeId) || isLastNode(currentFlowNodeId,
                flowNodeIds)) {
            return null;
        }
        return flowNodeIds.get(flowNodeIds.indexOf(currentFlowNodeId) + 1);
    }

    /**
     * 推送提醒人
     *
     * @param operation         是否同意（1同意0拒绝）
     * @param approvers         所有审批人
     * @param userId            用户id
     * @param projectId         签报id
     * @param currentFlowNodeId 当前的节点id
     * @param flowNodes         所有的节点id
     * @param approverMap       flowNodeId => userId
     * @param suggestion        意见
     */
    private void toInform(Integer operation, List<BudgetProjectFlowNodeVO> approvers,
                          Integer userId, Long projectId, String currentFlowNodeId, List<String> flowNodes,
                          Map<String, Integer> approverMap, String suggestion) {
        // 通过flowNodeId得到流程类型id
        Long flowTypeId = typeApi.getTypeIdByNode(flowNodes.get(0));
        BudgetProjectDetailVO budgetProject = budgetProjectService.getBudgetProject(projectId);
        // 签报名称
        String projectName = budgetProject.getProjectName();
        // 项目编号
        String projectNum = budgetProject.getProjectNum();
        // 获取用户姓名
        UserInfo user = userApi.getUserInfoById(userId);
        // 提醒信息(同意)
        if (operation.equals(OperationType.AGREE.value())) {
            // 判断是否为最后一位
            String inform;
            // 不是最后一位（提醒发起人和下一位节点）
            if (!approvers.get(approvers.size() - 1).getUserId().equals(userId)) {
                inform = getInform(currentFlowNodeId, FlowNodeNoticeState.DEFAULT_REMINDER.value());
                if (inform == null) {
                    return;
                }
                inform = inform.replace(FlowNodeNoticeTemp.TITLE.value(), projectNum + "_" + projectName);
                // 推送下一位审批者
                Integer nextApprover = approverMap.get(getNextNode(currentFlowNodeId, flowNodes));
                budgetProjectNoticeService.andSaveBudgetProjectNotice(
                        BudgetProjectNoticeReqVO.builder().projectId(Math.toIntExact(projectId))
                                .receiver(nextApprover)
                                .promoter(userId)
                                .content(inform)
                                .flowTypeId(flowTypeId)
                                .build());
                // 推送邮箱
                if (mailEnable) {
                    //  下一节点审批人信息
                    UserInfo nextApproverInfo = userApi.getUserInfoById(nextApprover);
                    // 推送内容
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
            // 推送发起人
            budgetProjectNoticeService.andSaveBudgetProjectNotice(
                    BudgetProjectNoticeReqVO.builder().projectId(Math.toIntExact(projectId))
                            .receiver(approvers.get(0).getUserId())
                            .promoter(userId)
                            .content(inform)
                            .flowTypeId(flowTypeId)
                            .build());
        }
        // 拒绝
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
     * 跨年时新增流程信息
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
            // 判断签报状态
            if (approveds == null || approveds.size() == 0) {
                // 添加快照
                budgetProjectFlowApprovedMapper.insertBatch(budgetProjectFlowApproveds);
            }
        }
    }

    /**
     * 添加签报和分类预算快照
     */
    private void insertProjectApprove(Integer projectId) {
        // 删除前一个快照
        budgetProjectApprovedMapper.deleteByProjectId(Math.toIntExact(projectId));
        // 添加签报快照
        BudgetProject budgetProject = budgetProjectService.getInfo(projectId);
        BudgetProjectApproved budgetProjectApproved = new BudgetProjectApproved();
        BeanUtils.copyProperties(budgetProject, budgetProjectApproved);
        budgetProjectApproved.setId(null);
        budgetProjectApproved.setProjectId(budgetProject.getId());
        budgetProjectApproved.setState(APPROVED.value());
        budgetProjectApproved.setTaixIncloudAmount(budgetProject.getTaxIncludeAmount());
        budgetProjectApproved.setFlowId(Math.toIntExact(budgetProject.getFlowId()));
        budgetProjectApprovedMapper.insertSelective(budgetProjectApproved);
        // 删除快照表中的分类预算快照
        budgetProjectSplitService.deleteApprovedByProjectId(projectId);
        // 新增分类预算快照
        List<BudgetProjectSplit> budgetProjectSplitList = budgetProjectSplitService.getSplitByProjectId(projectId);
        List<BudgetProjectSplitApproved> splitApproveds = BeanConverter
                .copyBeanList(budgetProjectSplitList, BudgetProjectSplitApproved.class);
        for (BudgetProjectSplitApproved splitApproved : splitApproveds) {
            splitApproved.setId(null);
        }
        budgetProjectSplitService.insertBatchApproved(splitApproveds);
    }
}