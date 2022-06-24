package de.digi4docs.model;

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
@Table(name = "course_group")
public class CourseGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NotEmpty(message = "Bitte gib eine Bezeichnung für die Gruppe an.")
    @Length(max = 255, message = "Die Bezeichnung darf maximal 255 Zeichen lang sein.")
    private String name;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToMany(mappedBy = "courseGroups")
    Set<User> users;
}
