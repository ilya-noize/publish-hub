package com.example.publishHub.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REFRESH;

@Entity
@Table(name = "usr")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "email",
            unique = true,
            nullable = false
    )
    private String email;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    private UserProfileEntity userProfile;

    @OneToMany(
            mappedBy = "author",
            fetch = FetchType.LAZY,
            cascade = {PERSIST, MERGE, REFRESH}
    )
    private List<PostEntity> posts = new ArrayList<>();

    // todo add roles
    //  (USER_ROLE, ADMIN_ROLE, MODERATOR_ROLE)
}
