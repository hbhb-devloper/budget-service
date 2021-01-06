package com.hbhb.cw.budget.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Budget implements Serializable {

    private static final long serialVersionUID = -4900414854946496543L;
    /**
     * id
     */
    private Long id;

    /**
     * 预算科目id
     */
    private Long budgetItemId;

    /**
     * 项目名称
     */
    private String projectItem;

    /**
     * 预算编号
     */
    private String budgetNum;

    /**
     * 预算编号
     */
    private String serialNum;

    /**
     * 今年预算值
     */
    private BigDecimal balance;

    /**
     * 去年预算值
     */
    private BigDecimal lastYearBalance;

    /**
     * 去年完成值
     */
    private BigDecimal lastYearFinishedBalance;

    /**
     * 金额阀值（超过该值后，项目需要上传必要附件）
     */
    private BigDecimal threshold;

    /**
     * 导入年份
     */
    private String importDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 导入时间
     */
    private Date createTime;

    /**
     * 导入用户
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;
}