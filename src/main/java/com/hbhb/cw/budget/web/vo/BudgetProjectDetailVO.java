package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectDetailVO implements Serializable {
    private static final long serialVersionUID = -1637966405054280614L;

    private Long id;

    @Schema(description = "单位id")
    private Integer unitId;

    @Schema(description = "预算id")
    private Long budgetId;

    @Schema(description = "项目编号")
    private String projectNum;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目预算总额（万元）")
    private String amount;

    @Schema(description = "可供分配预算（万元）")
    private String availableAmount;

    @Schema(description = "本年价税合计（万元）")
    private String taxIncludeAmount;

    @Schema(description = "本年项目成本（万元）")
    private String cost;

    @Schema(description = "增值税率")
    private String vatRate;

    @Schema(description = "本年增值税金(万元)")
    private String vatAmount;

    @Schema(description = "责任人")
    private String director;

    @Schema(description = "工程编号")
    private String engineeringNum;

    @Schema(description = "项目开始时间")
    private String startTime;

    @Schema(description = "项目结束时间")
    private String endTime;

    @Schema(description = "供应商")
    private String supplier;

    @Schema(description = "项目来源")
    private String origin;

    @Schema(description = "项目简介")
    private String introduction;

    @Schema(description = "项目详情说明")
    private String detail;

    @Schema(description = "项目实施目标")
    private String target;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "审批状态")
    private Integer state;

    @Schema(description = "附件")
    private List<BudgetProjectFileVO> files;
}
