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
package com.shorindo.docs.snapboard;

import com.shorindo.docs.ServiceFactory;
import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.document.DocumentController;
import com.shorindo.docs.view.AbstractView;

/**
 * 
 */
public class SnapboardController extends DocumentController {
    private SnapboardService snapboardService =
            ServiceFactory.getService(SnapboardService.class);

    @Override
    public AbstractView action(ActionContext context) {
        return null;
    }

    @ActionMethod
    public void save() {
        snapboardService.save(null);
    }

    @ActionMethod
    public void load() {
        snapboardService.load(null);
    }
}
