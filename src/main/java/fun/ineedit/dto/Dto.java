package fun.ineedit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public class Dto {

    // ── Auth ──────────────────────────────────────────────────────────
    public record RegisterRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8) String password
    ) {}

    public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
    ) {}

    public record AuthResponse(String token, UserInfo user) {}

    public record UserInfo(Long id, String name, String email) {}

    // ── Category ──────────────────────────────────────────────────────
    public record CategoryRequest(@NotBlank String name) {}

    public record CategoryResponse(Long id, String name, Instant createdAt) {}

    // ── Need ──────────────────────────────────────────────────────────
    public record NeedRequest(
        @NotBlank String text,
        String vendor,
        @jakarta.validation.constraints.NotNull Long categoryId
    ) {}

    public record NeedPatchRequest(
        String text,
        String vendor,
        Boolean active
    ) {}

    public record NeedResponse(
        Long id,
        String text,
        String vendor,
        boolean active,
        Long categoryId,
        String categoryName,
        Instant createdAt,
        Instant fulfilledAt
    ) {}

    public record ShareRequest(
        List<Long> needIds,
        String recipientPhone
    ) {}
    
    public record ShareResponse(
            String claimUrl
    ) {}
    
    public record SharePreviewResponse(
            String senderName,
            List<String> needTexts,
            boolean claimed,
            boolean expired
    ) {}
    
    public record ClaimResponse(
            int claimedCount,
            String categoryName
    ) {}
}
