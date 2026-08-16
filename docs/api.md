[« 要件定義書に戻る](requirements.md)

# API仕様：TaskManagement

## API一覧

| メソッド | URL | 内容 |
|---|---|---|
| GET | /cards | カード一覧取得 |
| GET | /cards/{id} | カード詳細取得 |
| POST | /cards | カード新規作成 |
| PUT | /cards/{id}/status | ステータス変更 |
| DELETE | /cards/{id} | カード削除 |

データ項目（Cardのフィールド）は[データベース設計](database.md)を参照。

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
    participant DB as H2 DB

    User->>FE: タイトルを入力して「作成」
    FE->>API: POST /cards { title }
    API->>SVC: createCard(title)
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
    participant DB as H2 DB

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

### UC3: ステータス変更

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as H2 DB

    User->>FE: カードの「→」をクリック
    FE->>API: PUT /cards/{id}/status
    API->>SVC: updateStatus(id)
    SVC->>REPO: findById(id)
    REPO->>DB: SELECT
    DB-->>REPO: Card
    REPO-->>SVC: Card
    SVC->>SVC: 次のステータスを算出
    SVC->>REPO: save(Card)
    REPO->>DB: UPDATE
    REPO-->>SVC: Card
    SVC-->>API: Card
    API-->>FE: 200 OK + Card
    FE-->>User: 該当する列へ移動して表示
```

### UC4: カード削除

```mermaid
sequenceDiagram
    actor User as 利用者
    participant FE as React (S1/S3)
    participant API as CardController
    participant SVC as CardService
    participant REPO as CardRepository
    participant DB as H2 DB

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
