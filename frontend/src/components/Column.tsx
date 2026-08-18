import { useState, type DragEvent } from "react";
import { useBoardActions } from "../hooks/useBoardActions";
import type { Card, Status } from "../types/card";
import { STATUS_LABEL } from "../utils/labels";
import CardItem from "./CardItem";

interface ColumnProps {
  status: Status;
  cards: Card[];
}

function Column({ status, cards }: ColumnProps) {
  const { onDropStatus } = useBoardActions();
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
          cards.map((card) => <CardItem key={card.id} card={card} />)
        )}
      </div>
    </section>
  );
}

export default Column;
