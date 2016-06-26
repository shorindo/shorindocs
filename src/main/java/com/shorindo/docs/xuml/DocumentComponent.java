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

/**
 * 
 */
public class DocumentComponent extends Component {
    @Override
    public String getHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>");
        sb.append("<html>");
        sb.append("<head>");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8>");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"\">");
        sb.append("<script type=\"text/javascript\" src=\"\"></script");
        sb.append("</head>");
        sb.append("<body>");
        for (Component c : getChildList()) {
            sb.append(c.getHtml());
        }
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }

}
