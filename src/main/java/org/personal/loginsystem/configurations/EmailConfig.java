package org.personal.loginsystem.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${emailsender.host}")
    private String EMAIL_HOST;

    @Value("${emailsender.port}")
    private int EMAIL_PORT;

    @Value("${emailsender.email}")
    private String EMAIL_FROM;

    @Value("${emailsender.password}")
    private String EMAIL_PASSWORD;

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(EMAIL_HOST);
        mailSender.setPort(EMAIL_PORT);
        mailSender.setUsername(EMAIL_FROM);
        mailSender.setPassword(EMAIL_PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}
