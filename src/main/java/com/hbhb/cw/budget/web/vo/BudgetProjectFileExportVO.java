package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Copyright (c) 2020 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author jiyu@myweimai.com
 * @since 2020-08-21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectFileExportVO {

    @Schema(description = "标题")
    private String fileName;

    @Schema(description = "文件url")
    private String filePath;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "时间")
    private String createTime;

    @Schema(description = "大小")
    private String fileSize;
}
