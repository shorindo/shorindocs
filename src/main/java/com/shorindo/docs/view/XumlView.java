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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.shorindo.docs.AbstractView;
import com.shorindo.docs.xuml.XumlDocument;
import com.shorindo.docs.xuml.XumlEngine;

/**
 * 
 */
public class XumlView extends AbstractView {
    private static String BASE_PATH = "/";
    private static final Logger LOG = Logger.getLogger(XumlView.class);
    private XumlDocument document;

    public static void setBasePath(String path) {
        BASE_PATH = path;
    }

    public XumlView(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(BASE_PATH + path));
            document = new XumlEngine().parse(fis);
        } catch (FileNotFoundException e) {
            LOG.error(e.getMessage(), e);
        } catch (SAXException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
        }
    }

    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    public String getContent() throws IOException {
        return document.getHtml();
    }

}
