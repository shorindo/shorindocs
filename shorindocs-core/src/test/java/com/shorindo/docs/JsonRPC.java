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
package com.shorindo.docs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.arnx.jsonic.JSON;

/**
 * 
 */
public class JsonRPC {
    public Object execute(String method, Object o) {
        try {
            URLConnection conn = new URL("http://localhost:8080/docs/api")
                .openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            JSON.encode(o, conn.getOutputStream());
            InputStream is = conn.getInputStream();
            return JSON.decode(is);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static void main(String[] args) throws Exception {
        JsonRPC rpc = new JsonRPC();
        Object o = rpc.execute("foo", new Object() {
           public int intValue = 123;
           public String stringValue = "string";
        });
    }
}
