package com.clone.ohouse.util;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@RequiredArgsConstructor
@Component
public class EmailSender {

    private final JavaMailSender javaMailSender;

    // 이메일로 code 전송
    @Async("mailExecutor")
    public void sendCheckCodeToEmail(String receiverEmail, String checkCode) {
        try {
            // Mail Message 생성
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(receiverEmail); //받는사람
            helper.setSubject("ohouse clone 회원가입 인증 메일"); //메일제목
            helper.setText(checkCode);

            // mail 전송
            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
