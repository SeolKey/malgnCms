package com.malgn.content.controller;

import com.malgn.content.bo.ContentBO;
import com.malgn.content.entity.Contents;
import com.malgn.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentRestController {

    private final ContentBO contentsBO;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Contents>> getAllContents(
            @PageableDefault(size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Contents> contents = contentsBO.getAllContents(pageable);
        return ResponseEntity.ok(contents);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Contents> getContentById(@PathVariable Long id) {
        Contents content = contentsBO.getContentById(id);
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<Contents> createContent(
            @Valid @RequestBody Contents content,
            @AuthenticationPrincipal User user) {
        // created_by에는 User 엔티티를 직접 설정
        content.setCreatedByUser(user);
        Contents savedContent = contentsBO.createContent(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedContent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contents> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody Contents content,
            @AuthenticationPrincipal User currentUser) {
        Contents updatedContent = contentsBO.updateContent(id, content, currentUser);
        return ResponseEntity.ok(updatedContent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        contentsBO.deleteContent(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
