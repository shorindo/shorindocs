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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shorindo.docs.BeanManager;

/**
 * 
 */
public abstract class Component {
    private List<Component> childList;
    private XumlView view;
    private Component parent;

    public abstract String getHtml();

    public Component(XumlView view) {
        childList = new ArrayList<Component>();
        this.view = view;
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
        childList.add(child);
        child.setParent(this);
        return child;
    }

    public List<Component> getChildList() {
        return childList;
    }

//    private Pattern p1 = Pattern.compile("(\\$|#|@)\\{([^\\.\\}]+)(\\.(.+))?\\}");
//    protected String eval(String str) {
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
//            if ("$".equals(m1.group(1))) {
//                String beanName = m1.group(2);
//                sb.append(BeanManager.getValue(
//                        getView().getAttribute(beanName),
//                        m1.group(4),
//                        m1.group()));
//            } else if ("@".equals(m1.group(1))) {
//                //TODO
//            } else if ("#".equals(m1.group(1))) {
//                //TODO
//            }
//            start = m1.end();
//            end = m1.end();
//        }
//        if (end < str.length()) {
//            sb.append(str, end, str.length() - 1);
//        }
//        return sb.toString().replaceAll("&", "&amp;")
//                .replaceAll("<", "&lt;")
//                .replaceAll(">", "&gt;")
//                .replaceAll("\"", "&quot;");
//    }
}
