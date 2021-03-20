package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分类预算
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectSplitVO implements Serializable {
    private static final long serialVersionUID = 3599538111022128365L;
    private Integer id;

    @Schema(description = "预算科目id")
    private Long budgetId;

    @Schema(description = "签报id")
    private Integer projectId;

    @Schema(description = "年份:yyyy")
    private String years;

    @Schema(description = "分类预算名称")
    private String className;

    @Schema(description = "单价")
    private String price;

    @Schema(description = "数量")
    private Integer amount;

    @Schema(description = "金额")
    private String cost;

    @Schema(description = "科目")
    private String itemName;

    @Schema(description = "说明")
    private String explains;


}
