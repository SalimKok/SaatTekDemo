package com.saattech.entity;

import com.saattech.enums.ContentType;
import com.saattech.enums.EntityStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "content")
@DynamicUpdate
@Data
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private Integer seasonNo;
    private Integer episodeNo;
    private String poster;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Enumerated(EnumType.STRING)
    private EntityStatus status = EntityStatus.ACTIVE;

    @CreationTimestamp
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Content parentContent;


    @OneToMany(mappedBy = "parentContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> subContents;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metadata_id", referencedColumnName = "id")
    private Metadata metadata;

    @ManyToMany
    @JoinTable(
            name = "content_cast",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "cast_id")
    )
    private List<Cast> casts;

    }
