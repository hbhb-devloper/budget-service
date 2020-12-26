package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetItem implements Serializable {
    private static final long serialVersionUID = -1564931426854896159L;

    private Long id;

    private String itemName;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

}