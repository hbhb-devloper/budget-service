package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectReqVO implements Serializable {
    private static final long serialVersionUID = -564844991133914916L;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "父类单位id")
    private Integer parentId;

    @Schema(description = "预算id")
    private Long budgetId;

    @Schema(description = "项目年度只需要传年份（yyyy）")
    private String projectYear;

    @Schema(description = "项目创建时间:yyyy-MM-dd")
    private String createTime;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目编号")
    private String projectNum;

    @Schema(description = "项目状态:10-未开始审批、20-正在审批、30-审批未通过、31-审批通过、40-调整中、50-审批调整中")
    private Integer state;
}
