import { useEffect, useState, type DragEvent } from "react";
import { createCard, getCards, updateCard, updateCardStatus } from "./api/cardApi";
import Board from "./components/Board";
import CardFormModal from "./components/CardFormModal";
import type { Card, CardCreateInput, Status } from "./types/card";
import "./App.css";

function App() {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingCard, setEditingCard] = useState<Card | null>(null);

  useEffect(() => {
    getCards()
      .then((data) => setCards(data))
      .catch((err: Error) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  async function handleCreateCard(input: CardCreateInput) {
    const created = await createCard(input);
    setCards((prev) => [...prev, created]);
    setIsCreateOpen(false);
  }

  async function handleUpdateCard(input: CardCreateInput) {
    if (!editingCard) return;
    const updated = await updateCard(editingCard.id, input);
    setCards((prev) => prev.map((card) => (card.id === updated.id ? updated : card)));
    setEditingCard(null);
  }

  function handleCardDragStart(event: DragEvent<HTMLElement>, card: Card) {
    event.dataTransfer.setData("text/plain", String(card.id));
    event.dataTransfer.effectAllowed = "move";
  }

  async function handleDropStatus(cardId: number, status: Status) {
    const target = cards.find((card) => card.id === cardId);
    if (!target || target.status === status) return;

    try {
      const updated = await updateCardStatus(cardId, status);
      setCards((prev) => prev.map((card) => (card.id === updated.id ? updated : card)));
    } catch (err) {
      alert(err instanceof Error ? err.message : "ステータスの変更に失敗しました");
    }
  }

  return (
    <div className="app">
      <header className="topbar">
        <h1>TaskManagement</h1>
        <button type="button" className="btn btn-primary" onClick={() => setIsCreateOpen(true)}>
          + 新規作成
        </button>
      </header>
      {loading && <p className="board-status">読み込み中...</p>}
      {!loading && error && <p className="board-status board-error">{error}</p>}
      {!loading && !error && (
        <Board
          cards={cards}
          onCardClick={setEditingCard}
          onCardDragStart={handleCardDragStart}
          onDropStatus={handleDropStatus}
        />
      )}
      {isCreateOpen && (
        <CardFormModal
          heading="新規カード作成"
          submitLabel="作成"
          submittingLabel="作成中..."
          onSubmit={handleCreateCard}
          onCancel={() => setIsCreateOpen(false)}
        />
      )}
      {editingCard && (
        <CardFormModal
          heading="カードを編集"
          submitLabel="保存"
          submittingLabel="保存中..."
          initialValues={{
            title: editingCard.title,
            priority: editingCard.priority,
            dueDate: editingCard.dueDate,
          }}
          onSubmit={handleUpdateCard}
          onCancel={() => setEditingCard(null)}
        />
      )}
    </div>
  );
}

export default App;
