package com.hbhb.cw.budget.model;

import java.io.Serializable;

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
public class BudgetBelong implements Serializable {

    private static final long serialVersionUID = 4086304855014150400L;

    private Long id;

    private Long budgetId;

    private Integer unitId;

    private Integer underUnitId;

    private String serialNum;
}