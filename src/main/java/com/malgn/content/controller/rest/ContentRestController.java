package com.malgn.content.controller.rest;

import com.malgn.content.bo.ContentsBO;
import com.malgn.content.entity.Contents;
import com.malgn.user.entity.User;
import com.malgn.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final ContentsBO contentsBO;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ContentsBO>> getAllContents(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ContentsBO> contents = contentsBO.getAllContents(pageable);
        return ResponseEntity.ok(contents);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentsBO> getContentById(@PathVariable Long id) {
        ContentsBO content = contentsBO.getContentById(id);
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<ContentsBO> createContent(
            @Valid @RequestBody Contents content,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        content.setCreatedBy(userPrincipal.getUsername());
        ContentsBO contentBO = contentsBO.createContent(content);
        return ResponseEntity.status(HttpStatus.CREATED).body(contentBO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentsBO> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody Contents content,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User currentUser = userPrincipal.getUser();
        ContentsBO contentBO = contentsBO.updateContent(id, content, currentUser);
        return ResponseEntity.ok(contentBO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User currentUser = userPrincipal.getUser();
        contentsBO.deleteContent(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
