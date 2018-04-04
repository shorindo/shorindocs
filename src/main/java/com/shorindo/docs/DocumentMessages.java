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
package com.shorindo.docs;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * 
 */
public enum DocumentMessages implements ActionMessages {
    DOCS_0001("パス[{0}]を[{1}]にマップします。"),
    DOCS_0003("クラス[{0}]をパス[{1}]にマッピングします。"),
    DOCS_0004("bean[{0}]の値がセットされていないため、デフォルト値[{1}]を使用します。"),
    DOCS_1105("サービス開始：{0}"),
    DOCS_1106("サービス終了：{0} : {1} ms"),
    DOCS_1107("アクション開始：{0}"),
    DOCS_1108("アクション終了：{0} : {1} ms"),
    DOCS_1109("初期化開始：{0}"),
    DOCS_1110("初期化終了：{0} : {1} ms"),
    DOCS_1120("コントローラ[{0}]を使用します。"),

    DOCS_3001("プロパティ[{0}]をセットできません。"),
    DOCS_3002("クラス[{0}]の情報を取得できません。"),
    DOCS_3003("アクション[{0}]に適合するメソッドがありません。"),
    DOCS_3004("ID生成時に例外が発生しました。"),
    DOCS_3005("DSDLファイル[{0}]のクローズに失敗しました。"),
    DOCS_3006("ドキュメント[{0}]が見つかりません。"),

    DOCS_5001("メッセージ[{0}]が見つかりません。"),
    DOCS_5002("メッセージ[{0}]に[{1}]をセットできません。"),
    DOCS_5003("パス[{0}]に対する処理が見つかりません。"),
    DOCS_5004("{0}を初期化中にエラーが発生しました。"),
    DOCS_5005("プロパティ[{0}]がありません。"),
    DOCS_5006("Bean[{0}]に対応するsetメソッドがありません。"),
    DOCS_5007("プロパティ名の指定[{0}]に誤りがあります。"),
    DOCS_5008("ドキュメントの読み込みに失敗しました。"),

    DOCS_9000("アプリケーションの初期化でエラーが発生しました。"),
    DOCS_9001("ドキュメントの取得に失敗しました。"),
    DOCS_9002("ドキュメント[{0}]の保存に失敗しました。"),
    DOCS_9003("ドキュメント[{0}]の削除に失敗しました。"),
    DOCS_9004("パス[{0}]の指定に失敗しました。"),
    DOCS_9999("未定義のエラーです。");

    private String message;

    private DocumentMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage(Locale locale, Object... args) {
        return MessageFormat.format(message, args);
    }

}
