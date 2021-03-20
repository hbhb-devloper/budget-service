package com.hbhb.cw.budget.web.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yzc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class BudgetItemVO implements Serializable {

    private static final long serialVersionUID = -116198628927276617L;

    @Schema(description = "预算科目id")
    private Long id;

    @Schema(description = "预算科目名称")
    private String itemName;
}
