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
package com.shorindo.docs.repository;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum DatabaseMessages implements ActionMessages {
    @Message(ja = "SQL開始 : {0}")
    DBMS_0001,
    @Message(ja = "SQL終了 : {0} ms")
    DBMS_0002,
    @Message(ja = "検索開始：{0}")
    DBMS_0003,
    @Message(ja = "検索終了：{0}")
    DBMS_0004,
    @Message(ja = "削除開始：{0}")
    DBMS_0005,
    @Message(ja = "削除終了：{0}")
    DBMS_0006,
    @Message(ja = "追加開始：{0}")
    DBMS_0007,
    @Message(ja = "追加終了：{0}")
    DBMS_0008,
    @Message(ja = "更新開始：{0}")
    DBMS_0009,
    @Message(ja = "更新終了：{0}")
    DBMS_0010,
    @Message(ja = "パラメータ：{0}")
    DBMS_0011,

    @Message(ja = "スキーマ定義[{0}.{1}]を読み込みました。")
    DBMS_1101,
    @Message(ja = "トランザクションをコミットします。")
    DBMS_1102,
    @Message(ja = "トランザクションをロールバックします。")
    DBMS_1103,
    @Message(ja = "エンティティ[{0}]の定義は正常です。")
    DBMS_1104,
    @Message(ja = "エンティティ[{0}]の定義に異常があります。")
    DBMS_1107,
    @Message(ja = "DB接続：{0}")
    DBMS_1105,
    @Message(ja = "DB切断：{0}")
    DBMS_1106,
    @Message(ja = "トランザクションを開始します。")
    DBMS_1108,

    @Message(ja = "トランザクション実行中ではありません。")
    DBMS_3001,

    @Message(ja = "データベース接続の初期化に失敗しました。")
    DBMS_5100,
    @Message(ja = "データベース処理の実行に失敗しました。")
    DBMS_5101,
    @Message(ja = "データベース接続のオープンに失敗しました。")
    DBMS_5102,
    @Message(ja = "データベース接続のクローズに失敗しました。")
    DBMS_5103,
    @Message(ja = "トランザクションのコミットに失敗しました。")
    DBMS_5104,
    @Message(ja = "トランザクションのロールバックに失敗しました。")
    DBMS_5105,
    @Message(ja = "ステートメントのクローズに失敗しました。")
    DBMS_5106,
    @Message(ja = "結果セットのクローズに失敗しました。")
    DBMS_5107,
    @Message(ja = "エンティティ[{0}]がありません。")
    DBMS_5108,
    @Message(ja = "カラム[{0}.{1}]が未定義です。")
    DBMS_5109,
    @Message(ja = "カラム[{0}.{1}]がデータベースにありません。")
    DBMS_5110,
    @Message(ja = "INSERT文の生成でエラーが発生しました。")
    DBMS_5111,
    @Message(ja = "UPDATE文の生成でエラーが発生しました。")
    DBMS_5112,
    @Message(ja = "DELETE文の生成でエラーが発生しました。")
    DBMS_5113,
    @Message(ja = "SELECT文の生成でエラーが発生しました。")
    DBMS_5114,
    @Message(ja = "INSERT文の実行でエラーが発生しました。")
    DBMS_5115,
    @Message(ja = "UPDATE文の実行でエラーが発生しました。")
    DBMS_5116,
    @Message(ja = "DELETE文の実行でエラーが発生しました。")
    DBMS_5117,
    @Message(ja = "SELECT文の実行でエラーが発生しました。")
    DBMS_5118,
    @Message(ja = "カラム[{0}]に対応するフィールド[{1}]がありません。")
    DBMS_5119,
    @Message(ja = "カラム[{0}]に対応するアクセッサ[{1}]がありません。")
    DBMS_5120,
    @Message(ja = "テーブル[{0}]の作成に失敗しました。")
    DBMS_5121,
    @Message(ja = "カラム[{0}]の一意キーの順序[{0}]が重複しています。")
    DBMS_5122,
    @Message(ja = "スキーマの検証に失敗しました。")
    DBMS_5123,
    @Message(ja = "カラム[{0}]に対応するスキーマタイプがありません。")
    DBMS_5124,
    @Message(ja = "テーブル指定アノテーションがありません。")
    DBMS_5125,
    @Message(ja = "カラム指定アノテーションが１つもありません。")
    DBMS_5126,
    @Message(ja = "エンティティ[{0}]の実体を作成しました。")
    DBMS_5127,
    @Message(ja = "エンティティ[{0}]の実体を作成できませんでした。")
    DBMS_5128,
    @Message(ja = "フィールド[{0}]の型[{1}]は不明です。")
    DBMS_5129,
    @Message(ja = "エンティティ[{0}]に一意キーがありません。")
    DBMS_5130,
    @Message(ja = "カラム[{0}]の型が不明です。")
    DBMS_5131,
    @Message(ja = "未知のエラーが発生しました。")
    DBMS_9999
    ;

    private Map<String,MessageFormat> bundle;

    private DatabaseMessages() {
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
