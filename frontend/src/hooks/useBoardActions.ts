import { useContext } from "react";
import { BoardActionsContext, type BoardActions } from "../context/boardActionsContext";

export function useBoardActions(): BoardActions {
  const actions = useContext(BoardActionsContext);
  if (!actions) {
    throw new Error("useBoardActions must be used within a BoardActionsProvider");
  }
  return actions;
}
