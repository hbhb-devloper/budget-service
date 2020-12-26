package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExportBudgetFileVO {

    @Schema(description = "文件名称")
    private String fileName;
    @Schema(description = "作者")
    private String author;
    @Schema(description = "时间")
    private String createTime;
    @Schema(description = "大小")
    private String fileSize;

}
