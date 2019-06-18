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

import com.shorindo.docs.outlogger.ProxyInputStream;
import com.shorindo.docs.outlogger.ProxyOutputStream;

import net.arnx.jsonic.JSON;

/**
 * 
 */
public class RpcClient {
    private String base;

    public RpcClient(String base) {
        this.base = base;
    }

    public Object execute(String docId, String methodName, Object...o) {
        try {
            URLConnection conn = new URL(base + docId)
                .openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            JsonRpcRequest request = new JsonRpcRequest();
            request.setId(String.valueOf(System.currentTimeMillis()));
            request.setMethod(methodName);
            request.setParams(o);
            ProxyOutputStream reqStream = new ProxyOutputStream(conn.getOutputStream());
            JSON.encode(request, reqStream);
            ProxyInputStream resStream = new ProxyInputStream(conn.getInputStream());
            JsonRpcResponse response = JSON.decode(resStream, JsonRpcResponse.class);
            Object result = response.getResult();
            System.out.println(">> " + reqStream.toString());
            System.out.println("<< " + resStream.toString());
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
