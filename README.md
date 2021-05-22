# 概要

```
本プロジェクトは、ソフトウェアの実現可能性の調査・研究のための実験的なプロジェクトです。
ご利用に伴う、あらゆるうトラブルに関し、開発者は一切の責任を負わないことをご承知おきください。
```

# 構成
# 設定
# コンテキスト
## ApplicationContext
* beans
* pathMappings

## ActionContext
* method
* path
* contextPath
* contentType
* parameter
* user := authenticated, lang

# コントローラ
## ActionController
# ビュー
* http header
  * status          固定
  * Content-Type    固定	
* http body
  * outputstream    リクエストごと
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
# メッセージング

# TODO

1. resourcesの下を闇雲に公開するのはよくないな
1. <s>xuml</s>
1. <s>パスマッピング</s>
1. <s>include</s>
1. <s>ApplicationContextのnamespace対応</s>
1. ActionContextをThreadLocalにする
1. Viewからpull
1. <s>バージョン管理 ・ 一時保存</s>
1. 認証
1. アクセス制御
1. Repository修正
1. copy
1. Jackson対応
1. ドキュメントの関連付け

