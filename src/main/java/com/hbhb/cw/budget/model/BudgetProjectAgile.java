package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
public class BudgetProjectAgile implements Serializable {

    private static final long serialVersionUID = -5760343338585208472L;

    private Long id;

    private String projectNum;

    private String projectName;

    private Integer unitId;

    private Long budgetId;

    private BigDecimal cost;

    private BigDecimal vatRate;

    private BigDecimal taxIncludeAmount;

    private String createBy;

    private Date createTime;
}