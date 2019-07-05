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

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import com.shorindo.docs.document.DocumentModelImpl;
import com.shorindo.docs.document.DocumentControllable;
import com.shorindo.docs.model.DocumentModel;

/**
 * 
 */
public class DocumentControllerTest {
    private DocumentClient client = new DocumentClient();

    @Test
    public void testLoad() throws Exception {
        DocumentModel model = client.load("index");
        assertEquals("index", model.getDocumentId());
        assertTrue(model.getTitle().contains("welcome"));
    }

    @Test
    public void testSave() throws Exception {
        DocumentModel model = new DocumentModel() {
            @Override
            public String getDocumentId() {
                return "index";
            }
            @Override
            public String getController() {
                return "com.shorindo.docs.document.DocumentController";
            }
            @Override
            public String getTitle() {
                return "welcome - " + new Date();
            }
            @Override
            public String getContent() {
                return new Date().toString();
            }
        };
        client.save(model);
    }

    public static class DocumentClient implements DocumentControllable {
        private static RpcClient rpcClient =
                new RpcClient("http://localhost:8080/docs/");

        public DocumentModel load(String documentId) {
            return rpcClient.execute(
                    DocumentModelImpl.class,
                    documentId,
                    "load");
        }

        public DocumentModel save(DocumentModel model) {
            return rpcClient.execute(
                    DocumentModelImpl.class,
                    model.getDocumentId(),
                    "save",
                    model);
        }

        @Override
        public DocumentModel remove(String documentId) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
