# システム概要

## 開発環境

| 項目         | 内容                         |
| ---------- | -------------------------- |
| フレームワーク    | Spring Boot 4              |
| テンプレートエンジン | Thymeleaf                  |
| DB         | Oracle Database            |
| ORM        | Spring Data JPA（Hibernate） |
| Java       | Java 17                    |

---

# 主な機能

## 会員機能

### 会員登録

* 新規会員登録
* メールアドレス重複チェック
* 入力値バリデーション

### ログイン・ログアウト

* メールアドレスとパスワードによる認証
* セッション管理

### 会員情報管理

* 会員情報参照
* 会員情報更新
* 退会

### 管理される情報

* 氏名
* メールアドレス
* パスワード
* 郵便番号
* 住所
* 電話番号

---

## 商品閲覧機能

### 商品一覧表示

商品情報

* 商品名
* 価格
* 商品説明
* 在庫数
* 商品画像
* カテゴリ

### カテゴリ分類

商品はカテゴリに所属します。

例：

* 文房具
* 書籍
* 玩具

---

## カート（買い物かご）機能

`BasketBean` を利用

### 機能

* 商品追加
* 数量変更
* 商品削除
* 合計金額計算

### 価格計算

価格計算は `PriceCalc` クラスで実装されています。

---

## 注文機能

### 注文登録

注文時に以下を入力します。

* 配送先郵便番号
* 配送先住所
* 配送先氏名
* 電話番号
* 支払方法

### 注文履歴表示

ユーザーは過去の注文を確認できます。

---

# 管理者機能

権限（authority）によって管理者を区別しています。

## 会員管理

管理者は以下を実行できます。

* 会員一覧
* 会員登録
* 会員編集
* 会員削除

### 対象コントローラ

* AdminUserShowController
* AdminUserRegistController
* AdminUserUpdateController
* AdminUserDeleteController

---

## 商品管理

管理者は以下を実行できます。

* 商品一覧
* 商品登録
* 商品更新
* 商品削除

### その他

* 商品画像アップロード対応

### 対象コントローラ

* AdminItemShowController
* AdminItemRegistController
* AdminItemUpdateController
* AdminItemDeleteController

---

## カテゴリ管理

管理者は以下を実行できます。

* カテゴリ登録
* カテゴリ更新
* カテゴリ削除

### 対象コントローラ

* AdminCategoryShowController
* AdminCategoryRegistController
* AdminCategoryUpdateController
* AdminCategoryDeleteController

---

## 注文管理

管理者は以下を実行できます。

* 全注文情報の閲覧

### 対象コントローラ

* AdminOrderShowController

---

# データベース構成

## users（会員）

| 項目          | 内容      |
| ----------- | ------- |
| id          | 会員ID    |
| email       | メールアドレス |
| password    | パスワード   |
| name        | 氏名      |
| postalCode  | 郵便番号    |
| address     | 住所      |
| phoneNumber | 電話番号    |
| authority   | 権限      |
| deleteFlag  | 削除フラグ   |

---

## categories（カテゴリ）

商品カテゴリ情報を管理します。

---

## items（商品）

| 項目          | 内容   |
| ----------- | ---- |
| id          | 商品ID |
| name        | 商品名  |
| price       | 価格   |
| description | 商品説明 |
| stock       | 在庫数  |
| image       | 商品画像 |
| category_id | カテゴリ |

---

## orders（注文）

| 項目        | 内容   |
| --------- | ---- |
| id        | 注文ID |
| user_id   | 購入者  |
| address   | 配送先  |
| payMethod | 支払方法 |

---

## order_items（注文明細）

注文と商品の中間テーブルです。

保持する情報

* 注文ID
* 商品ID
* 数量

---

# システム構成（MVC）

```text
Controller
    ↓
Form
    ↓
Entity
    ↓
Repository (JPA)
    ↓
Oracle DB
```

## 補助クラス

### BeanTools

Entity ⇔ Bean 変換

### PriceCalc

価格計算

### UploadFileService

画像アップロード

---

# このECサイトの特徴

* 会員制ショッピングサイト
* 管理者画面付き
* 商品画像アップロード対応
* カテゴリ管理対応
* カート機能実装済み
* 注文履歴管理対応
* Oracle DB + JPAによる永続化
* Spring Boot + Thymeleaf の標準的なMVC構成

---

# 開発時の禁止事項

* pom.xml の変更を生じる新たなライブラリの導入は禁止（フロントエンドのCDNは可）
* サービスレイヤは含まず、基礎的なMVCで構成する

  * Controllerから直接JPAリポジトリのメソッドを呼び出す
* コーディング規約は原則 Google Java Style に準拠する

  * クラス名：Upper Camel Case
  * メソッド名：lower camelCase

---

# Julesのルール

* セッション内のやり取りは日本語で記述する
* プルリクエストの内容は日本語で記述する
* 画面に変更がある場合はマルチモーダルで視覚的に示す

# Julesのルール

* セッション内のやり取り、プルリクエストの内容は日本語で記述して下さい
* 画面に変更がある場合はマルチモーダルで視覚的に示してください
---
