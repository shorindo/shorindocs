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

import static com.shorindo.docs.document.DocumentMessages.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shorindo.docs.IdentityManager;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.auth.AuthenticateService;
import com.shorindo.docs.model.DocumentModel;
import com.shorindo.docs.model.UserModel;
import com.shorindo.docs.repository.RepositoryException;
import com.shorindo.docs.repository.DatabaseSchema;
import com.shorindo.docs.repository.RepositoryService;
import com.shorindo.docs.repository.Transactional;

/**
 * 
 */
public class DocumentServiceImpl implements DocumentService {
    private static ActionLogger LOG = ActionLogger.getLogger(DocumentServiceImpl.class);
    protected AuthenticateService authenticateService;
    protected RepositoryService repositoryService;

    /**
     * 
     */
    public DocumentServiceImpl(
            AuthenticateService authenticateService,
            RepositoryService repositoryService) {
        this.authenticateService = authenticateService;
        this.repositoryService = repositoryService;
        //FIXME 起動時には初期化されないのでここじゃない this.validate();
    }

    public void validate() {
        String dsdlName = "Document.dsdl";
        LOG.debug(dsdlName);
        InputStream is = getClass().getResourceAsStream(dsdlName);
        if (is == null) {
            throw new RuntimeException(DOCS_9005.getMessage(dsdlName));
        }
        try {
            DatabaseSchema schema = repositoryService.loadSchema(is);
            repositoryService.validateSchema(schema);
        } catch (Exception e) {
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

//    public void listDocType() {
//        for (Entry<String,ApplicationContext> entry : ApplicationContext.getContextMap().entrySet()) {
//            //entry.getValue();
//        }
//    }

    @Override
    public DocumentModel load(String documentId) {
        return load(documentId, 0);
    }

    public DocumentModel load(String documentId, int version) {
        try {
            DocumentEntity entity = new DocumentEntity();
            entity.setDocumentId(documentId);
            entity.setVersion(version);
            return repositoryService.get(entity);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public DocumentModel create(String docType) throws RepositoryException {
        UserModel user = authenticateService.getUser();
        DocumentEntity entity = new DocumentEntity();
        entity.setDocumentId(Long.toString(IdentityManager.newId()));
        entity.setController(docType);
        entity.setNamespace(docType);
        entity.setVersion(-1);
        entity.setTitle("");
        entity.setOwnerId(user.getUserId());
        entity.setCreateUser(user.getUserId());
        entity.setCreateDate(new java.util.Date());
        entity.setUpdateUser(user.getUserId());
        entity.setUpdateDate(new java.util.Date());
        repositoryService.insert(entity);
        return load(entity.getDocumentId(), entity.getVersion());
    }

    @Transactional
    public DocumentModel save(DocumentModel model) {
        try {
            UserModel user = authenticateService.getUser();
            DocumentEntity entity = new DocumentEntity(model);
            Optional<DocumentEntity> prev = repositoryService.querySingle(
                    "SELECT * " +
                    "FROM   DOCS_DOCUMENT " +
                    "WHERE  DOCUMENT_ID=? AND VERSION=0",
                    DocumentEntity.class,
                    model.getDocumentId());
            entity.setOwnerId(user.getUserId());
            entity.setCreateUser(user.getUserId());
            entity.setCreateDate(new java.util.Date());
            entity.setUpdateUser(user.getUserId());
            entity.setUpdateDate(new java.util.Date());
            if (prev.isPresent()) {
                List<DocumentEntity> entityList = repositoryService.queryList(
                        "SELECT MAX(VERSION) VERSION " +
                        "FROM   DOCS_DOCUMENT " +
                        "WHERE  DOCUMENT_ID=?",
                        DocumentEntity.class,
                        model.getDocumentId());
                int version = entityList.get(0).getVersion();
                repositoryService.execute(
                        "UPDATE DOCS_DOCUMENT " +
                        "SET    VERSION=?, " +
                        "       UPDATE_USER=?," +
                        "       UPDATE_DATE=?" +
                        "WHERE  DOCUMENT_ID=? AND VERSION=0",
                        version + 1, prev.get().getDocumentId(),
                        user.getUserId(), new java.util.Date());
            }
            repositoryService.insert(entity);
            return repositoryService.get(entity);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public DocumentModel remove(String documentId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DocumentModel> recents(String documentId) {
      try {
        return repositoryService.queryList(
                  "SELECT document_id,title,update_date " +
                  "FROM   docs_document " +
                  "WHERE  version=0 " +
                  "ORDER  BY update_date DESC " +
                  "LIMIT  20",
                  DocumentEntity.class)
              .stream()
              .map(mapper -> {
                  return (DocumentModel)mapper;
              })
              .collect(Collectors.toList());
      } catch (RepositoryException e) {
          throw new RuntimeException(e);
      }
    }

//    @Override
//    public DocumentEntity newDocument() {
//        // TODO Auto-generated method stub
//        return null;
//    }

}
