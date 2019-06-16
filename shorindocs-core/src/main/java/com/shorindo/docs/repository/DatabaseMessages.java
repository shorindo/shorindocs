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

import com.shorindo.docs.ActionMessages;

/**
 * 
 */
public enum DatabaseMessages implements ActionMessages {
    DBMS_0001("SQL開始 : {0}"),
    DBMS_0002("SQL終了 : {0} ms"),
    DBMS_0003("検索開始：{0}"),
    DBMS_0004("検索終了：{0}"),
    DBMS_0005("削除開始：{0}"),
    DBMS_0006("削除終了：{0}"),
    DBMS_0007("追加開始：{0}"),
    DBMS_0008("追加終了：{0}"),
    DBMS_0009("更新開始：{0}"),
    DBMS_0010("更新終了：{0}"),
    DBMS_0011("パラメータ：{0}"),

    DBMS_1101("スキーマ定義[{0}.{1}]を読み込みました。"),
    DBMS_1102("トランザクションをコミットします。"),
    DBMS_1103("トランザクションをロールバックします。"),
    DBMS_1104("エンティティ[{0}]の定義は正常です。"),
    DBMS_1107("エンティティ[{0}]の定義に異常があります。"),
    DBMS_1105("DB接続：{0}"),
    DBMS_1106("DB切断：{0}"),

    DBMS_5100("データベース接続の初期化に失敗しました。"),
    DBMS_5101("データベース処理の実行に失敗しました。"),
    DBMS_5102("データベース接続のオープンに失敗しました。"),
    DBMS_5103("データベース接続のクローズに失敗しました。"),
    DBMS_5104("トランザクションのコミットに失敗しました。"),
    DBMS_5105("トランザクションのロールバックに失敗しました。"),
    DBMS_5106("ステートメントのクローズに失敗しました。"),
    DBMS_5107("結果セットのクローズに失敗しました。"),
    DBMS_5108("エンティティ[{0}]がありません。"),
    DBMS_5109("カラム[{0}.{1}]が未定義です。"),
    DBMS_5110("カラム[{0}.{1}]がデータベースにありません。"),
    DBMS_5111("INSERT文の生成でエラーが発生しました。"),
    DBMS_5112("UPDATE文の生成でエラーが発生しました。"),
    DBMS_5113("DELETE文の生成でエラーが発生しました。"),
    DBMS_5114("SELECT文の生成でエラーが発生しました。"),
    DBMS_5115("INSERT文の実行でエラーが発生しました。"),
    DBMS_5116("UPDATE文の実行でエラーが発生しました。"),
    DBMS_5117("DELETE文の実行でエラーが発生しました。"),
    DBMS_5118("SELECT文の実行でエラーが発生しました。"),
    DBMS_5119("カラム[{0}]に対応するフィールド[{1}]がありません。"),
    DBMS_5120("カラム[{0}]に対応するアクセッサ[{1}]がありません。"),
    DBMS_5121("テーブル[{0}]の作成に失敗しました。"),
    DBMS_5122("カラム[{0}]の一意キーの順序[{0}]が重複しています。"),
    DBMS_5123("スキーマの検証に失敗しました。"),
    DBMS_5124("カラム[{0}]に対応するスキーマタイプがありません。"),
    DBMS_5125("テーブル指定アノテーションがありません。"),
    DBMS_5126("カラム指定アノテーションが１つもありません。"),
    DBMS_5127("エンティティ[{0}]の実体を作成しました。"),
    DBMS_5128("エンティティ[{0}]の実体を作成できませんでした。")
    ;

    private String message;

    private DatabaseMessages(String message) {
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
