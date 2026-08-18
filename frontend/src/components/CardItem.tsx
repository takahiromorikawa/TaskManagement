import { useState, type DragEvent, type MouseEvent } from "react";
import { useBoardActions } from "../hooks/useBoardActions";
import type { Card } from "../types/card";
import { PRIORITY_LABEL, formatDueDate, isOverdue } from "../utils/labels";

type DropPosition = "before" | "after";

interface CardItemProps {
  card: Card;
}

function CardItem({ card }: CardItemProps) {
  const { onCardClick, onCardDragStart, onDropOnCard, onDeleteClick } = useBoardActions();
  const [dropPosition, setDropPosition] = useState<DropPosition | null>(null);
  const overdue = card.dueDate ? isOverdue(card.dueDate, card.status) : false;

  function handleDragOver(event: DragEvent<HTMLElement>) {
    event.preventDefault();
    event.stopPropagation();
    const rect = event.currentTarget.getBoundingClientRect();
    const isTopHalf = event.clientY < rect.top + rect.height / 2;
    setDropPosition(isTopHalf ? "before" : "after");
  }

  function handleDrop(event: DragEvent<HTMLElement>) {
    event.preventDefault();
    event.stopPropagation();
    const position = dropPosition ?? "before";
    setDropPosition(null);
    const draggedCardId = Number(event.dataTransfer.getData("text/plain"));
    if (Number.isFinite(draggedCardId) && draggedCardId !== card.id) {
      onDropOnCard(draggedCardId, card, position);
    }
  }

  function handleDeleteClick(event: MouseEvent<HTMLButtonElement>) {
    event.stopPropagation();
    onDeleteClick(card);
  }

  const dropClass =
    dropPosition === "before" ? " card-drop-before" : dropPosition === "after" ? " card-drop-after" : "";

  return (
    <article
      className={`card${dropClass}`}
      draggable
      onDragStart={(e) => onCardDragStart(e, card)}
      onDragOver={handleDragOver}
      onDragLeave={() => setDropPosition(null)}
      onDrop={handleDrop}
      onClick={() => onCardClick(card)}
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
        <button type="button" className="card-delete-btn" onClick={handleDeleteClick}>
          削除
        </button>
      </div>
    </article>
  );
}

export default CardItem;
