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
@ComponentReady("window")
public class Window extends Component {
    private String title;
    private String height = "auto";
    private String width = "auto";

    public Window(XumlView view) {
        super(view);
    }

    @Override
    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html>\n");
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        sb.append("<title>" + title + "</title>\n");
        sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"${application.contextPath}/css/xuml.css\">\n");
        sb.append("<script type=\"text/javascript\" src=\"${application.contextPath}/js/xuml.js\"></script>\n");
        sb.append("</head>\n");
        sb.append("<body class=\"");
        if ("fill".equals(height)) {
            sb.append("xuml-height-fill ");
        } else if ("auto".equals(height)) {
            sb.append("xuml-height-auto");
        }
        if ("fill".equals(width)) {
            sb.append("xuml-width-fill ");
        } else if ("auto".equals(width)) {
            sb.append("xuml-width-auto ");
        }
        sb.append("\">");
        for (Component c : getChildList()) {
            sb.append(c.render());
        }
        sb.append("</body>\n");
        sb.append("</html>\n");
        return sb.toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }
}
