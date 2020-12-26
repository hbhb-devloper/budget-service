package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yzc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectSplitApproved implements Serializable {

    private static final long serialVersionUID = 2878828144315427067L;
    private Integer id;

    private Integer budgetProjectSplitId;

    private Integer projectId;

    private String year;

    private String className;

    private BigDecimal price;

    private Integer amount;

    private BigDecimal cost;

    private String explains;

    private Date createTime;

    private String createBy;
}