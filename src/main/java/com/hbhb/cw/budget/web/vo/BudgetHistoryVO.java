package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetHistoryVO implements Serializable {

    private static final long serialVersionUID = -1605393027935400166L;

    @Schema(description = "预算id")
    private Long budgetId;

    @Schema(description = "预算科目/项目名称")
    private String budgetName;

    @Schema(description = "调整前预算值")
    private BigDecimal oldValue;

    @Schema(description = "调整后预算值")
    private BigDecimal newValue;

    @Schema(description = "差值")
    private BigDecimal difValue;

    @Schema(description = "金额阀值")
    private BigDecimal threshold;

    @Schema(description = "备注")
    private String remark;

    private Boolean hasChildren;

    private List<BudgetHistoryVO> children;
}
