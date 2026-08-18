import type { Priority, SortOrder, Status } from "../types/card";

export const STATUS_LABEL: Record<Status, string> = {
  TODO: "未着手",
  DOING: "進行中",
  DONE: "完了",
};

export const PRIORITY_LABEL: Record<Priority, string> = {
  HIGH: "高",
  MID: "中",
  LOW: "低",
};

export const SORT_ORDER_LABEL: Record<SortOrder, string> = {
  ADDED: "追加順",
  DUE_DATE: "期限順",
  PRIORITY: "優先度順",
};

export function formatDueDate(dueDate: string): string {
  const [, month, day] = dueDate.split("-");
  return `${Number(month)}/${Number(day)}`;
}

export function isOverdue(dueDate: string, status: Status): boolean {
  const today = new Date().toISOString().slice(0, 10);
  return dueDate < today && status !== "DONE";
}
