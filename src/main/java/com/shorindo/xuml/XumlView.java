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
package com.shorindo.xuml;

import static com.shorindo.docs.DocumentMessages.*;
import static com.shorindo.xuml.XumlMessages.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.shorindo.docs.ActionContext;
import com.shorindo.docs.ActionLogger;
import com.shorindo.docs.BeanUtil;
import com.shorindo.docs.LapCounter;
//import com.shorindo.docs.DocsMessages;
import com.shorindo.docs.view.View;

/**
 * 
 */
public class XumlView extends View {
    private static final ActionLogger LOG = ActionLogger.getLogger(XumlView.class);
    private static final Map<String,Class<?>> componentMap = new HashMap<String,Class<?>>();
    private String name;
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
                        LOG.info(XUML_1000, c.getName());
                    }
                } catch (ClassNotFoundException e) {
                    LOG.error(XUML_5001, e, className);
                }
            }
        }
    }

    public XumlView(String name, InputStream is) {
        LapCounter lap = new LapCounter();
        init();
        this.name = name;
        LOG.debug(DOCS_1109, name);
        try {
            component = parse(is);
        } catch (SAXException e) {
            LOG.error(DOCS_9999, e);
        } catch (IOException e) {
            LOG.error(DOCS_9999, e);
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                LOG.error(DOCS_9999, e);
            }
        }
        LOG.debug(DOCS_1110, name, lap.elapsed());
    }

    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }

    @Override
    public void render(ActionContext context, OutputStream os) {
        long st = System.currentTimeMillis();
        LOG.debug(XUML_1001, name);
        try {
            os.write(evalMustache(context, component.getHtml()).getBytes("UTF-8"));
        } catch (Exception e) {
            LOG.error(DOCS_9999, e);
        }
        LOG.debug(XUML_1002, name, System.currentTimeMillis() - st);
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
        //LOG.debug("コンポーネント[" + componentName  +"]を生成します。");
        Component component;
        Class<Component> clazz = (Class<Component>) componentMap.get(componentName);
        if (clazz != null) {
            try {
                Constructor<Component> c = clazz.getConstructor(XumlView.class);
                component = c.newInstance(this);
                for (int i = 0; i < attrs.getLength(); i++) {
                    String name = attrs.getLocalName(i);
                    String value = attrs.getValue(i);
                    BeanUtil.setValue(component, name, value);
                }
            } catch (Exception e) {
                LOG.error(XUML_5125, e, componentName);
                component = new General(this, componentName, attrs);
            }
        } else {
            component = new General(this, componentName, attrs);
        }
        return component;
    }
    
    protected String evalMustache(ActionContext context, String template) {
        //LOG.debug("evalMustache(" + template + ")");
        StringReader reader = new StringReader(template);
        StringWriter writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(reader, "xuml-template");
        mustache.execute(writer, context.getAttributes());
        return writer.toString();
    }

//    private Pattern p1 = Pattern.compile("(\\$|#|@)\\{([^\\.\\}]+?)(\\.(.+?))?\\}");
//    protected String eval(ActionContext context, String str) {
//        if (str == null) {
//            return str;
//        }
//        Matcher m1 = p1.matcher(str);
//        int start = 0, end = 0;
//        StringBuffer sb = new StringBuffer();
//        while (m1.find(start)) {
//            if (start < m1.start()) {
//                sb.append(str, start, m1.start());
//            }
//            String beanName = m1.group(2);
//            if ("$".equals(m1.group(1))) {
//                if (m1.group(3) != null) {
//                    sb.append(escape((String)BeanUtil.getValue(
//                            context.getAttribute(beanName),
//                            m1.group(4),
//                            m1.group())));
//                } else {
//                    sb.append(escape(String.valueOf(context.getAttribute(beanName))));
//                }
//            } else if ("@".equals(m1.group(1))) {
//                if (m1.group(3) != null) {
//                    sb.append(BeanUtil.getValue(
//                            context.getAttribute(beanName),
//                            m1.group(4),
//                            m1.group()));
//                } else {
//                    sb.append((String)context.getAttribute(beanName));
//                }   
//            } else if ("#".equals(m1.group(1))) {
//                sb.append(context.getMessage(m1.group(2) + m1.group(3)));
//            }
//            start = m1.end();
//            end = m1.end();
//        }
//        if (end < str.length()) {
//            sb.append(str, end, str.length() - 1);
//        }
//        return sb.toString();
//    }

    public String escape(String in) {
        if (in == null) {
            return in;
        } else {
            return in.toString().replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;");
        }
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
                curr.add(new CDATA(view, text));
            }
        }
    }
}
