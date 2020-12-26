package com.hbhb.cw.budget.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectSplit {

    private Integer id;

    private Integer projectId;

    private String years;

    private String className;

    private BigDecimal price;

    private Integer amount;

    private BigDecimal cost;

    private String explains;

    private Date createTime;

    private String createBy;
}