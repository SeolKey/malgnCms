package com.malgn.config;

import com.malgn.user.entity.User;
import com.malgn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 관리자 계정이 없으면 생성
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("password"))
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("관리자 계정이 생성되었습니다: admin / password");
        }

        // 테스트용 일반 사용자 계정 생성 (없으면)
        if (!userRepository.existsByUsername("user1")) {
            User user1 = User.builder()
                    .username("user1")
                    .password(passwordEncoder.encode("password"))
                    .role(User.Role.USER)
                    .build();
            userRepository.save(user1);
            log.info("테스트 사용자 계정이 생성되었습니다: user1 / password");
        }

        if (!userRepository.existsByUsername("user2")) {
            User user2 = User.builder()
                    .username("user2")
                    .password(passwordEncoder.encode("password"))
                    .role(User.Role.USER)
                    .build();
            userRepository.save(user2);
            log.info("테스트 사용자 계정이 생성되었습니다: user2 / password");
        }
    }
}
