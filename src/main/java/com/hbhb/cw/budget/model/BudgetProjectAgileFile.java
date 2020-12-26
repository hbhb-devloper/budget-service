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
public class BudgetProjectAgileFile implements Serializable {

    private static final long serialVersionUID = -6908445835735236226L;
    private Long id;

    private Long projectAgileId;

    private Boolean required;

    private String author;

    private Date createTime;

    private Long fileId;
}