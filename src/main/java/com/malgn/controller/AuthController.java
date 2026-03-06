package com.malgn.controller;

import com.malgn.dto.LoginRequest;
import com.malgn.dto.LoginResponse;
import com.malgn.dto.SignupRequest;
import com.malgn.entity.User;
import com.malgn.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/check-username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkUsername(@PathVariable String username) {
        boolean exists = authService.checkUsernameExists(username);
        return ResponseEntity.ok(java.util.Map.of("exists", exists, "available", !exists));
    }
}
