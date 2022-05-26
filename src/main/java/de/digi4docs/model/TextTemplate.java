package de.digi4docs.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "text_template", indexes = {@Index(name = "idx_text_template_user", columnList = "user")})
public class TextTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false)
    private User user;

    @NotEmpty(message = "Bitte gib eine Bezeichnung an.")
    @Length(max = 255, message = "Die Bezeichnung darf maximal 255 Zeichen lang sein.")
    private String title;

    @Type(type = "text")
    private String text;
}
