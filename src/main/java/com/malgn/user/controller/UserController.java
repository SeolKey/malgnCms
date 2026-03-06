package com.malgn.user.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
public class UserController {

    private Resource getHtmlResource(String filename) {
        return new ClassPathResource("templates/user/" + filename);
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

    @GetMapping(value = "/api/auth/login", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> loginPage() {
        return serveHtml("login.html");
    }

    @GetMapping(value = "/api/auth/signup", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> signupPage() {
        return serveHtml("signup.html");
    }
}
