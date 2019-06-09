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

import org.junit.Test;

import net.arnx.jsonic.JSON;

/**
 * 
 */
public class DocumentControllerTest {
    private Object execute(String docId, String methodName, Object...o) {
        try {
            URLConnection conn = new URL("http://localhost:8080/docs/" + docId)
                .openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            JsonRpcRequest request = new JsonRpcRequest();
            request.setId(String.valueOf(System.currentTimeMillis()));
            request.setMethod("show");
            request.setParams(o);
            JSON.encode(request, conn.getOutputStream());
            InputStream is = conn.getInputStream();
            JsonRpcResponse response = JSON.decode(is, JsonRpcResponse.class);
            return response.getResult();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    private Object show(String docId) {
        return execute(docId, "show");
    }

    @Test
    public void testView() throws Exception {
        Object result = show("specout");
        System.out.println(result);
    }
}
