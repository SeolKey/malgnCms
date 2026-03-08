package com.malgn.user.bo;

import com.malgn.user.entity.User;
import com.malgn.user.repository.UserRepository;
import com.malgn.exception.UserNotFoundException;
import com.malgn.exception.UsernameExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBO {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // 비즈니스 로직: 관리자 여부 확인 (userId로 DB에서 조회)
    public boolean isAdmin(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return user.getRole() == User.Role.ADMIN;
    }

    // 비즈니스 로직: 사용자명 일치 확인 (userId로 DB에서 조회)
    public boolean isSameUser(String userId1, String userId2) {
        User user1 = userRepository.findByUserId(userId1)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId1));
        return user1.getUserId().equals(userId2);
    }

    // 비즈니스 로직: 로그인 처리
    public User login(String userId, String password, HttpServletRequest request) {
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userId,
            password
        );
        
        // Spring Security가 자동으로 인증 처리
        Authentication authentication = authenticationManager.authenticate(authToken);

        // SecurityContext에 인증 정보 저장 (세션 생성)
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        
        // 세션에 SecurityContext 저장
        request.getSession().setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            securityContext
        );

        // 사용자 정보 가져오기
        User loggedInUser = userRepository.findByUserId(authentication.getName())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return loggedInUser;
    }

    // 비즈니스 로직: 회원가입 처리
    @Transactional
    public User signup(String userId, String username, String password) {
        // userId 중복 확인
        if (userRepository.existsByUserId(userId)) {
            throw new UsernameExistsException("UserID already exists: " + userId);
        }

        // username 중복 확인 (username은 unique)
        if (username != null && userRepository.existsByUsername(username)) {
            throw new UsernameExistsException("Username already exists: " + username);
        }

        // username 필수 확인
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        // Entity 생성
        User newUser = User.builder()
            .userId(userId)
            .username(username.trim())
            .password(passwordEncoder.encode(password))
            .role(User.Role.USER)
            .build();
        
        return userRepository.save(newUser);
    }

    // 비즈니스 로직: userId 중복 확인
    public boolean checkUseridExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 비즈니스 로직: 사용자 조회 (userId로)
    public User findByUserid(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
    }
}
