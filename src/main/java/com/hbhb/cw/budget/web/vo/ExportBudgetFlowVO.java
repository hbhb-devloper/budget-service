package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportBudgetFlowVO {

    @Schema(description = "审批人角色名称")
    private String approverRole;

    @Schema(description = "审批人名称")
    private String userName;

    @Schema(description = "审批意见")
    private BudgetProjectFlowSuggestionVO suggestion;

    @Schema(description = "更新时间")
    String updateTime;
}
