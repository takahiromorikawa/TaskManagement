package raisetech.task.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import raisetech.task.management.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {
}
