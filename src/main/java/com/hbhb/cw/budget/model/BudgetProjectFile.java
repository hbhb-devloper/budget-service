package com.hbhb.cw.budget.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetProjectFile {

    private Long id;

    private Long fileId;

    private String author;

    private Date createTime;

    private Long projectId;

    private Byte required;

    private Byte isApproved;


}