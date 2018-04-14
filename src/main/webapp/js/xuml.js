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
    };

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
        this.hook = {};

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
            },
            set: function(c) {
                self.edit.col = c;
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
        var line = new TextLine(this, text);
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
    scope.TextEdit.prototype.addHook = function(name, f) {
        if (this.hook[name]) {
            this.hook[name].push(f);
        } else {
            this.hook[name] = [ f ];
        }
    };
    scope.TextEdit.prototype.doHook = function(name, target) {
        if (this.hook[name]) {
            var args = [];
            for (var i = 1; i < arguments.length; i++) {
                args.push(arguments[i]);
            }
            var hooks = this.hook[name];
            for (var i = 0; i < hooks.length; i++) {
                hooks[i].apply(target, args);
            }
            return true;
        } else {
            return false;
        }
    };
    scope.TextEdit.prototype.bind = function(line) {
        this.edit.bind(line);
    };

    scope.TextEdit.prototype.forwardChar = function() {
        var last = this.lines[this.lines.length - 1];
        if (this.row == this.lines.length && this.col == last.text.length) {
            return;
        } else if (this.col >= this.edit.text.length) {
            this.edit.fix();
            this.nextLine();
            this.col = 0;
        } else {
            this.col = this.col + 1;
        }
    };
    scope.TextEdit.prototype.backwardChar = function() {
        if (this.row == 0 && this.col == 0) {
            return;
        } else if (this.col <= 0) {
            this.edit.fix();
            this.previousLine();
            this.col = -1;
        } else {
            this.col = this.col - 1;
        }
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
        this.col = 0;
    };
    scope.TextEdit.prototype.endOfLine = function() {
        this.col = -1;
    };
    scope.TextEdit.prototype.openLine = function() {
        var text = this.edit.text;
        var curr = text.substr(0, this.col);
        var next = text.substr(this.col);
        this.edit.target.text = curr;
        var nextLine = new TextLine(this, next);
        this.add(nextLine, this.edit.target);
        nextLine.edit();
        return this;
    };
    scope.TextEdit.prototype.killLine = function() {
        var c = this.col;
        this.edit.text = this.edit.text.substr(0, c);
        this.col = c;
        return this;
    };
    scope.TextEdit.prototype.deleteBackwardChar = function() {
        //console.log("deleteBackwardChar");
        var text = this.edit.text;
        var c = this.col;
        if (c == 0) {
            if (this.row == 0) {
                return;
            }
            var currLine = this.lines[this.row];
            var prevLine = this.lines[this.row - 1];
            this.lines.splice(this.row, 1);
            currLine.dom.parentNode.removeChild(currLine.dom);
            c = prevLine.text.length;
            prevLine.text = prevLine.text + currLine.text;
            this.row = this.row - 1;
            this.col = c;
        } else {
            var prev = text.substring(0, c - 1);
            var next = text.substring(c);
            this.edit.text = prev + next;
            this.col = c - 1;
        }
    };
    scope.TextEdit.prototype.deleteChar = function() {
        //console.log("deleteChar");
        var text = this.edit.text;
        var c = this.col;
        if (c == text.length) {
            if (this.row == this.lines.length - 1) {
                return;
            }
            var nextLine = this.lines[this.row + 1];
            this.lines.splice(this.row + 1, 1);
            nextLine.dom.parentNode.removeChild(nextLine.dom);
            this.edit.text = text + nextLine.text;
        } else {
            var prev = text.substring(0, c);
            var next = text.substring(c + 1);
            this.edit.text = prev + next;
        }
        this.col = c;
    };

    /*
     * editline
     */
    function EditLine() {
        var self = this;
        var _dom = element("div", "xuml-textline edit");
        _dom.contentEditable = true;
        _dom.style.position = "relative";
        _dom.style.top = "-1000px";
        Object.defineProperty(this, "dom", {
            get: function() { return _dom; }
        });

        Object.defineProperty(this, "text", {
            get: function() {
                return _dom.textContent;
                //return _dom.innerHTML;
            },
            set: function(s) {
                _dom.textContent = s;
                //_dom.innerHTML = s;
            }
        });

        var _col = 0;
        Object.defineProperty(this, "col", {
            get: function() {
                var range = window.getSelection().getRangeAt(0);
                return range.startOffset;
            },
            set: function(c) {
                if (c < 0) _col = self.text.length;
                else if (c > self.text.length) _col = self.text.length;
                else _col = c;
                if (window.getSelection().rangeCount > 1) {
                    var range = window.getSelection().getRangeAt(0);
                    range.setStart(this.edit.dom, _col);
                    range.setEnd(this.edit.dom, _col);
                } else {
                    var range = document.createRange();
                    if (self.dom.firstChild) {
                        range.setStart(self.dom.firstChild, _col);
                    } else {
                        range.setStart(self.dom, _col);
                    }
                    window.getSelection().removeAllRanges();
                    window.getSelection().addRange(range);
                }
            }
        });

        _dom.addEventListener("keydown", function(evt) {
            //console.log(evt);
            if (evt.ctrlKey) {
                evt.preventDefault();
                evt.stopPropagation();
                switch (evt.keyCode) {
                case 37: // left arrow
                    self.editor.beginningOfLine();
                    break;
                case 39: // right arrow
                    self.editor.endOfLine();
                    break;
                case 75: // k
                    self.editor.killLine();
                }
            } else {
                switch (evt.keyCode) {
                case 8: // backspace
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.editor.deleteBackwardChar();
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
                case 37: // left arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.editor.backwardChar();
                    break;
                case 38: // up arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.fix();
                    self.editor.previousLine();
                    break;
                case 39: // right arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.editor.forwardChar();
                    break;
                case 40: // down arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.fix();
                    self.editor.nextLine();
                    break;
                case 46: // delete
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.editor.deleteChar();
                    break;
                default:
                    //console.log(evt.keyCode);
                }
            }
        }, false);
    }
    EditLine.prototype.bind = function(line) {
        var parent = line.dom.parentNode;
        var parentTop = parent.scrollTop;
        var parentHeight = parent.clientHeight;
        var parentBottom = parentTop + parentHeight;
        if (line.dom.offsetTop < parentTop) {
            line.dom.parentNode.scrollTop = line.dom.offsetTop;
        } else if (line.dom.offsetTop + line.dom.offsetHeight > parentBottom) {
            line.dom.parentNode.scrollTop = line.dom.offsetTop - parentHeight + line.dom.offsetHeight + 1;
        }
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
    TextLine = function(parent, text) {
        var self = this;
        this.editor = parent;
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

        this.on("mousedown", function(evt) {
            self.editor.edit.dom.style.top = "-10000px";
        });

        this.on("mouseup", function(evt) {
            // FIXME
            var lines = self.editor.lines;
            for (var r = 0; r < lines.length; r++) {
                if (self == lines[r]) {
                    var range = window.getSelection().getRangeAt(0);
                    var c = range.startOffset;
                    self.edit();
                    self.editor.row = r;
                    self.editor.col = c;
                    break;
                }
            }
        });
    };
    TextLine.prototype.edit = function() {
        this.editor.bind(this);
        return this;
    };
    TextLine.prototype.unedit = function() {
        removeClass(this.dom, "edit");
        //this.dom.contentEditable = false;
        this.text = this.dom.textContent;
        //this.text = this.dom.innerHTML;
        this.render();
        return this;
    };
    TextLine.prototype.on = function(evt, callback) {
        this.dom.addEventListener(evt, callback, false);
        return this;
    };
    TextLine.prototype.render = function() {
        if (!this.editor.doHook("RENDER", this, this.text)) {
            this.dom.innerHTML = this.text;
        }
    };

    function Markdown() {}
    Markdown.prototype.parse = function(text, prevText) {
        var result = this.escape(text, prevText);
        result = this.header(result, prevText);
        result = this.ul(result, prevText);
        result = this.ol(result, prevText);
        result = this.pre(result, prevText);
        return result;
    };
    Markdown.prototype.escape = function(text) {
        return text
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
    };
    Markdown.prototype.header = function(text) {
        var self = this;
        return this.inline(text.replace(/^(#{1,6})\s+(.*?)#*$/g, function(str, p1, p2, offset, s) {
            switch(p1) {
            case "#":
                return '<h1>' + self.inline(p2) + '</h1>';
            case "##":
                return '<h2>' + self.inline(p2) + '</h2>';
            case "###":
                return '<h3>' + self.inline(p2) + '</h3>';
            case "####":
                return '<h4>' + self.inline(p2) + '</h4>';
            case "#####":
                return '<h5>' + self.inline(p2) + '</h5>';
            case "######":
                return '<h6>' + self.inline(p2) + '</h6>';
            }
            return str;
        }));
    };

    var UL_PATTERN = /^(\s*)([\*\-\+])\s+(.*)$/;
    Markdown.prototype.ul = function(text, prevText) {
        if (UL_PATTERN.test(text)) {
            var indent = 0;
            if (prevText && prevText.match(UL_PATTERN)) {
                indent = RegExp.$1.length;
            }
            return text.replace(UL_PATTERN, function(str, p1, p2, p3) {
                var depth = p1.length - indent;
                var result = "";
                result += '<ul style="margin-top:0;margin-bottom:0;">';
                if (depth >= 2) {
                    result += '<ul style="margin-top:0;margin-bottom:0;">';
                }
                result += '<li>' + p3 + '</li>';
                result += '</ul>';
                if (depth >= 2) {
                    result += '</ul>';
                }
                return result;
            });
        } else {
            return text;
        }
    };

    Markdown.prototype.ol = function(text) {
        var expr = /^\s{0,3}\d+\.\s+(.*)$/;
        if (expr.test(text)) {
            return text.replace(expr, '<ol style="margin-top:0;margin-bottom:0;"><li value="1">' + RegExp.$1 + "</li></ol>");
        } else {
            return text;
        }
    };

    Markdown.prototype.pre = function(text) {
        var expr = /^\s{4}(.*)$/;
        if (expr.test(text)) {
            return text.replace(expr, '<pre style="margin-top:0;margin-bottom:0;">' + RegExp.$1 + "</pre>");
        } else {
            return text;
        }
    };

    Markdown.prototype.inline = function(text) {
        return text.replace(/&([#:a-zA-Z0-9]+){(.+?)}/g, function(str, p1, p2, offset, s) {
            var op = p1.split(/:/);
            var result = p2;
            for (var i = 0; i < op.length; i++) {
                switch (op[i]) {
                case "em":
                    result = '<b>' + result + '</b>';
                    break;
                case "it":
                    result = '<i>' + result + '</i>';
                    break;
                case "del":
                    result = '<del>' + result + '</del>';
                    break;
                default:
                    result = '<span style="color:' + op[i] + ';">' + result + '</span>';
                }
            }
            return result;
        });
    };

    scope.markdown = new Markdown();

    return scope;
})();
