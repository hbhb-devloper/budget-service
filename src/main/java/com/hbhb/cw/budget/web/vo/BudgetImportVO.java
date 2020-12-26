package com.hbhb.cw.budget.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author ln
 * @Date 2020/6/29 0029 16:28
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetImportVO implements Serializable {
    private static final long serialVersionUID = 4225449905534187160L;

    @ExcelProperty(value = "归口部门", index = 0)
    private String unitShortName;
    @ExcelProperty(value = "预算科目", index = 1)
    private String budgetItem;
    @ExcelProperty(value = "项目名称", index = 2)
    private String projectItem;
    @ExcelProperty(value = "预算编号", index = 3)
    private String budgetNum;
    @ExcelProperty(value = "预算金额", index = 4)
    private BigDecimal balance;
    @ExcelProperty(value = "是否分解", index = 5)
    private String isBreak;
    @ExcelProperty(value = "市场", index = 6)
    private BigDecimal shichang;
    @ExcelProperty(value = "网络", index = 7)
    private BigDecimal wangluo;
    @ExcelProperty(value = "综合", index = 8)
    private BigDecimal zonghe;
    @ExcelProperty(value = "安保", index = 9)
    private BigDecimal anbao;
    @ExcelProperty(value = "采购", index = 10)
    private BigDecimal caigou;
    @ExcelProperty(value = "人力", index = 11)
    private BigDecimal renli;
    @ExcelProperty(value = "财务", index = 12)
    private BigDecimal caiwu;
    @ExcelProperty(value = "工会", index = 13)
    private BigDecimal gonghui;
    @ExcelProperty(value = "党群", index = 14)
    private BigDecimal dangqun;
    @ExcelProperty(value = "工程", index = 15)
    private BigDecimal gongcheng;
    @ExcelProperty(value = "政企", index = 16)
    private BigDecimal zhengqi;
    @ExcelProperty(value = "纪检", index = 17)
    private BigDecimal jijian;
    @ExcelProperty(value = "亚运办", index = 18)
    private BigDecimal yayunban;
    @ExcelProperty(value = "武林", index = 19)
    private BigDecimal wulin;
    @ExcelProperty(value = "西湖", index = 20)
    private BigDecimal xihu;
    @ExcelProperty(value = "江干", index = 21)
    private BigDecimal jianggan;
    @ExcelProperty(value = "拱墅", index = 22)
    private BigDecimal gongshu;
    @ExcelProperty(value = "钱塘", index = 23)
    private BigDecimal qiantang;
    @ExcelProperty(value = "滨江", index = 24)
    private BigDecimal binjiang;
    @ExcelProperty(value = "萧山", index = 25)
    private BigDecimal xiaoshan;
    @ExcelProperty(value = "余杭", index = 26)
    private BigDecimal yuhang;
    @ExcelProperty(value = "富阳", index = 27)
    private BigDecimal fuyang;
    @ExcelProperty(value = "临安", index = 28)
    private BigDecimal linan;
    @ExcelProperty(value = "桐庐", index = 29)
    private BigDecimal tonglu;
    @ExcelProperty(value = "建德", index = 30)
    private BigDecimal jiande;
    @ExcelProperty(value = "淳安", index = 31)
    private BigDecimal chunan;
}
