package de.digi4docs.model;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_task")
public class UserTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "task", referencedColumnName = "id", nullable = false)
    private Task task;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "subject", referencedColumnName = "id")
    private Subject subject;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "teacher", referencedColumnName = "id")
    private User teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    private LocalDate date;

    @Type(type = "text")
    private String solution;

    @Type(type = "text")
    private String comment;

    @Column(name = "transmitted_at")
    private LocalDateTime transmittedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "done_at")
    private LocalDateTime doneAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "file", referencedColumnName = "id")
    private File file;
}
