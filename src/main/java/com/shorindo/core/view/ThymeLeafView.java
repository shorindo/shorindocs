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
package com.shorindo.core.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.shorindo.core.ActionContext;

/**
 * 
 */
public class ThymeLeafView extends View {
    private static final Logger LOG = Logger.getLogger(ThymeLeafView.class);
    private static TemplateEngine templateEngine;
    private String path;

    private TemplateEngine getTemplateEngine() {
        if (templateEngine == null) {
            templateEngine = new TemplateEngine();
            TemplateResolver resolver = new ClassLoaderTemplateResolver();
            resolver.setTemplateMode("XHTML");
            resolver.setPrefix("");
            resolver.setSuffix(".html");
            templateEngine.setTemplateResolver(resolver);
        }
        return templateEngine;
    }

    public ThymeLeafView(String path, ActionContext context) {
        super(context);
        this.path = path;
    }

    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public InputStream getContent() {
        WebContext ctx = new WebContext(
                context.getRequest(),
                context.getResponse(),
                context.getApplication(),
                context.getRequest().getLocale());
        try {
            
            return new ByteArrayInputStream(getTemplateEngine().process(path, ctx).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
            return new ByteArrayInputStream(new byte[0]);
        }
    }

}
