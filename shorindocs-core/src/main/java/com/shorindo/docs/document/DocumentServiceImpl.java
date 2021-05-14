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
import com.shorindo.docs.auth.entity.UserEntity;
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

    @Override
    @Transactional
    public DocumentModel create(DocumentModel model) {
        try {
//            UserModel user = authenticateService.getUser();
            DocumentEntity entity = new DocumentEntity(model);
            if (entity.getDocumentId() == null) {
                entity.setDocumentId(Long.toString(IdentityManager.newId()));
            }
//            entity.setVersion(-1);
//            entity.setOwnerId(user.getUserId());
//            entity.setCreateUser(user.getUserId());
//            entity.setCreateDate(new java.util.Date());
//            entity.setUpdateUser(user.getUserId());
//            entity.setUpdateDate(new java.util.Date());
//            repositoryService.insert(entity);
//            return repositoryService.get(entity);
            return edit(entity);
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            //throw new DocumentException(DOCS_9999, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public DocumentModel edit(DocumentModel model) throws DocumentException {
        try {
            DocumentEntity document;
            UserModel user = authenticateService.getUser();
            List<DocumentEntity> entityList = repositoryService.queryList(
                "SELECT * " +
                "FROM   DOCS_DOCUMENT " +
                "WHERE  DOCUMENT_ID=? " +
                "AND    VERSION <= 0 " +
                "ORDER  BY VERSION DESC",
                DocumentEntity.class, model.getDocumentId());
            if (entityList.size() == 0) {
                document = new DocumentEntity(model);
                document.setVersion(-1);
                document.setOwnerId(user.getUserId());
                document.setCreateUser(user.getUserId());
                document.setCreateDate(new java.util.Date());
                document.setUpdateUser(user.getUserId());
                document.setUpdateDate(new java.util.Date());
            } else if (entityList.get(0).getVersion() < 0) {
                // ドキュメントなし・誰かが編集中あり
                Optional<DocumentEntity> entity = entityList.stream()
                    .filter(e -> {
                        return e.getVersion() != 0 &&
                            user.getUserId().equals(e.getUpdateUser());
                     })
                    .findFirst();
                if (entity.isEmpty()) {
                    // 自分の編集中なし
                    document = new DocumentEntity(model);
                    DocumentEntity last = entityList.get(entityList.size() - 1);
                    document.setVersion(last.getVersion() - 1);
                } else {
                    // 自分の編集中あり
                    return entity.get();
                }
            } else {
                Optional<DocumentEntity> entity = entityList.stream()
                    .filter(e -> {
                        return e.getVersion() != 0 &&
                            user.getUserId().equals(e.getUpdateUser());
                     })
                    .findFirst();
                if (entity.isEmpty()) {
                    // ドキュメントあり・編集中なし
                    DocumentEntity first = entityList.get(0);
                    DocumentEntity last = entityList.get(entityList.size() - 1);
                    first.setVersion(last.getVersion() - 1);
                    document = first;
                } else {
                    // ドキュメントあり・編集中あり
                    return entity.get();
                }
            }
            document.setUpdateUser(user.getUserId());
            document.setUpdateDate(new java.util.Date());
            repositoryService.insert(document);
            return repositoryService.get(document);
        } catch (RepositoryException e) {
            throw new DocumentException(DOCS_9000, e);
        }
    }

    @Override
    @Transactional
    public DocumentModel commit(String documentId, int version) {
        try {
            UserModel user = authenticateService.getUser();
            DocumentEntity entity = new DocumentEntity();
            entity.setDocumentId(documentId);
            entity.setVersion(version);
            entity = repositoryService.get(entity);
            Optional<DocumentEntity> optEntity = repositoryService.querySingle(
                "SELECT MAX(VERSION) VERSION " +
                "FROM   DOCS_DOCUMENT " +
                "WHERE  DOCUMENT_ID=? " +
                "AND    VERSION >= 0",
                DocumentEntity.class, documentId);
            if (optEntity.isPresent()) {
                int maxVersion = optEntity.get().getVersion();
                repositoryService.execute(
                    "UPDATE DOCS_DOCUMENT " +
                    "SET    VERSION=?, " +
                    "       UPDATE_USER=?, " +
                    "       UPDATE_DATE=? " +
                    "WHERE  DOCUMENT_ID=? AND VERSION=0",
                    maxVersion + 1, user.getUserId(), 
                    new java.util.Date(), documentId);
            }
            repositoryService.execute(
                "UPDATE DOCS_DOCUMENT " +
                "SET    VERSION=0, " +
                "       UPDATE_USER=?, " +
                "       UPDATE_DATE=? " +
                "WHERE  DOCUMENT_ID=? AND VERSION=?",
                user.getUserId(), new java.util.Date(), documentId, version);
            entity.setVersion(0);
            return repositoryService.get(entity);
        } catch (RepositoryException e) {
            throw new RuntimeException(e); // TODO
        }
    }

    @Override
    @Transactional
    public DocumentModel save(DocumentModel model) {
        UserModel user = authenticateService.getUser();
        try {
            repositoryService.execute(
                "UPDATE DOCS_DOCUMENT " +
                "SET    TITLE=?, " +
                "       CONTENT=?, " +
                "       UPDATE_USER=?, " +
                "       UPDATE_DATE=? " +
                "WHERE  DOCUMENT_ID=? AND VERSION=?",
                model.getTitle(), model.getContent(), user.getUserId(),
                new java.util.Date(), model.getDocumentId(),
                ((DocumentEntity)model).getVersion());
            return repositoryService.get(model);
        } catch (RepositoryException e) {
            throw new RuntimeException(e); // TODO
        }

    }

//    @Override
//    @Transactional
//    public DocumentModel commit(String documentId, int version) throws DocumentException {
//        try {
//            UserModel user = authenticateService.getUser();
//            int count = repositoryService.execute(
//                    "UPDATE DOCS_DOCUMENT " +
//                    "SET    VERSION=0, UPDATE_USER=?, UPDATE_DATE=? " +
//                    "WHERE  DOCUMENT_ID=? AND VERSION=?",
//                    DocumentEntity.class, user.getUserId(), new java.util.Date(),
//                    documentId, version);
//            if (count > 0) {
//                DocumentEntity entity = new DocumentEntity();
//                entity.setDocumentId(documentId);
//                entity.setVersion(0);
//                return repositoryService.get(entity);
//            } else {
//                throw new DocumentException(DOCS_9007, documentId);
//            }
//        } catch (RepositoryException e) {
//            throw new DocumentException(DOCS_9007, e, documentId);
//        }
//    }

    @Override
    @Transactional
    public DocumentModel remove(String documentId) {
        // TODO versionを最大値+1とし、version=0をなくすことで削除とする
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

}
