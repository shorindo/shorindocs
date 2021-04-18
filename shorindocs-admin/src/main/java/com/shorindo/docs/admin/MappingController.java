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
package com.shorindo.docs.admin;

import static com.shorindo.docs.document.DocumentMessages.DOCS_9999;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionController;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.view.View;

/**
 * 
 */
@ActionMapping("/admin/mapping")
public class MappingController extends ActionController {
    private static final ActionLogger LOG = ActionLogger.getLogger(MappingController.class);

    /**
     *
     */
    @Override
    public View action(ActionContext context, Object...args) {
        return null;
    }

}
