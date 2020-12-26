package com.hbhb.cw.budget.service;

/**
 * @author xiaokang
 * @since 2020-10-21
 */
public interface MailService {

    /**
     * 发送邮件提醒
     *
     * @param receiver 接收人邮箱
     * @param name     接收人姓名
     * @param flowName 流程名称
     */
    void postMail(String receiver, String name, String flowName);
}
