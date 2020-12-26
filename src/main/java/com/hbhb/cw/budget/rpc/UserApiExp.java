package com.hbhb.cw.budget.rpc;

import com.hbhb.cw.systemcenter.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author yzc
 * @since 2020-11-27
 */
@FeignClient(value = "system-center-prd", contextId = "UserApi", path = "user")
public interface UserApiExp extends UserApi {
}
