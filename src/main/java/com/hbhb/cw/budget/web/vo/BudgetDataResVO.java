package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xiaokang
 * @since 2020-08-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDataResVO implements Serializable {
    private static final long serialVersionUID = -6541796354588830219L;

    private Long id;
    @Schema(description = "预算id")
    private Long budgetId;
    @Schema(description = "单位id")
    private Integer unitId;
    @Schema(description = "预算金额")
    private BigDecimal balance;
    @Schema(description = "导入时间")
    private Date createTime;
    @Schema(description = "归口单位")
    private Integer underUnitId;
    @Schema(description = "单位名称")
    private String underUnitName;
    @Schema(description = "是否禁用（0禁用）")
    private Integer flag;
}
