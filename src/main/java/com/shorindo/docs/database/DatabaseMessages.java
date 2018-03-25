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
    DTBS_0001("SQL文実行開始 : #{0} {1}"),
    DTBS_0002("SQL文実行終了 : #{0} : {1} ms"),
    DTBS_0003("検索処理開始：{0}"),
    DTBS_0004("検索処理終了：{0}"),
    DTBS_0005("削除処理開始：{0}"),
    DTBS_0006("削除処理終了：{0}"),
    DTBS_0007("新規登録開始：{0}"),
    DTBS_0008("新規登録終了：{0}"),
    DTBS_0009("更新登録開始：{0}"),
    DTBS_0010("更新登録終了：{0}"),

    DTBS_1101("スキーマ定義[{0}.{1}]を読み込みました。"),
    DTBS_1102("トランザクションをコミットします。"),
    DTBS_1103("トランザクションをロールバックします。"),
    DTBS_1104("エンティティ[{0}]の定義は正常です。"),

    DTBS_5100("データベース接続の初期化に失敗しました。"),
    DTBS_5101("データベース処理の実行に失敗しました。"),
    DTBS_5102("データベース接続のオープンに失敗しました。"),
    DTBS_5103("データベース接続のクローズに失敗しました。"),
    DTBS_5104("トランザクションのコミットに失敗しました。"),
    DTBS_5105("トランザクションのロールバックに失敗しました。"),
    DTBS_5106("ステートメントのクローズに失敗しました。"),
    DTBS_5107("結果セットのクローズに失敗しました。"),
    DTBS_5108("エンティティ[{0}]がありません。"),
    DTBS_5109("カラム[{0}.{1}]がデータベースにありません。"),
    DTBS_5110("カラム[{0}.{1}]が未定義です。"),
    DTBS_5111("INSERT文の生成でエラーが発生しました。"),
    DTBS_5112("UPDATE文の生成でエラーが発生しました。"),
    DTBS_5113("DELETE文の生成でエラーが発生しました。"),
    DTBS_5114("SELECT文の生成でエラーが発生しました。"),
    DTBS_5115("INSERT文の実行でエラーが発生しました。"),
    DTBS_5116("UPDATE文の実行でエラーが発生しました。"),
    DTBS_5117("DELETE文の実行でエラーが発生しました。"),
    DTBS_5118("SELECT文の実行でエラーが発生しました。"),
    DTBS_5119("カラム[{0}]に対応するフィールド[{1}]がありません。"),
    DTBS_5120("カラム[{0}]に対応するアクセッサ[{1}]がありません。"),
    DTBS_5121("テーブル[{0}]の作成に失敗しました。"),
    DTBS_5122("カラム[{0}]の一意キーの順序[{0}]が重複しています。"),
    DTBS_5123("スキーマの検証に失敗しました。"),
    DTBS_5124("カラム[{0}]に対応するスキーマタイプがありません。"),
    DTBS_5125("テーブル指定アノテーションがありません。"),
    DTBS_5126("カラム指定アノテーションが１つもありません。"),
    DTBS_5127("エンティティ[{0}]の実体を作成しました。"),
    DTBS_5128("エンティティ[{0}]の実体を作成できませんでした。")
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
