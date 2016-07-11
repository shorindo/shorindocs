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
@ComponentReady("hbox")
public class HBoxComponent extends Component {

    public HBoxComponent(XumlView view) {
        super(view);
    }

    @Override
    public String getHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class=\"hbox\" style=\"" + getStyles() + "\"><tr>");
        for (Component c : getChildList()) {
            sb.append("<td style=\"");
            sb.append(c.getStyles());
            sb.append("\">");
            sb.append(c.getHtml());
            sb.append("</td>");
        }
        sb.append("</td></table>");
        return sb.toString();
    }

}
