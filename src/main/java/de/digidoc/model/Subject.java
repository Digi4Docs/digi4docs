package de.digidoc.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Bitte geben Sie eine Bezeichnung für das Fach an.")
    @Length(max = 255, message = "Die Bezeichnung darf maximal 255 Zeichen lang sein.")
    private String name;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToMany
    @OrderBy("lastname, firstname")
    Set<User> users;

    @Override
    public String toString() {
        /* is used in task.html to map all subjects to teacher within the select option */
        return String.valueOf(id);
    }
}
