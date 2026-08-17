[« 要件定義書に戻る](requirements.md)

# API仕様：TaskManagement

## API一覧

| メソッド | URL | 内容 |
|---|---|---|
| GET | /cards | カード一覧取得（id昇順＝作成順で返却） |
| GET | /cards/{id} | カード詳細取得 |
| POST | /cards | カード新規作成（title・priority・dueDateを指定） |
| PUT | /cards/{id}/status | ステータス変更（リクエストボディで指定した任意のステータスに変更） |
| DELETE | /cards/{id} | カード削除 |

データ項目（Cardのフィールド）は[データベース設計](database.md)を参照。

「期限順」「優先度順」の並び替え、および同一列内の手動並び替えは、いずれもフロントエンド側で
`GET /cards` の結果を並べ替えるのみで実現する。並び替え専用のAPI・クエリパラメータは設けない。

## シーケンス図

各ユースケースにおける、画面（React）とバックエンド（Controller / Service / Repository / DB）間のやり取りを示す。
各ユースケースの詳細は[機能要件](features.md)を参照。

### UC1: カード作成

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1/S2)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as PostgreSQL DB

    User->>FE: タイトル・優先度・期限を入力して「作成」
    FE->>API: POST /cards { title, priority, dueDate }
    API->>SVC: createCard(title, priority, dueDate)
    SVC->>REPO: save(Card)
    REPO->>DB: INSERT
    DB-->>REPO: 採番されたid
    REPO-->>SVC: Card
    SVC-->>API: Card
    API-->>FE: 201 Created + Card
    FE-->>User: 「未着手」列にカードを表示
```

### UC2: カード一覧取得

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as PostgreSQL DB

    User->>FE: カンバンボード画面を開く
    FE->>API: GET /cards
    API->>SVC: getAllCards()
    SVC->>REPO: findAll()
    REPO->>DB: SELECT
    DB-->>REPO: カード一覧
    REPO-->>SVC: List<Card>
    SVC-->>API: List<Card>
    API-->>FE: 200 OK + カード一覧
    FE-->>User: ステータスごとに3列へ振り分けて表示
```

### UC3: ステータス変更（ドラッグ&ドロップ）

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as PostgreSQL DB

    User->>FE: カードを別の列にドラッグ&ドロップ
    FE->>API: PUT /cards/{id}/status { status }
    API->>SVC: updateStatus(id, status)
    SVC->>REPO: findById(id)
    REPO->>DB: SELECT
    DB-->>REPO: Card
    REPO-->>SVC: Card
    SVC->>SVC: statusをリクエスト値で上書き
    SVC->>REPO: save(Card)
    REPO->>DB: UPDATE
    REPO-->>SVC: Card
    SVC-->>API: Card
    API-->>FE: 200 OK + Card
    FE-->>User: 該当する列へ移動して表示
```

移動先が前の列（例: 完了→進行中）であっても同じAPIで対応する。ステータスの前後関係による制約はない。

### UC4: カード削除

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1/S3)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as PostgreSQL DB

    User->>FE: 「削除」→確認ダイアログで「削除」
    FE->>API: DELETE /cards/{id}
    API->>SVC: deleteCard(id)
    SVC->>REPO: deleteById(id)
    REPO->>DB: DELETE
    DB-->>REPO: 完了
    REPO-->>SVC: 完了
    SVC-->>API: 完了
    API-->>FE: 204 No Content
    FE-->>User: 一覧からカードを削除
```

### UC5: 並び替え（クライアント側のみ）

並び替え（追加順／期限順／優先度順への切り替え、同一列内の手動ドラッグ並び替え）はAPI通信を伴わない。
`GET /cards`（UC2）で取得済みのカード一覧を、フロントエンドの状態として保持したまま並べ替えるのみで完結する。
画面を再読み込みすると、並び替え条件は初期値（追加順）に戻る。
