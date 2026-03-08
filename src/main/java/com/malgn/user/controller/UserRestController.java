package com.malgn.user.controller;

import com.malgn.user.bo.UserBO;
import com.malgn.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserRestController {

    private final UserBO userBO;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> signup(@Valid @RequestBody User user) {
        User savedUser = userBO.signup(user.getUserId(), user.getUsername(), user.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> login(@RequestBody User user, HttpServletRequest request) {
        // 로그인은 userId와 password만 필요하므로 @Valid 제거 (username은 불필요)
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("UserID is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        User loggedInUser = userBO.login(user.getUserId(), user.getPassword(), request);
        // 세션이 자동으로 생성됨 (Spring Security가 처리)
        return ResponseEntity.ok(loggedInUser);
    }

    @GetMapping(value = "/check-userid/{userid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> checkUserid(@PathVariable String userid) {
        boolean exists = userBO.checkUseridExists(userid);
        return ResponseEntity.ok(java.util.Map.of("exists", exists, "available", !exists));
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCurrentUser(
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "인증되지 않은 사용자입니다."));
        }
        // User.getUsername()은 UserDetails 인터페이스로 인해 userId를 반환하므로,
        // 실제 사용자명 필드를 가져오기 위해 getActualUsername() 메서드 사용
        String actualUsername = user.getActualUsername(); // username 필드 값 (NOT NULL)
        return ResponseEntity.ok(java.util.Map.of(
                "userId", user.getUserId(),
                "username", actualUsername,
                "role", user.getRole().name()
        ));
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> logout(jakarta.servlet.http.HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok(java.util.Map.of("message", "로그아웃되었습니다."));
    }

    @GetMapping(value = "/csrf-token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getCsrfToken(CsrfToken csrfToken) {
        if (csrfToken != null) {
            return ResponseEntity.ok(java.util.Map.of(
                "token", csrfToken.getToken(),
                "headerName", csrfToken.getHeaderName(),
                "parameterName", csrfToken.getParameterName()
            ));
        }
        return ResponseEntity.ok(java.util.Map.of("token", "", "headerName", "X-CSRF-TOKEN"));
    }
}
