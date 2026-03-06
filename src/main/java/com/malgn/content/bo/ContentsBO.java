package com.malgn.content.bo;

import com.malgn.content.entity.Contents;
import com.malgn.content.repository.ContentsRepository;
import com.malgn.user.entity.User;
import com.malgn.user.repository.UserRepository;
import com.malgn.exception.ResourceNotFoundException;
import com.malgn.exception.UnauthorizedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentsBO {
    private final ContentsRepository contentsRepository;
    private final UserRepository userRepository;

    @Getter
    private Contents contents;

    // Entity 설정
    public void setContents(Contents contents) {
        this.contents = contents;
    }

    // Entity로부터 BO 생성 (인스턴스 메서드)
    public ContentsBO withContents(Contents entity) {
        this.contents = entity;
        return this;
    }

    // 편의 메서드: Entity의 필드에 쉽게 접근
    public Long getId() {
        return contents.getId();
    }

    public String getTitle() {
        return contents.getTitle();
    }

    public String getDescription() {
        return contents.getDescription();
    }

    public Long getViewCount() {
        return contents.getViewCount();
    }

    public java.time.LocalDateTime getCreatedDate() {
        return contents.getCreatedDate();
    }

    public String getCreatedBy() {
        String createdBy = contents.getCreatedBy();
        if (createdBy == null || createdBy.isEmpty()) {
            return createdBy;
        }
        
        // created_by 값이 userid인지 확인하고, userid인 경우 username으로 변환
        try {
            // 먼저 userid로 조회 시도 (created_by에 userid가 저장된 경우)
            java.util.Optional<User> userOpt = userRepository.findByUserid(createdBy);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // userid로 조회 성공 -> username 반환 (없으면 userid 반환)
                String username = user.getUsername();
                if (username != null && !username.isEmpty()) {
                    return username; // username 반환 (예: "admin" -> "관리자")
                }
                // username이 없는 경우 userid 반환
                return createdBy;
            }
            
            // userid로 조회 실패 -> username으로 조회 시도 (created_by에 이미 username이 저장된 경우)
            userOpt = userRepository.findByUsername(createdBy);
            if (userOpt.isPresent()) {
                // 이미 username인 경우 그대로 반환
                return createdBy;
            }
        } catch (Exception e) {
            // 조회 실패 시 원래 값 반환 (예외 발생 시)
            // 로그는 필요시 추가 가능
        }
        
        return createdBy; // 변환 실패 시 원래 값 반환
    }

    public java.time.LocalDateTime getLastModifiedDate() {
        return contents.getLastModifiedDate();
    }

    public String getLastModifiedBy() {
        String lastModifiedBy = contents.getLastModifiedBy();
        if (lastModifiedBy == null || lastModifiedBy.isEmpty()) {
            return lastModifiedBy;
        }
        
        // last_modified_by 값이 userid인지 확인하고, userid인 경우 username으로 변환
        try {
            User user = userRepository.findByUserid(lastModifiedBy).orElse(null);
            if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
                return user.getUsername(); // username 반환
            }
            // userid로 조회 실패 시 username으로 조회 시도
            user = userRepository.findByUsername(lastModifiedBy).orElse(null);
            if (user != null) {
                return lastModifiedBy; // 이미 username인 경우 그대로 반환
            }
        } catch (Exception e) {
            // 조회 실패 시 원래 값 반환
        }
        
        return lastModifiedBy; // 변환 실패 시 원래 값 반환
    }

    // 비즈니스 로직: 전체 목록 조회
    @Transactional(readOnly = true)
    public Page<ContentsBO> getAllContents(Pageable pageable) {
        return contentsRepository.findAll(pageable)
            .map(entity -> {
                ContentsBO bo = new ContentsBO(contentsRepository, userRepository);
                bo.setContents(entity);
                // created_by가 userid인 경우 username으로 변환하여 저장
                String createdBy = entity.getCreatedBy();
                if (createdBy != null && !createdBy.isEmpty()) {
                    try {
                        User user = userRepository.findByUserid(createdBy).orElse(null);
                        if (user != null && user.getUsername() != null && !user.getUsername().isEmpty()) {
                            // userid로 조회 성공 -> username으로 업데이트 (메모리상에서만)
                            entity.setCreatedBy(user.getUsername());
                        }
                    } catch (Exception e) {
                        // 변환 실패 시 무시
                    }
                }
                return bo;
            });
    }

    // 비즈니스 로직: ID로 조회 및 조회수 증가
    @Transactional
    public ContentsBO getContentById(Long id) {
        Contents content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        
        // 조회수만 증가 (lastModifiedDate는 변경되지 않음)
        contentsRepository.incrementViewCount(id);
        
        // 엔티티를 다시 조회하여 업데이트된 조회수 반영
        content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        
        ContentsBO contentBO = new ContentsBO(contentsRepository, userRepository);
        contentBO.setContents(content);
        
        return contentBO;
    }

    // 비즈니스 로직: 콘텐츠 생성
    @Transactional
    public ContentsBO createContent(Contents content) {
        // viewCount가 null이면 0으로 설정
        if (content.getViewCount() == null) {
            content.setViewCount(0L);
        }
        
        Contents saved = contentsRepository.save(content);
        
        ContentsBO contentBO = new ContentsBO(contentsRepository, userRepository);
        contentBO.setContents(saved);
        return contentBO;
    }

    // 비즈니스 로직: 콘텐츠 수정
    @Transactional
    public ContentsBO updateContent(Long id, Contents content, User currentUser) {
        Contents existingContent = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        ContentsBO contentBO = new ContentsBO(contentsRepository, userRepository);
        contentBO.setContents(existingContent);
        
        // 권한 확인
        contentBO.validateUpdatePermission(currentUser);
        
        // 수정 처리: lastModifiedBy와 lastModifiedDate 명시적으로 설정
        // @LastModifiedDate를 제거했으므로 수동으로만 설정됨 (조회수 증가 시 자동 업데이트되지 않음)
        // last_modified_by에는 항상 username(사용자명)만 저장
        // User.getUsername()은 UserDetails 인터페이스로 인해 userid를 반환하므로,
        // 실제 사용자명 필드를 가져오기 위해 getActualUsername() 메서드 사용
        String modifiedBy = currentUser.getActualUsername(); // username 필드 값 반환 (null일 수 있음)
        existingContent.setTitle(content.getTitle());
        existingContent.setDescription(content.getDescription());
        existingContent.setLastModifiedBy(modifiedBy);
        existingContent.setLastModifiedDate(java.time.LocalDateTime.now());

        Contents updated = contentsRepository.save(existingContent);
        
        // 저장 후 다시 조회하여 확실히 저장되었는지 확인
        updated = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        
        ContentsBO updatedBO = new ContentsBO(contentsRepository, userRepository);
        updatedBO.setContents(updated);
        return updatedBO;
    }

    // 비즈니스 로직: 콘텐츠 삭제
    @Transactional
    public void deleteContent(Long id, User currentUser) {
        Contents content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        ContentsBO contentBO = new ContentsBO(contentsRepository, userRepository);
        contentBO.setContents(content);
        
        // 권한 확인
        contentBO.validateDeletePermission(currentUser);

        contentsRepository.delete(content);
    }

    // 비즈니스 로직: 조회수 증가 (더 이상 사용하지 않음 - Repository의 incrementViewCount 사용)
    @Deprecated
    public void incrementViewCount() {
        Long currentCount = contents.getViewCount() != null ? contents.getViewCount() : 0L;
        contents.setViewCount(currentCount + 1);
    }

    // 비즈니스 로직: 권한 확인 (수정 가능 여부)
    public void validateUpdatePermission(User currentUser) {
        // created_by와 비교 (username 또는 userid 모두 확인)
        String currentUserIdentifier = currentUser.getUsername() != null && !currentUser.getUsername().isEmpty()
            ? currentUser.getUsername()
            : currentUser.getUserid();
        if (!contents.getCreatedBy().equals(currentUserIdentifier)
            && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to update this content");
        }
    }

    // 비즈니스 로직: 권한 확인 (삭제 가능 여부)
    public void validateDeletePermission(User currentUser) {
        // created_by와 비교 (username 또는 userid 모두 확인)
        String currentUserIdentifier = currentUser.getUsername() != null && !currentUser.getUsername().isEmpty()
            ? currentUser.getUsername()
            : currentUser.getUserid();
        if (!contents.getCreatedBy().equals(currentUserIdentifier)
            && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to delete this content");
        }
    }

    // 비즈니스 로직: 수정 처리
    public void update(String title, String description, String modifiedBy) {
        contents.setTitle(title);
        contents.setDescription(description);
        contents.setLastModifiedBy(modifiedBy);
        contents.setLastModifiedDate(java.time.LocalDateTime.now());
    }
}
