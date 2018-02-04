/*
 * Copyright 2016 Shorindo, Inc.
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
package com.shorindo.auth;

import com.shorindo.core.ActionContext;
import com.shorindo.core.ActionController;
import com.shorindo.core.annotation.ActionMapping;
import com.shorindo.core.view.View;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
@ActionMapping("/login")
public class LoginController extends ActionController {

    public LoginController() {
    }

    @Override
    public View view(ActionContext context) {
        return new XumlView(context, createClassPath("xuml/login.xuml"));
    }

}
