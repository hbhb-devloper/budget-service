package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectFlowApproved implements Serializable {

    private static final long serialVersionUID = 3757234321179709368L;
    private Long id;

    private Long budgetProjectFlowId;

    private Long projectId;

    private String flowNodeId;

    private Integer underUnitId;

    private Long flowRoleId;

    private Integer userId;

    private String roleDesc;

    private Long assigner;

    private Boolean controlAccess;

    private Boolean isJoin;

    private Integer operation;

    private String suggestion;

    private Boolean isDelete;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

}