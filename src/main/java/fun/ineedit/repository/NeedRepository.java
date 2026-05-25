package fun.ineedit.repository;

import fun.ineedit.entity.Category;
import fun.ineedit.entity.Need;
import fun.ineedit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NeedRepository extends JpaRepository<Need, Long> {
    List<Need> findByCategoryAndUserOrderByCreatedAtDesc(Category category, User user);
    Optional<Need> findByIdAndUser(Long id, User user);
    void deleteAllByCategoryAndUser(Category category, User user);
}
