package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
@Entity
@Table(name = "metadata")
@Data

public class Metadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private Integer releaseYear;
    private String plot;
    private String posterUrl;
    private String language;
    private String country;

}
