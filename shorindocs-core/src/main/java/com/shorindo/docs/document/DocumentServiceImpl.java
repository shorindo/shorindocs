/*
 * Copyright 2018 Shorindo, Inc.
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

import java.io.IOException;
import java.io.InputStream;

import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.repository.DatabaseException;
import com.shorindo.docs.repository.DatabaseSchema;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.RepositoryServiceImpl;

/**
 * 
 */
public class DocumentServiceImpl implements DocumentService {
    private static ActionLogger LOG =
            ActionLogger.getLogger(DocumentServiceImpl.class);
    protected RepositoryService repositoryService =
            ServiceFactory.getService(RepositoryService.class);

    /**
     * 
     */
    public DocumentServiceImpl() {
        //validate();
    }

    public void validate() {
        String dsdlName = getClass().getSimpleName() + ".dsdl";
        LOG.debug(dsdlName);
        InputStream is = getClass().getResourceAsStream(dsdlName);
        try {
            DatabaseSchema schema = repositoryService.loadSchema(is);
            for (DatabaseSchema.Entity e : schema.getEntityList()) {
                String ddl = repositoryService.generateDDL((DatabaseSchema.Table)e);
                LOG.info(ddl);
            }
        } catch (DatabaseException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
        }
    }
}
