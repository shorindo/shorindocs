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

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.shorindo.sample.AbstractMessages.Message;

/**
 * 
 */
public enum SampleMessages implements AbstractMessages {
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

    private Map<String,String> bundle = new LinkedHashMap<String,String>();
    
    private SampleMessages() {
        MessageUtil.bundle(this);
    }

    @Override
    public Map<String, String> getBundle() {
        return bundle;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return MessageUtil.getMessage(this);
    }

    @Override
    public String getMessage(Locale locale) {
        return MessageUtil.getMessage(locale, this);
    }

    public String toString() {
        return MessageUtil.getString(this);
    }
}
