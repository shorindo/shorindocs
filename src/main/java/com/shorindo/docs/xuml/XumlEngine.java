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
package com.shorindo.docs.xuml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.shorindo.docs.BeanUtils;

/**
 * 
 */
public class XumlEngine {
    private static final Logger LOG = Logger.getLogger(XumlEngine.class);
    private static Map<String,Class<?>> componentMap = new HashMap<String,Class<?>>();

    public static void main(String[] args) {
        try {
            init("C:/Users/kazm/workspace/shorindocs/target/classes");
            String xml =
                    "<window>" +
                    "  <vbox>" +
                    "    <hbox>" +
                    "      <box><div>side</div></box>" +
                    "      <box><text-viewer data-source=\"${document}\"/></box>" +
                    "    </hbox>" +
                    "  </vbox>" +
                    "</window>";
            XumlEngine engine = new XumlEngine();
            XumlDocument document = engine.parse(new ByteArrayInputStream(xml.getBytes()));
            System.out.println(document.getHtml());
        } catch (SAXException e) {
            LOG.error(e.getMessage(), e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

    }

    public static void init(String path) {
        find(new File(path), new File(path));
    }

    private static void defineComponent(Class<?> c) {
        Componentable tag = c.getAnnotation(Componentable.class);
        if (tag != null && tag.value() != null) {
            componentMap.put(tag.value(), c);
        }
    }

    private static void find(File base, File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                find(base, child);
            }
        } else {
            if (file.getName().endsWith(".class")) {
                String baseURI = base.toURI().toString();
                String fileURI = file.toURI().toString();
                String className = fileURI.substring(baseURI.length())
                        .replaceAll("^(.*?)\\.class$", "$1")
                        .replaceAll("/", ".");
                try {
                    Class<?> c = Class.forName(className);
                    if (Component.class.isAssignableFrom(c)) {
                        defineComponent(c);
                        LOG.info("Component[" + c.getName() + "] loaded.");
                    }
                } catch (ClassNotFoundException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
    }

    public XumlDocument parse(InputStream input) throws SAXException, IOException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        final XumlDocument document = new XumlDocument();

        reader.setContentHandler(new DefaultHandler() {
            private Map<String,String> prefixMap = new HashMap<String,String>();
            private Component curr = document;

            public void startPrefixMapping(String prefix, String uri)
                    throws SAXException {
                prefixMap.put(prefix, uri);
            }

            public void startElement(String uri, String localName,
                    String qName, Attributes attrs) throws SAXException {
                //System.out.println(qName);
                Class<?> comp = componentMap.get(qName);
                if (comp != null) {
                    try {
                        curr = curr.add((Component)comp.newInstance());
                        setProperties(curr, attrs);
                    } catch (InstantiationException e) {
                        LOG.error(e.getMessage(), e);
                    } catch (IllegalAccessException e) {
                        LOG.error(e.getMessage(), e);
                    }
                } else {
                    curr = curr.add(new GeneralComponent(localName));
                }
            }

            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
                curr = curr.getParent();
            }

            @Override
            public void characters(char[] ch, int start, int length)
                    throws SAXException {
                String text = new String(ch, start, length);
                text = text.trim();
                if (text.length() > 0) {
                    //System.out.println("#" + text);
                    curr.add(new TextComponent(text));
                }
            }
        });
        reader.parse(new InputSource(input));
        LOG.info(document.getHtml());
        return document;
    }

    private void setProperties(Object bean, Attributes attrs) {
        for (int i = 0; i < attrs.getLength(); i++) {
            String name = attrs.getQName(i);
            String value = attrs.getValue(i);
            BeanUtils.setProperty(bean, name, value);
        }
    }
}
