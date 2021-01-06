package com.hbhb.cw.budget.web.vo;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetFlowStateVO implements Serializable {

    private static final long serialVersionUID = 3648789198558319474L;
    @Schema(description = "单位id")
    private Integer unitId;
    @Schema(description = "预算id")
    private Long budgetId;
    @Schema(description = "审批状态")
    private Integer state;
    @Schema(description = "导入时间")
    private String importDate;
    @Schema(description = "预算编号+年份")
    private String serialNum;
}
