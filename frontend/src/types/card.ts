export type Status = "TODO" | "DOING" | "DONE";
export type Priority = "HIGH" | "MID" | "LOW";

export interface Card {
  id: number;
  title: string;
  status: Status;
  priority: Priority;
  dueDate: string | null;
}

export interface CardCreateInput {
  title: string;
  priority: Priority;
  dueDate: string | null;
}
