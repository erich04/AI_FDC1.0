package com.smartarchive.archivemanage.repository;

import com.smartarchive.archivemanage.domain.DocumentAttach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentAttachRepository extends JpaRepository<DocumentAttach, Long> {
    // 可以根据需要添加自定义查询方法
}
