import { useState, type DragEvent } from "react";
import type { Card } from "../types/card";
import { PRIORITY_LABEL, formatDueDate, isOverdue } from "../utils/labels";

interface CardItemProps {
  card: Card;
  onClick: (card: Card) => void;
  onDragStart: (event: DragEvent<HTMLElement>, card: Card) => void;
  onDropOnCard: (draggedCardId: number, targetCard: Card) => void;
}

function CardItem({ card, onClick, onDragStart, onDropOnCard }: CardItemProps) {
  const [isDragOver, setIsDragOver] = useState(false);
  const overdue = card.dueDate ? isOverdue(card.dueDate, card.status) : false;

  function handleDragOver(event: DragEvent<HTMLElement>) {
    event.preventDefault();
    event.stopPropagation();
    setIsDragOver(true);
  }

  function handleDrop(event: DragEvent<HTMLElement>) {
    event.preventDefault();
    event.stopPropagation();
    setIsDragOver(false);
    const draggedCardId = Number(event.dataTransfer.getData("text/plain"));
    if (Number.isFinite(draggedCardId) && draggedCardId !== card.id) {
      onDropOnCard(draggedCardId, card);
    }
  }

  return (
    <article
      className={`card${isDragOver ? " card-drag-over" : ""}`}
      draggable
      onDragStart={(e) => onDragStart(e, card)}
      onDragOver={handleDragOver}
      onDragLeave={() => setIsDragOver(false)}
      onDrop={handleDrop}
      onClick={() => onClick(card)}
    >
      <p className="card-title">{card.title}</p>
      <div className="card-meta">
        <span className={`priority-pill priority-${card.priority.toLowerCase()}`}>
          優先度: {PRIORITY_LABEL[card.priority]}
        </span>
        {card.dueDate && (
          <span className={`due-date${overdue ? " overdue" : ""}`}>
            {overdue ? "期限超過 " : "期限 "}
            {formatDueDate(card.dueDate)}
          </span>
        )}
      </div>
    </article>
  );
}

export default CardItem;
