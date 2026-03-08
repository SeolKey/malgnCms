package com.malgn.content.repository;

import com.malgn.content.entity.Contents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentsRepository extends JpaRepository<Contents, Long> {
    @EntityGraph(attributePaths = {"createdBy", "lastModifiedBy"})
    Page<Contents> findAll(Pageable pageable);
    
    @EntityGraph(attributePaths = {"createdBy", "lastModifiedBy"})
    java.util.Optional<Contents> findById(Long id);
    
    @Modifying
    @Query("UPDATE Contents c SET c.viewCount = c.viewCount + 1 WHERE c.id = :id")
    void incrementViewCount(@Param("id") Long id);
}
