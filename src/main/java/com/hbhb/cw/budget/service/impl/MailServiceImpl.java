package com.hbhb.cw.budget.service.impl;


import com.hbhb.cw.budget.rpc.MailApiExp;
import com.hbhb.cw.budget.service.MailService;
import com.hbhb.cw.messagehub.vo.MailVO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author xiaokang
 * @since 2020-10-21
 */
@Service
public class MailServiceImpl implements MailService {

    @Resource
    private MailApiExp mailApi;

    @Value("${mail.title}")
    private String title;
    @Value("${mail.content}")
    private String content;

    @Override
    public void postMail(String receiver, String name, String flowName) {
        mailApi.postMail(MailVO.builder()
                .receiver(receiver)
                .title(String.format(title, flowName))
                .content(String.format(content, name, flowName))
                .build());
    }
}
