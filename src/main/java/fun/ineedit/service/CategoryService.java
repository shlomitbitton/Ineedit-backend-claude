package fun.ineedit.service;

import fun.ineedit.dto.Dto;
import fun.ineedit.entity.Category;
import fun.ineedit.entity.User;
import fun.ineedit.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Dto.CategoryResponse> getAll(User user) {
        return categoryRepository.findByUserOrderByCreatedAtAsc(user)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public Dto.CategoryResponse create(Dto.CategoryRequest req, User user) {
        if (categoryRepository.existsByNameAndUser(req.name(), user)) {
            throw new IllegalArgumentException("Category already exists");
        }
        Category cat = categoryRepository.save(
                Category.builder().name(req.name()).user(user).build());
        return toResponse(cat);
    }

    @Transactional
    public void delete(Long id, User user) {
        Category cat = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        categoryRepository.delete(cat);
    }

    public Dto.CategoryResponse toResponse(Category cat) {
        return new Dto.CategoryResponse(cat.getId(), cat.getName(), cat.getCreatedAt());
    }
}
