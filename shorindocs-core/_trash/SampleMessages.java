/*
 * Copyright 2019 Shorindo, Inc.
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
package com.shorindo.sample;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.shorindo.sample.AbstractMessages.Message;

/**
 * 
 */
public class SampleMessages extends AbstractMessages {

    public enum Messages implements AbstractMessages.Messages {
        @Message(
                ja = "日本語",
                en = "English"
                )
        SMPL_000,

        @Message(
                ja = "ほげ"
                )
        SMPL_001,
        SMPL_002,
        SMPL_003
        ;

        private Map<String,String> bundle;
// FIXME
//        private Messages() {
//            bundle = SampleMessages.this.bundle(this);
//        }

        @Override
        public Map<String, MessageFormat> getBundle() {
            return null;
        }

        @Override
        public String getCode() {
            return null;
        }

        @Override
        public String getMessage() {
            return null;
        }

        @Override
        public String getMessage(Locale locale) {
            return null;
        }
    }
}
