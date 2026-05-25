package fun.ineedit.service;

import fun.ineedit.dto.Dto;
import fun.ineedit.entity.Category;
import fun.ineedit.entity.Need;
import fun.ineedit.entity.User;
import fun.ineedit.repository.CategoryRepository;
import fun.ineedit.repository.NeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NeedService {

    private final NeedRepository needRepository;
    private final CategoryRepository categoryRepository;

    public List<Dto.NeedResponse> getByCategory(Long categoryId, User user) {
        Category cat = categoryRepository.findByIdAndUser(categoryId, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        return needRepository.findByCategoryAndUserOrderByCreatedAtDesc(cat, user)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dto.NeedResponse create(Dto.NeedRequest req, User user) {
        Category cat = categoryRepository.findByIdAndUser(req.categoryId(), user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        Need need = Need.builder()
                .text(req.text())
                .vendor(req.vendor())
                .active(true)
                .user(user)
                .category(cat)
                .build();
        return toResponse(needRepository.save(need));
    }

    @Transactional
    public Dto.NeedResponse patch(Long id, Dto.NeedPatchRequest req, User user) {
        Need need = needRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Need not found"));

        if (req.text()   != null) need.setText(req.text());
        if (req.vendor() != null) need.setVendor(req.vendor());
        if (req.active() != null) {
            need.setActive(req.active());
            need.setFulfilledAt(req.active() ? null : Instant.now());
        }
        return toResponse(needRepository.save(need));
    }

    @Transactional
    public void delete(Long id, User user) {
        Need need = needRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Need not found"));
        needRepository.delete(need);
    }

    private Dto.NeedResponse toResponse(Need n) {
        return new Dto.NeedResponse(
                n.getId(), n.getText(), n.getVendor(), n.isActive(),
                n.getCategory().getId(), n.getCategory().getName(),
                n.getCreatedAt(), n.getFulfilledAt());
    }
}
