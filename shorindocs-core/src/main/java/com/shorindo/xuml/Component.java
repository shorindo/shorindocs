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

import static com.shorindo.xuml.XumlMessages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.w3c.dom.Node;

import com.shorindo.docs.ActionLogger;

/**
 * 
 */
public abstract class Component {
    private static ActionLogger LOG = ActionLogger.getLogger(Component.class);
    private List<Component> childList;
    private Map<String,String> attrMap;
    private XumlView view;
    private Component parent;
    private Node context;
    private Map<String,String> styles = new HashMap<String,String>();

    public abstract String getHtml();

    public Component(XumlView view) {
        this.view = view;
        this.childList = new ArrayList<Component>();
        this.attrMap = new HashMap<String,String>();
    }

    public Component add(Component child) {
        if (childList.size() > 0) {
            Component last = childList.get(childList.size() - 1);
            if (last instanceof CDATA && child instanceof CDATA) {
                CDATA text = (CDATA)last;
                text.setText(text.getText() + ((CDATA)child).getText());
                //LOG.debug("コンポーネント[CDATA]を追加します。");
                return text;
            }
        }
        //LOG.debug("コンポーネント[" + child + "]を追加します。");
        childList.add(child);
        child.setParent(this);
        return child;
    }

    public Component attr(String name, String value) {
        switch (name) {
        case "id":
        case "class":
        case "width":
        case "height":
        case "font":
        case "color":
        case "background":
        case "if":
        case "iterate":
            attrMap.put(name, value);
            break;
        default:
            LOG.warn(XUML_3002, name);
        }
        return this;
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

    public Node getContext() {
        return context;
    }

    public void setContext(String context) {
        //LOG.debug("setContext(" + view.eval(context) + ")");
    }

    public void setStyle(String style) {
        String[] styles = style.split("\\s*;\\s*");
        for (String part : styles) {
            String[] keyValue = part.split("\\s*:\\s*");
            setStyle(keyValue[0], keyValue[1]);
        }
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + getClass().getSimpleName() + "/>");
        return sb.toString();
    }
}
