package com.fares7elsadek.syncspace.messaging.infrastructure.repository;

import com.fares7elsadek.syncspace.messaging.domain.model.MessageAttachments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<MessageAttachments, String> {
}
