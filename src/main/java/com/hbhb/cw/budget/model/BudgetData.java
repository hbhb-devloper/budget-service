package com.hbhb.cw.budget.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author yzc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetData implements Serializable {

    private static final long serialVersionUID = 2753395451868317134L;

    private Long id;
    @Schema(description = "预算id")
    private Long budgetId;
    @Schema(description = "单位id")
    private Integer unitId;
    @Schema(description = "预算金额")
    private BigDecimal balance;
    @Schema(description = "导入时间")
    private Date createTime;
}
