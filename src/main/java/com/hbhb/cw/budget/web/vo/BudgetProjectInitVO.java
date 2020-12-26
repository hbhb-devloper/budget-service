package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetProjectInitVO implements Serializable {

    private static final long serialVersionUID = -4093922408173892142L;

    @Schema(description = "项目签报id")
    Integer projectId;
    @Schema(description = "流程类型id")
    Long flowTypeId;
    @Schema(description = "用户id")
    Integer userId;
}
