システム概要

フレームワーク：Spring Boot 4
テンプレートエンジン：Thymeleaf
DB：Oracle Database
ORM：Spring Data JPA（Hibernate）
Java：Java 17


主な機能
1. 会員機能
会員登録

新規会員登録
メールアドレス重複チェック
入力値バリデーション
ログイン・ログアウト

メールアドレスとパスワードによる認証
セッション管理
会員情報管理

会員情報参照
会員情報更新
退会
管理される情報

氏名
メールアドレス
パスワード
郵便番号
住所
電話番号


2. 商品閲覧機能
商品一覧表示
商品情報

商品名
価格
商品説明
在庫数
商品画像
カテゴリ
カテゴリ分類
商品はカテゴリに所属
例：

文房具
書籍
玩具
など

3. カート（買い物かご）機能
BasketBean を利用
機能

商品追加
数量変更
商品削除
合計金額計算
価格計算は

PriceCalcクラスで実装されています。

4. 注文機能
注文登録
注文時に

配送先郵便番号
配送先住所
配送先氏名
電話番号
支払方法
を入力
注文履歴表示
ユーザーは過去の注文を確認できます。

管理者機能
権限（authority）によって管理者を区別しています。

1. 会員管理
管理者は

会員一覧
会員登録
会員編集
会員削除
を実行可能
対象コントローラ
AdminUserShowController
AdminUserRegistController
AdminUserUpdateController
AdminUserDeleteController

2. 商品管理
管理者は

商品一覧
商品登録
商品更新
商品削除
を実行可能
さらに

商品画像アップロード
にも対応
対象コントローラ
AdminItemShowController
AdminItemRegistController
AdminItemUpdateController
AdminItemDeleteController

3. カテゴリ管理
管理者は

カテゴリ登録
カテゴリ更新
カテゴリ削除
を実行可能
対象コントローラ
AdminCategoryShowController
AdminCategoryRegistController
AdminCategoryUpdateController
AdminCategoryDeleteController

4. 注文管理
管理者は

全注文情報の閲覧
が可能
対象
AdminOrderShowController

データベース構成
エンティティは5種類です。
users（会員）
項目内容id会員IDemailメールpasswordパスワードname氏名postalCode郵便番号address住所phoneNumber電話番号authority権限deleteFlag削除フラグ

categories（カテゴリ）
商品カテゴリ情報

items（商品）
項目内容id商品IDname商品名price価格description説明stock在庫image画像category_idカテゴリ

orders（注文）
項目内容id注文IDuser_id購入者address配送先payMethod支払方法

order_items（注文明細）
注文と商品の中間テーブル

注文ID
商品ID
数量
を保持

システム構成（MVC）
Controller
   ↓
Form
   ↓
Entity
   ↓
Repository(JPA)
   ↓
Oracle DB補助クラス
BeanTools       ：Entity⇔Bean変換
PriceCalc       ：価格計算
UploadFileService：画像アップロード

このECサイトの特徴

会員制ショッピングサイト
管理者画面付き
商品画像アップロード対応
カテゴリ管理対応
カート機能実装済み
注文履歴管理対応
Oracle DB + JPAによる永続化
Spring Boot + Thymeleaf の標準的なMVC構成


---
# 開発時の禁止事項

* pom.xmlの変更を生じる新たなライブラリの導入は禁止（フロントエンドのCDNはOK）
* サービスレイヤは含まず、基礎的なＭＶＣで構成する（Controllerから直接JPAリポジトリのメソッドを呼んでいる）
* コーディング規約はクラスはUpperキャメル、メソッドはlowerキャメルなど原則、Google Java Styleに準拠する
---
# Julesのルール

* セッション内のやり取り、プルリクエストの内容は日本語で記述して下さい
* 画面に変更がある場合はマルチモーダルで視覚的に示してください
---
