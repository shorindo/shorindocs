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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

import com.shorindo.docs.outlogger.ProxyInputStream;
import com.shorindo.docs.outlogger.ProxyOutputStream;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.TypeReference;

/**
 * 
 */
public class RpcClient {
    private String base;

    public RpcClient(String base) {
        this.base = base;
    }

    public Object execute(String docId, String methodName, Object... params) {
        try {
            long st = System.currentTimeMillis();
            URLConnection conn = new URL(base + docId)
                .openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            JsonRpcRequest request = new JsonRpcRequest();
            request.setId(String.valueOf(System.currentTimeMillis()));
            request.setMethod(methodName);
            request.setParams(Arrays.asList(params));
            ProxyOutputStream reqStream = new ProxyOutputStream(conn.getOutputStream());
            JSON.encode(request, reqStream);
            System.out.println(">> " + reqStream.toString());
            ProxyInputStream resStream = new ProxyInputStream(conn.getInputStream());
            TypeReference<JsonRpcResponse> ref = new TypeReference<JsonRpcResponse>(){};
            JsonRpcResponse response = JSON.decode(resStream, ref);
            System.out.println("<< " + resStream.toString());
            System.out.println("elapsed: " + (System.currentTimeMillis() - st) + " ms");
            return response.getResult();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
