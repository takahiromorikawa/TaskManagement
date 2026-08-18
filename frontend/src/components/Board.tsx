import type { DragEvent } from "react";
import type { Card, Status } from "../types/card";
import Column from "./Column";

const STATUSES: Status[] = ["TODO", "DOING", "DONE"];

interface BoardProps {
  cards: Card[];
  onCardClick: (card: Card) => void;
  onCardDragStart: (event: DragEvent<HTMLElement>, card: Card) => void;
  onDropStatus: (cardId: number, status: Status) => void;
}

function Board({ cards, onCardClick, onCardDragStart, onDropStatus }: BoardProps) {
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
        />
      ))}
    </main>
  );
}

export default Board;
