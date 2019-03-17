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
package com.shorindo.docs.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shorindo.docs.entity.DocumentEntity;
import com.shorindo.docs.entity.UserEntity;
import com.shorindo.docs.repository.DocumentRepository;

/**
 * 
 */
@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository repository;

    @Override
    public void put(UserEntity context, DocumentEntity entity) {
        // TODO Auto-generated method stub
        repository.save(entity);
    }

    @Override
    public DocumentEntity get(UserEntity user, String docId) {
        // TODO Auto-generated method stub
        return repository.getOne(docId);
    }

    @Override
    public void remove(UserEntity user, String docId) {
        // TODO Auto-generated method stub
        repository.deleteById(docId);
    }

    @Override
    public List<DocumentEntity> search(UserEntity user) {
        // TODO Auto-generated method stub
        return repository.findAll();
    }
}
