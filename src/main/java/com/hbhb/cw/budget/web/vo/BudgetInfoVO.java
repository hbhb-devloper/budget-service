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
public class BudgetInfoVO implements Serializable {

    private static final long serialVersionUID = 8491694451486418074L;

    @Schema(description = "预算表id(必填)")
    private Long id;

    @Schema(description = "科目id（必填）")
    private Long budgetItemId;

    @Schema(description = "归口单位（必填）")
    private Integer underUnitId;

    @Schema(description = "项目名称（必填）")
    private String projectItem;

    @Schema(description = "预算编号（必填）")
    private String budgetNum;

    @Schema(description = "去年预算值(无需赋值)")
    private BigDecimal lastYearBalance;

    @Schema(description = "金额阀值（选填）")
    private BigDecimal threshold;

    @Schema(description = "备注（选填）")
    private String remark;

    @Schema(description = "导入年限（必填）")
    private String importDate;
}
