package org.test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.entity.RequestLog;

import java.util.UUID;

public interface RequestLogRepository extends JpaRepository<RequestLog, UUID> {
}
