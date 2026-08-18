import type { Card, Status } from "../types/card";
import Column from "./Column";

const STATUSES: Status[] = ["TODO", "DOING", "DONE"];

interface BoardProps {
  cards: Card[];
}

function Board({ cards }: BoardProps) {
  return (
    <main className="board">
      {STATUSES.map((status) => (
        <Column key={status} status={status} cards={cards.filter((card) => card.status === status)} />
      ))}
    </main>
  );
}

export default Board;
