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
package com.shorindo.docs.outlogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 */
public class ProxyInputStream extends InputStream {
    private InputStream is;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public ProxyInputStream(InputStream is) {
        this.is = is;
    }

    @Override
    public int read() throws IOException {
        int c = is.read();
        baos.write(c);
        return c;
    }

    public String toString() {
        return baos.toString();
    }
}
