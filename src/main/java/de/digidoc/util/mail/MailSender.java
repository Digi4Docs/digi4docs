package de.digidoc.util.mail;

import de.digidoc.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class MailSender {
    private final ConfigService configService;

    @Autowired
    public MailSender(ConfigService configService) {
        this.configService = configService;
    }

    public JavaMailSender getMailSender()
    {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(configService.getMailHost());
        mailSender.setPort(configService.getMailPort());

        mailSender.setUsername(configService.getMailUser());
        mailSender.setPassword(configService.getMailPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}