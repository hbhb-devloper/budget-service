package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetHistory implements Serializable {
    private static final long serialVersionUID = 8207053931861209649L;

    private Long id;

    /**
     * 预算id
     */
    private Long budgetId;
    /**
     * 单位id
     */
    private Integer unitId;
    /**
     * 调整前预算值
     */
    private BigDecimal oldValue;
    /**
     * 调整后预算值
     */
    private BigDecimal newValue;
    /**
     * 差值
     */
    private BigDecimal difValue;
    /**
     * 调整时间
     */
    private Date createTime;

    private String createBy;
}