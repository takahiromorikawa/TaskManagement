import type { DragEvent } from "react";
import type { Card, Status } from "../types/card";
import Column from "./Column";

const STATUSES: Status[] = ["TODO", "DOING", "DONE"];

interface BoardProps {
  cards: Card[];
  onCardClick: (card: Card) => void;
  onCardDragStart: (event: DragEvent<HTMLElement>, card: Card) => void;
  onDropStatus: (cardId: number, status: Status) => void;
  onDropOnCard: (draggedCardId: number, targetCard: Card) => void;
}

function Board({ cards, onCardClick, onCardDragStart, onDropStatus, onDropOnCard }: BoardProps) {
  return (
    <main className="board">
      {STATUSES.map((status) => (
        <Column
          key={status}
          status={status}
          cards={cards.filter((card) => card.status === status)}
          onCardClick={onCardClick}
          onCardDragStart={onCardDragStart}
          onDropStatus={onDropStatus}
          onDropOnCard={onDropOnCard}
        />
      ))}
    </main>
  );
}

export default Board;
