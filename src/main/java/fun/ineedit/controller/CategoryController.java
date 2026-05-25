package fun.ineedit.controller;

import fun.ineedit.dto.Dto;
import fun.ineedit.entity.User;
import fun.ineedit.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Dto.CategoryResponse> getAll(@AuthenticationPrincipal User user) {
        return categoryService.getAll(user);
    }

    @PostMapping
    public ResponseEntity<Dto.CategoryResponse> create(
            @Valid @RequestBody Dto.CategoryRequest req,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(req, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user) {
        categoryService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
