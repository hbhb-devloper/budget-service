package com.hbhb.cw.budget.model;

import com.alibaba.excel.annotation.ExcelProperty;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright (c) 2020 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author 预算映射对象
 * @since 2020-06-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetMap {

  @ExcelProperty(value = "归口部门", index = 0)
  private String underUnitId;
  @ExcelProperty(value = "预算科目", index = 1)
  private String budgetItem;
  @ExcelProperty(value = "旧项目名称（2018版）", index = 2)
  private String oldName;
  @ExcelProperty(value = "新项目名称（2020版）", index = 3)
  private String newName;
  @ExcelProperty(value = "预算编号", index = 4)
  private String budgetNum;
  @ExcelProperty(value = "预算金额", index = 5)
  private BigDecimal balance;
  @ExcelProperty(value = "是否分解", index = 6)
  private String isBreak;
  @ExcelProperty(value = "市场", index = 7)
  private BigDecimal shichang;
  @ExcelProperty(value = "网络", index = 8)
  private BigDecimal wangluo;
  @ExcelProperty(value = "综合", index = 9)
  private BigDecimal zonghe;
  @ExcelProperty(value = "安保", index = 10)
  private BigDecimal anbao;
  @ExcelProperty(value = "采购", index = 11)
  private BigDecimal caigou;
  @ExcelProperty(value = "人力", index = 12)
  private BigDecimal renli;
  @ExcelProperty(value = "财务", index = 13)
  private BigDecimal caiwu;
  @ExcelProperty(value = "工会", index = 14)
  private BigDecimal gonghui;
  @ExcelProperty(value = "党群", index = 15)
  private BigDecimal dangqun;
  @ExcelProperty(value = "工程", index = 16)
  private BigDecimal gongcheng;
  @ExcelProperty(value = "政企", index = 17)
  private BigDecimal zhengqi;
  @ExcelProperty(value = "纪检", index = 18)
  private BigDecimal jijian;
  @ExcelProperty(value = "亚运办", index = 19)
  private BigDecimal yayunhui;
  @ExcelProperty(value = "武林", index = 20)
  private BigDecimal wulin;
  @ExcelProperty(value = "西湖", index = 21)
  private BigDecimal xihu;
  @ExcelProperty(value = "江干", index = 22)
  private BigDecimal jianggan;
  @ExcelProperty(value = "拱墅", index = 23)
  private BigDecimal gongshu;
  @ExcelProperty(value = "钱塘", index = 24)
  private BigDecimal qiantang;
  @ExcelProperty(value = "滨江", index = 25)
  private BigDecimal binjiang;
  @ExcelProperty(value = "萧山", index = 26)
  private BigDecimal xiaoshan;
  @ExcelProperty(value = "余杭", index = 27)
  private BigDecimal yuhang;
  @ExcelProperty(value = "富阳", index = 28)
  private BigDecimal fuyang;
  @ExcelProperty(value = "临安", index = 29)
  private BigDecimal linan;
  @ExcelProperty(value = "桐庐", index = 30)
  private BigDecimal tonglu;
  @ExcelProperty(value = "建德", index = 31)
  private BigDecimal jiande;
  @ExcelProperty(value = "淳安", index = 32)
  private BigDecimal chunan;
  @ExcelProperty(value = "公共项", index = 33)
  private BigDecimal gonggongxiang;
}

