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

import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.repository.Repository;

/**
 * 
 */
public abstract class DocumentServiceFactory {
    private DocumentService documentService;
    private DatabaseService databaseService;
    private Repository repository;


    public synchronized DocumentService getDocumentService() {
        if (documentService == null) {
            documentService = new DocumentService();
        }
        return documentService;
    }
    
    public synchronized DatabaseService getDatabaseService() {
        if (databaseService == null) {
            databaseService = new DatabaseService();
        }
        return databaseService;
    }
}
