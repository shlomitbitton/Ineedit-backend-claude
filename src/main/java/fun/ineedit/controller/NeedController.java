package fun.ineedit.controller;

import fun.ineedit.dto.Dto;
import fun.ineedit.entity.User;
import fun.ineedit.service.NeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/needs")
@RequiredArgsConstructor
public class NeedController {

    private final NeedService needService;

    @GetMapping
    public List<Dto.NeedResponse> getByCategory(
            @RequestParam Long categoryId,
            @AuthenticationPrincipal User user) {
        return needService.getByCategory(categoryId, user);
    }

    @PostMapping
    public ResponseEntity<Dto.NeedResponse> create(
            @Valid @RequestBody Dto.NeedRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(needService.create(req, user));
    }

    @PatchMapping("/{id}")
    public Dto.NeedResponse patch(
            @PathVariable Long id,
            @RequestBody Dto.NeedPatchRequest req,
            @AuthenticationPrincipal User user) {
        return needService.patch(id, req, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        needService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
