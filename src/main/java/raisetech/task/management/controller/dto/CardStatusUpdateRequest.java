package raisetech.task.management.controller.dto;

import jakarta.validation.constraints.NotNull;
import raisetech.task.management.entity.Status;

public record CardStatusUpdateRequest(
        @NotNull
        Status status
) {
}
