package raisetech.task.management.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import raisetech.task.management.entity.Status;

public record CardReorderRequest(
        @NotNull
        Status status,
        @NotEmpty
        List<Long> cardIds
) {
}
