package com.hbhb.cw.budget.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BudgetProgressVO implements Serializable {
    private static final long serialVersionUID = -1345480966661049610L;

    private Long id;

    @Schema(description = "项目类别名称")
    private String itemName;

    @Schema(description = "类别预算值")
    private BigDecimal balance;

    @Schema(description = "已立项值")
    private BigDecimal amount;

    @Schema(description = "本年结余")
    private BigDecimal surplus;

    @Schema(description = "预算进度百分比")
    private BigDecimal percentage;

    private Boolean hasChildren;
    private List<BudgetProgressVO> children;
}
