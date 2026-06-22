package fun.ineedit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "shared_need_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedNeedBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "recipient_phone", nullable = false)
    private String recipientPhone;

    // Snapshot of the need text/vendor at share time, stored as JSON-ish
    // simple strings so the share survives even if the original needs
    // are later edited or deleted by the sender.
    @ElementCollection
    @CollectionTable(name = "shared_need_items", joinColumns = @JoinColumn(name = "batch_id"))
    @Column(name = "need_text")
    private List<String> needTexts;

    @ElementCollection
    @CollectionTable(name = "shared_need_vendors", joinColumns = @JoinColumn(name = "batch_id"))
    @Column(name = "vendor")
    private List<String> vendors;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean claimed;

    @Column(name = "claimed_by_user_id")
    private Long claimedByUserId;

    @Column(name = "claimed_at")
    private Instant claimedAt;
}