package com.example.publishHub.controller;

import com.example.publishHub.model.tag.TagDto;
import com.example.publishHub.model.tag.TagMapper;
import com.example.publishHub.model.tag.TagPostRequest;
import com.example.publishHub.model.tag.TagPutRequest;
import com.example.publishHub.model.tag.TagResponse;
import com.example.publishHub.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tags")
@Slf4j
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;
    private final TagMapper tagMapper;


    @PostMapping
    public TagResponse createTag(
            @RequestBody @Valid
            TagPostRequest request
    ) {
        TagDto domain = tagMapper.toDomain(request);

        return tagMapper.toResponse(tagService.saveTag(domain));
    }

    @PutMapping("/{tagId}")
    public TagResponse updateTag(
            @PathVariable
            Long tagId,
            @RequestBody @Valid
            TagPutRequest request
    ) {
        TagDto domain = tagMapper.toDomain(request);

        return tagMapper.toResponse(tagService.updateTag(tagId, domain));
    }

    @DeleteMapping("/{tagId}")
    public void deleteTag(
            @PathVariable("tagId") Long tagId
    ) {
        tagService.deleteTag(tagId);
    }
}
