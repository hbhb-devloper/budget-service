package com.hbhb.cw.budget.rpc;

import com.hbhb.cw.messagehub.api.MailApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author xiaokang
 * @since 2020-10-19
 */
@FeignClient(value = "message-hub", path = "mail")
public interface MailApiExp extends MailApi {
}