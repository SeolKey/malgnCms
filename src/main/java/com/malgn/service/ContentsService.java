package com.malgn.service;

import com.malgn.dto.ContentsRequest;
import com.malgn.dto.ContentsResponse;
import com.malgn.entity.Contents;
import com.malgn.entity.User;
import com.malgn.exception.ResourceNotFoundException;
import com.malgn.exception.UnauthorizedException;
import com.malgn.repository.ContentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsRepository contentsRepository;

    @Transactional(readOnly = true)
    public Page<ContentsResponse> getAllContents(Pageable pageable) {
        return contentsRepository.findAll(pageable)
            .map(this::toResponse);
    }

    @Transactional
    public ContentsResponse getContentById(Long id) {
        Contents content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
        
        // 조회수 증가
        content.setViewCount(content.getViewCount() + 1);
        contentsRepository.save(content);
        
        return toResponse(content);
    }

    @Transactional
    public ContentsResponse createContent(ContentsRequest request, String username) {
        Contents content = Contents.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .viewCount(0L)
            .createdBy(username)
            .build();

        Contents saved = contentsRepository.save(content);
        return toResponse(saved);
    }

    @Transactional
    public ContentsResponse updateContent(Long id, ContentsRequest request, User currentUser) {
        Contents content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        // 권한 확인: 작성자 본인 또는 ADMIN만 수정 가능
        if (!content.getCreatedBy().equals(currentUser.getUsername())
            && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to update this content");
        }

        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setLastModifiedBy(currentUser.getUsername());

        Contents updated = contentsRepository.save(content);
        return toResponse(updated);
    }

    @Transactional
    public void deleteContent(Long id, User currentUser) {
        Contents content = contentsRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));

        // 권한 확인: 작성자 본인 또는 ADMIN만 삭제 가능
        if (!content.getCreatedBy().equals(currentUser.getUsername())
            && currentUser.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to delete this content");
        }

        contentsRepository.delete(content);
    }

    private ContentsResponse toResponse(Contents content) {
        return ContentsResponse.builder()
            .id(content.getId())
            .title(content.getTitle())
            .description(content.getDescription())
            .viewCount(content.getViewCount())
            .createdDate(content.getCreatedDate())
            .createdBy(content.getCreatedBy())
            .lastModifiedDate(content.getLastModifiedDate())
            .lastModifiedBy(content.getLastModifiedBy())
            .build();
    }
}
