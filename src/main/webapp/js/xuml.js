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

    function addClass(dom, clazz) {
        var clazzes = (dom.className ? dom.className : "").split(/\s+/);
        var idx = clazzes.indexOf(clazz);
        if (idx < 0) {
            clazzes.push(clazz);
            dom.className = clazzes.join(" ");
        }
    }

    function removeClass(dom, clazz) {
        var clazzes = (dom.className ? dom.className : "").split(/\s+/);
        var idx = clazzes.indexOf(clazz);
        if (idx >= 0) {
            clazzes.splice(idx, 1);
            dom.className = clazzes.join(" ");
        }
    }

    function geometry(dom) {
        var g = { left:0, top:0, width:dom.offsetWidth, height:dom.offsetHeight };
        var parent = dom;
        while (parent) {
            g.left += parent.offsetLeft;
            g.top += parent.offsetTop;
            parent = parent.offsetParent;
        }
        return g;
    }

    function getPosition() {
        var range = window.getSelection().getRangeAt(0);
        return range.startOffset;
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
    Component.prototype.add = function(child) {
        this.dom.appendChild(child.dom);
        return this;
    };
    Component.prototype.on = function(evt, callback) {
        var self = this;
        this.dom.addEventListener(evt, function(event) {
            callback.apply(self, event);
        });
    }

    /**
     * Container
     */
    /*
    Container = Component.extend(function(){});
    Container.extend = function(f) {
        for (var key in Container.prototype) {
            f.prototype[key] = Container.prototype[key];
        }
        return f;
    };
    */

    /**
     * windowコンポーネント
     */
    scope.Window = Component.extend(function(parent) {
        this.dom = parent;
    });

    /**
     * boxコンポーネント
     */
    scope.Box = Component.extend(function() {
        this.dom = element("div", "xuml-box");
    });

    /**
     * hboxコンポーネント
     */
    scope.HBox = Component.extend(function() {
        this.dom = element("div", "xuml-hbox");
    });

    /**
     * vboxコンポーネント
     */
    scope.VBox = Component.extend(function() {
        this.dom = element("div", "xuml-vbox");
    });

    /**
     * gridコンポーネント
     */
    scope.Grid = Component.extend(function() {
        this.dom = element("table");
    });

    /**
     * dialogコンポーネント
     */
    scope.Dialog = Component.extend(function(title) {
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
        var label = this.dom = element("label", "xuml-label");
        label.appendChild(text(value));
    });

    /**
     * button
     */
    scope.Button = Component.extend(function(value) {
        var button = this.dom = element("button", "xuml-button");
        button.appendChild(text(value));
    });

    /*
     * textedit
     */
    scope.TextEdit = Component.extend(function(text) {
        var self = this;

        var _dom = element("div", "xuml-textedit");
        Object.defineProperty(this, "dom", {
            get: function() {
                return _dom;
            }
        });

        var _lines = [];
        Object.defineProperty(this, "lines", {
            get: function() {
                return _lines;
            }
        });

        var _row = 0;
        Object.defineProperty(this, "row", {
            get: function() {
                return _row;
            },
            set: function(r) {
                if (r < 0) _row = 0;
                else if (r >= _lines.length) _row = _lines.length - 1;
                else _row = r;
                _lines[_row].edit();
            }
        });

        var _col = 0;
        Object.defineProperty(this, "col", {
            get: function() {
                return self.edit.col;
//                return _col;
            },
            set: function(c) {
                self.edit.col = c;
//                var line = _lines[_row];
//                line.col = c;
//                if (c < 0) _col = line_text.length - 1;
//                else if (c >= line.text.length) _col = line_text.length - 1;
//                else _col = c;
            }
        });

        Object.defineProperty(this, "value", {
            get: function() {
                var result = "";
                for (var i = 0; i < _lines.length; i++) {
                    result += _lines[i].text;
                }
                return result;
            },
            set: function(s) {
                var lines = s.split(/\n/);
                for (var i = 0; i < lines.length; i++) {
                    self.newLine(lines[i]);
                }
            }
        });

        this.edit = new EditLine();
        this.edit.editor = this;
        this.dom.appendChild(this.edit.dom);

        if (text) {
            this.value = text;
        }
    });
    scope.TextEdit.prototype.newLine = function(text) {
        var line = new TextLine(text);
        line.editor = this;
        this.lines.push(line);
        this.dom.appendChild(line.dom);
        return this;
    };
    scope.TextEdit.prototype.add = function(line, prev) {
        line.editor = this;
        if (prev && prev != this.lines[this.lines.length - 1]) {
            // FIXME
            for (var i = 0; i < this.lines.length - 1; i++) {
                if (this.lines[i] == prev) {
                    var parent = prev.dom.parentNode;
                    parent.insertBefore(line.dom, prev.dom.nextSibling);
                    this.lines.splice(i + 1, 0, line);
                    this.row = i + 1;
                    break;
                }
            }
        } else {
            var last = this.lines[this.lines.length - 1];
            this.lines.push(line);
            this.dom.appendChild(line.dom);
            this.row = this.lines.length - 1;
        }
        return this;
    };

    scope.TextEdit.prototype.forwardChar = function() {
        this.col = this.col + 1;
    };
    scope.TextEdit.prototype.backwardChar = function() {
        this.col = this.col + 1;
    };
    scope.TextEdit.prototype.previousLine = function() {
        var c = this.col;
        this.row = this.row - 1;
        this.col = c;
    };
    scope.TextEdit.prototype.nextLine = function() {
        var c = this.col;
        this.row = this.row + 1;
        this.col = c;
    };
    scope.TextEdit.prototype.beginningOfLine = function() {
        this.edit.col = 0;
    };
    scope.TextEdit.prototype.endOfLine = function() {
        this.edit.col = this.edit.text.length;
    };
    scope.TextEdit.prototype.openLine = function() {
        var text = this.edit.text;
        var curr = text.substr(0, this.col);
        var next = text.substr(this.col);
        this.edit.target.text = curr;
        var nextLine = new TextLine(next);
        this.add(nextLine, this.edit.target);
        nextLine.edit();
        return this;
    };

    /*
     * editline
     */
    function EditLine() {
        var self = this;
        var _dom = element("div", "xuml-textline edit");
        _dom.contentEditable = true;
        _dom.style.position = "absolute";
        Object.defineProperty(this, "dom", {
            get: function() { return _dom; }
        });

        //var _text;
        Object.defineProperty(this, "text", {
            get: function() {
                return _dom.textContent;
                //return _text;
            },
            set: function(s) {
                _text = _dom.innerHTML = s;
            }
        });

        var _col = 0;
        Object.defineProperty(this, "col", {
            get: function() {
                var range = window.getSelection().getRangeAt(0);
                return range.startOffset;
            },
            set: function(c) {
                if (c < 0) _col = _text.length - 1;
                else if (c > _text.length) _col = _text.length - 1;
                else _col = c;
                var range = document.createRange();
                range.setStart(self.dom.firstChild, _col);
                window.getSelection().removeAllRanges();
                window.getSelection().addRange(range);
            }
        });

//        _dom.addEventListener("blur", function() {
//            self.unedit();
//        }, false);

        _dom.addEventListener("keydown", function(evt) {
            //console.log(evt);
            if (evt.ctrlKey) {
                switch (evt.keyCode) {
                case 37: // left arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.editor.beginningOfLine();
                    break;
                case 39: // right arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.editor.endOfLine();
                    break;
                }
            } else {
                switch (evt.keyCode) {
                case 8: // backspace
                    break;
                case 9: // tab
                    evt.preventDefault();
                    evt.stopPropagation();
                    break;
                case 13: // enter
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.fix();
                    self.editor.openLine();
                    break;
                case 38: // up arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.fix();
                    self.editor.previousLine();
                    break;
                case 40: // down arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.fix();
                    self.editor.nextLine();
                    break;
                default:
                    //console.log(evt.keyCode);
                }
            }
        }, false);
    }
    EditLine.prototype.bind = function(line) {
        var geo = geometry(line.dom);
        this.target = line;
        this.text = line.text;
        this.dom.style.left = geo.left + "px";
        this.dom.style.top = geo.top + "px";
        this.dom.style.width = geo.width + "px";
        this.dom.style.height = geo.height + "px";
        this.dom.focus();
    };
    EditLine.prototype.fix = function() {
        this.target.text = this.dom.textContent;
    };

    /*
     * textline
     */
    TextLine = function(text) {
        var self = this;
        this.dom = document.createElement("div");
        addClass(this.dom, "xuml-textline");

        var _text;
        Object.defineProperty(this, "text", {
            get: function() {
                return _text;
            },
            set: function(s) {
                _text = s;
                self.render();
            }
        });
        this.text = text;

//        var _col = 0;
//        Object.defineProperty(this, "col", {
//            get: function() {
//                var range = window.getSelection().getRangeAt(0);
//                return range.startOffset;
//            },
//            set: function(c) {
//                if (c < 0) _col = _text.length - 1;
//                else if (c >= _text.length) _col = _text.length - 1;
//                else _col = c;
//                var range = document.createRange();
//                range.setStart(self.dom.firstChild, _col);
//                window.getSelection().removeAllRanges();
//                window.getSelection().addRange(range);
//            }
//        });

        this.on("click", function() {
            self.edit();
        });
//        this.on("blur", function() {
//            self.unedit();
//        });
//        this.on("keypress", function(evt) {
//            //console.log(evt);
//            if (evt.ctrlKey) {
//                switch (evt.keyCode) {
//                case 37: // left arrow
//                    evt.preventDefault();
//                    evt.stopPropagation();
//                    self.editor.gotoLine(self, 0);
//                    break;
//                case 39: // right arrow
//                    evt.preventDefault();
//                    evt.stopPropagation();
//                    console.log(self.text.length);
//                    self.editor.gotoLine(self, 1);
//                    break;
//                }
//            } else {
//                switch (evt.keyCode) {
//                case 8: // backspace
//                    break;
//                case 9: // tab
//                    evt.preventDefault();
//                    evt.stopPropagation();
//                    break;
//                case 13: // enter
//                    evt.preventDefault();
//                    evt.stopPropagation();
//                    self.split();
//                    break;
//                case 38: // up arrow
//                    evt.preventDefault();
//                    evt.stopPropagation();
//                    self.editor.previousLine();
//                    break;
//                case 40: // down arrow
//                    evt.preventDefault();
//                    evt.stopPropagation();
//                    self.editor.nextLine();
//                    break;
//                default:
//                    //console.log(evt.keyCode);
//                }
//            }
//        });
    };
    TextLine.prototype.edit = function() {
//        addClass(this.dom, "edit");
//        this.dom.textContent = this.text;
//        this.dom.contentEditable = true;
//        this.dom.focus();
        this.editor.edit.bind(this);
        return this;
    };
    TextLine.prototype.unedit = function() {
        removeClass(this.dom, "edit");
        this.dom.contentEditable = false;
        this.text = this.dom.textContent;
        this.render();
        return this;
    };
    TextLine.prototype.on = function(evt, callback) {
        this.dom.addEventListener(evt, callback, false);
        return this;
    };
    TextLine.prototype.split = function() {
        var range = window.getSelection().getRangeAt(0);
        var curr = this.dom.textContent.substr(0, range.startOffset);
        var next = this.dom.textContent.substr(range.startOffset);
        this.dom.innerHTML = curr;
        var nextLine = new TextLine(next);
        this.editor.add(nextLine, this);
        nextLine.edit();
        return this;
    };
    TextLine.prototype.joinWithBefore = function() {
    };
    TextLine.prototype.joinWithAfter = function() {
    }
    TextLine.prototype.render = function() {
        this.dom.innerHTML = this.text;
    };

    return scope;
})();
