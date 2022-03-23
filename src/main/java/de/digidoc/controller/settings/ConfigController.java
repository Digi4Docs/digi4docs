package de.digidoc.controller.settings;

import de.digidoc.controller.AbstractController;
import de.digidoc.form.ConfigForm;
import de.digidoc.model.Config;
import de.digidoc.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
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
        List<Config> configEntries = configService.findAll();
        for (Config config : configEntries) {
            if (ConfigService.KEY_SERVER_BASE_URL.equals(config.getConfigKey())) {
                config.setConfigValue(configForm.getBaseUrl());
            }
            if (ConfigService.KEY_MAIL_HOST.equals(config.getConfigKey())) {
                config.setConfigValue(configForm.getMailHost());
            }
            if (ConfigService.KEY_MAIL_PORT.equals(config.getConfigKey())) {
                config.setConfigValue(String.valueOf(configForm.getMailPort()));
            }
            if (ConfigService.KEY_MAIL_USER.equals(config.getConfigKey())) {
                config.setConfigValue(configForm.getMailUser());
            }
            if (ConfigService.KEY_MAIL_PASSWORD.equals(config.getConfigKey()) && !configForm.getMailPassword().isEmpty()) {
                config.setConfigValue(configForm.getMailPassword());
            }
            if (ConfigService.KEY_MAIL_SENDER.equals(config.getConfigKey())) {
                config.setConfigValue(configForm.getMailSender());
            }
        }

        try {
            configService.saveAll(configEntries);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showConfigPage(model, configForm, false);
    }

    private String showConfigPage(Model model, ConfigForm configForm, boolean initFormData) {
        Map<String, String> configMap = configService.findAll().stream().collect(Collectors.toMap(Config::getConfigKey, Config::getConfigValue));
        String passwordValue = configMap.get(ConfigService.KEY_MAIL_PASSWORD);
        if (initFormData) {
            configForm.setBaseUrl(configMap.get(ConfigService.KEY_SERVER_BASE_URL));
            configForm.setMailHost(configMap.get(ConfigService.KEY_MAIL_HOST));
            configForm.setMailPort(Integer.valueOf(configMap.get(ConfigService.KEY_MAIL_PORT)));
            configForm.setMailUser(configMap.get(ConfigService.KEY_MAIL_USER));
            configForm.setMailPassword(passwordValue);
            configForm.setMailSender(configMap.get(ConfigService.KEY_MAIL_SENDER));
        }

        String maskedPassword = String.valueOf(passwordValue.charAt(0));
        maskedPassword += new String(new char[passwordValue.length()]).replace("\0", "*");
        maskedPassword += passwordValue.charAt(passwordValue.length() - 1);
        model.addAttribute("maskedPassword", maskedPassword);

        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "settings/config/config";
    }

    private void addBasicBreadcrumbs() {
        getBreadcrumbs(true).put("/settings/config", "Einstellungen / Konfiguration");
    }
}
