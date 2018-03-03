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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * 
 */
public class XumlEngineTest {

    @Test
    public void testWindow() throws Exception {
        OutputStream bos = new ByteArrayOutputStream();
        XumlEngine engine = new XumlEngine();
        engine.render(parse("<window style='color:red;'>foo</window>"), bos);
        String actual = bos.toString();
    }

    @Test
    public void testDialog() throws Exception {
        XumlEngine engine = new XumlEngine();
        engine.render(parse("<dialog><b>foo</b></dialog>"), System.out);
    }

    @Test
    public void testMessage() throws Exception {
        XumlEngine engine = new XumlEngine();
        engine.render(parse("<xuml><message/></xuml>"), System.out);
        engine.render(parse("<message/>"), System.out);
    }

    private Document parse(String xml) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        return builder.parse(is);
    }

    private void assertDom(Node node, String path, String expect) throws XPathExpressionException {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        assertEquals(expect, xpath.evaluate(path, node));
    }
}
