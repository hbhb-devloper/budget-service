package com.hbhb.cw.budget.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author yzc
 * @since 2020-09-24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetAgileAddVO implements Serializable {

    private static final long serialVersionUID = 8871063645538815289L;

    @Schema(description = "预算类型（预算编号）")
    private String budgetType;

    @Schema(description = "本年时间（不需要传）")
    private String importDate;

    @Schema(description = "不含税金额")
    private BigDecimal cost;

    @Schema(description = "签报名称")
    private String projectName;

    @Schema(description = "税率")
    private BigDecimal vatRate;

    @Schema(description = "价税合计")
    private BigDecimal taxIncludeAmount;

    @Schema(description = "附件")
    private List<BudgetProjectAgileFileVO> files;
}
