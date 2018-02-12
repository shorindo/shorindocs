/*
 * Copyright 2016-2018 Shorindo, Inc.
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
package com.shorindo.docs.auth;

import com.shorindo.docs.ActionContext;
import com.shorindo.docs.ActionController;
import com.shorindo.docs.annotation.ActionMapping;
import com.shorindo.docs.annotation.ActionMethod;
import com.shorindo.docs.view.RedirectView;
import com.shorindo.docs.view.View;

/**
 * 
 */
@ActionMapping("/logout")
public class LogoutController extends ActionController {

    public LogoutController() {
    }

    @Override @ActionMethod
    public View view(ActionContext context) {
        return new RedirectView("/", context);
    }

}
