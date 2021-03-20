package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (c) 2020 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author jiyu@myweimai.com
 * @since 2020-08-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectDetailExportReqVO implements Serializable {
    private static final long serialVersionUID = 350194395739690513L;

    @Schema(description = "项目签报的流程节点")
    private List<BudgetProjectFlowInfoVO> flows;

    @Schema(description = "项目签报的统计信息")
    private List<BudgetProgressDeclareVO> statistics;

    @Schema(description = "项目签报的详细信息")
    private BudgetProjectDetailVO detail;

    @Schema(description = "项目签报的分类预算信息")
    private List<BudgetProjectSplitVO> splits;
}
