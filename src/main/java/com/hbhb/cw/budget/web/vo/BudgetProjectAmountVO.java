package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author yzcyzc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectAmountVO implements Serializable {

    private static final long serialVersionUID = -9036624851601518929L;

    private Long id;

    @Schema(description = "类别名称")
    private String projectItemName;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "项目编号")
    private String projectNum;

    @Schema(description = "创建人")
    private String creatBy;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "单位")
    private String unitName;
}
