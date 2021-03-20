package com.hbhb.cw.budget.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDataReqVO implements Serializable {
    private static final long serialVersionUID = 479675719165112277L;

    private Long budgetId;
    private Long unitId;
}