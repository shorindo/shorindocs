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
package com.shorindo.docs.view;

import static com.shorindo.docs.DocumentMessages.DOCS_9999;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.shorindo.docs.ActionContext;
import com.shorindo.docs.ActionLogger;
import com.shorindo.xuml.XumlView;

/**
 * 
 */
public class ErrorView extends View {
    private static final ActionLogger LOG = ActionLogger.getLogger(ErrorView.class);

    public ErrorView(int status) {
        init();
        setStatus(status);
    }

    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    /**
     *
     */
    @Override
    public void render(ActionContext context, OutputStream os) {
        context.setAttribute("status", getStatus());
        context.setAttribute("message", context.getMessage("error." + getStatus()));
        InputStream is = getClass().getClassLoader().getResourceAsStream("xuml/error.xuml");
        try {
            new XumlView(String.valueOf(getStatus()), is).render(context, os);
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}
