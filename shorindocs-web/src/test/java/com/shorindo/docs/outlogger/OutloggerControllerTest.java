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

import static org.junit.Assert.*;

//import mockit.Invocation;
//import mockit.Mock;
//import mockit.MockUp;
//
//import org.junit.BeforeClass;
import org.junit.Test;

import com.shorindo.docs.RpcClient;
import com.shorindo.docs.model.DocumentModel;

/**
 * 
 */
public class OutloggerControllerTest {
    private RpcClient client = new RpcClient("http://localhost:8080/docs/");

//    @BeforeClass
//    public static void setupBefore() {
//        new MockUp<RpcClient>() {
//            @Mock
//            public Object execute(Invocation inv, String docId, String methodName, Object...params) {
//                System.out.println("mock!(" + docId + "," + methodName + ")");
//                return inv.proceed(docId, methodName, params);
//            }
//        };
//    }

    @Test
    public void testListLog() throws Exception {
        client.execute(
                "outlogger",
                "listLog");
    }

    @Test
    public void testPutLog() throws Exception {
        OutloggerModel model = new OutloggerModel() {
            @Override
            public String getDocumentId() {
                return "outlogger";
            }
            @Override
            public Integer getLogId() {
                return null;
            }
            @Override
            public int getVersion() {
                return 0;
            }
            @Override
            public int getDisplayOrder() {
                return 0;
            }
            @Override
            public short getLevel() {
                return 0;
            }
            @Override
            public String getContent() {
                return "putLog";
            }
            @Override
            public Integer getParentId() {
                return null;
            }
        };
        client.execute(
                "outlogger",
                "putLog",
                model);
    }
}
