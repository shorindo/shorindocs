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
package com.shorindo.docs.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.shorindo.docs.PluginSettings;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.repository.DatabaseSchema;

/**
 * 
 */
public class AdminSettings extends PluginSettings {

    @Override
    public List<ActionController> getControllers() {
        List<ActionController> controllers = new ArrayList<ActionController>();
        controllers.add(new AclController());
        controllers.add(new GroupController());
        controllers.add(new MappingController());
        controllers.add(new UserController());
        return controllers;
    }

    /* (non-Javadoc)
     * @see com.shorindo.docs.PluginSettings#getSchemas()
     */
    @Override
    public List<DatabaseSchema> getSchemas() {
        List<DatabaseSchema> schemaList = new ArrayList<DatabaseSchema>();
        Collections.unmodifiableList(schemaList);
        return schemaList;
    }

}
