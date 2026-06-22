package fun.ineedit.service;

import fun.ineedit.dto.Dto.*;
import fun.ineedit.entity.Category;
import fun.ineedit.entity.Need;
import fun.ineedit.entity.SharedNeedBatch;
import fun.ineedit.entity.User;
import fun.ineedit.repository.CategoryRepository;
import fun.ineedit.repository.NeedRepository;
import fun.ineedit.repository.SharedNeedBatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SharingService {

    private final SharedNeedBatchRepository batchRepository;
    private final NeedRepository needRepository;
    private final CategoryRepository categoryRepository;

    @Value("${ineedit.share.base-url}")
    private String shareBaseUrl;

    private static final int EXPIRY_DAYS = 30;

    @Transactional
    public ShareResponse createShare(User sender, ShareRequest request) {
        if (request.needIds() == null || request.needIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one need to share");
        }
        if (request.recipientPhone() == null || request.recipientPhone().isBlank()) {
            throw new IllegalArgumentException("Recipient phone number is required");
        }

        // Only allow sharing needs that belong to the requesting user
        List<Need> needs = needRepository.findAllById(request.needIds());
        needs.forEach(n -> {
            if (!n.getUser().getId().equals(sender.getId())) {
                throw new IllegalArgumentException("One or more needs do not belong to you");
            }
        });
        if (needs.isEmpty()) {
            throw new IllegalArgumentException("No valid needs found to share");
        }

        SharedNeedBatch batch = SharedNeedBatch.builder()
                .sender(sender)
                .recipientPhone(request.recipientPhone().trim())
                .needTexts(needs.stream().map(Need::getText).toList())
                .vendors(needs.stream().map(n -> n.getVendor() == null ? "" : n.getVendor()).toList())
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plus(EXPIRY_DAYS, ChronoUnit.DAYS))
                .claimed(false)
                .build();

        batchRepository.save(batch);

        String claimUrl = shareBaseUrl + "/share/" + batch.getId();
        return new ShareResponse(claimUrl);
    }

    public SharePreviewResponse getPreview(String batchId) {
        SharedNeedBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new NoSuchElementException("Share not found"));

        boolean expired = Instant.now().isAfter(batch.getExpiresAt());

        return new SharePreviewResponse(
                batch.getSender().getName(),
                batch.getNeedTexts(),
                batch.isClaimed(),
                expired
        );
    }

    @Transactional
    public ClaimResponse claim(String batchId, User recipient) {
        SharedNeedBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new NoSuchElementException("Share not found"));

        if (Instant.now().isAfter(batch.getExpiresAt())) {
            throw new IllegalArgumentException("This share link has expired");
        }
        if (batch.isClaimed()) {
            throw new IllegalArgumentException("This share has already been claimed");
        }
        if (batch.getSender().getId().equals(recipient.getId())) {
            throw new IllegalArgumentException("You cannot claim your own shared needs");
        }

        String categoryName = "Shared - " + batch.getSender().getName();

        Category category = categoryRepository
                .findByUserAndName(recipient, categoryName)
                .orElseGet(() -> categoryRepository.save(
                        Category.builder()
                                .user(recipient)
                                .name(categoryName)
                                .build() // createdAt auto-set via @PrePersist
                ));

        List<String> texts = batch.getNeedTexts();
        List<String> vendors = batch.getVendors();

        for (int i = 0; i < texts.size(); i++) {
            String vendor = (vendors != null && i < vendors.size() && !vendors.get(i).isBlank())
                    ? vendors.get(i) : null;

            Need need = Need.builder()
                    .user(recipient)
                    .category(category)
                    .text(texts.get(i))
                    .vendor(vendor)
                    .active(true)
                    .build(); // createdAt auto-set via @PrePersist
            needRepository.save(need);
        }

        batch.setClaimed(true);
        batch.setClaimedByUserId(recipient.getId());
        batch.setClaimedAt(Instant.now());
        batchRepository.save(batch);

        return new ClaimResponse(texts.size(), categoryName);
    }
}