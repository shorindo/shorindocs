/*
 * Copyright 2018 Shorindo, Inc.
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
var xuml = (function() {
    var scope = {};

    function element(name, clazz) {
        var el = document.createElement(name);
        if (clazz) {
            el.className = clazz;
        }
        return el;
    }

    function text(value) {
        return document.createTextNode(value);
    }

    scope.load = function(xuml) {
    };

    /**
     * 基本コンポーネント
     */
    function Component() {}
    Component.extend = function(f) {
        for (var key in Component.prototype) {
            f.prototype[key] = Component.prototype[key];
        }
        return f;
    };
    Component.prototype.attr = function(name, value) {
        this.dom.setAttribute(name, value);
        return this;
    };
    Component.prototype.style = function(name, value) {
        var style = this.dom.getAttribute("style");
        if (!style) {
            style = "";
        }
        style += name + ":" + value + ";";
        this.dom.setAttribute("style", style);
        return this;
    };

    /**
     * Container
     */
    Container = Component.extend(function(){});
    Container.extend = function(f) {
        for (var key in Container.prototype) {
            f.prototype[key] = Container.prototype[key];
        }
        return f;
    };
    Container.prototype.add = function(child) {
        this.dom.appendChild(child.dom);
        return this;
    }

    /**
     * windowコンポーネント
     */
    scope.Window = Container.extend(function(parent) {
        this.dom = parent;
    });

    /**
     * boxコンポーネント
     */
    scope.Box = Container.extend(function() {
        this.dom = element("div");
        this.dom.className = "xuml-box";
    });

    /**
     * hboxコンポーネント
     */
    scope.HBox = Container.extend(function() {
        this.dom = element("div");
        this.dom.className = "xuml-hbox";
    });

    /**
     * vboxコンポーネント
     */
    scope.VBox = Container.extend(function() {
        this.dom = element("div");
        this.dom.className = "xuml-vbox";
    });

    /**
     * gridコンポーネント
     */
    scope.Grid = Container.extend(function() {
        this.dom = element("table");
    });

    /**
     * dialogコンポーネント
     */
    scope.Dialog = Container.extend(function(title) {
        var screen = this.screen = document.body.appendChild(element("div", "xuml-dialog-pane"));
        var dialog = this.dom = element("div", "xuml-dialog");
        this.head = dialog.appendChild(element("div", "xuml-dialog-head"));
        this.head.appendChild(text(title));
        this.body = dialog.appendChild(element("div", "xuml-dialog-body"));
        this.foot = dialog.appendChild(element("div", "xuml-dialog-foot"));

        var self = this;
        document.addEventListener("keypress", function(evt) {
            switch (evt.keyCode) {
            case 27:
                self.close();
            }
        });
    });
    scope.Dialog.prototype.add = function(child) {
        this.body.appendChild(child.dom);
        return this;
    };
    scope.Dialog.prototype.addButton = function(button) {
        this.foot.appendChild(button.dom);
        return this;
    };
    scope.Dialog.prototype.close = function() {
        var parent = this.dom.parentNode;
        parent.removeChild(this.dom);
        document.body.removeChild(this.screen);
        return this;
    };

    /**
     * label
     */
    scope.Label = Component.extend(function(value) {
        var label = this.dom = element("label");
        label.className = "xuml-label";
        label.appendChild(text(value));
    });

    /**
     * button
     */
    scope.Button = Component.extend(function(value) {
        var button = this.dom = element("button");
        button.className = "xuml-button";
        button.appendChild(text(value));
    });

    return scope;
})();
