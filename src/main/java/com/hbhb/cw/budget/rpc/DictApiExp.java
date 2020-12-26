package com.hbhb.cw.budget.rpc;

import com.hbhb.cw.systemcenter.api.DictApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yzc
 * @since 2020-11-27
 */
@FeignClient(value = "system-center-prd", contextId = "DictApi", path = "dict")
public interface DictApiExp extends DictApi {
}
