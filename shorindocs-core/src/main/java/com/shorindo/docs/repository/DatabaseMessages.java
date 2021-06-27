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
    @Message(lang="ja", content="SQL開始 : {0}")
    DBMS_0001,
    @Message(lang="ja", content="SQL終了 : {0} ms")
    DBMS_0002,
    @Message(lang="ja", content="検索開始：{0}")
    DBMS_0003,
    @Message(lang="ja", content="検索終了：{0}")
    DBMS_0004,
    @Message(lang="ja", content="削除開始：{0}")
    DBMS_0005,
    @Message(lang="ja", content="削除終了：{0}")
    DBMS_0006,
    @Message(lang="ja", content="追加開始：{0}")
    DBMS_0007,
    @Message(lang="ja", content="追加終了：{0}")
    DBMS_0008,
    @Message(lang="ja", content="更新開始：{0}")
    DBMS_0009,
    @Message(lang="ja", content="更新終了：{0}")
    DBMS_0010,
    @Message(lang="ja", content="パラメータ：{0}")
    DBMS_0011,

    @Message(lang="ja", content="スキーマ定義[{0}.{1}]を読み込みました。")
    DBMS_1101,
    @Message(lang="ja", content="トランザクション[{0}]をコミットします。")
    DBMS_1102,
    @Message(lang="ja", content="トランザクション[{0}]をロールバックします。")
    DBMS_1103,
    @Message(lang="ja", content="エンティティ[{0}]の定義は正常です。")
    DBMS_1104,
    @Message(lang="ja", content="エンティティ[{0}]の定義に異常があります。")
    DBMS_1107,
    @Message(lang="ja", content="DB接続：{0}({1}ms)")
    DBMS_1105,
    @Message(lang="ja", content="DB切断：{0}")
    DBMS_1106,
    @Message(lang="ja", content="トランザクション[{0}]を開始します。")
    DBMS_1108,
    @Message(lang="ja", content="トランザクション[{0}]が入れ子になっているため、コミットせず処理を継続します。")
    DBMS_1109,
    @Message(lang="ja", content="トランザクション[{0}]が入れ子になっているため、ロールバックせず処理を継続します。")
    DBMS_1110,

    @Message(lang="ja", content="トランザクション実行中ではありません。")
    DBMS_3001,

    @Message(lang="ja", content="データベース接続の初期化に失敗しました。")
    DBMS_5100,
    @Message(lang="ja", content="データベース処理の実行に失敗しました。")
    DBMS_5101,
    @Message(lang="ja", content="データベース接続のオープンに失敗しました。")
    DBMS_5102,
    @Message(lang="ja", content="データベース接続のクローズに失敗しました。")
    DBMS_5103,
    @Message(lang="ja", content="トランザクションのコミットに失敗しました。")
    DBMS_5104,
    @Message(lang="ja", content="トランザクションのロールバックに失敗しました。")
    DBMS_5105,
    @Message(lang="ja", content="ステートメントのクローズに失敗しました。")
    DBMS_5106,
    @Message(lang="ja", content="結果セットのクローズに失敗しました。")
    DBMS_5107,
    @Message(lang="ja", content="エンティティ[{0}]がありません。")
    DBMS_5108,
    @Message(lang="ja", content="カラム[{0}.{1}]が未定義です。")
    DBMS_5109,
    @Message(lang="ja", content="カラム[{0}.{1}]がデータベースにありません。")
    DBMS_5110,
    @Message(lang="ja", content="INSERT文の生成でエラーが発生しました。")
    DBMS_5111,
    @Message(lang="ja", content="UPDATE文の生成でエラーが発生しました。")
    DBMS_5112,
    @Message(lang="ja", content="DELETE文の生成でエラーが発生しました。")
    DBMS_5113,
    @Message(lang="ja", content="SELECT文の生成でエラーが発生しました。")
    DBMS_5114,
    @Message(lang="ja", content="INSERT文の実行でエラーが発生しました。")
    DBMS_5115,
    @Message(lang="ja", content="UPDATE文の実行でエラーが発生しました。")
    DBMS_5116,
    @Message(lang="ja", content="DELETE文の実行でエラーが発生しました。")
    DBMS_5117,
    @Message(lang="ja", content="SELECT文の実行でエラーが発生しました。")
    DBMS_5118,
    @Message(lang="ja", content="カラム[{0}]に対応するフィールド[{1}]がありません。")
    DBMS_5119,
    @Message(lang="ja", content="カラム[{0}]に対応するアクセッサ[{1}]がありません。")
    DBMS_5120,
    @Message(lang="ja", content="テーブル[{0}]の作成に失敗しました。")
    DBMS_5121,
    @Message(lang="ja", content="カラム[{0}]の一意キーの順序[{0}]が重複しています。")
    DBMS_5122,
    @Message(lang="ja", content="スキーマの検証に失敗しました。")
    DBMS_5123,
    @Message(lang="ja", content="カラム[{0}]に対応するスキーマタイプがありません。")
    DBMS_5124,
    @Message(lang="ja", content="テーブル指定アノテーションがありません。")
    DBMS_5125,
    @Message(lang="ja", content="カラム指定アノテーションが１つもありません。")
    DBMS_5126,
    @Message(lang="ja", content="エンティティ[{0}]の実体を作成しました。")
    DBMS_5127,
    @Message(lang="ja", content="エンティティ[{0}]の実体を作成できませんでした。")
    DBMS_5128,
    @Message(lang="ja", content="フィールド[{0}]の型[{1}]は不明です。")
    DBMS_5129,
    @Message(lang="ja", content="エンティティ[{0}]に一意キーがありません。")
    DBMS_5130,
    @Message(lang="ja", content="カラム[{0}]の型が不明です。")
    DBMS_5131,
    @Message(lang="ja", content="[{0}]は[{1}]のインスタンスではありません。")
    DBMS_5132,
    @Message(lang="ja", content="未知のエラーが発生しました。")
    DBMS_9999
    ;

}
