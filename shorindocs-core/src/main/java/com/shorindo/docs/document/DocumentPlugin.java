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
package com.shorindo.docs.document;

import com.shorindo.docs.ApplicationContext;
import com.shorindo.docs.action.ActionPlugin;

/**
 * 
 */
public class DocumentPlugin extends ActionPlugin {

    @Override
    public String getId() {
        return "document";
    }

    @Override
    public void initialize() {
        addSchema(getClass().getResourceAsStream("Document.dsdl"));
//        ApplicationContext.addBean(DocumentController.class);
//        ApplicationContext.addBean(DocumentService.class, DocumentServiceImpl.class);
    }

}
