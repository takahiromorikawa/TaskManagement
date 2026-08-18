import { createContext, type DragEvent } from "react";
import type { Card, Status } from "../types/card";

export interface BoardActions {
  onCardClick: (card: Card) => void;
  onCardDragStart: (event: DragEvent<HTMLElement>, card: Card) => void;
  onDropStatus: (cardId: number, status: Status) => void;
  onDropOnCard: (draggedCardId: number, targetCard: Card, position: "before" | "after") => void;
  onDeleteClick: (card: Card) => void;
}

export const BoardActionsContext = createContext<BoardActions | null>(null);
