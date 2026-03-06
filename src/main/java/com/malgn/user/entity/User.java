package com.malgn.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid", unique = true, nullable = false, length = 50)
    private String userid;

    @Column(name = "username", length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getUsername() {
        return userid; // Spring Security는 userid를 사용
    }

    // 실제 사용자명(username 필드)을 반환하는 메서드
    // UserDetails의 getUsername()이 userid를 반환하므로, 실제 username 필드를 가져오기 위한 메서드
    public String getActualUsername() {
        return username; // username 필드 값 반환 (null일 수 있음)
    }
    
    // 표시용 이름 반환 (username이 있으면 username, 없으면 userid)
    public String getDisplayName() {
        return username != null && !username.isEmpty() ? username : userid;
    }

    public enum Role {
        ADMIN, USER
    }
}
