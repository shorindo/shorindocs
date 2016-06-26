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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 */
public class XumlEngine {
    private static Map<String,Class<?>> componentMap = new HashMap<String,Class<?>>();

    public static void main(String[] args) {
        try {
            init("C:/Users/kazm/workspace/shorindocs/target/classes");
//            defineComponent(WindowComponent.class);
//            defineComponent(BoxComponent.class);
//            defineComponent(HBoxComponent.class);
//            defineComponent(VBoxComponent.class);

            String xml =
                    "<window xmlns:html='http://www.w3.org/1999/xhtml'>" +
                    "  <vbox>" +
                    "    <hbox>" +
                    "      <box><html:div>side</html:div></box>" +
                    "      <box><text-viewer data-source=\"aaa\"/></box>" +
                    "    </hbox>" +
                    "  </vbox>" +
                    "</window>";
            XumlEngine engine = new XumlEngine();
            engine.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void init(String path) {
        find(new File(path), new File(path));
    }

    private static void defineComponent(Class<?> c) {
        Tag tag = c.getAnnotation(Tag.class);
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
                        System.out.println("class:" + c.getName());
                        defineComponent(c);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void parse(InputStream input) throws SAXException, IOException {
        XMLReader reader = XMLReaderFactory.createXMLReader();
        final DocumentComponent document = new DocumentComponent();

        reader.setContentHandler(new DefaultHandler() {
            private Map<String,String> prefixMap = new HashMap<String,String>();
            private Component curr = document;

            public void startPrefixMapping(String prefix, String uri)
                    throws SAXException {
                prefixMap.put(prefix, uri);
            }

            public void startElement(String uri, String localName,
                    String qName, Attributes atts) throws SAXException {
                System.out.println(qName);
                Class<?> comp = componentMap.get(qName);
                if (comp != null) {
                    try {
                        curr = curr.add((Component)comp.newInstance());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    curr = curr.add(new HtmlComponent(localName));
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
                    System.out.println("#" + text);
                    curr.add(new TextComponent(text));
                }
            }
        });
        reader.parse(new InputSource(input));
        System.out.println(document.getHtml());
    }

}
