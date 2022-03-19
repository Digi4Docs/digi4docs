package de.digidoc.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Bitte geben Sie einen Titel für die Aufgabe an.")
    @Length(max = 255, message = "Der Titel darf maximal 255 Zeichen lang sein.")
    private String title;

    @Type(type = "text")
    private String description;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "edited_by", referencedColumnName = "id")
    private User editedBy;

    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "module", referencedColumnName = "id")
    private Module module;
}
