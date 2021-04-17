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
package com.shorindo.xuml;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import com.shorindo.docs.action.ActionMessages;

/**
 * 
 */
public enum XumlMessages implements ActionMessages {
    @Message(lang="ja", content="コンポーネント[{0}]をロードしました。")
    XUML_1000,
    @Message(lang="ja", content="レンダリング開始：{0}")
    XUML_1001,
    @Message(lang="ja", content="レンダリング終了：{0} : {1} ms")
    XUML_1002,
    @Message(lang="ja", content="XUMLテンプレート[{0}]のクローズに失敗しました。")
    XUML_3001,
    @Message(lang="ja", content="属性[{0}]は不明です。")
    XUML_3002,
    @Message(lang="ja", content="コンポーネント[{0}]の指定ができませんでした。")
    XUML_5001,
    @Message(lang="ja", content="XUMLエンジンの初期化に失敗しました。")
    XUML_5010,
    @Message(lang="ja", content="XUMLのレンダリングに失敗しました。")
    XUML_5020,
    @Message(lang="ja", content="ルート要素が'xuml'ではありません。")
    XUML_5030,
    @Message(lang="ja", content="[{0}]の生成に失敗したため、Generalコンポーネントを使用します。")
    XUML_5125,
    @Message(lang="ja", content="データ出力に失敗しました。")
    XUML_5200,
    @Message(lang="ja", content="CSSセレクタの構文が不正です")
    XUML_6000,
    @Message(lang="ja", content="予期せぬエラーが発生しました：{0}")
    XUML_9999
    ;

}
