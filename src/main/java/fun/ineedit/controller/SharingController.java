package fun.ineedit.controller;

import fun.ineedit.dto.Dto.*;
import fun.ineedit.entity.User;
import fun.ineedit.service.SharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SharingController {

    private final SharingService sharingService;

    // Authenticated — sender shares a set of needs
    @PostMapping("/api/needs/share")
    public ResponseEntity<ShareResponse> share(
            @AuthenticationPrincipal User sender,
            @RequestBody ShareRequest request) {
        return ResponseEntity.ok(sharingService.createShare(sender, request));
    }

    // Public — no auth required, used by the landing page before login
    @GetMapping("/api/share/{id}")
    public ResponseEntity<SharePreviewResponse> preview(@PathVariable String id) {
        return ResponseEntity.ok(sharingService.getPreview(id));
    }

    // Authenticated — recipient claims the shared needs into their account
    @PostMapping("/api/share/{id}/claim")
    public ResponseEntity<ClaimResponse> claim(
            @PathVariable String id,
            @AuthenticationPrincipal User recipient) {
        return ResponseEntity.ok(sharingService.claim(id, recipient));
    }
}