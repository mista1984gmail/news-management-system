package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@Table(name = "author")
@SQLDelete(sql = "UPDATE author SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Author implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="id", updatable = false)
    private Long id;

    @Column(name="username",
            nullable = false, unique = true)
    private String username;

    @Column(name="address",
            nullable = false)
    private String address;

    @Column(name="email",
            nullable = false, unique = true)
    private String email;

    @Column(name="telephone",
            nullable = false, unique = true)
    private String telephone;

    @Column(name="registration_date",
            nullable = false)
    private LocalDateTime registrationDate;

    @Column(name="is_blocked",
            nullable = false)
    private boolean isBlocked;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @OneToMany(mappedBy = "author", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JsonIgnore
    private List<Comment> comments;

}
