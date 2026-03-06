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
        // 기존 데이터 마이그레이션: userid가 null인 경우 username 값을 userid로 복사
        try {
            java.util.List<User> allUsers = userRepository.findAll();
            boolean hasMigration = false;
            for (User user : allUsers) {
                if (user.getUserid() == null || user.getUserid().isEmpty()) {
                    // 기존 username을 userid로 사용
                    String oldUsername = user.getUsername();
                    if (oldUsername != null && !oldUsername.isEmpty()) {
                        user.setUserid(oldUsername);
                        // username은 그대로 유지 (사용자명)
                        userRepository.save(user);
                        hasMigration = true;
                        log.info("기존 사용자 데이터 마이그레이션: {} -> userid: {}, username: {}", oldUsername, oldUsername, oldUsername);
                    } else {
                        // username도 없는 경우는 삭제하거나 기본값 설정
                        log.warn("userid와 username이 모두 없는 사용자 발견 (ID: {}), 삭제합니다.", user.getId());
                        userRepository.delete(user);
                    }
                }
            }
            if (hasMigration) {
                log.info("데이터 마이그레이션이 완료되었습니다.");
            }
        } catch (Exception e) {
            log.error("데이터 마이그레이션 중 오류 발생", e);
        }

        // 관리자 계정이 없으면 생성
        if (!userRepository.existsByUserid("admin")) {
            User admin = User.builder()
                    .userid("admin")
                    .username("관리자")
                    .password(passwordEncoder.encode("password"))
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("관리자 계정이 생성되었습니다: admin / password");
        }

        // 테스트용 일반 사용자 계정 생성 (없으면)
        if (!userRepository.existsByUserid("user1")) {
            User user1 = User.builder()
                    .userid("user1")
                    .username("사용자1")
                    .password(passwordEncoder.encode("password"))
                    .role(User.Role.USER)
                    .build();
            userRepository.save(user1);
            log.info("테스트 사용자 계정이 생성되었습니다: user1 / password");
        }

        if (!userRepository.existsByUserid("user2")) {
            User user2 = User.builder()
                    .userid("user2")
                    .username("사용자2")
                    .password(passwordEncoder.encode("password"))
                    .role(User.Role.USER)
                    .build();
            userRepository.save(user2);
            log.info("테스트 사용자 계정이 생성되었습니다: user2 / password");
        }
    }
}
