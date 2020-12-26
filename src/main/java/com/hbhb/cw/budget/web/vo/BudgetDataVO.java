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
public class BudgetDataVO implements Serializable {

    private static final long serialVersionUID = -795567809055547683L;

    @Schema(description = "预算id")
    private Long budgetId;
    @Schema(description = "单位id")
    private Integer unitId;
    @Schema(description = "预算金额")
    private BigDecimal balance;
    @Schema(description = "导入时间")
    private String createTime;
    @Schema(description = "归口单位id")
    private Integer deptId;
}
