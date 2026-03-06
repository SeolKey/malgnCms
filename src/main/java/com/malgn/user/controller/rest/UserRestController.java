package com.malgn.user.controller.rest;

import com.malgn.user.bo.UserBO;
import com.malgn.user.entity.User;
import com.malgn.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserRestController {

    private final UserBO userBO;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> signup(@Valid @RequestBody User user) {
        User savedUser = userBO.signup(user.getUsername(), user.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> login(@Valid @RequestBody User user) {
        User loggedInUser = userBO.login(user.getUsername(), user.getPassword());
        // 세션이 자동으로 생성됨 (Spring Security가 처리)
        return ResponseEntity.ok(loggedInUser);
    }

    @GetMapping(value = "/check-username/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkUsername(@PathVariable String username) {
        boolean exists = userBO.checkUsernameExists(username);
        return ResponseEntity.ok(java.util.Map.of("exists", exists, "available", !exists));
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "인증되지 않은 사용자입니다."));
        }
        return ResponseEntity.ok(java.util.Map.of(
                "username", userPrincipal.getUsername(),
                "role", userPrincipal.getUser().getRole().name()
        ));
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> logout(jakarta.servlet.http.HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok(java.util.Map.of("message", "로그아웃되었습니다."));
    }
}
