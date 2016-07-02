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
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.shorindo.docs.AbstractView;
import com.shorindo.docs.xuml.XumlEngine;

/**
 * 
 */
public class XumlView extends AbstractView {
    private static final Logger LOG = Logger.getLogger(XumlView.class);
    private InputStream is;
    private XumlView document;

    public XumlView(InputStream is) {
        this.is = is;
    }

    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    public String getContent() throws IOException {
        try {
            document = new XumlEngine().parse(is);
        } catch (FileNotFoundException e) {
            throw new IOException(e.getMessage());
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
        return document.getHtml();
    }

}
