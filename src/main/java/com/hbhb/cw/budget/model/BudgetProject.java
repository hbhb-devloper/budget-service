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
public class BudgetProject implements Serializable {

    private static final long serialVersionUID = 3566293167163312381L;
    private Integer id;

    private Long budgetId;

    private Long unitId;

    private String projectName;

    private BigDecimal amount;

    private BigDecimal availableAmount;

    private BigDecimal taxIncludeAmount;

    private BigDecimal cost;

    private BigDecimal vatRate;

    private BigDecimal vatAmount;

    private String director;

    private String projectNum;

    private Date startTime;

    private Date endTime;

    private String supplier;

    private String origin;

    private String introduction;

    private String detail;

    private String target;

    private String engineeringNum;

    private Integer deleteFlag;

    private String remark;

    private Integer state;

    private Date createTime;

    private String createBy;

    private Long flowId;

    private Date updateTime;

    private String updateBy;

}