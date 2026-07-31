package com.saattech.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saattech.enums.CastType;
import com.saattech.enums.EntityStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "casts")
@DynamicUpdate
@Data
public class Cast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String poster;
    private String name;

    @Enumerated(EnumType.STRING)
    private CastType type;

    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.ACTIVE;

    @ManyToMany(mappedBy = "casts")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Content> contents;
}
