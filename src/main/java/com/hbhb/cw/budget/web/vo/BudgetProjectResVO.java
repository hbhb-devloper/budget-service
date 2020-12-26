package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xiaokang
 * @since 2020-07-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectResVO implements Serializable {

    private static final long serialVersionUID = 2931200218691526228L;

    private Integer id;

    @Schema(description = "项目编号")
    private String projectNum;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目类型名称")
    private String projectTypeName;

    @Schema(description = "预算编号")
    private String budgetNum;

    @Schema(description = "金额（万元）")
    private String amount;

    @Schema(description = "项目成本")
    private String cost;

    @Schema(description = "增值税金")
    private String vatAmount;

    @Schema(description = "单位名字")
    private String unitName;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "项目来源")
    private String origin;

    @Schema(description = "状态值")
    private Integer state;

    @Schema(description = "状态名称")
    private String stateLabel;
}
