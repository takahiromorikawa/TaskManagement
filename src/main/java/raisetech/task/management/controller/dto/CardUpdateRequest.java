package raisetech.task.management.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import raisetech.task.management.entity.Priority;

public record CardUpdateRequest(
        @NotBlank
        String title,
        @NotNull
        Priority priority,
        LocalDate dueDate
) {
}
