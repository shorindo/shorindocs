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

import static com.shorindo.docs.document.DocumentMessages.*;
import static com.shorindo.docs.outlogger.OutloggerMessages.*;
import static com.shorindo.docs.repository.DatabaseMessages.*;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXB;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.document.DocumentServiceImpl;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.NotFoundException;

/**
 * 
 */
public class OutloggerServiceImpl extends DocumentServiceImpl implements OutloggerService {
    private static final ActionLogger LOG = ActionLogger.getLogger(OutloggerServiceImpl.class);

    public OutloggerServiceImpl() {
    }

    /*==========================================================================
     * 初期設定
     */
    public void createSchema() throws RepositoryException {
        try {
            OutloggerEntity entity = new OutloggerEntity();
            repositoryService.createTableFromSchema(entity.getTableSchema());
            LOG.info(DBMS_5127, entity.getClass().getName());
        } catch (RepositoryException e) {
            LOG.warn(DBMS_5128, e, OutloggerEntity.class.getName());
        }
    }

    /*==========================================================================
     * ドキュメントの基本情報を操作する。
     */
    /**
     * メタデータを生成する
     * @return　メタデータ(XML)
     */
    public String createMetaData() {
        StringWriter writer = new StringWriter();
        JAXB.marshal(new OutloggerMetaData(), writer);
        return writer.toString();
    }

    public void registMetaData() {
    }

    public void removeMetaData() {
    }

    public void commitMetaData() {
    }

    public void rollbackMetaData() {
    }

    /**=========================================================================
     * ドキュメントのアクセス権を操作する。
     */
    public void listAcl() {
    }

    public void addAcl() {
    }

    public void removeAcl() {
    }

    /**=========================================================================
     * アウトラインを操作する。
     */
    public List<OutloggerEntity> listLog(OutloggerEntity entity) throws DocumentException {
        try {
            List<OutloggerEntity> entityList = repositoryService.query(
                    "SELECT * " +
                    "FROM   DOCS_OUTLOGGER " +
                    "WHERE  DOCUMENT_ID=? " +
                    "AND    VERSION=0 " +
                    "ORDER BY DISPLAY_ORDER ASC",
                    OutloggerEntity.class,
                    entity.getDocumentId());
            return entityList;
        } catch (RepositoryException e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }

    /**
     * 
     * @param entity
     * @return
     * @throws DocumentException
     */
    public OutloggerEntity putLog(OutloggerEntity entity) throws DocumentException {
        long st = System.currentTimeMillis();
        LOG.debug(OLOG_0001, "putLog");
        try {
            if (entity.getLogId() == null) {
                entity.setLogId(getNextLogId(entity.getDocumentId()));
                repositoryService.insert(entity);
            } else {
                OutloggerEntity prev = removeLog(entity);
                if (prev != null) {
                    entity.setCreateUser(prev.getCreateUser());
                    entity.setCreateDate(prev.getCreateDate());
                }
                repositoryService.insert(entity);
            }
            return repositoryService.get(entity);
        } catch (NotFoundException e) {
            LOG.warn(DOCS_9999, e);
            return null;
        } catch (RepositoryException e) {
            LOG.warn(DOCS_9999, e);
            return null;
        } finally {
            LOG.debug(OLOG_0002, "putLog", (System.currentTimeMillis() - st));
        }
    }

    /**
     * 
     * @param entity
     * @return
     * @throws DocumentException
     */
    public OutloggerEntity getLog(OutloggerEntity entity) throws DocumentException {
        long st = System.currentTimeMillis();
        LOG.debug(OLOG_0001, "getLog");
        try {
            return repositoryService.get(entity);
        } catch (NotFoundException e) {
            return null;
        } catch (RepositoryException e) {
            throw new DocumentException(e.getMessage(), e);
        } finally {
            LOG.debug(OLOG_0002, "getLog", (System.currentTimeMillis() - st));
        }
    }

    /**
     * 
     * @param entity
     * @return
     * @throws DocumentException
     */
    public OutloggerEntity removeLog(OutloggerEntity entity) throws DocumentException {
        long st = System.currentTimeMillis();
        LOG.debug(OLOG_0001, "putLog");
        try {
            // 前バージョンのデータがあったらバージョンをインクリメントする
            int version = 0;
            OutloggerEntity prev = new OutloggerEntity();
            prev.setDocumentId(entity.getDocumentId());
            prev.setLogId(entity.getLogId());
            prev.setVersion(0);
            prev = repositoryService.get(prev);

            List<OutloggerEntity> versions = repositoryService.query(
                    "SELECT MAX(VERSION) VERSION " +
                    "FROM   DOCS_OUTLOGGER " +
                    "WHERE  DOCUMENT_ID=? AND LOG_ID=?",
                    OutloggerEntity.class,
                    entity.getDocumentId(),
                    entity.getLogId());
            version = versions.get(0).getVersion() + 1;
            prev.setVersion(version);

            repositoryService.execute(
                    "UPDATE DOCS_OUTLOGGER " +
                    "SET    VERSION=? " +
                    "WHERE  DOCUMENT_ID=? AND LOG_ID=? AND VERSION=0",
                    version, prev.getDocumentId(), prev.getLogId());

            return prev;
        } catch (RepositoryException e) {
            throw new DocumentException(e.getMessage(), e);
        } catch (NotFoundException e) {
            return null;
        } finally {
            LOG.debug(OLOG_0002, "getLog", (System.currentTimeMillis() - st));
        }
    }

    /**
     * 
     * @param documentId
     * @return
     * @throws RepositoryException
     */
    private int getNextLogId(String documentId) throws RepositoryException {
        List<OutloggerEntity> entityList = repositoryService.query(
                "SELECT MAX(LOG_ID) + 1 LOG_ID " +
                "FROM   DOCS_OUTLOGGER " +
                "WHERE  DOCUMENT_ID=?",
                OutloggerEntity.class,
                documentId);
        Integer logId = entityList.get(0).getLogId();
        if (logId == null) {
            logId = 1;
        }
        return logId;
    }

//    /**
//     * 
//     */
//    public void generateSchemaEntity() throws Exception {
//        InputStream is = getClass().getResourceAsStream("Outlogger.dsdl");
//        try {
//            DatabaseService service = DatabaseService.getInstance();
//            DatabaseSchema schema = service.loadSchema(is);
//            service.generateSchemaEntity(schema);
//
//            String ddl = service.generateDDL((DatabaseSchema.Table)schema.getEntiry("DOCS_OUTLOGGER"));
//            System.out.println(ddl);
//        } finally {
//            is.close();
//        }
//    }

//    /**
//     * 
//     */
//    public static void main(String[] args) {
//        try {
//            OutloggerService service = new OutloggerService();
//            service.generateSchemaEntity();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
