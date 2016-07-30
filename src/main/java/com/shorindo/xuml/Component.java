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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

/**
 * 
 */
public abstract class Component {
    private static Logger LOG = Logger.getLogger(Component.class);
    private List<Component> childList;
    private XumlView view;
    private Component parent;
    private Node context;
    private String id;
    private String className;
    private String width;
    private String height;
    private Map<String,String> styles = new HashMap<String,String>();

    public abstract String render();

    public Component(XumlView view) {
        childList = new ArrayList<Component>();
        this.view = view;
    }

    public final String escape(String in) {
        if (in == null) {
            return in;
        } else {
            return in
                    .replaceAll("&", "&amp;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;")
                    .replaceAll("\"", "&quot;");
        }
    }

    public XumlView getView() {
        return view;
    }

    public Component getParent() {
        return parent;
    }

    protected void setParent(Component parent) {
        this.parent = parent;
    }

    public Component add(Component child) {
        if (childList.size() > 0) {
            Component last = childList.get(childList.size() - 1);
            if (last instanceof CDATA && child instanceof CDATA) {
                CDATA text = (CDATA)last;
                text.setText(text.getText() + ((CDATA)child).getText());
                return text;
            }
        }
        childList.add(child);
        child.setParent(this);
        return child;
    }

    public List<Component> getChildList() {
        return childList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
        this.styles.put("width", width);
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
        this.styles.put("height", width);
    }

    public Node getContext() {
        return context;
    }

    public void setContext(String context) {
        LOG.debug("setContext(" + view.eval(context) + ")");
    }

    protected void setStyle(String name, String value) {
        styles.put(name, value);
    }

    protected String getStyles() {
        StringBuffer sb = new StringBuffer();
        for (Entry<String,String> entry : styles.entrySet()) {
            sb.append(entry.getKey() + ":" + entry.getValue() + ";");
        }
        return sb.toString();
    }
}
