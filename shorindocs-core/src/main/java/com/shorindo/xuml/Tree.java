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

/**
 * 
 */
@ComponentReady("tree")
public class Tree extends Container {

    public Tree(XumlView view) {
        super(view);
    }

    @Override
    public String getHtml() {
//        new XumlDOM("table")
//            .setAttr("class","tree")
//            .setAttr("style", getStyles())
//            .addChild(child)
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"tree\" style=\"" + getStyles() + "\">");
        for (Component c : getChildList()) {
            sb.append(c.getHtml());
        }
        sb.append("</table>");
        return sb.toString();
    }

}
