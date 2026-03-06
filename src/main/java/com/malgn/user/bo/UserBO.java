package com.malgn.user.bo;

import com.malgn.user.entity.User;
import com.malgn.user.repository.UserRepository;
import com.malgn.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBO implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // 비즈니스 로직: 관리자 여부 확인 (username으로 DB에서 조회)
    public boolean isAdmin(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getRole() == User.Role.ADMIN;
    }

    // 비즈니스 로직: 사용자명 일치 확인 (username으로 DB에서 조회)
    public boolean isSameUser(String username1, String username2) {
        User user1 = userRepository.findByUsername(username1)
            .orElseThrow(() -> new RuntimeException("User not found: " + username1));
        return user1.getUsername().equals(username2);
    }

    // 비즈니스 로직: 로그인 처리
    public User login(String username, String password) {
        // 인증 토큰 생성
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            username,
            password
        );
        
        // Spring Security가 자동으로 인증 처리하고 세션 생성
        Authentication authentication = authenticationManager.authenticate(authToken);

        // 사용자 정보 가져오기
        User loggedInUser = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return loggedInUser;
    }

    // 비즈니스 로직: 회원가입 처리
    @Transactional
    public User signup(String username, String password) {
        // 중복 확인
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        // Entity 생성
        User newUser = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .role(User.Role.USER)
            .build();
        
        return userRepository.save(newUser);
    }

    // 비즈니스 로직: 사용자명 중복 확인
    public boolean checkUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    // 비즈니스 로직: 사용자 조회
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Spring Security UserDetailsService 구현: 인증을 위한 사용자 정보 제공
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new UserPrincipal(foundUser);
    }
}
