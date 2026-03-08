package com.example.publishHub.service;

import com.example.publishHub.entity.TagEntity;
import com.example.publishHub.model.tag.TagDto;
import com.example.publishHub.model.tag.TagMapper;
import com.example.publishHub.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public TagDto saveTag(TagDto domain) {
        if (tagRepository.existsByNameIgnoreCase(domain.name())) {
            throw new IllegalArgumentException("Tag name:%s must be unique".formatted(domain.name()));
        }
        TagEntity tag = tagMapper.toEntity(domain);

        return tagMapper.toDomain(tagRepository.save(tag));
    }

    public TagDto updateTag(Long tagId, TagDto domain) {
        if (!tagId.equals(domain.id())) {
            throw new IllegalArgumentException("Tag ID:%s and JSON ID:%s must be same".formatted(tagId, domain.id()));
        }
        existsTagById(tagId);
        return saveTag(domain);
    }

    public void deleteTag(Long tagId) {
        existsTagById(tagId);
        tagRepository.deleteById(tagId);
    }

    private void existsTagById(Long tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new NoSuchElementException("No such tag by ID:%s".formatted(tagId));
        }
    }
}
