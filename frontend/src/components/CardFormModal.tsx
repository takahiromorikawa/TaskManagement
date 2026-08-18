import { useState, type SubmitEvent } from "react";
import { useModalA11y } from "../hooks/useModalA11y";
import type { CardCreateInput, Priority } from "../types/card";
import { PRIORITY_LABEL } from "../utils/labels";

interface CardFormModalProps {
  heading: string;
  submitLabel: string;
  submittingLabel: string;
  initialValues?: CardCreateInput;
  onSubmit: (input: CardCreateInput) => Promise<void>;
  onCancel: () => void;
}

const PRIORITIES: Priority[] = ["HIGH", "MID", "LOW"];

function CardFormModal({
  heading,
  submitLabel,
  submittingLabel,
  initialValues,
  onSubmit,
  onCancel,
}: CardFormModalProps) {
  const [title, setTitle] = useState(initialValues?.title ?? "");
  const [priority, setPriority] = useState<Priority>(initialValues?.priority ?? "MID");
  const [dueDate, setDueDate] = useState(initialValues?.dueDate ?? "");
  const [titleError, setTitleError] = useState<string | null>(null);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const containerRef = useModalA11y(onCancel);

  async function handleSubmit(event: SubmitEvent) {
    event.preventDefault();

    if (!title.trim()) {
      setTitleError("タイトルを入力してください");
      return;
    }
    setTitleError(null);
    setSubmitError(null);
    setSubmitting(true);

    try {
      await onSubmit({
        title: title.trim(),
        priority,
        dueDate: dueDate || null,
      });
    } catch (err) {
      setSubmitError(err instanceof Error ? err.message : "処理に失敗しました");
      setSubmitting(false);
    }
  }

  return (
    <div className="modal-overlay">
      <div className="modal" ref={containerRef} role="dialog" aria-modal="true" aria-labelledby="card-form-title">
        <div className="modal-header">
          <h2 id="card-form-title">{heading}</h2>
          <button type="button" className="modal-close" onClick={onCancel} aria-label="閉じる">
            ×
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="card-title">タイトル</label>
            <input
              id="card-title"
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              autoFocus
            />
            {titleError && <p className="field-error">{titleError}</p>}
          </div>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="card-priority">優先度</label>
              <select
                id="card-priority"
                value={priority}
                onChange={(e) => setPriority(e.target.value as Priority)}
              >
                {PRIORITIES.map((p) => (
                  <option key={p} value={p}>
                    {PRIORITY_LABEL[p]}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label htmlFor="card-due-date">期限（任意）</label>
              <input
                id="card-due-date"
                type="date"
                value={dueDate}
                onChange={(e) => setDueDate(e.target.value)}
              />
            </div>
          </div>
          {submitError && <p className="field-error">{submitError}</p>}
          <div className="modal-actions">
            <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={submitting}>
              キャンセル
            </button>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? submittingLabel : submitLabel}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CardFormModal;
