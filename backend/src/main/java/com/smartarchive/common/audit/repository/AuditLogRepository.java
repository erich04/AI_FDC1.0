package com.smartarchive.common.audit.repository;

import com.smartarchive.common.audit.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // 可以根据需要添加自定义查询方法
}
