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

import static com.shorindo.xuml.XumlMessages.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.shorindo.docs.ActionLogger;

/**
 * 
 */
public class XumlEngine {
    private static ActionLogger LOG = ActionLogger.getLogger(XumlEngine.class);
    private String template;
    private Mustache mustache;
    public static final String POSTFIX = ".xuml";

    public static XumlExecutor compile(Class<?> clazz) throws XumlException {
        String name = clazz.getName().replaceAll("\\.", "/") + POSTFIX;
        InputStream is = clazz.getClassLoader().getResourceAsStream(name);
        try {
            return compile(name, is);
        } finally {
            close(name, is);
        }
    }

    public static XumlExecutor compile(String name, InputStream is) throws XumlException {
        return null;
    }

    private static void close(String name, InputStream is) {
        if (is != null)
            try {
                is.close();
            } catch (IOException e) {
                LOG.warn(XUML_3001, name);
            }
    }

    /**
     * 1. XSLTをセットアップ
     * 2. XUMLをHTMLに変換
     * 3. mustacheコンパイル
     * @throws XumlException 
     */
    public XumlEngine(InputStream is) throws XumlException {
        String fileName = getClass().getSimpleName() + ".xsl";
        InputStream xsltis = null;
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            xsltis = getClass().getResourceAsStream(fileName);
            Transformer transformer = factory.newTransformer(new StreamSource(xsltis));
            xsltis.close();

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xuml = builder.parse(is);
            if (!"xuml".equals(xuml.getDocumentElement().getNodeName())) {
                throw new XumlException(XUML_5030);
            }
            xuml = applyWrapper(xuml);
            //printDom(xuml);

            Source source = new DOMSource(xuml);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Result result = new StreamResult(baos);
            transformer.transform(source, result);
            baos.close();
            template = baos.toString("UTF-8");

            StringReader reader = new StringReader(template);
            MustacheFactory mf = new DefaultMustacheFactory();
            mustache = mf.compile(reader, "xuml-template");
            reader.close();
        } catch (TransformerException e) {
            LOG.error(XUML_5010, e);
        } catch (UnsupportedEncodingException e) {
            LOG.error(XUML_5010, e);
        } catch (ParserConfigurationException e) {
            LOG.error(XUML_5010, e);
        } catch (SAXException e) {
            LOG.error(XUML_5010, e);
        } catch (IOException e) {
            LOG.error(XUML_5010, e);
        }
    }

    private void printDom(Document document) throws TransformerException {
        StringWriter writer = new StringWriter();
        TransformerFactory factory = TransformerFactory.newInstance(); 
        Transformer transformer = factory.newTransformer(); 

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

        transformer.transform(new DOMSource(document), new StreamResult(writer)); 
        //LOG.debug(writer.toString());
    }

    public void render(Object scope, OutputStream os) throws IOException {
        Writer writer = new OutputStreamWriter(os);
        mustache.execute(writer, scope).flush();
    }

    /**
     * 
     * @param child
     * @return
     */
    private Document applyWrapper(Document child) {
        Node root = child.getDocumentElement();
        if (root.hasAttributes()) {
            Node outer = root.getAttributes().getNamedItem("outer");
            if (outer == null) {
                return child;
            } else {
                String outerId = outer.getNodeValue();
                return null; //TODO
            }
        } else {
            return child;
        }
    }
}
