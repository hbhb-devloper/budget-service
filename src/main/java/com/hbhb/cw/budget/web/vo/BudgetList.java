package com.hbhb.cw.budget.web.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author ln
 * @Date 2020/7/4 0004 11:13
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetList implements Serializable {


    private static final long serialVersionUID = -8061525792497650976L;
    /**
     * ID
     */
    private Long id;

    /**
     * 金额
     */
    private BigDecimal balance;


}
