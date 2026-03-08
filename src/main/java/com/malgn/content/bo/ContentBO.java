package com.malgn.content.bo;

import com.malgn.content.entity.Contents;
import com.malgn.content.repository.ContentsRepository;
import com.malgn.user.entity.User;
import com.malgn.exception.ResourceNotFoundException;
import com.malgn.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentBO {
    private final ContentsRepository contentsRepository;

    // 비즈니스 로직: 전체 목록 조회
    @Transactional(readOnly = true)
    public Page<Contents> getAllContents(Pageable pageable) {
        Page<Contents> contents = contentsRepository.findAll(pageable);
        // LAZY 로딩된 User 엔티티를 트랜잭션 내에서 초기화
        contents.getContent().forEach(content -> {
            if (content.getCreatedByUser() != null) {
                content.getCreatedByUser().getActualUsername(); // 강제 초기화
            }
            if (content.getLastModifiedByUser() != null) {
                content.getLastModifiedByUser().getActualUsername(); // 강제 초기화
            }
        });
        return contents;
    }

    // 비즈니스 로직: ID로 조회 및 조회수 증가
    @Transactional
    public Contents getContentById(Long id) {
        Contents content = findByIdWithUser(id);
        
        // 조회수만 증가 (lastModifiedDate는 변경되지 않음)
        contentsRepository.incrementViewCount(id);
        
        // 엔티티를 다시 조회하여 업데이트된 조회수 반영
        return findByIdWithUser(id);
    }
    
    // User 엔티티를 함께 로드하는 조회 메서드
    @Transactional(readOnly = true)
    public Contents findByIdWithUser(Long id) {
        return contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
    }

    // 비즈니스 로직: 콘텐츠 생성
    @Transactional
    public Contents createContent(Contents content) {
        // viewCount가 null이면 0으로 설정
        if (content.getViewCount() == null) {
            content.setViewCount(0L);
        }
        
        return contentsRepository.save(content);
    }

    // 비즈니스 로직: 콘텐츠 수정
    @Transactional
    public Contents updateContent(Long id, Contents content, User currentUser) {
        Contents existingContent = findByIdWithUser(id);
        
        // 권한 확인
        validateUpdatePermission(existingContent, currentUser);
        
        // 수정 처리: title과 description만 업데이트
        // createdBy는 절대 변경되지 않도록 보호 (원래 작성자 유지)
        // viewCount도 변경되지 않도록 보호
        existingContent.setTitle(content.getTitle());
        existingContent.setDescription(content.getDescription());
        
        // 수정자 정보 업데이트 (관리자가 수정한 경우 관리자로 표시)
        existingContent.setLastModifiedByUser(currentUser);
        existingContent.setLastModifiedDate(java.time.LocalDateTime.now());

        return contentsRepository.save(existingContent);
    }

    // 비즈니스 로직: 콘텐츠 삭제
    @Transactional
    public void deleteContent(Long id, User currentUser) {
        Contents content = findByIdWithUser(id);
        
        // 권한 확인
        validateDeletePermission(content, currentUser);

        contentsRepository.delete(content);
    }

    // 비즈니스 로직: 권한 확인 (수정 가능 여부)
    public void validateUpdatePermission(Contents content, User currentUser) {
        User createdByUser = content.getCreatedByUser();
        if (createdByUser == null) {
            throw new UnauthorizedException("Content creator not found");
        }
        // username으로 비교 (username은 unique이므로 정확한 비교 가능)
        if (!createdByUser.getActualUsername().equals(currentUser.getActualUsername())
            && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to update this content");
        }
    }

    // 비즈니스 로직: 권한 확인 (삭제 가능 여부)
    public void validateDeletePermission(Contents content, User currentUser) {
        User createdByUser = content.getCreatedByUser();
        if (createdByUser == null) {
            throw new UnauthorizedException("Content creator not found");
        }
        // username으로 비교 (username은 unique이므로 정확한 비교 가능)
        if (!createdByUser.getActualUsername().equals(currentUser.getActualUsername())
            && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to delete this content");
        }
    }
}
