import type { Card } from "../types/card";
import { PRIORITY_LABEL, formatDueDate, isOverdue } from "../utils/labels";

interface CardItemProps {
  card: Card;
}

function CardItem({ card }: CardItemProps) {
  const overdue = card.dueDate ? isOverdue(card.dueDate, card.status) : false;

  return (
    <article className="card">
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
