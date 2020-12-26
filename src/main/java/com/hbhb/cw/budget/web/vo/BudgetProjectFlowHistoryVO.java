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
public class BudgetProjectFlowHistoryVO implements Serializable {

    private static final long serialVersionUID = 8258189533574390742L;

    @Schema(description = "流程版本")
    private String moder;

    @Schema(description = "处理环节")
    private String roleDesc;

    @Schema(description = "处理人")
    private String nickName;

    @Schema(description = "处理结果")
    private Integer operation;

    @Schema(description = "处理意见")
    private String suggestion;

    @Schema(description = "达到时间")
    private String arrivalTime;

    @Schema(description = "发出时间")
    private String sendingTime;

}
