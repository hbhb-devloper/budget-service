package com.hbhb.cw.budget.rpc;


import com.hbhb.cw.flowcenter.api.FlowTypeApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author wangxiaogang
 */
@FeignClient(value = "${provider.flow-center}", url = "", contextId = "FlowTypeApi", path = "/type")
public interface FlowTypeApiExp extends FlowTypeApi {
}
