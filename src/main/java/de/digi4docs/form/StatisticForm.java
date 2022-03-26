package de.digi4docs.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StatisticForm {

    @NotNull(message = "Bitte wähle einen Kurs aus.")
    @Min(value = 0, message = "Bitte wähle einen Kurs aus.")
    protected Integer courseId;

    @NotEmpty(message = "Bitte gib ein Start-Datum an.")
    protected String startDate;

    @NotEmpty(message = "Bitte gib ein End-Datum an.")
    protected String endDate;

}
