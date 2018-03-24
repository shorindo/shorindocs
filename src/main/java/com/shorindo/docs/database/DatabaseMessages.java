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
package com.shorindo.docs.database;

import java.text.MessageFormat;
import java.util.Locale;

import com.shorindo.docs.ActionMessages;

/**
 * 
 */
public enum DatabaseMessages implements ActionMessages {
    DB_0005("SQL文実行開始 : #{0} {1}"),
    DB_0006("SQL文実行終了 : #{0} : {1} ms"),
    DB_0007("検索処理開始：{0}"),
    DB_0008("検索処理終了：{0}"),
    DB_0009("削除処理開始：{0}"),
    DB_0010("削除処理終了：{0}"),
    DB_0011("新規登録開始：{0}"),
    DB_0012("新規登録終了：{0}"),
    DB_0013("更新登録開始：{0}"),
    DB_0014("更新登録終了：{0}"),

    DB_1101("スキーマ定義[{0}.{1}]を読み込みました。"),
    DB_1102("トランザクションをコミットします。"),
    DB_1103("トランザクションをロールバックします。"),
    DB_1104("エンティティ[{0}]の定義は正常です。"),

    DB_5100("データベース接続の初期化に失敗しました。"),
    DB_5101("データベース処理の実行に失敗しました。"),
    DB_5102("データベース接続のオープンに失敗しました。"),
    DB_5103("データベース接続のクローズに失敗しました。"),
    DB_5104("トランザクションのコミットに失敗しました。"),
    DB_5105("トランザクションのロールバックに失敗しました。"),
    DB_5106("ステートメントのクローズに失敗しました。"),
    DB_5107("結果セットのクローズに失敗しました。"),
    DB_5108("エンティティ[{0}]がありません。"),
    DB_5109("カラム[{0}.{1}]がデータベースにありません。"),
    DB_5110("カラム[{0}.{1}]が未定義です。"),
    DB_5111("INSERT文の生成でエラーが発生しました。"),
    DB_5112("UPDATE文の生成でエラーが発生しました。"),
    DB_5113("DELETE文の生成でエラーが発生しました。"),
    DB_5114("SELECT文の生成でエラーが発生しました。"),
    DB_5115("INSERT文の実行でエラーが発生しました。"),
    DB_5116("UPDATE文の実行でエラーが発生しました。"),
    DB_5117("DELETE文の実行でエラーが発生しました。"),
    DB_5118("SELECT文の実行でエラーが発生しました。"),
    DB_5119("カラム[{0}]に対応するフィールド[{1}]がありません。"),
    DB_5120("カラム[{0}]に対応するアクセッサ[{1}]がありません。"),
    DB_5121("テーブル[{0}]の作成に失敗しました。"),
    DB_5122("カラム[{0}]の一意キーの順序[{0}]が重複しています。"),
    DB_5123("スキーマの検証に失敗しました。"),
    DB_5124("カラム[{0}]に対応するスキーマタイプがありません。"),
    DB_5125("テーブル指定アノテーションがありません。"),
    DB_5126("カラム指定アノテーションが１つもありません。"),
    DB_5127("エンティティ[{0}]の実体を作成しました。"),
    DB_5128("エンティティ[{0}]の実体を作成できませんでした。")
    ;

    private String message;

    private DatabaseMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name().replaceAll("_", "-");
    }

    @Override
    public String getMessage(Locale locale, Object... args) {
        return MessageFormat.format(message, args);
    }

}
