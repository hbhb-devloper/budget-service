package com.hbhb.cw.budget.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetReqVO implements Serializable {

    private static final long serialVersionUID = 5539695326013400495L;
    private Integer unitId;
    private String importDate;
    private String projectItem;
    private String lastYear;
}
