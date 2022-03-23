package de.digidoc.service;

import de.digidoc.model.Config;
import de.digidoc.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConfigService {
    public static String KEY_MAIL_HOST = "mail.host";
    public static String KEY_MAIL_PORT = "mail.port";
    public static String KEY_MAIL_USER = "mail.user";
    public static String KEY_MAIL_PASSWORD = "mail.password";
    public static String KEY_MAIL_SENDER = "mail.sender";
    public static String KEY_SERVER_BASE_URL = "base.url";

    private final ConfigRepository configRepository;

    @Autowired
    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public Optional<Config> findByKey(String key) {
        return configRepository.findByConfigKey(key);
    }

    public String getMailHost() {
        return getStringValue(KEY_MAIL_HOST, "Mail host");
    }

    public int getMailPort() {
        String mailPort = getStringValue(KEY_MAIL_PORT, "Mail port");
        return Integer.parseInt(mailPort);
    }

    public String getMailUser() {
        return getStringValue(KEY_MAIL_USER, "Mail user");
    }

    public String getMailPassword() {
        return getStringValue(KEY_MAIL_PASSWORD, "Mail password");
    }

    public String getMailSender() {
        return getStringValue(KEY_MAIL_SENDER, "Mail sender");
    }

    public String getServerBaseUrl() {
        return getStringValue(KEY_SERVER_BASE_URL, "Server base url");
    }

    private String getStringValue(String key, String exceptionIdentifier) {
        Optional<Config> config = findByKey(key);
        if (config.isEmpty()) {
            throw new RuntimeException(exceptionIdentifier + " not configured");
        }

        return config.get().getConfigValue();
    }
}
