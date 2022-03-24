package de.digidoc.model;

import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "module")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    @NotEmpty(message = "Bitte geben Sie einen Titel für das Modul an.")
    @Length(max = 255, message = "Der Titel darf maximal 255 Zeichen lang sein.")
    private String title;

    @Column(name = "sub_title", nullable = true, length = 255)
    @Length(max = 255, message = "Der Untertitel darf maximal 255 Zeichen lang sein.")
    private String subTitle;

    @Type(type = "text")
    private String description;

    @Column(length = 100)
    private String icon;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "parent_modul", referencedColumnName = "id")
    private Module parent;

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
    @JoinColumn(name = "course", referencedColumnName = "id")
    private Course course;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "is_active = '1'")
    @OrderBy("title")
    private List<Module> modules;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "is_active = '1'")
    private List<Task> tasks;
}
