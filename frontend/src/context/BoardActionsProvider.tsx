import type { ReactNode } from "react";
import { BoardActionsContext, type BoardActions } from "./boardActionsContext";

interface BoardActionsProviderProps {
  value: BoardActions;
  children: ReactNode;
}

export function BoardActionsProvider({ value, children }: BoardActionsProviderProps) {
  return <BoardActionsContext.Provider value={value}>{children}</BoardActionsContext.Provider>;
}
