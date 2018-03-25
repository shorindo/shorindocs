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
package com.shorindo.docs.outlogger;

import static com.shorindo.docs.database.DatabaseMessages.*;
import java.io.InputStream;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.database.DatabaseException;
import com.shorindo.docs.database.DatabaseSchema;
import com.shorindo.docs.database.DatabaseService;

/**
 * 
 */
public class OutloggerService {
    private static final ActionLogger LOG = ActionLogger.getLogger(OutloggerService.class);
    private static final DatabaseService databaseService = DatabaseService.newInstance();

    /*==========================================================================
     * 初期設定
     */
    public void createSchema() throws DatabaseException {
        try {
            databaseService.createTableFromSchema(new OutloggerEntity().getTableSchema());
            LOG.info(DTBS_5127, OutloggerEntity.class.getName());
        } catch (DatabaseException e) {
            LOG.warn(DTBS_5128, e, OutloggerEntity.class.getName());
        }
    }

    /**=========================================================================
     * ドキュメントの基本情報を操作する。
     */
    public void createDocument() {}
    public void registDocument() {}
    public void removeDocument() {}
    public void commitDocument() {}
    public void rollbackDocument() {}

    /**=========================================================================
     * ドキュメントのアクセス権を操作する。
     */
    public void listAcl() {}
    public void addAcl() {}
    public void removeAcl() {}

    /**=========================================================================
     * アウトラインを操作する。
     */
    public void listOutlog() {}
    public void createOutlog() {}
    public void registOutlog() {}
    public void removeOutlog() {}
    public void commitOutlog() {}

    /**
     * 
     */
    public void generateSchemaEntity() throws Exception {
        InputStream is = getClass().getResourceAsStream("Outlogger.dsdl");
        try {
            DatabaseService service = DatabaseService.newInstance();
            DatabaseSchema schema = service.loadSchema(is);
            service.generateSchemaEntity(schema);

            String ddl = service.generateDDL((DatabaseSchema.Table)schema.getEntiry("DOCS_OUTLOGGER"));
            System.out.println(ddl);
        } finally {
            is.close();
        }
    }

    /**
     * 
     */
    public static void main(String[] args) {
        try {
            OutloggerService service = new OutloggerService();
            service.generateSchemaEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
