package com.hbhb.cw.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectFlowHistory implements Serializable {

    private static final long serialVersionUID = 9007574893228734468L;

    @Schema(description = "主键id")
    private Long id;
    @Schema(description = "项目签报id")
    private Long projectId;
    @Schema(description = "节点id")
    private String flowNodeId;
    @Schema(description = "归口单位id")
    private Integer underUnitId;
    @Schema(description = "审批人角色id")
    private Long flowRoleId;
    @Schema(description = "审批人用户id")
    private Integer userId;
    @Schema(description = "审批人用户名称")
    private String userName;
    @Schema(description = "用户描述")
    private String roleDesc;
    @Schema(description = "分配者id")
    private Long assigner;
    @Schema(description = "是否能够自定义流程（0-否、1-是）")
    private Boolean controlAccess;
    @Schema(description = "是否允许被设置不参与流程（0-不参与、1-参与）")
    private Boolean isJoin;
    @Schema(description = "操作（同意，拒绝，未操作）")
    private Integer operation;
    @Schema(description = "意见")
    private String suggestion;
    @Schema(description = "是否删除")
    private Boolean isDelete;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "创建者")
    private String createBy;
    @Schema(description = "更新时间")
    private Date updateTime;
    @Schema(description = "更新者")
    private String updateBy;

}