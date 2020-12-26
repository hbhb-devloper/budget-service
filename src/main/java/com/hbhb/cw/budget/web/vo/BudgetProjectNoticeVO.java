package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetProjectNoticeVO implements Serializable {

    private static final long serialVersionUID = 1879117041887648872L;

    @Schema(description = "当前用户id")
    private int userId;

    @Schema(description = "签报编号")
    private String projectNum;

    @Schema(description = "判断区间第一个值")
    private String firstNum;

    @Schema(description = "判断区间第二个值")
    private String twoNum;

}
