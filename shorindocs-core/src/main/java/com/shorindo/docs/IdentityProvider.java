/*
 * Copyright 2016 Shorindo, Inc.
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

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentMessages;

/**
 * http://d.hatena.ne.jp/maachang/20150624/1435116219
 */
public class IdentityProvider {
    private static final ActionLogger LOG = ActionLogger.getLogger(IdentityProvider.class);
    private static final long BASE_TIME = 0x151f88d7980L; // 2016/01/01 00:00:00
    private static final long TIME_MASK = bit2long(53);
    private static final long SEQ_MASK  = bit2long(6);
    private static final long SERVER_MASK = bit2long(4);
    private static long last = -1;
    private static long seq = 0;
    private static long host = 0;

    public synchronized static long newId() {
        long time = getTime();
        seq = (seq  + 1) & SEQ_MASK;
        if (last == time && seq == 0) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                LOG.warn(DocumentMessages.DOCS_3004, e);
            }
            last = time = getTime();
        } else {
            last = time;
        }
        return time | (seq << 4) | host;
    }

    public static String hash(String source) {
        return String.format("%08x", source.hashCode());
    }

    private static long bit2long(int bit) {
        long l = 0;
        for (int i = 0; i < bit; i++) {
            l = l<<1 | 1;
        }
        return l;
    }

    private static long getTime() {
        return ((System.currentTimeMillis() - BASE_TIME) & TIME_MASK) << 10;
    }
}
