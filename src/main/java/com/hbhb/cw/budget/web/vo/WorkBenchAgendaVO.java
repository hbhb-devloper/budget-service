package com.hbhb.cw.budget.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author wxg
 * @since 2020-10-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkBenchAgendaVO implements Serializable {
    private static final long serialVersionUID = -4404445568769885866L;

    @Schema(description = "待办提醒id")
    private Long id;

    @Schema(description = "发起人id")
    private Integer promoter;

    @Schema(description = "项目id")
    private Integer projectId;

    @Schema(description = "发起人姓名")
    private String userName;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "日期")
    private String date;


}
