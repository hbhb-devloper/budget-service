package com.hbhb.cw.budget.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BudgetProjectNotice {
    private Long id;

    private Integer projectId;

    private Integer receiver;

    private Integer promoter;

    private String content;

    private Integer state;

    private Integer priority;

    private Date createTime;

    private Long flowTypeId;
}