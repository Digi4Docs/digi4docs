package de.digidoc.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Bitte geben Sie einen Vornamen an.")
    @Length(max = 255, message = "Der Vorname darf maximal 255 Zeichen lang sein.")
    private String firstname;

    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Bitte geben Sie einen Nachnamen an.")
    @Length(max = 255, message = "Der Nachname darf maximal 255 Zeichen lang sein.")
    private String lastname;

    @Column(nullable = false, length = 255, unique = true)
    @NotEmpty(message = "Bitte geben Sie eine E-Mail-Adresse an.")
    @Length(max = 255, message = "Die E-Mail-Adresse darf maximal 255 Zeichen lang sein.")
    private String email;

    @Column(length = 255)
    @NotEmpty(message = "Bitte geben Sie ein Passwort an.")
    @Length(max = 255, message = "Das Passwort darf maximal 255 Zeichen lang sein.")
    private String password;

    @Column(name = "class_number")
    private Integer classNumber;

    @Column(name = "class_identifier", length = 1)
    @Length(max = 1, message = "Der Klassen-Buchstabe darf maximal ein Zeichen lang sein.")
    private String classIdentifier;

    @Column(name = "class_year")
    private Integer classYear;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private Set<UserRole> roles;

    @ManyToMany(mappedBy = "users")
    Set<Subject> subjects;

    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private List<UserTask> userTasks;


    public String getFullname() {
        return (null != lastname ? lastname : "") + ", " + (null != firstname ? firstname : "");
    }
}