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
package com.shorindo.docs.text;

import com.shorindo.docs.xuml.Component;
import com.shorindo.docs.xuml.Tag;

/**
 * 
 */
@Tag("text-viewer")
public class TextViewer extends Component {

    @Override
    public String getHtml() {
        return "<div class=\"text-viewer\"></div>";
    }

}
