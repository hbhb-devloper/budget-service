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
public class BudgetProjectFlowNodeVO implements Serializable {
    private static final long serialVersionUID = -2039133776147743201L;

    @Schema(description = "流程节点id")
    private String flowNodeId;
    @Schema(description = "审批人id")
    private Integer userId;
}
