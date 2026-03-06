package com.malgn.service;

import com.malgn.dto.LoginRequest;
import com.malgn.dto.LoginResponse;
import com.malgn.dto.SignupRequest;
import com.malgn.entity.User;
import com.malgn.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        // 사용자 정보 가져오기
        User user = userService.findByUsername(authentication.getName());
        String token = tokenProvider.generateToken(authentication.getName(), user.getRole().name());

        return new LoginResponse(token, "Bearer");
    }

    @Transactional
    public User signup(SignupRequest request) {
        return userService.createUser(
            request.getUsername(),
            request.getPassword(),
            User.Role.USER
        );
    }

    public boolean checkUsernameExists(String username) {
        return userService.existsByUsername(username);
    }
}
