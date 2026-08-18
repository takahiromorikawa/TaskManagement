package raisetech.task.management.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import raisetech.task.management.entity.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByOrderByPositionAsc();

    @Query("select coalesce(max(c.position), -1) from Card c")
    long findMaxPosition();
}
