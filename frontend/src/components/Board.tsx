import { useEffect, useState } from "react";
import { getCards } from "../api/cardApi";
import type { Card, Status } from "../types/card";
import Column from "./Column";

const STATUSES: Status[] = ["TODO", "DOING", "DONE"];

function Board() {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getCards()
      .then((data) => setCards(data))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <p className="board-status">読み込み中...</p>;
  }

  if (error) {
    return <p className="board-status board-error">{error}</p>;
  }

  return (
    <main className="board">
      {STATUSES.map((status) => (
        <Column
          key={status}
          status={status}
          cards={cards.filter((card) => card.status === status)}
        />
      ))}
    </main>
  );
}

export default Board;
