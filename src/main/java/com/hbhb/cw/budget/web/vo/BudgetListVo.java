package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author ln
 * @Date 2020/7/6 0006 15:03
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetListVo implements Serializable {

    private static final long serialVersionUID = 8698498840043769464L;

    private Long id;
    @Schema(description = "预算类别名称")
    private String itemName;
    @Schema(description = "去年预算值")
    private BigDecimal lastYearBalance;
    @Schema(description = "本年预算值")
    private BigDecimal balance;
    @Schema(description = "金额阀值")
    private BigDecimal threshold;
    @Schema(description = "备注")
    private String remark;
}
