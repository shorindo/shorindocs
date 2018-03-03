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
package com.shorindo.xuml;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Node;

import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.SystemMessages;

/**
 * 
 */
public class XumlEngine {
    private ActionLogger LOG = ActionLogger.getLogger(XumlEngine.class);
    private Transformer transformer;

    /**
     * 
     */
    public XumlEngine() {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            InputStream is = getClass().getResourceAsStream(getClass().getSimpleName() + ".xsl");
            transformer = factory.newTransformer(new StreamSource(is));
        } catch (TransformerConfigurationException e) {
            LOG.error(SystemMessages.E9999, e);
        }
    }

    public void render(Node xml, OutputStream os) {
        try {
            DOMSource domSource = new DOMSource(xml);
            Result outputTarget = new StreamResult(os);
            transformer.transform(domSource, outputTarget);
        } catch (TransformerException e) {
            LOG.error(SystemMessages.E9999, e);
        }
    }
}
