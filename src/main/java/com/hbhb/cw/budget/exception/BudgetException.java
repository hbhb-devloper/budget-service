package com.hbhb.cw.budget.exception;

import com.hbhb.core.bean.MessageConvert;
import com.hbhb.cw.budget.enums.BudgetErrorCode;
import com.hbhb.web.exception.BusinessException;
import lombok.Getter;

/**
 * @author wangxiaogang
 */
@Getter
public class BudgetException extends BusinessException {

    private final String code;

    public BudgetException(BudgetErrorCode code) {
        super(code.getCode(), MessageConvert.convert(code.getMessage()));
        this.code = code.getCode();
    }
}
