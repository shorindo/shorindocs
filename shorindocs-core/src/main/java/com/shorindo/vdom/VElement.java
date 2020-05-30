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
package com.shorindo.vdom;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 */
public class VElement {
    private String name;
    private Map<String,String> attrs;
    private List<VElement> childList;

    public static VElement create(Document document) {
        return null;
    }

    public static VElement create(InputStream xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return create(builder.parse(xml));
    }
    
    public static VElement create(String name) {
        return new VElement(name);
    }

    private VElement(String name) {
        this.name = name;
        this.attrs = new LinkedHashMap<>();
        this.childList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    public void setAttr(String name, String value) {
        this.attrs.put(name, value);
    }

    public List<VElement> getChildList() {
        return childList;
    }

    public void setChildList(List<VElement> childList) {
        this.childList = childList;
    }
    
    public String getXml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + getName());
        for (Entry<String,String> entry : attrs.entrySet()) {
            sb.append(" " + entry.getKey() + "=\"" + entry.getValue() + "\"");
        }
        if (childList.size() == 0) {
            sb.append("/>");
        } else {
            for (VElement el : childList) {
                sb.append(el.getXml());
            }
            sb.append("</" + getName() + ">");
        }
        return sb.toString();
    }
}
