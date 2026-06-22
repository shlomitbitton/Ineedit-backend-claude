package fun.ineedit.repository;

import fun.ineedit.entity.SharedNeedBatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SharedNeedBatchRepository extends JpaRepository<SharedNeedBatch, String> {
}