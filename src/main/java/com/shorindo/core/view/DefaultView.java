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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.shorindo.core.ActionContext;

/**
 * 
 */
public class DefaultView extends View {
    private static final Logger LOG = Logger.getLogger(DefaultView.class);
    private File file;

    public DefaultView(File file, ActionContext context) {
        super(context);
        this.file = file;
    }

    @Override
    public String getContentType() {
        String ext = file.getName().replaceAll("^.*?(\\.(.+))?$", "$2");
        if ("css".equals(ext)) {
            return "text/css";
        } else if ("js".equals(ext)) {
            return "text/javascript";
        } else {
            return "application/octet-stream";
        }
    }

    @Override
    public InputStream getContent() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

}
