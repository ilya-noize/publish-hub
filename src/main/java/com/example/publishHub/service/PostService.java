package com.example.publishHub.service;

import com.example.publishHub.model.PostDto;
import com.example.publishHub.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;

//@Service
public class PostService {
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public PostDto createPost(PostDto domain) {
//        postRepository
        return null;
    }

    public PostDto getById(Long id) {
        return null;
    }

    public Page<PostDto> getAllPosts(Pageable pageable) {
        return null;
    }
}
