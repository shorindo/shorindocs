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
package com.shorindo.docs;

import java.util.ArrayList;
import java.util.List;

import com.shorindo.docs.repository.DatabaseSchema;

/**
 * 
 */
public abstract class PluginSettings {
    private static List<PluginSettings> settingsList
        = new ArrayList<PluginSettings>();
    public PluginSettings() {}
    public abstract List<ActionController> getControllers();
    public abstract List<DatabaseSchema> getSchemas();

    public static void add(Class<? extends PluginSettings> settings) {
        try {
            settingsList.add(settings.newInstance());
//          ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
//          if (mapping != null && ActionController.class.isAssignableFrom(clazz)) {
//              LOG.info(DOCS_0001, mapping.value(), clazz);
//              try {
//                  actionMap.put(mapping.value(), (ActionController)clazz.newInstance());
//              } catch (InstantiationException e) {
//                  LOG.error(DOCS_9004, e, mapping.value());
//              } catch (IllegalAccessException e) {
//                  LOG.error(DOCS_9004, e, mapping.value());
//              }
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
