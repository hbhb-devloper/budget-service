package com.hbhb.cw.budget.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * @author yzc
 * @since 2020-10-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectAgileExportWordVO implements Serializable {
    private static final long serialVersionUID = -1420661272224397757L;

    @Schema(description = "预算名称")
    private String budgetName;

    @Schema(description = "不含税金额")
    private String cost;

    @Schema(description = "签报名称")
    private String projectName;

    @Schema(description = "签报编号")
    private String projectNum;

    @Schema(description = "税率")
    private String vatRate;

    @Schema(description = "价税合计")
    private String taxIncludeAmount;

    @Schema(description = "单位名称")
    private String unitName;

    @Schema(description = "申报人")
    private String createBy;

    @Schema(description = "附件")
    private List<BudgetProjectAgileFileVO> files;
}
