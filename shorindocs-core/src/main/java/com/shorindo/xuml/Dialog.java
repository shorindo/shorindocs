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

import com.shorindo.docs.action.ActionLogger;

/**
 * 
 */
@ComponentReady("dialog")
public class Dialog extends Container {
    private static final ActionLogger LOG = ActionLogger.getLogger(Dialog.class);
    private String title;

    public Dialog(XumlView view) {
        super(view);
    }

    @Override
    public String getHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"dialog-pane\">");
        sb.append("<div class=\"dialog\" style=\"" + getStyles() + "\">");
        sb.append("<div class=\"dialog-head\">{{status}} - {{title}}</div>");
        for (Component c : getChildList()) {
            //LOG.debug("コンポーネント[" + c.getClass().getSimpleName() + "]を呼び出します。");
            sb.append(c.getHtml());
        }
        sb.append("</div>");
        sb.append("</div>");
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
