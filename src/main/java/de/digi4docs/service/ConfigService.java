package de.digi4docs.service;

import de.digi4docs.model.Config;
import de.digi4docs.repository.ConfigRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Component
@Log
public class ConfigService {
    public static String KEY_MAIL_HOST = "mail.host";
    public static String KEY_MAIL_PORT = "mail.port";
    public static String KEY_MAIL_USER = "mail.user";
    public static String KEY_MAIL_PASSWORD = "mail.password";
    public static String KEY_MAIL_SENDER = "mail.sender";
    public static String KEY_SERVER_BASE_URL = "base.url";
    public static String KEY_IMPRINT_URL = "imprint.url";
    public static String KEY_IMPRINT_INSTITUTION = "imprint.institution";

    private final ConfigRepository configRepository;

    @Autowired
    public ConfigService(ConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    public Optional<Config> findByKey(String key) {
        return configRepository.findByConfigKey(key);
    }

    public List<Config> findAll() {
        return configRepository.findAll();
    }

    public List<Config> saveAll(List<Config> configEntries) {
        return configRepository.saveAll(configEntries);
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

    public String getImprintUrl() {
        return getStringValue(KEY_IMPRINT_URL, "Imprint url");
    }

    public String getImprintInstitution() {
        try {
            return getStringValue(KEY_IMPRINT_INSTITUTION, "Imprint institution");
        } catch (Exception e) {
            log.log(Level.WARNING, "Missing imprint institution", e);
        }
        return "";
    }

    private String getStringValue(String key, String exceptionIdentifier) {
        Optional<Config> config = findByKey(key);
        if (config.isEmpty()) {
            throw new RuntimeException(exceptionIdentifier + " not configured");
        }

        return config.get()
                     .getConfigValue();
    }
}
