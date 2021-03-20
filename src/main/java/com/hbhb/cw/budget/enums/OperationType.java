package com.hbhb.cw.budget.enums;

/**
 * @author xiaokang
 * @since 2020-08-01
 */
public enum OperationType {

    /**
     * 拒绝
     */
    REJECT(0),

    /**
     * 同意
     */
    AGREE(1),

    /**
     * 未执行
     */
    UN_EXECUTED(2),
    ;

    private final Integer value;

    OperationType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
