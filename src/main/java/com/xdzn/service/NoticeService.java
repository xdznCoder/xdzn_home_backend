package com.xdzn.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.xdzn.model.entity.QqGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * NoticeService
 * <p>
 * 站外通知发送服务：向 QQ 群发送消息、向邮箱发送邮件。
 * <p>
 * 说明：
 * <ul>
 *     <li><b>QQ 群</b>：通过 go-cqhttp / OneBot HTTP 接口发送（配置 {@code app.qq-bot.url} 后启用），
 *         未配置时仅记录日志跳过（预留实现）</li>
 *     <li><b>邮箱</b>：基于 JavaMailSender（配置 SMTP 后启用），未配置时记录日志跳过</li>
 * </ul>
 *
 * @author xdzn
 */
@Service
@Slf4j
public class NoticeService {

    /**
     * JavaMail 发送器（Spring Boot 自动配置）
     */
    private final JavaMailSender mailSender;

    /**
     * go-cqhttp / OneBot HTTP 接口地址（未配置则跳过 QQ 群发送）
     */
    private final String qqBotUrl;

    /**
     * 发件邮箱（SMTP 用户名）
     */
    private final String mailFrom;

    /**
     * 构造注入
     *
     * @param mailSender JavaMail 发送器
     * @param qqBotUrl   QQ 机器人地址
     * @param mailFrom   发件邮箱
     */
    public NoticeService(JavaMailSender mailSender,
                         @Value("${app.qq-bot.url:}") String qqBotUrl,
                         @Value("${spring.mail.username:}") String mailFrom) {
        this.mailSender = mailSender;
        this.qqBotUrl = qqBotUrl;
        this.mailFrom = mailFrom;
    }

    /**
     * 向指定 QQ 群发送消息（go-cqhttp HTTP API，未配置地址则跳过）
     *
     * @param group   QQ 群配置
     * @param title   公告标题
     * @param content 公告内容
     */
    public void sendQqGroupMessage(QqGroup group, String title, String content) {
        if (qqBotUrl == null || qqBotUrl.isBlank()) {
            log.info("未配置 QQ 机器人地址（app.qq-bot.url），跳过群 [{}] 发送", group.getGroupName());
            return;
        }
        try {
            String message = "【" + title + "】\n" + content;
            Map<String, Object> body = new HashMap<>();
            body.put("group_id", Long.parseLong(group.getGroupNo()));
            body.put("message", message);
            String resp = HttpRequest.post(qqBotUrl + "/send_group_msg")
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(body))
                    .execute()
                    .body();
            log.info("QQ 群 [{}] 发送完成，响应：{}", group.getGroupName(), resp);
        } catch (Exception e) {
            log.error("QQ 群 [{}] 发送失败", group.getGroupName(), e);
        }
    }

    /**
     * 发送邮件（未配置 SMTP 则跳过）
     *
     * @param to      收件人邮箱
     * @param subject 主题
     * @param content 正文
     */
    public void sendEmail(String to, String subject, String content) {
        if (mailFrom == null || mailFrom.isBlank()) {
            log.info("未配置 SMTP（spring.mail.username），跳过邮件发送至 [{}]", to);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件已发送至 {}", to);
        } catch (Exception e) {
            log.error("邮件发送至 {} 失败", to, e);
        }
    }
}
