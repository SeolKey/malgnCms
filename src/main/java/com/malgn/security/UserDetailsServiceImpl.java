package com.malgn.security;

import com.malgn.user.entity.User;
import com.malgn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        // Spring Security의 loadUserByUsername은 실제로는 userid를 받음
        User foundUser = userRepository.findByUserid(userid)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userid));
        return new UserPrincipal(foundUser);
    }
}
