package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("审批-历史审批信息")
public class BudgetProjectFlowHistory implements Serializable {

    private static final long serialVersionUID = 9007574893228734468L;

    @ApiModelProperty("主键id")
    private Long id;
    @ApiModelProperty("项目签报id")
    private Long projectId;
    @ApiModelProperty("节点id")
    private String flowNodeId;
    @ApiModelProperty("归口单位id")
    private Integer underUnitId;
    @ApiModelProperty("审批人角色id")
    private Long flowRoleId;
    @ApiModelProperty("审批人用户id")
    private Integer userId;
    @ApiModelProperty("审批人用户名称")
    private String userName;
    @ApiModelProperty("用户描述")
    private String roleDesc;
    @ApiModelProperty("分配者id")
    private Long assigner;
    @ApiModelProperty("是否能够自定义流程（0-否、1-是）")
    private Boolean controlAccess;
    @ApiModelProperty("是否允许被设置不参与流程（0-不参与、1-参与）")
    private Boolean isJoin;
    @ApiModelProperty("操作（同意，拒绝，未操作）")
    private Integer operation;
    @ApiModelProperty("意见")
    private String suggestion;
    @ApiModelProperty("是否删除")
    private Boolean isDelete;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("创建者")
    private String createBy;
    @ApiModelProperty("更新时间")
    private Date updateTime;
    @ApiModelProperty("更新者")
    private String updateBy;

}