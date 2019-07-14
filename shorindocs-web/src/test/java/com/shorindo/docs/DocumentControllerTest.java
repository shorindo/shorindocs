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

import net.arnx.jsonic.JSON;

import org.junit.Test;

import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.plaintext.PlainTextController;

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
                return PlainTextController.class.getName();
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

    public static class DocumentClient {
        private static RpcClient rpcClient =
                new RpcClient("http://localhost:8080/docs/");

        public DocumentModel load(String documentId) {
            Object result = rpcClient.execute(
                    documentId,
                    "load",
                    documentId);
            return JSON.decode(JSON.encode(result), Document.class);
        }

        public DocumentModel save(DocumentModel entity) {
            Object result = rpcClient.execute(
                    entity.getDocumentId(),
                    "save",
                    entity);
            return JSON.decode(JSON.encode(result), Document.class);
        }

        public DocumentModel remove(String documentId) {
            Object result = rpcClient.execute(
                    documentId,
                    "remove");
            return JSON.decode(JSON.encode(result), Document.class);
        }
    }

    public static class Document implements DocumentModel {
        private String documentId;
        private String controller;
        private String title;
        private String content;

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public void setController(String controller) {
            this.controller = controller;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String getDocumentId() {
            return documentId;
        }

        @Override
        public String getController() {
            return controller;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getContent() {
            return content;
        }
        
    }
}
