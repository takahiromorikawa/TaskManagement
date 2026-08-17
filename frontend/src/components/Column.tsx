import type { Card, Status } from "../types/card";
import { STATUS_LABEL } from "../utils/labels";
import CardItem from "./CardItem";

interface ColumnProps {
  status: Status;
  cards: Card[];
}

function Column({ status, cards }: ColumnProps) {
  return (
    <section className="column">
      <div className="column-head">
        <span className="column-label">{STATUS_LABEL[status]}</span>
        <span className="column-count">{cards.length}</span>
      </div>
      <div className="column-body">
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
