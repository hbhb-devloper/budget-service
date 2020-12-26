package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProgressReqVO implements Serializable {

    private static final long serialVersionUID = -1635164445622875008L;

    @Schema(description = "单位id", required = true)
    private Integer unitId;

    @Schema(description = "项目名称")
    private String projectItem;

    @Schema(description = "时间(年限)")
    private String year;

    @Schema(description = "时间(精确到月)")
    private String date;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "预算id")
    private Long budgetId;
}