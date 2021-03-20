package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetHistoryInfoVO implements Serializable {

    private static final long serialVersionUID = 237407316120710863L;

    @Schema(description = "id")
    private Long id;

    @Schema(description = "项目名称")
    private String budgetName;

    @Schema(description = "项目编号")
    private String budgetNum;

    @Schema(description = "调整前预算值")
    private BigDecimal oldValue;

    @Schema(description = "调整后预算值")
    private BigDecimal newValue;

    @Schema(description = "差值")
    private BigDecimal difValue;

    @Schema(description = "备注")
    private String remark;
}
