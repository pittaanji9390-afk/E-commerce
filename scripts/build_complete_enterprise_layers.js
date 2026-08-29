const { write } = require('./generator_helper');

console.log('Building Complete Enterprise Layers...');

// 1. Messaging Domain
write('backend/src/main/java/com/marketplace/messaging/domain/MessageThread.java', `
package com.marketplace.messaging.domain;

import com.marketplace.customer.domain.Customer;
import com.marketplace.seller.domain.Seller;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message_threads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageThread extends AuditableEntity {

    @Column(name = "thread_subject", nullable = false, length = 200)
    private String threadSubject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @Column(name = "last_message_at", nullable = false)
    @Builder.Default
    private Instant lastMessageAt = Instant.now();

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    public void addMessage(ChatMessage msg) {
        messages.add(msg);
        msg.setThread(this);
        this.lastMessageAt = Instant.now();
    }
}
`);

write('backend/src/main/java/com/marketplace/messaging/domain/ChatMessage.java', `
package com.marketplace.messaging.domain;

import com.marketplace.identity.domain.User;
import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    private MessageThread thread;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "sent_at", nullable = false)
    @Builder.Default
    private Instant sentAt = Instant.now();
}
`);

write('backend/src/main/java/com/marketplace/messaging/repository/MessageThreadRepository.java', `
package com.marketplace.messaging.repository;

import com.marketplace.messaging.domain.MessageThread;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageThreadRepository extends JpaRepository<MessageThread, UUID> {
    Page<MessageThread> findByCustomerIdOrderByLastMessageAtDesc(UUID customerId, Pageable pageable);
    Page<MessageThread> findBySellerIdOrderByLastMessageAtDesc(UUID sellerId, Pageable pageable);
}
`);

write('backend/src/main/java/com/marketplace/messaging/repository/ChatMessageRepository.java', `
package com.marketplace.messaging.repository;

import com.marketplace.messaging.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    Page<ChatMessage> findByThreadIdOrderBySentAtAsc(UUID threadId, Pageable pageable);
}
`);

// 2. Compliance & GDPR Erasure Domain
write('backend/src/main/java/com/marketplace/compliance/domain/GdprRequestType.java', `
package com.marketplace.compliance.domain;

public enum GdprRequestType {
    DATA_EXPORT,
    DATA_RECTIFICATION,
    DATA_ERASURE_RIGHT_TO_BE_FORGOTTEN,
    RESTRICTION_OF_PROCESSING
}
`);

write('backend/src/main/java/com/marketplace/compliance/domain/GdprRequestStatus.java', `
package com.marketplace.compliance.domain;

public enum GdprRequestStatus {
    SUBMITTED,
    IN_PROGRESS,
    FULFILLED,
    REJECTED
}
`);

write('backend/src/main/java/com/marketplace/compliance/domain/GdprComplianceRequest.java', `
package com.marketplace.compliance.domain;

import com.marketplace.identity.domain.User;
import com.marketplace.shared.domain.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "gdpr_compliance_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GdprComplianceRequest extends AuditableEntity {

    @Column(name = "request_number", nullable = false, unique = true, length = 50)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", length = 50, nullable = false)
    private GdprRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30, nullable = false)
    @Builder.Default
    private GdprRequestStatus status = GdprRequestStatus.SUBMITTED;

    @Column(name = "export_download_url", length = 500)
    private String exportDownloadUrl;

    @Column(name = "fulfilled_at")
    private Instant fulfilledAt;

    @Column(name = "compliance_officer_notes", columnDefinition = "TEXT")
    private String complianceOfficerNotes;
}
`);

write('backend/src/main/java/com/marketplace/compliance/repository/GdprComplianceRequestRepository.java', `
package com.marketplace.compliance.repository;

import com.marketplace.compliance.domain.GdprComplianceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GdprComplianceRequestRepository extends JpaRepository<GdprComplianceRequest, UUID> {
    Page<GdprComplianceRequest> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
`);

console.log('Enterprise Layers built.');
`);
