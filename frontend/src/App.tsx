import { useEffect, useState, type DragEvent } from "react";
import { createCard, deleteCard, getCards, reorderCards, updateCard, updateCardStatus } from "./api/cardApi";
import Board from "./components/Board";
import CardFormModal from "./components/CardFormModal";
import DeleteConfirmDialog from "./components/DeleteConfirmDialog";
import type { Card, CardCreateInput, SortOrder, Status } from "./types/card";
import { SORT_ORDER_LABEL } from "./utils/labels";
import { sortCards } from "./utils/sort";
import "./App.css";

const SORT_ORDERS: SortOrder[] = ["ADDED", "DUE_DATE", "PRIORITY"];

function App() {
  const [cards, setCards] = useState<Card[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [editingCard, setEditingCard] = useState<Card | null>(null);
  const [deletingCard, setDeletingCard] = useState<Card | null>(null);
  const [sortOrder, setSortOrder] = useState<SortOrder>("ADDED");

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

  function computeReorderedCards(
    prev: Card[],
    draggedCardId: number,
    targetCardId: number,
    position: "before" | "after",
  ): Card[] {
    const fromIndex = prev.findIndex((card) => card.id === draggedCardId);
    if (fromIndex === -1) return prev;

    const next = [...prev];
    const [dragged] = next.splice(fromIndex, 1);
    const targetIndex = next.findIndex((card) => card.id === targetCardId);
    if (targetIndex === -1) return prev;
    const insertIndex = position === "after" ? targetIndex + 1 : targetIndex;
    next.splice(insertIndex, 0, dragged);
    return next;
  }

  async function handleDropOnCard(draggedCardId: number, targetCard: Card, position: "before" | "after") {
    const dragged = cards.find((card) => card.id === draggedCardId);
    if (!dragged || dragged.id === targetCard.id) return;

    const statusChanged = dragged.status !== targetCard.status;
    const cardsWithStatus = statusChanged
      ? cards.map((card) => (card.id === draggedCardId ? { ...card, status: targetCard.status } : card))
      : cards;
    const reordered = computeReorderedCards(cardsWithStatus, draggedCardId, targetCard.id, position);
    if (reordered === cardsWithStatus && !statusChanged) return;

    setCards(reordered);
    setSortOrder("ADDED");

    try {
      if (statusChanged) {
        await updateCardStatus(draggedCardId, targetCard.status);
      }
      const cardIdsInStatus = reordered
        .filter((card) => card.status === targetCard.status)
        .map((card) => card.id);
      await reorderCards(targetCard.status, cardIdsInStatus);
    } catch (err) {
      alert(err instanceof Error ? err.message : "並び替えの保存に失敗しました");
      getCards().then(setCards).catch(() => {});
    }
  }

  async function handleConfirmDelete() {
    if (!deletingCard) return;
    await deleteCard(deletingCard.id);
    setCards((prev) => prev.filter((card) => card.id !== deletingCard.id));
    setDeletingCard(null);
  }

  const displayCards = sortCards(cards, sortOrder);

  return (
    <div className="app">
      <header className="topbar">
        <h1>タスク管理</h1>
        <div className="topbar-actions">
          <label className="sort-label" htmlFor="sort-order">
            並び替え:
          </label>
          <select
            id="sort-order"
            className="sort-select"
            value={sortOrder}
            onChange={(e) => setSortOrder(e.target.value as SortOrder)}
          >
            {SORT_ORDERS.map((order) => (
              <option key={order} value={order}>
                {SORT_ORDER_LABEL[order]}
              </option>
            ))}
          </select>
          <button type="button" className="btn btn-primary" onClick={() => setIsCreateOpen(true)}>
            + 新規作成
          </button>
        </div>
      </header>
      {loading && <p className="board-status">読み込み中...</p>}
      {!loading && error && <p className="board-status board-error">{error}</p>}
      {!loading && !error && (
        <Board
          cards={displayCards}
          onCardClick={setEditingCard}
          onCardDragStart={handleCardDragStart}
          onDropStatus={handleDropStatus}
          onDropOnCard={handleDropOnCard}
          onDeleteClick={setDeletingCard}
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
      {deletingCard && (
        <DeleteConfirmDialog
          card={deletingCard}
          onConfirm={handleConfirmDelete}
          onCancel={() => setDeletingCard(null)}
        />
      )}
    </div>
  );
}

export default App;
