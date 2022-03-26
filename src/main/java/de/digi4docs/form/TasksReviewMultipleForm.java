package de.digi4docs.form;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TasksReviewMultipleForm {
    protected List<Integer> userTaskIds;
}
