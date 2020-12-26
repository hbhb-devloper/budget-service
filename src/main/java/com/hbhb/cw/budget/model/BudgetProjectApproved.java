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
public class BudgetProjectApproved implements Serializable {

    private static final long serialVersionUID = -4900414854946496543L;
    private Long id;

    private String projectNum;

    private String projectName;

    private BigDecimal amount;

    private BigDecimal availableAmount;

    private BigDecimal taixIncloudAmount;

    private BigDecimal cost;

    private BigDecimal vatRate;

    private BigDecimal vatAmount;

    private String director;

    private String engineeringNum;

    private Date startTime;

    private Date endTime;

    private String supplier;

    private String origin;

    private String introduction;

    private String detail;

    private String target;

    private String remarks;

    private Integer state;

    private String createBy;

    private Date createTime;

    private Integer flowId;

    private Integer projectId;
}