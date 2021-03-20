package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author yzc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectApproveVO implements Serializable {

    private static final long serialVersionUID = 9132132416770832380L;

    @Schema(description = "项目签报流程信息id")
    private Long id;

    @Schema(description = "操作")
    private Integer operation;

    @Schema(description = "审批意见")
    private String suggestion;

    @Schema(description = "所有节点的审批人 用来校验分配者")
    private List<BudgetProjectFlowNodeVO> approvers;
}
