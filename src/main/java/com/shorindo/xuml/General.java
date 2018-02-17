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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;

/**
 * 
 */
public class General extends Component {
    private String tagName;
    private Map<String,String> attrMap;

    public General(XumlView view, String tagName, Attributes attrs) {
        super(view);
        this.tagName = tagName;
        this.attrMap = new HashMap<String,String>();
        for (int i = 0; i < attrs.getLength(); i++) {
            attrMap.put(attrs.getQName(i), attrs.getValue(i));
        }
    }

    @Override
    public String getHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<" + tagName);
        for (Entry<String,String> e : attrMap.entrySet()) {
            sb.append(" ");
            sb.append(e.getKey());
            sb.append("=\"");
            sb.append(e.getValue());
            sb.append("\"");
        }
        sb.append(">");
        for (Component c : getChildList()) {
            sb.append(c.getHtml());
        }
        sb.append("</" + tagName + ">");
        return sb.toString();
    }

}
