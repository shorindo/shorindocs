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

/**
 * 
 */
public enum DocsMessages implements ActionMessages {
    I_0001("パス[{0}]を[{1}]にマップします。"),
    I_0002("コンポーネント[{0}]をロードしました。"),
    I_0003("クラス[{0}]をパス[{1}]にマッピングします。"),
    I_1101("スキーマ定義[{0}.{1}]を読み込みました。"),
    I_1102("トランザクションをコミットします。"),
    I_1103("トランザクションをロールバックします。"),

    W_3001("プロパティ[{0}]をセットできません。"),
    W_3002("クラス[{0}]の情報を取得できません。"),
    W_3003("アクション[{0}]に適合するメソッドがありません。"),

    E_5001("メッセージ[{0}]が見つかりません。"),
    E_5002("メッセージ[{0}]に[{1}]をセットできません。"),
    E_5003("パス[{0}]に対する処理が見つかりません。"),
    E_5004("{0}を初期化中にエラーが発生しました。"),
    E_5100("データベース接続の初期化に失敗しました。"),
    E_5101("データベース処理の実行に失敗しました。"),
    E_5102("データベース接続のオープンに失敗しました。"),
    E_5103("データベース接続のクローズに失敗しました。"),
    E_5104("トランザクションのコミットに失敗しました。"),
    E_5105("トランザクションのロールバックに失敗しました。"),
    E_5106("ステートメントのクローズに失敗しました。"),
    E_5107("結果セットのクローズに失敗しました。"),
    E_5108("エンティティ[{0}]がありません。"),
    E_5109("カラム[{0}.{1}]がデータベースにありません。"),
    E_5110("カラム[{0}.{1}]が未定義です。"),
    E_9999("予期せぬエラー");

    private String message;

    private DocsMessages(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage(Object... args) {
        return MessageFormat.format(message, args);
    }

}
