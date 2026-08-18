import type { Card, Priority, SortOrder } from "../types/card";

const PRIORITY_RANK: Record<Priority, number> = { HIGH: 0, MID: 1, LOW: 2 };

function compareByDueDate(a: Card, b: Card): number {
  if (a.dueDate === b.dueDate) return 0;
  if (a.dueDate === null) return 1;
  if (b.dueDate === null) return -1;
  return a.dueDate < b.dueDate ? -1 : 1;
}

function compareByPriority(a: Card, b: Card): number {
  return PRIORITY_RANK[a.priority] - PRIORITY_RANK[b.priority];
}

export function sortCards(cards: Card[], order: SortOrder): Card[] {
  if (order === "ADDED") return cards;
  const comparator = order === "DUE_DATE" ? compareByDueDate : compareByPriority;
  return [...cards].sort(comparator);
}
