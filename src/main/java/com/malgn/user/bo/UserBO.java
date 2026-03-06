package com.malgn.user.bo;

import com.malgn.user.entity.User;
import com.malgn.user.repository.UserRepository;
import com.malgn.exception.UserNotFoundException;
import com.malgn.exception.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
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
    @Lazy
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // 비즈니스 로직: 관리자 여부 확인 (userid로 DB에서 조회)
    public boolean isAdmin(String userid) {
        User user = userRepository.findByUserid(userid)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userid));
        return user.getRole() == User.Role.ADMIN;
    }

    // 비즈니스 로직: 사용자명 일치 확인 (userid로 DB에서 조회)
    public boolean isSameUser(String userid1, String userid2) {
        User user1 = userRepository.findByUserid(userid1)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userid1));
        return user1.getUserid().equals(userid2);
    }

    // 비즈니스 로직: 로그인 처리
    public User login(String userid, String password, HttpServletRequest request) {
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userid,
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
        User loggedInUser = userRepository.findByUserid(authentication.getName())
            .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        return loggedInUser;
    }

    // 비즈니스 로직: 회원가입 처리
    @Transactional
    public User signup(String userid, String username, String password) {
        // userid 중복 확인
        if (userRepository.existsByUserid(userid)) {
            throw new UsernameAlreadyExistsException("UserID already exists: " + userid);
        }

        // Entity 생성
        User newUser = User.builder()
            .userid(userid)
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(User.Role.USER)
            .build();
        
        return userRepository.save(newUser);
    }

    // 비즈니스 로직: userid 중복 확인
    public boolean checkUseridExists(String userid) {
        return userRepository.existsByUserid(userid);
    }

    // 비즈니스 로직: 사용자 조회 (userid로)
    public User findByUserid(String userid) {
        return userRepository.findByUserid(userid)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userid));
    }
}
