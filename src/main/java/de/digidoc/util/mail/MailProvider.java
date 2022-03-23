package de.digidoc.util.mail;

import de.digidoc.model.PasswordForgotten;
import de.digidoc.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
public class MailProvider {
    private final ConfigService configService;
    private final MailSender mailSender;

    @Autowired
    public MailProvider(ConfigService configService, MailSender mailSender) {
        this.configService = configService;
        this.mailSender = mailSender;
    }

    public void sendPasswordForgottenMail(PasswordForgotten passwordForgotten) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(configService.getMailSender());
        message.setTo(passwordForgotten.getUser().getEmail());
        message.setSubject("Dein Passwort zurücksetzen");
        message.setText("Kopiere den folgenden Link in deinen Browser, um ein neues Passwort zu vergeben.\n\n" +
                configService.getServerBaseUrl() + "/new-password/" + passwordForgotten.getHash());
        mailSender.getMailSender().send(message);
    }
}
