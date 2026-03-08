package com.malgn.content.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.malgn.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Contents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "view_count", nullable = false)
    @Builder.Default
    private Long viewCount = 0L;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDateTime createdDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "username", nullable = false, updatable = false) // 작성자는 수정 불가
    @Getter(AccessLevel.NONE) // Lombok이 이 필드에 대한 getter를 생성하지 않도록 함
    private User createdBy;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_modified_by", referencedColumnName = "username")
    @Getter(AccessLevel.NONE) // Lombok이 이 필드에 대한 getter를 생성하지 않도록 함
    private User lastModifiedBy;

    // 편의 메서드: created_by의 username 반환 (JSON 직렬화용)
    @JsonProperty("createdBy")
    public String getCreatedBy() {
        if (createdBy == null) {
            return null;
        }
        // LAZY 로딩 강제 초기화
        try {
            return createdBy.getActualUsername();
        } catch (org.hibernate.LazyInitializationException e) {
            // 트랜잭션 밖에서 호출된 경우 null 반환
            return null;
        }
    }

    // 편의 메서드: last_modified_by의 username 반환 (JSON 직렬화용)
    @JsonProperty("lastModifiedBy")
    public String getLastModifiedBy() {
        if (lastModifiedBy == null) {
            return null;
        }
        // LAZY 로딩 강제 초기화
        try {
            return lastModifiedBy.getActualUsername();
        } catch (org.hibernate.LazyInitializationException e) {
            // 트랜잭션 밖에서 호출된 경우 null 반환
            return null;
        }
    }

    // User 엔티티를 설정하는 메서드 (내부 사용)
    public void setCreatedByUser(User user) {
        this.createdBy = user;
    }

    // User 엔티티를 설정하는 메서드 (내부 사용)
    public void setLastModifiedByUser(User user) {
        this.lastModifiedBy = user;
    }

    // User 엔티티를 가져오는 메서드 (내부 사용)
    public User getCreatedByUser() {
        return createdBy;
    }

    // User 엔티티를 가져오는 메서드 (내부 사용)
    public User getLastModifiedByUser() {
        return lastModifiedBy;
    }
}
