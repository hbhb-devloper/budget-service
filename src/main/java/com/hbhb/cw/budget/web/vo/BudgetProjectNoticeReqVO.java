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
public class BudgetProjectNoticeReqVO implements Serializable {

    private static final long serialVersionUID = -2963184819678898269L;

    private Long id;

    @Schema(description = "项目签报id")
    private Integer projectId;

    @Schema(description = "接收人id")
    private Integer receiver;

    @Schema(description = "发起人id")
    private Integer promoter;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "状态")
    private Integer state;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "流程类型id")
    private Long flowTypeId;
}
