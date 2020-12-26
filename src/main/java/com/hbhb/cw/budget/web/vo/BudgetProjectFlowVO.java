package com.hbhb.cw.budget.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetProjectFlowVO implements Serializable {

    private static final long serialVersionUID = 8092512014163540510L;

    private Long id;
    private Long projectId;
    private String flowNodeId;
    private Long flowRoleId;
    private String approverRole;
    private String nickName;
    private Integer approver;
    private Integer operation;
    private String suggestion;
    private Boolean controlAccess;
    private Boolean isJoin;
    private Long assigner;
    private String roleDesc;
    private String updateTime;
}
