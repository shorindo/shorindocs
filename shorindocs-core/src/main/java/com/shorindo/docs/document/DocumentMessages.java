/*
 * Copyright 2018 Shorindo, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shorindo.docs.document;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum DocumentMessages implements ActionMessages {
    @Message(ja = "パス[{0}]を[{1}]にマップします。" )
    DOCS_0001,
    @Message(ja = "クラス[{0}]をパス[{1}]にマッピングします。" )
    DOCS_0003,
    @Message(ja = "bean[{0}]の値がセットされていないため、デフォルト値[{1}]を使用します。")
    DOCS_0004,
    @Message(ja = "Servlet開始：{0}")
    DOCS_1105,
    @Message(ja = "Servlet終了：{0} : {1} ms")
    DOCS_1106,
    @Message(ja = "アクション開始：{0}")
    DOCS_1107,
    @Message(ja = "アクション終了：{0} : {1} ms")
    DOCS_1108,
    @Message(ja = "初期化開始：{0}")
    DOCS_1109,
    @Message(ja = "初期化終了：{0} : {1} ms")
    DOCS_1110,
    @Message(ja = "コントローラ[{0}]を使用します。")
    DOCS_1120,

    @Message(ja = "プロパティ[{0}]をセットできません。")
    DOCS_3001,
    @Message(ja = "クラス[{0}]の情報を取得できません。")
    DOCS_3002,
    @Message(ja = "アクション[{0}]に適合するメソッドがありません。")
    DOCS_3003,
    @Message(ja = "ID生成時に例外が発生しました。")
    DOCS_3004,
    @Message(ja = "DSDLファイル[{0}]のクローズに失敗しました。")
    DOCS_3005,
    @Message(ja = "ドキュメント[{0}]が見つかりません。")
    DOCS_3006,

    @Message(ja = "メッセージ[{0}]が見つかりません。")
    DOCS_5001,
    @Message(ja = "メッセージ[{0}]に[{1}]をセットできません。")
    DOCS_5002,
    @Message(ja = "パス[{0}]に対する処理が見つかりません。")
    DOCS_5003,
    @Message(ja = "{0}を初期化中にエラーが発生しました。")
    DOCS_5004,
    @Message(ja = "プロパティ[{0}]がありません。")
    DOCS_5005,
    @Message(ja = "Bean[{0}]に対応するsetメソッドがありません。")
    DOCS_5006,
    @Message(ja = "プロパティ名の指定[{0}]に誤りがあります。")
    DOCS_5007,
    @Message(ja = "ドキュメントの読み込みに失敗しました。")
    DOCS_5008,
    @Message(ja = "未対応のコンテントタイプです：{0}")
    DOCS_5009,
    @Message(ja = "パス[{0}]に対応するコントローラーが見つかりません。")
    DOCS_5010,

    @Message(ja = "アプリケーションの初期化でエラーが発生しました。")
    DOCS_9000,
    @Message(ja = "ドキュメントの取得に失敗しました。")
    DOCS_9001,
    @Message(ja = "ドキュメント[{0}]の保存に失敗しました。")
    DOCS_9002,
    @Message(ja = "ドキュメント[{0}]の削除に失敗しました。")
    DOCS_9003,
    @Message(ja = "パス[{0}]の指定に失敗しました。")
    DOCS_9004,
    @Message(ja = "スキーマ定義ファイル[{0}]が読み込めません。")
    DOCS_9005,
    @Message(ja = "[{0}]は既に登録されているため、無視します。")
    DOCS_9006,
    @Message(ja = "未知のエラーです。")
    DOCS_9999;

    private Map<String,MessageFormat> bundle;

    private DocumentMessages() {
        bundle = ActionMessages.Util.bundle(this);
    }

    @Override
    public Map<String, MessageFormat> getBundle() {
        return bundle;
    }

    @Override
    public String getCode() {
        return ActionMessages.Util.getCode(this);
    }

    @Override
    public String getMessage(Object... params) {
        return ActionMessages.Util.getMessage(this, params);
    }

    @Override
    public String getMessage(Locale locale, Object... params) {
        return ActionMessages.Util.getMessage(this, params);
    }
}
