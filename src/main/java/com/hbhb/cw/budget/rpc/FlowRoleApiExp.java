package com.hbhb.cw.budget.rpc;

import com.hbhb.cw.flowcenter.api.FlowRoleApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "${provider.flow-center}", url = "${feign-url}", contextId = "FlowRoleApi", path = "/role")
public interface FlowRoleApiExp extends FlowRoleApi {
}
