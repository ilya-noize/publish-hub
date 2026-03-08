package com.example.publishHub.repository;

public interface PostWithCommentCountProjection {
    Long getId();
    String getTitle();
    Long getCommentCount();
}
