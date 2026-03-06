package com.malgn.content.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;

@Controller
public class ContentController {

    private Resource getHtmlResource(String filename) {
        return new ClassPathResource("templates/content/" + filename);
    }

    private ResponseEntity<String> serveHtml(String filename) {
        try {
            Resource resource = getHtmlResource(filename);
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            String content = new String(resource.getInputStream().readAllBytes());
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(value = "/api/contents", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> contentsListPage() {
        return serveHtml("contents.html");
    }

    @GetMapping(value = "/api/contents/new", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> contentNewPage() {
        return serveHtml("content-new.html");
    }

    @GetMapping(value = "/api/contents/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> contentDetailPage(@PathVariable Long id) {
        return serveHtml("content-detail.html");
    }

    @GetMapping(value = "/api/contents/{id}/edit")
    public ResponseEntity<String> contentEditPage(@PathVariable Long id) {
        return serveHtml("content-edit.html");
    }
}
