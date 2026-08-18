import { useState, type DragEvent } from "react";
import type { Card, Status } from "../types/card";
import { STATUS_LABEL } from "../utils/labels";
import CardItem from "./CardItem";

interface ColumnProps {
  status: Status;
  cards: Card[];
  onCardClick: (card: Card) => void;
  onCardDragStart: (event: DragEvent<HTMLElement>, card: Card) => void;
  onDropStatus: (cardId: number, status: Status) => void;
}

function Column({ status, cards, onCardClick, onCardDragStart, onDropStatus }: ColumnProps) {
  const [isDragOver, setIsDragOver] = useState(false);

  function handleDragOver(event: DragEvent<HTMLDivElement>) {
    event.preventDefault();
    setIsDragOver(true);
  }

  function handleDrop(event: DragEvent<HTMLDivElement>) {
    event.preventDefault();
    setIsDragOver(false);
    const cardId = Number(event.dataTransfer.getData("text/plain"));
    if (Number.isFinite(cardId)) {
      onDropStatus(cardId, status);
    }
  }

  return (
    <section className="column">
      <div className="column-head">
        <span className="column-label">{STATUS_LABEL[status]}</span>
        <span className="column-count">{cards.length}</span>
      </div>
      <div
        className={`column-body${isDragOver ? " drag-over" : ""}`}
        onDragOver={handleDragOver}
        onDragLeave={() => setIsDragOver(false)}
        onDrop={handleDrop}
      >
        {cards.length === 0 ? (
          <p className="empty-hint">カードはありません</p>
        ) : (
          cards.map((card) => (
            <CardItem key={card.id} card={card} onClick={onCardClick} onDragStart={onCardDragStart} />
          ))
        )}
      </div>
    </section>
  );
}

export default Column;
