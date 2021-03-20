package com.hbhb.cw.budget.rpc;

import com.hbhb.cw.flowcenter.api.FlowNodeNoticeApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yzc
 * @since 2020-12-04
 */
@FeignClient(value = "${provider.flow-center}", url = "", contextId = "FlowNoticeApi", path = "/node/notice")
public interface FlowNoticeApiExp extends FlowNodeNoticeApi {
}
