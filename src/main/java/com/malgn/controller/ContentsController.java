package com.malgn.controller;

import com.malgn.dto.ContentsRequest;
import com.malgn.dto.ContentsResponse;
import com.malgn.entity.User;
import com.malgn.security.UserPrincipal;
import com.malgn.service.ContentsService;
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
public class ContentsController {

    private final ContentsService contentsService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ContentsResponse>> getAllContents(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ContentsResponse> contents = contentsService.getAllContents(pageable);
        return ResponseEntity.ok(contents);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentsResponse> getContentById(@PathVariable Long id) {
        ContentsResponse content = contentsService.getContentById(id);
        return ResponseEntity.ok(content);
    }

    @PostMapping
    public ResponseEntity<ContentsResponse> createContent(
            @Valid @RequestBody ContentsRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String username = userPrincipal.getUsername();
        ContentsResponse content = contentsService.createContent(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(content);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentsResponse> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody ContentsRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User currentUser = userPrincipal.getUser();
        ContentsResponse content = contentsService.updateContent(id, request, currentUser);
        return ResponseEntity.ok(content);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        User currentUser = userPrincipal.getUser();
        contentsService.deleteContent(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
