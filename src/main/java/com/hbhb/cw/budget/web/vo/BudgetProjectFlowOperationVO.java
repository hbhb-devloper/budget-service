package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiaokang
 * @since 2020-08-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectFlowOperationVO implements Serializable {

    private static final long serialVersionUID = -2195906058621745936L;

    @Schema(description = "操作按钮值")
    private Integer value;
    @Schema(description = "操作按钮是否显示（true-隐藏、false-显示）")
    private Boolean hidden;
}
