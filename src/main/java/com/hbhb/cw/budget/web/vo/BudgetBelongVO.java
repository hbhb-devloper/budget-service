package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yzc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetBelongVO implements Serializable {

    private static final long serialVersionUID = -6932344682550071194L;

    @Schema(description = "归口部门")
    private Integer deptId;

    @Schema(description = "预算id")
    private Long budgetId;
}
