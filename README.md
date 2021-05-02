# 概要
# 構成
# 設定
# コンテキスト
## ApplicationContext
## ActionContext
# コントローラ
## ActionController
# ビュー
* http header
    * 	status			固定
　　　　* 	Content-Type	固定	
* http body
　　  * 	outputstream	リクエストごと
* message property	リクエストごと
* context path		初期化時
* model				リクエストごと
* template			初期化時

```
<xuml:xuml>
  <xuml:label key="title">
    <xuml:text lang="ja">タイトル</xuml:text>
    <xuml:text lang="en">Title</xuml:text>
  </xuml:label>

  <xuml:import name="layout.xuml" />

  <xuml:template name="layout.left">
    #{title} : ${model.title}
    <a href="@{/}">TOP</a>
    <xuml:switch value="${status}">
      <xuml:case eq="0">A</xuml:case>
      <xuml:case ne="1">B</xuml:case>
      <xuml:default>C</xuml:default>
    </xuml:switch>

    <ol>
    <xuml:each value="item : ${list}">
      <li>${item.name}</li>
    </xuml:each>
    <ol>
  </xuml:template>

  <xuml:apply name="layout.full" />
</xuml:xuml>
```

## XUML
### テンプレート名前空間
### 変数のスコープ
* each
* switch

# データベース
# メッセージ・ログ・例外
# プラグイン

# TODO

1. xuml
1. パスマッピング
1. include
1. ApplicationContextのnamespace対応
1. ActionContextをThreadLocalにする
1. Viewからpull
1. バージョン管理 ・ 一時保存
1. namespace
1. 認証
1. アクセス制御
1. Repository修正

