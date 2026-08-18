import type { Card, CardCreateInput, Status } from "../types/card";

const API_BASE_URL = "http://localhost:8080";

export async function getCards(): Promise<Card[]> {
  const response = await fetch(`${API_BASE_URL}/cards`);
  if (!response.ok) {
    throw new Error(`カード一覧の取得に失敗しました (status: ${response.status})`);
  }
  return response.json();
}

export async function createCard(input: CardCreateInput): Promise<Card> {
  const response = await fetch(`${API_BASE_URL}/cards`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  if (!response.ok) {
    throw new Error(`カードの作成に失敗しました (status: ${response.status})`);
  }
  return response.json();
}

export async function updateCard(id: number, input: CardCreateInput): Promise<Card> {
  const response = await fetch(`${API_BASE_URL}/cards/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  if (!response.ok) {
    throw new Error(`カードの更新に失敗しました (status: ${response.status})`);
  }
  return response.json();
}

export async function updateCardStatus(id: number, status: Status): Promise<Card> {
  const response = await fetch(`${API_BASE_URL}/cards/${id}/status`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status }),
  });
  if (!response.ok) {
    throw new Error(`ステータスの変更に失敗しました (status: ${response.status})`);
  }
  return response.json();
}

export async function deleteCard(id: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/cards/${id}`, {
    method: "DELETE",
  });
  if (!response.ok) {
    throw new Error(`カードの削除に失敗しました (status: ${response.status})`);
  }
}

export async function reorderCards(status: Status, cardIds: number[]): Promise<Card[]> {
  const response = await fetch(`${API_BASE_URL}/cards/reorder`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status, cardIds }),
  });
  if (!response.ok) {
    throw new Error(`並び替えの保存に失敗しました (status: ${response.status})`);
  }
  return response.json();
}
