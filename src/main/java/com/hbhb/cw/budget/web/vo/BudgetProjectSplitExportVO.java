package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright (c) 2020 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author jiyu@myweimai.com
 * @since 2020-08-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectSplitExportVO {

    @Schema(description = "序号")
    private Integer index;

    @Schema(description = "分类预算名称")
    private String className;

    @Schema(description = "数量")
    private Integer amount;

    @Schema(description = "单价")
    private String price;

    @Schema(description = "金额")
    private String cost;

    @Schema(description = "科目")
    private String itemName;

    @Schema(description = "说明")
    private String explains;
}
