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
@ComponentReady("textbox")
public class TextBox extends Component {
    private boolean multiline;
    
    public TextBox(XumlView view) {
        super(view);
    }

    @Override
    public String render() {
        if (multiline) {
            return "<textarea class=\"textbox\">" +
                    "</textarea>";
        } else {
            return "<input type=\"text\">";
        }
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(String multiline) {
        this.multiline = "true".equals(multiline) ? true : false;
    }

}
