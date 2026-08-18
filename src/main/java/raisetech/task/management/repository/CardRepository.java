package raisetech.task.management.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import raisetech.task.management.entity.Card;
import raisetech.task.management.entity.Status;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByOrderByPositionAsc();

    @Query("select coalesce(max(c.position), -1) from Card c")
    long findMaxPosition();

    @Query("select coalesce(max(c.position), -1) from Card c where c.status = :status")
    long findMaxPositionByStatus(@Param("status") Status status);
}
