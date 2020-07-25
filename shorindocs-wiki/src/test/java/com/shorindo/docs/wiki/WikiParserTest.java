/*
 * Copyright 2020 Shorindo, Inc.
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
package com.shorindo.docs.wiki;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class WikiParserTest {
    private WikiParser parser = new WikiParser();

    @Test
    public void testHeader() {
        parser.parse("====== 見出し１ ======");
        parser.parse("====== 見出し１ ======\n");
        parser.parse("====== 見出し１ =======");
        parser.parse("====== 見出し４ ===");
        parser.parse("====== 見出し１ ======\n===== 見出し２ =====\n==== 見出し３ ====");
    }

}
