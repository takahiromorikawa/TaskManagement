import { useState } from "react";
import { useModalA11y } from "../hooks/useModalA11y";
import type { Card } from "../types/card";

interface DeleteConfirmDialogProps {
  card: Card;
  onConfirm: () => Promise<void>;
  onCancel: () => void;
}

function DeleteConfirmDialog({ card, onConfirm, onCancel }: DeleteConfirmDialogProps) {
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const containerRef = useModalA11y(onCancel);

  async function handleConfirm() {
    setError(null);
    setDeleting(true);
    try {
      await onConfirm();
    } catch (err) {
      setError(err instanceof Error ? err.message : "削除に失敗しました");
      setDeleting(false);
    }
  }

  return (
    <div className="modal-overlay">
      <div
        className="modal modal-small"
        ref={containerRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="delete-confirm-title"
      >
        <div className="modal-header">
          <h2 id="delete-confirm-title">確認</h2>
        </div>
        <p>「{card.title}」を削除しますか？</p>
        {error && <p className="field-error">{error}</p>}
        <div className="modal-actions">
          <button type="button" className="btn btn-secondary" onClick={onCancel} disabled={deleting} autoFocus>
            キャンセル
          </button>
          <button type="button" className="btn btn-danger" onClick={handleConfirm} disabled={deleting}>
            {deleting ? "削除中..." : "削除"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default DeleteConfirmDialog;
