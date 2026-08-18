[« 要件定義書に戻る](requirements.md)

# API仕様：TaskManagement

## API一覧

| メソッド | URL | 内容 |
|---|---|---|
| GET | /cards | カード一覧取得（position昇順＝表示順で返却） |
| GET | /cards/{id} | カード詳細取得 |
| POST | /cards | カード新規作成（title・priority・dueDateを指定） |
| PUT | /cards/{id} | カード編集（title・priority・dueDateを更新。statusは変更しない） |
| PUT | /cards/{id}/status | ステータス変更（リクエストボディで指定した任意のステータスに変更） |
| PUT | /cards/reorder | 同一列内の並び替え（status・並び替え後の全cardIdsを指定してpositionを振り直す） |
| DELETE | /cards/{id} | カード削除 |

データ項目（Cardのフィールド）は[データベース設計](database.md)を参照。

「期限順」「優先度順」への切り替えはフロントエンド側で `GET /cards` の結果を並べ替えるのみで実現し、
API通信・永続化は行わない。同一列内の手動ドラッグ並び替えは `PUT /cards/reorder` でサーバーに保存する。

バリデーションエラー・不正なリクエストボディの場合は、`GlobalExceptionHandler`により
`{"message": "..."}` 形式のレスポンスボディで統一して400を返す。

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
    API->>SVC: createCard(request)
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
    SVC->>REPO: findAllByOrderByPositionAsc()
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

    User->>FE: カードを別の列の空き領域にドラッグ&ドロップ
    FE->>API: PUT /cards/{id}/status { status }
    API->>SVC: updateStatus(id, status)
    SVC->>REPO: findById(id)
    REPO->>DB: SELECT
    DB-->>REPO: Card
    REPO-->>SVC: Card
    alt ステータスが変わる場合
        SVC->>REPO: findMaxPositionByStatus(status)
        REPO->>DB: SELECT MAX(position)
        DB-->>REPO: 移動先列の最大position
        REPO-->>SVC: 最大position
        SVC->>SVC: 移動先列の末尾になるようpositionを採番し直す
    end
    SVC->>SVC: statusをリクエスト値で上書き
    SVC->>REPO: save(Card)
    REPO->>DB: UPDATE
    REPO-->>SVC: Card
    SVC-->>API: Card
    API-->>FE: 200 OK + Card
    FE-->>User: 該当する列の末尾へ移動して表示
```

移動先が前の列（例: 完了→進行中）であっても同じAPIで対応する。ステータスの前後関係による制約はない。
別の列の特定のカードの上にドロップした場合は、このAPIに続けてUC5（並び替え）の`PUT /cards/reorder`が呼ばれ、
挿入位置（直前/直後）が反映される。

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

### UC5: 並び替え

「期限順」「優先度順」への切り替えはAPI通信を伴わず、`GET /cards`（UC2）で取得済みのカード一覧を
フロントエンドの状態として保持したまま並べ替えるのみで完結する。画面を再読み込みすると、並び替え条件は初期値（追加順）に戻る。

別カードの上への手動ドラッグ並び替えは、以下のシーケンスでサーバーに保存する。
別の列のカードにドロップした場合は、事前にUC3の`PUT /cards/{id}/status`でステータス変更が行われた上でこのAPIが呼ばれる。

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as PostgreSQL DB

    User->>FE: カードを別カードの上（同じ列／別の列）にドラッグ&ドロップ
    FE->>FE: ドロップ先の上半分なら直前・下半分なら直後にカードを挿入して画面を即時更新
    FE->>API: PUT /cards/reorder { status, cardIds }
    API->>SVC: reorderCards(status, cardIds)
    SVC->>REPO: findAllById(cardIds)
    REPO->>DB: SELECT
    DB-->>REPO: カード一覧
    REPO-->>SVC: List<Card>
    SVC->>SVC: cardIdsの順に0始まりのpositionを採番
    SVC->>REPO: saveAll(cards)
    REPO->>DB: UPDATE（一括）
    REPO-->>SVC: List<Card>
    SVC-->>API: List<Card>
    API-->>FE: 200 OK + カード一覧
```

`cardIds`に含まれるカードが指定した`status`と一致しない場合、またはIDが1件でも存在しない場合は400を返す。

### UC6: カード編集

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1/S4)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as PostgreSQL DB

    User->>FE: カードをクリックしてS4（カード編集フォーム）を開く
    User->>FE: タイトル・優先度・期限を変更して「保存」
    FE->>API: PUT /cards/{id} { title, priority, dueDate }
    API->>SVC: updateCard(id, request)
    SVC->>REPO: findById(id)
    REPO->>DB: SELECT
    DB-->>REPO: Card
    REPO-->>SVC: Card
    SVC->>SVC: title・priority・dueDateを上書き（statusは変更しない）
    SVC->>REPO: save(Card)
    REPO->>DB: UPDATE
    REPO-->>SVC: Card
    SVC-->>API: Card
    API-->>FE: 200 OK + Card
    FE-->>User: ポップアップを閉じ、更新後の内容を表示
```

対象カードが存在しない場合は404を返す。
