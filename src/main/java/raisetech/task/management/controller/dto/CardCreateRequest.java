package raisetech.task.management.controller.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import raisetech.task.management.entity.Priority;

public record CardCreateRequest(
        @NotBlank
        String title,
        Priority priority,
        LocalDate dueDate
) {
}
