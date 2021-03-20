package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yzc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProgressResVO implements Serializable {

    private static final long serialVersionUID = -7732509246795048333L;

    @Schema(description = "行号")
    private Long budgetId;

    @Schema(description = "科目名称")
    private String budgetItemName;

    @Schema(description = "类别编号")
    private String budgetNum;

    @Schema(description = "类别名称")
    private String projectItemName;

    @Schema(description = "预算值")
    private BigDecimal budgetBalance;

    @Schema(description = "立项值")
    private BigDecimal amount;

    @Schema(description = "本年结余")
    private BigDecimal surplus;

    @Schema(description = "进度百分比")
    private BigDecimal percentage;

    @Schema(description = "单位id")
    private String unitName;

    @Schema(description = "备注")
    private String remark;
}