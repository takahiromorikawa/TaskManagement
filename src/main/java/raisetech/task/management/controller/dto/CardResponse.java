package raisetech.task.management.controller.dto;

import java.time.LocalDate;
import raisetech.task.management.entity.Card;
import raisetech.task.management.entity.Priority;
import raisetech.task.management.entity.Status;

public record CardResponse(
        Long id,
        String title,
        Status status,
        Priority priority,
        LocalDate dueDate
) {

    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getTitle(),
                card.getStatus(),
                card.getPriority(),
                card.getDueDate()
        );
    }
}
