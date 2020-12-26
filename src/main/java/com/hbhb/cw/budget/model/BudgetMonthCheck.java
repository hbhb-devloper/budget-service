package com.hbhb.cw.budget.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;

/**
 * @author yzc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("预算执行-月度考核情况")
public class BudgetMonthCheck implements Serializable {

    private static final long serialVersionUID = -8993012778836538902L;

    @ExcelProperty(value = "序号", index = 0)
    @ApiModelProperty("序号")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private Long lineNumber;

    @ExcelProperty(value = "预算科目", index = 0)
    @ApiModelProperty("预算科目")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private String budgetItem;

    @ExcelProperty(value = "计量单位", index = 0)
    @ApiModelProperty("计量单位")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private String measureUnit;

    @ExcelProperty(value = "本年预算值", index = 0)
    @ApiModelProperty("本年预算值")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private String nowBudgetBalance;

    @ExcelProperty(value = "本期调整后完成值", index = 0)
    @ApiModelProperty("本期调整后完成值")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private Long afterAdjustmentValue;

    @ExcelProperty(value = "调整前累计完成值", index = 0)
    @ApiModelProperty("调整前累计完成值")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private Long beforeCompletionValue;

    @ExcelProperty(value = "调整值", index = 0)
    @ApiModelProperty("调整值")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private Long adjustmentValue;

    @ExcelProperty(value = "调整后累计完成值", index = 0)
    @ApiModelProperty("调整后累计完成值")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private Long afterCompletionValue;

    @ExcelProperty(value = "累计完成比例", index = 0)
    @ApiModelProperty("累计完成比例")
    @HeadFontStyle(fontHeightInPoints = 11, bold = false)
    @HeadStyle(fillPatternType = FillPatternType.NO_FILL, wrapped = false,
            borderLeft = BorderStyle.NONE, borderRight = BorderStyle.NONE,
            borderTop = BorderStyle.NONE, borderBottom = BorderStyle.NONE)
    private BigDecimal completionRatio;
}
