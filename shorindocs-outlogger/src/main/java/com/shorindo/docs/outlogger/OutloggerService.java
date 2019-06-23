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

import java.util.List;

import com.shorindo.docs.document.DocumentException;
import com.shorindo.docs.repository.RepositoryException;

/**
 * 
 */
public interface OutloggerService {
    public void createSchema() throws RepositoryException;

    /**
     * メタデータを生成する
     * @return　メタデータ(XML)
     */
    public String createMetaData();
    public void registMetaData();
    public void removeMetaData();
    public void commitMetaData();
    public void rollbackMetaData();

    /**=========================================================================
     * ドキュメントのアクセス権を操作する。
     */
    public void listAcl();
    public void addAcl();
    public void removeAcl();

    /**=========================================================================
     * アウトラインを操作する。
     */
    public List<OutloggerEntity> listLog(OutloggerEntity entity) throws DocumentException;
    public OutloggerEntity putLog(OutloggerEntity entity) throws DocumentException;
    public OutloggerEntity getLog(OutloggerEntity entity) throws DocumentException;
    public OutloggerEntity removeLog(OutloggerEntity entity) throws DocumentException;
}
