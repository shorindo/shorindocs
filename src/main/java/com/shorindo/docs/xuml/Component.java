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

/**
 * 
 */
public abstract class Component {
    private List<Component> childList;
    private Component parent;

    public abstract String getHtml();

    public Component() {
        childList = new ArrayList<Component>();
    }

    public XumlDocument getDocument() {
        Component parent = this;
        while (!(parent instanceof XumlDocument)) {
            parent = parent.getParent();
        }
        return (XumlDocument)parent;
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
}
