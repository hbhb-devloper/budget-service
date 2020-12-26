package com.hbhb.cw.budget.rpc;

import com.hbhb.cw.flowcenter.api.FlowNodeApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yzc
 * @since 2020-12-03
 */
@FeignClient(value = "${provider.flow-center}", url = "", contextId = "FlowNodeApi", path = "/node")
public interface FlowNodeApiExp extends FlowNodeApi {
}
