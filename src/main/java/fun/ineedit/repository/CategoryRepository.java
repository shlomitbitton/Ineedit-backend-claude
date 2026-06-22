package fun.ineedit.repository;

import fun.ineedit.entity.Category;
import fun.ineedit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserOrderByCreatedAtAsc(User user);
    Optional<Category> findByIdAndUser(Long id, User user);
    boolean existsByNameAndUser(String name, User user);

    Optional<Category> findByUserAndName(User user, String name);
}
