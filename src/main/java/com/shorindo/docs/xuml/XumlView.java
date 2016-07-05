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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
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

import com.shorindo.docs.AbstractView;
import com.shorindo.docs.BeanManager;

/**
 * 
 */
public class XumlView extends AbstractView {
    private static final Logger LOG = Logger.getLogger(XumlView.class);
    private static final Map<String,Class<?>> componentMap = new HashMap<String,Class<?>>();
    private Map<String,Object> dsMap = new HashMap<String,Object>();
    private Component component;

    public static void init(String path) {
        find(new File(path), new File(path));
    }

    private static void defineComponent(Class<?> c) {
        ComponentReady tag = c.getAnnotation(ComponentReady.class);
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

    public XumlView(InputStream is) throws IOException {
        try {
            component = parse(is);
        } catch (SAXException e) {
            throw new IOException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void setData(String dataSource, Object result) {
        dsMap.put(dataSource, result);
    }

    public String getData(String dataSource) {
        return (String)dsMap.get(dataSource);
    }

    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public String getContent() throws IOException {
        return eval(component.getHtml());
    }

    public Component parse(InputStream input) throws SAXException, IOException {
        XumlHandler handler = new XumlHandler(this);
        XMLReader reader = XMLReaderFactory.createXMLReader();
        reader.setContentHandler(handler);
        reader.parse(new InputSource(input));
        return handler.getRoot();
    }

    @SuppressWarnings("unchecked")
    public Component createComponent(String componentName, Attributes attrs) {
        Component component;
        try {
            Class<Component> clazz = (Class<Component>) componentMap.get(componentName);
            Constructor<Component> c = clazz.getConstructor(XumlView.class);
            component = c.newInstance(this);
        } catch (Exception e) {
            component = new GeneralComponent(this, componentName);
        }
        for (int i = 0; i < attrs.getLength(); i++) {
            String name = attrs.getLocalName(i);
            String value = attrs.getValue(i);
            BeanManager.setProperty(component, name, value);
        }
        return component;
    }
    
    private Pattern p1 = Pattern.compile("(\\$|#|@)\\{([^\\.\\}]+?)(\\.(.+?))?\\}");
    protected String eval(String str) {
        if (str == null) {
            return str;
        }
        Matcher m1 = p1.matcher(str);
        int start = 0, end = 0;
        StringBuffer sb = new StringBuffer();
        while (m1.find(start)) {
            if (start < m1.start()) {
                sb.append(str, start, m1.start());
            }
            String beanName = m1.group(2);
            if ("$".equals(m1.group(1))) {
                if (m1.group(3) != null) {
                    sb.append(escape((String)BeanManager.getValue(
                            getAttribute(beanName),
                            m1.group(4),
                            m1.group())));
                } else {
                    sb.append(escape((String)getAttribute(beanName)));
                }
            } else if ("@".equals(m1.group(1))) {
                if (m1.group(3) != null) {
                    sb.append(BeanManager.getValue(
                            getAttribute(beanName),
                            m1.group(4),
                            m1.group()));
                } else {
                    sb.append((String)getAttribute(beanName));
                }   
            } else if ("#".equals(m1.group(1))) {
                //TODO
            }
            start = m1.end();
            end = m1.end();
        }
        if (end < str.length()) {
            sb.append(str, end, str.length() - 1);
        }
        return sb.toString();
    }

    public String escape(String in) {
        return in.toString().replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;");
    }

    public class XumlHandler extends DefaultHandler {
        private Map<String,String> prefixMap = new HashMap<String,String>();
        private XumlView view;
        private Component curr;
        private Component root;

        public XumlHandler(XumlView view) {
            this.view = view;
        }

        public Component getRoot() {
            return root;
        }

        public void startPrefixMapping(String prefix, String uri)
                throws SAXException {
            prefixMap.put(prefix, uri);
        }

        public void startElement(String uri, String localName,
                String qName, Attributes attrs) throws SAXException {
            Component component = createComponent(qName, attrs);
            if (root == null) {
                root = component;
            }
            if (curr == null) {
                curr = component;
            } else {
                curr = curr.add(component);
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
                curr.add(new TextComponent(view, text));
            }
        }
    }
}
