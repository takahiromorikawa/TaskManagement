import { useEffect, useState } from "react";
import { createCard, getCards } from "./api/cardApi";
import Board from "./components/Board";
import CardFormModal from "./components/CardFormModal";
import type { Card, CardCreateInput } from "./types/card";
import "./App.css";

function App() {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);

  useEffect(() => {
    getCards()
      .then((data) => setCards(data))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  async function handleCreateCard(input: CardCreateInput) {
    const created = await createCard(input);
    setCards((prev) => [...prev, created]);
    setIsFormOpen(false);
  }

  return (
    <div className="app">
      <header className="topbar">
        <h1>TaskManagement</h1>
        <button type="button" className="btn btn-primary" onClick={() => setIsFormOpen(true)}>
          + 新規作成
        </button>
      </header>
      {loading && <p className="board-status">読み込み中...</p>}
      {!loading && error && <p className="board-status board-error">{error}</p>}
      {!loading && !error && <Board cards={cards} />}
      {isFormOpen && (
        <CardFormModal onCreate={handleCreateCard} onCancel={() => setIsFormOpen(false)} />
      )}
    </div>
  );
}

export default App;
