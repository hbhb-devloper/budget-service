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
public class BudgetAdjustVO implements Serializable {
    private static final long serialVersionUID = 7450681919351968963L;

    @Schema(description = "预算id")
    private Long budgetId;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "本年预算值")
    private BigDecimal balance;

    @Schema(description = "归口部门单位")
    private Integer deptId;

    @Schema(description = "金额阀值")
    private BigDecimal threshold;

    @Schema(description = "备注")
    private String remark;
}
