package de.digi4docs.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String type;

    @NotNull
    private long size;

    @NotNull
    @Lob
    private byte[] data;

    @OneToOne(mappedBy = "file", cascade = CascadeType.DETACH)
    private UserTask userTask;
}