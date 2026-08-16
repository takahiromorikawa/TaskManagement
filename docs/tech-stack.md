[« 要件定義書に戻る](requirements.md)

# 技術スタック：TaskManagement

- バックエンド：Java / Spring Boot / Spring Data JPA
- DB：H2（インメモリ）
- フロントエンド：React（fetch/axiosでREST APIを呼び出し）
- フロントエンド・バックエンド間：別オリジン構成のためCORS設定が必要
- 確認方法：React画面から動作確認。API単体の確認にはPostman等も併用可能
