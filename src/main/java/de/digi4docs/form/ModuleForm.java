package de.digi4docs.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ModuleForm {
    protected Integer id;

    @NotEmpty(message = "Bitte gibt einen Titel für den Kurs an.")
    @Size(max = 255, message = "Der Titel darf maximal 255 Zeichen lang sein.")
    protected String title;

    @Size(max = 255, message = "Der Untertitel darf maximal 255 Zeichen lang sein.")
    protected String subTitle;

    protected String icon;

    @Size(max = 9, message = "Die Farbe darf maximal 9 Zeichen lang sein.")
    protected String color;

    @Size(max = 20, message = "Der Badge-Text darf maximal 20 Zeichen lang sein.")
    protected String badgeText;

    protected Boolean badgeHasBorder;
    
    protected Boolean asBadge;

    protected String description;

    protected Boolean isActive;
}
