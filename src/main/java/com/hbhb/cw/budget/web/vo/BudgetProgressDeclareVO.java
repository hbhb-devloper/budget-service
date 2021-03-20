package com.hbhb.cw.budget.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BudgetProgressDeclareVO implements Serializable {

    private static final long serialVersionUID = -2062912433117838811L;

    @Schema(description = "预算id")
    private Long budgetId;

    @Schema(description = "序号")
    private Long lineNumber;

    @Schema(description = "科目名称")
    private String budgetItemName;

    @Schema(description = "项目名称")
    private String projectItemName;

    @Schema(description = "该项目下单位预算值")
    private BigDecimal budgetBalance;

    @Schema(description = "立项值")
    private BigDecimal amount;

    @Schema(description = "本年结余")
    private BigDecimal surplus;

    @Schema(description = "审批通过")
    private BigDecimal declared;

    @Schema(description = "审批通过(百分比)")
    private BigDecimal declaredPer;

    @Schema(description = "正在审批")
    private BigDecimal declaring;

    @Schema(description = "正在审批（百分比）")
    private BigDecimal declaringPer;

    @Schema(description = "未审批")
    private BigDecimal declaration;

    @Schema(description = "未审批（百分比）")
    private BigDecimal declarationPer;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "备注")
    private String remark;
}
