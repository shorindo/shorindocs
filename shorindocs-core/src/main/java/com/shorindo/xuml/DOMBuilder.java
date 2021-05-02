/*
 * Copyright 2020 Shorindo, Inc.
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.shorindo.docs.action.ActionLogger;
import com.shorindo.xuml.CSSSelector.CSSException;

/**
 * 
 */
public abstract class DOMBuilder {
    private static String encoding = "UTF-8";
    private static ActionLogger LOG = ActionLogger.getLogger(DOMBuilder.class);
        
    public static Element document() {
        return new DocumentElement();
    }

    public static Element cdata(String text) {
        return new CDATA(text);
    }

    public static Element text(String text, Object...args) {
        return new TextElement(text, args);
    }

    public static Element marker(String name) {
        return new MarkerElement(name);
    }

    protected static String getEncoding() {
        return encoding;
    }

    public static abstract class Element {
        private String tagName;
        private Element parent;
        private Map<String,String> attrs;
        private List<Element> childList;
        private Map<String,List<Predicate<Event>>> eventHandler;

        protected Element(String name) {
            this.tagName = name;
            this.attrs = new LinkedHashMap<>();
            this.childList = new ArrayList<>();
        }

        public final String getTagName() {
            return tagName;
        }

        private void setParent(Element parent) {
            this.parent = parent;
        }

        public final Element attr(String name, String value) {
            attrs.put(name, value);
            return this;
        }

        public final Element attr(String name) {
            attr(name, null);
            return this;
        }
  
        public final Element add(Element child) {
            if (child != null) {
                childList.add(child);
                child.setParent(this);
            }
            return this;
        }

        public final Element add(String text, Object...args) {
            return add(new TextElement(text, args));
        }

        public final <P> Element eval(P param, BiConsumer<Element,P> action) {
            action.accept(this, param);
            return this;
        }

        public final Element put(String name, Element...elements) {
            for (Element marker : this.findByCssSelector("marker[name='" + name + "']")) {
                //LOG.debug("marker[name=" + marker.getAttr("name") + "] <- " + name);
                for (Element element : elements) {
                    marker.add(element);
                }
            }
            return this;
        }

        public final Element on(String eventType, Predicate<Event> action) {
            if (eventHandler == null) {
                eventHandler = new HashMap<>();
            }
            List<Predicate<Event>> handlers = eventHandler.get(eventType);
            if (handlers == null) {
                handlers = new ArrayList<>();
                eventHandler.put(eventType, handlers);
            }
            handlers.add(action);
            return this;
        }
        
        public final Map<String,String> getAttrs() {
            return attrs;
        }

        public final String getAttr(String name) {
            return attrs.get(name);
        }
        
        public final List<Element> getChildList() {
            return childList;
        }
        
        public List<Element> findById(String id) {
            List<Element> resultList = new ArrayList<>(); 
            if (id.equals(this.getAttr("id"))) {
                resultList.add(this);
            }
            for (Element child : getChildList()) {
                resultList.addAll(child.findById(id));
            }
            return resultList;
        }

        public List<Element> findByTagName(String tagName) {
            List<Element> resultList = new ArrayList<>(); 
            if (getTagName().equals(tagName)) {
                resultList.add(this);
            }
            for (Element child : getChildList()) {
                resultList.addAll(child.findByTagName(tagName));
            }
            return resultList;
        }

        public List<Element> findByCssSelector(String text) {
            List<Element> resultList = new ArrayList<>();
            //long st = System.currentTimeMillis();
            try {
                List<List<CSSSelector>> list = CSSSelector.parse(text);
                for (List<CSSSelector> groupList : list) {
                    List<Element> filteredResult = new ArrayList<>();
                    filteredResult.add(this);
                    for (CSSSelector selector : groupList) {
                        filteredResult = filteredResult.stream()
                            .flatMap(e -> {
                                return e.findByCssSelector(selector).stream();
                            })
                            .collect(Collectors.toList());
                    }
                    resultList.addAll(filteredResult);
                }
            } catch (CSSException e) {
                LOG.error(e.getMessage(), e, text);
            } finally {
                //LOG.debug("findByCssSelector({0}) => count:{1} time:{2}ms",
                //    text, resultList.size(), (System.currentTimeMillis() - st));
            }
            return resultList;
        }
        
        private List<Element> findByCssSelector(CSSSelector selector) {
            List<Element> result = new ArrayList<>();
            switch (selector.getCombinator()) {
            case DESCENDANT:
                for (Element child : getChildList()) {
                    if (selector.match(child)) {
                        result.add(child);
                    }
                    result.addAll(child.findByCssSelector(selector));
                }
                break;
            case CHILD:
                for (Element child : getChildList()) {
                    if (selector.match(child)) {
                        result.add(child);
                    }
                }
                break;
            case SIBLING:
                Element parent = this.parent;
                for (int i = 0; i < parent.getChildList().size() - 1; i++) {
                    Element curr = parent.childList.get(i);
                    Element next = parent.childList.get(i + 1);
                    if (curr == this) {
                        OUT:for (int j = i + 1; j < parent.childList.size(); j++) {
                            next = parent.childList.get(j);
                            if (selector.match(next)) {
                                result.add(next);
                                break OUT;
                            }
                        }
                        
                    }
                }
                break;
            case ADJACENT:
                parent = this.parent;
                for (int i = 0; i < parent.getChildList().size() - 1; i++) {
                    Element curr = parent.childList.get(i);
                    Element next = parent.childList.get(i + 1);
                    if (curr == this && selector.match(next)) {
                        result.add(next);
                        break;
                    }
                }
                break;
            }
            return result;
        }
        
        public void render(OutputStream os) throws IOException {
            render(os, false, 0);
        }

        private boolean doEvent(String eventType) {
            if (eventHandler == null) {
                return true;
            }
            List<Predicate<Event>> handlerList = eventHandler.get(eventType);
            if (handlerList == null) {
                return true;
            }
            Event event = new Event(eventType, this);
            for (Predicate<Event> p : handlerList) {
                if (!p.test(event)) {
                    return false;
                }
            }
            return true;
        }

        protected void render(OutputStream os, boolean useIndent, int level) throws IOException {
            if (!doEvent("RENDER_BEFORE")) {
                return;
            }
            if ("#text".equals(getTagName())) {
                StringBuffer sb = new StringBuffer();
                sb.append(indent(useIndent, level))
                    .append(toString())
                    .append(lf(useIndent));
                os.write(sb.toString().getBytes(getEncoding()));
                return;
            }
            StringBuffer sb = new StringBuffer(indent(useIndent, level) + "<" + getTagName());
            for (Entry<String,String> attr : attrs.entrySet()) {
                if (attr.getValue() != null) {
                    sb.append(" " + attr.getKey() + "=\"" + attr.getValue() + "\"");
                } else {
                    sb.append(" " + attr.getKey());
                }
            }
            if (childList.size() == 1 && "#text".equals(childList.get(0).getTagName())) {
                sb.append(">");
                sb.append(childList.get(0).toString());
                sb.append("</" + getTagName() + ">");
                sb.append(lf(useIndent));
                os.write(sb.toString().getBytes(getEncoding()));
            } else if (childList.size() > 0) {
                sb.append(">");
                sb.append(lf(useIndent));
                os.write(sb.toString().getBytes(getEncoding()));
                for (Element child : childList) {
                    //child.render(os, useIndent, level + 1);
                    child.render(os);
                }
                sb = new StringBuffer();
                sb.append(indent(useIndent, level) + "</" + getTagName() + ">");
                sb.append(lf(useIndent));
                os.write(sb.toString().getBytes(getEncoding()));
            } else {
                sb.append("/>");
                sb.append(lf(useIndent));
                os.write(sb.toString().getBytes(getEncoding()));
            }
            doEvent("RENDER_AFTER");
        }
        
        protected String lf(boolean useIndent) {
            return useIndent ? "\n" : "";
        }

        protected String indent(boolean useIndent, int level) {
            if (!useIndent) return "";
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < level; i++) {
                sb.append("  ");
            }
            return sb.toString();
        }

        public String toString() {
            return tagName;
        }
    }

    public static String escape(String s) {
        if (s == null) return null;
        return s.replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }

    protected static class DocumentElement extends Element {
        public static final String TAG = "#document";
        public DocumentElement() {
            super(TAG);
        }
        @Override
        public void render(OutputStream os, boolean useIndent, int level) throws IOException {
            os.write("<!doctype html>".getBytes());
            for (Element child : getChildList()) {
                child.render(os);
            }
        }
    }

    protected static class CDATA extends Element {
        public static final String TAG = "#CDATA";
        private String text;

        protected CDATA(String text) {
            super(TAG);
            this.text = text;
        }

        @Override
        public void render(OutputStream os, boolean useIndent, int level)
            throws IOException {
            StringBuffer sb = new StringBuffer(indent(useIndent, level));
            sb.append(text);
            os.write(sb.toString().getBytes(getEncoding()));
        }

        public String toString() {
            return text;
        }
    }

    protected static class TextElement extends Element {
        public static final String TAG = "#text";
        private String text;

        protected TextElement(String text, Object...args) {
            super(TAG);
            this.text = text == null ? "" : escape(String.format(text, args));
        }

        @Override
        public void render(OutputStream os, boolean useIndent, int level)
            throws IOException {
            StringBuffer sb = new StringBuffer(indent(useIndent, level));
            sb.append(text);
            sb.append(lf(useIndent));
            os.write(sb.toString().getBytes(getEncoding()));
        }

        public String toString() {
            return text;
        }
    }

    protected static class MarkerElement extends Element {
        public static final String TAG = "marker";

        protected MarkerElement(String name) {
            super(TAG);
            attr("name", name);
        }

        @Override
        public void render(OutputStream os, boolean useIndent, int level)
            throws IOException {
            for (Element child : getChildList()) {
                child.render(os);
            }
        }
    }

    public enum EventType {
        CLICK,
        CHANGE,
        RENDER_BEFORE,
        RENDER_AFTER;
    }

    public static class Event {
        String eventType;
        Element target;

        public Event(String eventType, Element target) {
            this.eventType = eventType;
            this.target = target;
        }

        public String getEventType() {
            return eventType;
        }

        public Element getTarget() {
            return target;
        }
    }
}
