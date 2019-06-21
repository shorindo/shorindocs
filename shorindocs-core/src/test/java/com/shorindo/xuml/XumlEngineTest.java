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
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

//import nu.validator.htmlparser.dom.HtmlDocumentBuilder;


import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
public class XumlEngineTest {
    ActionLogger LOG = ActionLogger.getLogger(XumlEngineTest.class);

    @Test
    public void testXuml() throws Exception {
        Document actual = render(getMethodName(), "<xuml title='title' style='background:white;'>foo</xuml>", new ScopeDog());
        assertDom(actual, "//head/link/@href", "/css/xuml.css");
        assertDom(actual, "//head/script/@src", "/js/xuml.js");
        assertDom(actual, "/html/head/title/text()", "title");
        assertDom(actual, "/html/body/@style", "background:white;");
    }

    @Test
    public void testXumlWrapper() throws Exception {
        Document actual = render(getMethodName(), "<xuml wrapper=''>foo</xuml>", new ScopeDog());
        assertDom(actual, "/html/body/text()", "foo");
    }

    @Test
    public void testWindow() throws Exception {
        Document actual = render(getMethodName(), "<xuml><window title='window.title' style='color:red;'>foo</window></xuml>", new ScopeDog());
        assertDom(actual, "//div[@class='xuml-window-head']/text()", "window.title");
    }

    @Test
    public void testDialog() throws Exception {
        Document actual = render(getMethodName(), "<xuml><dialog title='{{type}}'></dialog></xuml>", new ScopeDog());
        assertDom(actual, "//div[@class='xuml-dialog-head']/text()", "dog");
    }

    @Test
    public void testMessage() throws Exception {
        Document actual = render(getMethodName(), "<xuml><message>{{bark}}</message></xuml>", new ScopeDog());
        assertDom(actual, "//div[@class='xuml-message']", "bowwow");
    }

    @Test
    public void testListBox() throws Exception {
        Document actual = render(getMethodName(), "<xuml><listbox><invalid>foo</invalid><listitem>A</listitem><listitem>B</listitem></listbox></xuml>", new ScopeDog());
        assertDom(actual, "//ul[@class='xuml-listbox']/li[@class='xuml-listitem' and position()=1]/text()", "A");
        assertDom(actual, "//ul[@class='xuml-listbox']/li[@class='xuml-listitem' and position()=2]/text()", "B");
        assertDom(actual, "//ul[@class='xuml-listbox']/invalid/text()", "");
    }

    @Test
    public void testListBoxInvalid() throws Exception {
        Document actual = render(getMethodName(), "<xuml><listbox><other/></listbox></xuml>", new ScopeDog());
        assertDom(actual, "//ul[@class='xuml-listbox']/other", "");
    }

    @Test
    public void testHtml() throws Exception {
        Document actual = render(getMethodName(), "<xuml xmlns:html='http://www.w3.org/1999/xhtml'><box><html:ol><html:li>bar</html:li></html:ol><br style='fond:arial;'/></box></xuml>", new ScopeDog());
        assertDom(actual, "//ol/li/text()", "bar");
        assertDom(actual, "//br/@style", "");
    }

    /*==========================================================================
     * 
     */
    private Document render(String id, String source, Object scope) throws Exception {
        OutputStream bos = new ByteArrayOutputStream();
        XumlEngine engine = new XumlEngine(new ByteArrayInputStream(source.getBytes()));
        engine.render(scope, bos);
        Document document = parseHtml(bos.toString());
        printDom(document);
        return document;
    }

    private Document parseHtml(String html) throws SAXException, IOException {
//        HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
//        Reader reader = new StringReader(html);
//        try {
//            return builder.parse(new InputSource(reader));
//        } finally {
//            reader.close();
//        }
        return null;
    }

    private String getMethodName() {
        StackTraceElement[] e = new Exception().getStackTrace();
        return e[1].getMethodName();
    }

    private void printDom(Document document) throws TransformerException {
        StringWriter writer = new StringWriter();
        TransformerFactory factory = TransformerFactory.newInstance(); 
        Transformer transformer = factory.newTransformer(); 

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

        transformer.transform(new DOMSource(document), new StreamResult(writer)); 
        LOG.debug(writer.toString());
    }

    private void assertDom(Document document, String path, String expect) throws XPathExpressionException, TransformerException, ParserConfigurationException {
        document = cloneDocument(document);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        assertEquals(expect, xpath.evaluate(path, document));
    }

    private Document cloneDocument(Document document) throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document newDocument = builder.newDocument();
        newDocument.appendChild(cloneNode(newDocument, document.getDocumentElement()));
        return newDocument;
    }
    
    private Node cloneNode(Document owner, Node node) {
        switch (node.getNodeType()) {
        case Node.ELEMENT_NODE:
            Element element = owner.createElement(node.getNodeName());
            NamedNodeMap attrs = node.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Node attr = attrs.item(i);
                element.setAttribute(attr.getNodeName(), attr.getNodeValue());
            }
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                element.appendChild(cloneNode(owner, child));
            }
            return element;

        case Node.TEXT_NODE:
            Text textNode = owner.createTextNode(node.getNodeValue());
            return textNode;
        }

        return null;
    }

    public static class ScopeDog {
        private String type = "dog";
        private String bark = "bowwow";

        public String getType() {
            return type;
        }
        public String getBark() {
            return bark;
        }
    }
}
