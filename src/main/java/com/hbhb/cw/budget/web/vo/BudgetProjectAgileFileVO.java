package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yzc
 * @since 2020-09-25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectAgileFileVO implements Serializable {

    private static final long serialVersionUID = -258085161569471484L;
    private Long id;

    @Schema(description = "常用性签报id")
    private Long projectAgileId;

    @Schema(description = "附件id")
    private Long fileId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件大小")
    private String fileSize;

    @Schema(description = "是否为毕传")
    private Boolean required;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "创建时间")
    private String createTime;
}
