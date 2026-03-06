package com.malgn.user.controller;

import com.malgn.user.bo.UserBO;
import com.malgn.user.entity.User;
import com.malgn.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserRestController {

    private final UserBO userBO;

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> signup(@Valid @RequestBody User user) {
        User savedUser = userBO.signup(user.getUserid(), user.getUsername(), user.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> login(@Valid @RequestBody User user, HttpServletRequest request) {
        User loggedInUser = userBO.login(user.getUserid(), user.getPassword(), request);
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
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        if (userPrincipal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("message", "인증되지 않은 사용자입니다."));
        }
        User user = userPrincipal.getUser();
        // User.getUsername()은 UserDetails 인터페이스로 인해 userid를 반환하므로,
        // 실제 사용자명 필드를 가져오기 위해 getActualUsername() 메서드 사용
        String actualUsername = user.getActualUsername(); // username 필드 값 (null일 수 있음)
        return ResponseEntity.ok(java.util.Map.of(
                "userid", user.getUserid(),
                "username", actualUsername != null ? actualUsername : user.getUserid(), // username이 없으면 userid 사용
                "role", user.getRole().name()
        ));
    }

    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> logout(jakarta.servlet.http.HttpServletRequest request) {
        request.getSession().invalidate();
        return ResponseEntity.ok(java.util.Map.of("message", "로그아웃되었습니다."));
    }
}
