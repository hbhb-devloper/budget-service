package com.hbhb.cw.budget.enums;

/**
 * @author yzc
 * @since 2020-09-01
 */
public enum EnableCond {

    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL((Integer) 10),
    /**
     * 大于
     */
    GREATER_THAN((Integer) 20),
    /**
     * 等于
     */
    EQUAL_TO((Integer) 30),
    /**
     * 小于
     */
    LESS_THAN((Integer) 40),
    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL((Integer) 50),
    ;

    private final Integer value;

    EnableCond(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
