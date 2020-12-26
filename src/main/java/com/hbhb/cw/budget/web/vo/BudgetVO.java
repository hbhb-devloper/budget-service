package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetVO implements Serializable {

    private static final long serialVersionUID = 5114813004970850534L;

    private Long id;

    @Schema(description = "科目名称")
    private String itemName;

    @Schema(description = "科目名称修改时间")
    private Date updateTime;

    @Schema(description = "项目名称")
    private String label;

    @Schema(description = "年限")
    private String importDate;

    @Schema(description = "去年预算值")
    private BigDecimal lastYearBalance;

    @Schema(description = "去年完成值")
    private BigDecimal lastYearFinishedBalance;

    @Schema(description = "项目名称")
    private String projectItem;

    @Schema(description = "本年预算值")
    private BigDecimal balance;

    @Schema(description = "金额阀值")
    private BigDecimal threshold;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "科目id")
    private Long budgetItemId;

    @Schema(description = "项目编号")
    private String budgetNum;

    private Boolean hasChildren;

    private List<BudgetVO> children;
}
