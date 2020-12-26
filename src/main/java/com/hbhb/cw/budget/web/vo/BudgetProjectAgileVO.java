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
 * @since 2020-09-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BudgetProjectAgileVO implements Serializable {

    private static final long serialVersionUID = 7001686391568175902L;

    private Long id;

    @Schema(description = "序号")
    private String lineNumber;

    @Schema(description = "编号")
    private String projectNum;

    @Schema(description = "名称")
    private String projectName;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "项目类型")
    private String budgetName;

    @Schema(description = "不含税金额")
    private BigDecimal cost;

    @Schema(description = "税率")
    private String vatRate;

    @Schema(description = "价税合计")
    private BigDecimal taxIncludeAmount;

    @Schema(description = "申报人")
    private String createBy;
}
