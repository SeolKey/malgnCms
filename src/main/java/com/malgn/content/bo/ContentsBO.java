package com.malgn.content.bo;

import com.malgn.content.entity.Contents;
import com.malgn.content.repository.ContentsRepository;
import com.malgn.user.entity.User;
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
        return contents.getCreatedBy();
    }

    public java.time.LocalDateTime getLastModifiedDate() {
        return contents.getLastModifiedDate();
    }

    public String getLastModifiedBy() {
        return contents.getLastModifiedBy();
    }

    // 비즈니스 로직: 전체 목록 조회
    @Transactional(readOnly = true)
    public Page<ContentsBO> getAllContents(Pageable pageable) {
        return contentsRepository.findAll(pageable)
            .map(entity -> {
                ContentsBO bo = new ContentsBO(contentsRepository);
                bo.setContents(entity);
                return bo;
            });
    }

    // 비즈니스 로직: ID로 조회 및 조회수 증가
    @Transactional
    public ContentsBO getContentById(Long id) {
        Contents content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        
        ContentsBO contentBO = new ContentsBO(contentsRepository);
        contentBO.setContents(content);
        
        // 조회수 증가
        contentBO.incrementViewCount();
        
        contentsRepository.save(content);
        
        return contentBO;
    }

    // 비즈니스 로직: 콘텐츠 생성
    @Transactional
    public ContentsBO createContent(Contents content) {
        Contents saved = contentsRepository.save(content);
        
        ContentsBO contentBO = new ContentsBO(contentsRepository);
        contentBO.setContents(saved);
        return contentBO;
    }

    // 비즈니스 로직: 콘텐츠 수정
    @Transactional
    public ContentsBO updateContent(Long id, Contents content, User currentUser) {
        Contents existingContent = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        ContentsBO contentBO = new ContentsBO(contentsRepository);
        contentBO.setContents(existingContent);
        
        // 권한 확인 및 수정
        contentBO.validateUpdatePermission(currentUser);
        contentBO.update(content.getTitle(), content.getDescription(), currentUser.getUsername());

        Contents updated = contentsRepository.save(existingContent);
        ContentsBO updatedBO = new ContentsBO(contentsRepository);
        updatedBO.setContents(updated);
        return updatedBO;
    }

    // 비즈니스 로직: 콘텐츠 삭제
    @Transactional
    public void deleteContent(Long id, User currentUser) {
        Contents content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        ContentsBO contentBO = new ContentsBO(contentsRepository);
        contentBO.setContents(content);
        
        // 권한 확인
        contentBO.validateDeletePermission(currentUser);

        contentsRepository.delete(content);
    }

    // 비즈니스 로직: 조회수 증가
    public void incrementViewCount() {
        Long currentCount = contents.getViewCount() != null ? contents.getViewCount() : 0L;
        contents.setViewCount(currentCount + 1);
    }

    // 비즈니스 로직: 권한 확인 (수정 가능 여부)
    public void validateUpdatePermission(User currentUser) {
        if (!contents.getCreatedBy().equals(currentUser.getUsername())
            && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to update this content");
        }
    }

    // 비즈니스 로직: 권한 확인 (삭제 가능 여부)
    public void validateDeletePermission(User currentUser) {
        if (!contents.getCreatedBy().equals(currentUser.getUsername())
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
