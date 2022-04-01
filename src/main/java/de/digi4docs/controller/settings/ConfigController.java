package de.digi4docs.controller.settings;

import de.digi4docs.controller.AbstractController;
import de.digi4docs.form.ConfigForm;
import de.digi4docs.model.Config;
import de.digi4docs.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ConfigController extends AbstractController {

    @Autowired
    private ConfigService configService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/config")
    public String config(Model model, ConfigForm configForm) {
        return showConfigPage(model, configForm, true);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/settings/config")
    public String config(Model model, @Valid ConfigForm configForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return showConfigPage(model, configForm, false);
        }

        Map<String, Config> configEntries = configService.findAll()
                                                         .stream()
                                                         .collect(Collectors.toMap(Config::getConfigKey,
                                                                 config -> config));
        List<Config> configsToSave = new ArrayList<>();

        Config configBaseUrl = getByConfigKey(configEntries, ConfigService.KEY_SERVER_BASE_URL);
        configBaseUrl.setConfigValue(configForm.getBaseUrl());
        configsToSave.add(configBaseUrl);

        Config configImprintInstitution = getByConfigKey(configEntries, ConfigService.KEY_IMPRINT_INSTITUTION);
        configImprintInstitution.setConfigValue(configForm.getImprintInstitution());
        configsToSave.add(configImprintInstitution);

        Config configImprintUrl = getByConfigKey(configEntries, ConfigService.KEY_IMPRINT_URL);
        configImprintUrl.setConfigValue(configForm.getImprintUrl());
        configsToSave.add(configImprintUrl);

        Config configMailHost = getByConfigKey(configEntries, ConfigService.KEY_MAIL_HOST);
        configMailHost.setConfigValue(configForm.getMailHost());
        configsToSave.add(configMailHost);

        Config configMailPort = getByConfigKey(configEntries, ConfigService.KEY_MAIL_PORT);
        configMailPort.setConfigValue(String.valueOf(configForm.getMailPort()));
        configsToSave.add(configMailPort);

        Config configMailUser = getByConfigKey(configEntries, ConfigService.KEY_MAIL_USER);
        configMailUser.setConfigValue(configForm.getMailUser());
        configsToSave.add(configMailUser);

        if (null != configForm.getMailPassword() && !configForm.getMailPassword()
                                                               .isEmpty()) {
            Config configMailPassword = getByConfigKey(configEntries, ConfigService.KEY_MAIL_PASSWORD);
            configMailPassword.setConfigValue(configForm.getMailPassword());
            configsToSave.add(configMailPassword);
        }

        Config configMailSender = getByConfigKey(configEntries, ConfigService.KEY_MAIL_SENDER);
        configMailSender.setConfigValue(configForm.getMailSender());
        configsToSave.add(configMailSender);

        Config configCertificateFooter = getByConfigKey(configEntries, ConfigService.KEY_CERTIFICATE_FOOTER);
        configCertificateFooter.setConfigValue(configForm.getCertificateFooter());
        configsToSave.add(configCertificateFooter);

        try {
            configService.saveAll(configsToSave);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showConfigPage(model, configForm, false);
    }

    private Config getByConfigKey(Map<String, Config> configEntries, String configKey) {
        Config config = Optional.ofNullable(configEntries.get(configKey))
                                .orElse(new Config());
        config.setConfigKey(configKey);
        return config;
    }

    private String showConfigPage(Model model, ConfigForm configForm, boolean initFormData) {
        Map<String, String> configMap = configService.findAll()
                                                     .stream()
                                                     .collect(Collectors.toMap(Config::getConfigKey,
                                                             Config::getConfigValue));
        String passwordValue = configMap.get(ConfigService.KEY_MAIL_PASSWORD);
        if (initFormData) {
            configForm.setBaseUrl(configMap.get(ConfigService.KEY_SERVER_BASE_URL));
            configForm.setImprintInstitution(configMap.get(ConfigService.KEY_IMPRINT_INSTITUTION));
            configForm.setImprintUrl(configMap.get(ConfigService.KEY_IMPRINT_URL));
            configForm.setMailHost(configMap.get(ConfigService.KEY_MAIL_HOST));
            configForm.setMailPort(Integer.valueOf(configMap.get(ConfigService.KEY_MAIL_PORT)));
            configForm.setMailUser(configMap.get(ConfigService.KEY_MAIL_USER));
            configForm.setMailPassword(passwordValue);
            configForm.setMailSender(configMap.get(ConfigService.KEY_MAIL_SENDER));
            configForm.setCertificateFooter(configMap.get(ConfigService.KEY_CERTIFICATE_FOOTER));
        }

        String maskedPassword = "";
        if (null != passwordValue && !passwordValue.isEmpty()) {
            maskedPassword = String.valueOf(passwordValue.charAt(0));
            maskedPassword += new String(new char[passwordValue.length()]).replace("\0", "*");
            maskedPassword += passwordValue.charAt(passwordValue.length() - 1);
        }
        model.addAttribute("maskedPassword", maskedPassword);

        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "settings/config/config";
    }

    private void addBasicBreadcrumbs() {
        getBreadcrumbs(true).put("/settings/config", "Einstellungen / Konfiguration");
    }
}
