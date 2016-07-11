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
public abstract class Container extends Component {
    private String width;
    private String height;

    public Container(XumlView view) {
        super(view);
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
        addStyle("width", width);
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
        addStyle("height", height);
    }
}
