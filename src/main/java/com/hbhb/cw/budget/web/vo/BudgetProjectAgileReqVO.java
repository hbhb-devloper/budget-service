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
public class BudgetProjectAgileReqVO implements Serializable {

    private static final long serialVersionUID = 6529032254520825056L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "查询时间")
    private String date;

    @Schema(description = "预算类型")
    private String budgetType;

    @Schema(description = "编号")
    private String projectNum;

    @Schema(description = "不含税金额")
    private BigDecimal cost;

    @Schema(description = "预算执行导入时间（不需要传）")
    private String importDate;
}
