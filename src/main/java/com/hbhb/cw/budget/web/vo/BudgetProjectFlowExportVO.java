package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright (c) 2020 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author jiyu@myweimai.com
 * @since 2020-08-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectFlowExportVO {
    @Schema(description = "审批人角色名称")
    private String approverRole;

    @Schema(description = "审批人名称")
    private String nickName;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "审批意见")
    private String suggestion;
}
