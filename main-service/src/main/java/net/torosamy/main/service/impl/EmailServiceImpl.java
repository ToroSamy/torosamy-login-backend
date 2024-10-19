package net.torosamy.main.service.impl;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import net.torosamy.main.constant.MessageConstant;
import net.torosamy.main.exception.EmailSendFailedException;
import net.torosamy.main.service.EmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendCaptcha(String qq, String captcha) {
        String to = qq+"@qq.com";
        Context context = new Context();
        context.setVariable("code", captcha);
        String htmlContent = templateEngine.process("email-template", context);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setFrom("3781050855@qq.com");
            messageHelper.setTo(to);
            messageHelper.setSubject("龙猫云创邮箱验证");
            messageHelper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailSendFailedException(MessageConstant.EMAIL_SEND_FAILED);
        }
    }
}
