package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetProjectFileVO implements Serializable {

    private static final long serialVersionUID = -999583779159797096L;
    private Long id;

    @Schema(description = "签报id")
    private Long projectId;

    @Schema(description = "附件id")
    private Long fileId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件大小")
    private String fileSize;

    @Schema(description = "是否为毕传")
    private byte required;

    @Schema(description = "是否审批通过")
    private byte isApproved;

    @Schema(description = "作者")
    private String author;

    @Schema(description = "创建时间")
    private String createTime;

}
