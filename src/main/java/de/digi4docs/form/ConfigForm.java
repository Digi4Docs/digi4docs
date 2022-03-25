package de.digi4docs.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ConfigForm {

    @NotEmpty(message = "Der Wert für den SMTP-Host darf nicht leer sein.")
    @Size(max = 255, message = "Der Wert darf maximal 255 Zeichen lang sein.")
    protected String mailHost;

    @Min(value = 0, message = "Bitte gib einen Mail-Port an.")
    protected Integer mailPort;

    @NotEmpty(message = "Der Wert für den Mail-Benutzer darf nicht leer sein.")
    @Size(max = 255, message = "Der Wert darf maximal 255 Zeichen lang sein.")
    @Email(message = "Der Wert für den Mail-Benutzer muss eine gültige E-Mail sein.")
    protected String mailUser;

    @Size(max = 255, message = "Der Wert darf maximal 255 Zeichen lang sein.")
    protected String mailPassword;

    @NotEmpty(message = "Der Wert für das Mail-Absender darf nicht leer sein.")
    @Size(max = 255, message = "Der Wert darf maximal 255 Zeichen lang sein.")
    @Email(message = "Der Wert für den Mail-Absender muss eine gültige E-Mail sein.")
    protected String mailSender;

    @NotEmpty(message = "Der Wert für das Basis-URL des Servers darf nicht leer sein.")
    @Size(max = 255, message = "Der Wert darf maximal 255 Zeichen lang sein.")
    protected String baseUrl;


}
