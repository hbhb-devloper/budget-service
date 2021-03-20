package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BudgetProjectNoticeResVO implements Serializable {

    private static final long serialVersionUID = -3312734305180016243L;

    private Long id;

    @Schema(description = "项目签报id")
    private Integer projectId;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "发起时金额")
    private String amount;

    @Schema(description = "流程类型")
    private String flowType;

    @Schema(description = "发起人")
    private String userName;

    @Schema(description = "发起单位")
    private String unitName;

    @Schema(description = "发起时间")
    private String createTime;

    @Schema(description = "状态值")
    private Integer state;

    @Schema(description = "状态名称")
    private String stateLabel;

}
