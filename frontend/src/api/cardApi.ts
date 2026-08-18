import type { Card, CardCreateInput } from "../types/card";

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
