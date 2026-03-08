package com.malgn.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uk_users_userid", columnNames = "userid"),
    @UniqueConstraint(name = "uk_users_username", columnNames = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid", unique = true, nullable = false, length = 50)
    private String userId;

    @NotBlank(message = "Username is required")
    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // 역직렬화(요청)는 허용, 직렬화(응답)는 제외
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
    public String getUsername() {
        return userId; // Spring Security는 userId를 사용
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

    // 실제 사용자명(username 필드)을 반환하는 메서드
    public String getActualUsername() {
        return username;
    }

    public enum Role {
        ADMIN, USER
    }
}
