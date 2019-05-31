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
package com.shorindo.docs;

import java.io.IOException;
import java.io.InputStream;

import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.DatabaseSchema;
import com.shorindo.docs.database.DatabaseService;
import com.shorindo.docs.repository.Repository;

/**
 * 
 */
public class DocumentService {
    private static ActionLogger LOG = ActionLogger.getLogger(DocumentService.class);
    private DatabaseService databaseService = BeanManager.inject(DatabaseService.class);
    private Repository repository = BeanManager.inject(Repository.class);

    /**
     * 
     */
    protected DocumentService() {
    }

    public void setUp() throws IOException {
        String dsdlName = getClass().getSimpleName() + ".dsdl";
        LOG.debug(dsdlName);
        InputStream is = getClass().getResourceAsStream(dsdlName);
        try {
            DatabaseSchema schema = databaseService.loadSchema(is);
            for (DatabaseSchema.Entity e : schema.getEntityList()) {
                String ddl = databaseService.generateDDL((DatabaseSchema.Table)e);
                LOG.info(ddl);
            }
        } catch (DatabaseException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (is != null) is.close();
        }
    }
}
