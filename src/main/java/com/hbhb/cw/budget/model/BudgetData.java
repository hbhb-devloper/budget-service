package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
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
public class BudgetData implements Serializable {

    private static final long serialVersionUID = 2753395451868317134L;

    private Long id;
    @ApiModelProperty("预算id")
    private Long budgetId;
    @ApiModelProperty("单位id")
    private Integer unitId;
    @ApiModelProperty("预算金额")
    private BigDecimal balance;
    @ApiModelProperty("导入时间")
    private Date createTime;
}
