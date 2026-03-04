package com.example.publishHub.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Entity
@Table(name = "posts")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraphs({
        //Для страницы поста с комментариями
        @NamedEntityGraph(
                name = "post-with-comments-and-authors",
                attributeNodes = {
                        @NamedAttributeNode(value = "comments", subgraph = "comment-with-user")
                },
                subgraphs = @NamedSubgraph(
                        name = "comment-with-user",
                        attributeNodes = @NamedAttributeNode("user")
                )
        ),
        //Для ленты с тегами и активностью
        @NamedEntityGraph(
                name = "post-with-tags-and-comments-count",
                attributeNodes = {
                        @NamedAttributeNode("tags")
                }
        ),
        //Админка или экспорт данных
        @NamedEntityGraph(
                name = "post-full-details",
                attributeNodes = {
                        @NamedAttributeNode("author"),
                        @NamedAttributeNode("comments"),
                        @NamedAttributeNode("tags")
                }
        )
})
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "title",
            nullable = false
    )
    private String title;

    @Column(
            name = "text"
    )
    private String content;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id"
    )
    private UserEntity author;

    @OneToMany(
            mappedBy = "post",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<CommentEntity> comments = new ArrayList<>();

    @Formula("(SELECT COUNT(*) FROM CommentEntity c WHERE c.post_id = id)")
    private Long commentCount;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<TagEntity> tags = new ArrayList<>();


    @PrePersist
    void create() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", PostEntity.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("title='" + title + "'")
                .add("content='" + content + "'")
                .toString();
    }
}
