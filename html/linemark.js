/*
 *
 */
var linemark = (function() {
    var scope = {};

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

    function getPosition() {
        var range = window.getSelection().getRangeAt(0);
        return range.startOffset;
    }

    function parse(text, prevText) {
        var result = escape(text, prevText);
        result = header(result, prevText);
        result = ul(result, prevText);
        result = ol(result, prevText);
        result = pre(result, prevText);
        return result;
    }

    function escape(text) {
        return text
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;");
    }

    function header(text) {
        return inline(text.replace(/^(#{1,6})\s+(.*?)#*$/g, function(str, p1, p2, offset, s) {
            switch(p1) {
            case "#":
                return '<h1>' + inline(p2) + '</h1>';
            case "##":
                return '<h2>' + inline(p2) + '</h2>';
            case "###":
                return '<h3>' + inline(p2) + '</h3>';
            case "####":
                return '<h4>' + inline(p2) + '</h4>';
            case "#####":
                return '<h5>' + inline(p2) + '</h5>';
            case "######":
                return '<h6>' + inline(p2) + '</h6>';
            }
            return str;
        }));
    }

    var UL_PATTERN = /^(\s*)([\*\-\+])\s+(.*)$/;
    function ul(text, prevText) {
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
    }

    function ol(text) {
        var expr = /^\s{0,3}\d+\.\s+(.*)$/;
        if (expr.test(text)) {
            return text.replace(expr, '<ol style="margin-top:0;margin-bottom:0;"><li value="3">' + RegExp.$1 + "</li></ol>");
        } else {
            return text;
        }
    }

    function pre(text) {
        var expr = /^\s{4}(.*)$/;
        if (expr.test(text)) {
            return text.replace(expr, '<pre style="margin-top:0;margin-bottom:0;">' + RegExp.$1 + "</pre>");
        } else {
            return text;
        }
    }

    function inline(text) {
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
    }

    /*
     * editor
     */
    scope.Editor = function(dom) {
        this.dom = dom;
        this.lines = [];
    };
    scope.Editor.prototype.add = function(line, prev) {
        line.editor = this;
        if (prev && prev != this.lines[this.lines.length - 1]) {
            // FIXME
            for (var i = 0; i < this.lines.length - 1; i++) {
                if (this.lines[i] == prev) {
//                    if (i > 0) {
//                        line.prevLine = this.lines[i - 1];
//                        this.lines[i - 1].nextLine = line;
//                    }
//                    if (i < this.lines.length - 1) {
//                        line.nextLine = this.lines[i + 1];
//                        this.lines[i + 1].prevLine = line;
//                    }

                    var parent = prev.dom.parentNode;
                    parent.insertBefore(line.dom, prev.dom.nextSibling);
                    this.lines.splice(i, 0, line);
                    break;
                }
            }
        } else {
            var last = this.lines[this.lines.length - 1];
            if (last) {
                last.nextLine = line;
                line.prevLine = last;
            }
            this.lines.push(line);
            this.dom.appendChild(line.dom);
        }
        return this;
    };
    scope.Editor.prototype.gotoLine = function(line, col) {
        if (line) {
            line.edit();
            var range = window.getSelection().getRangeAt(0);
            range.setStart(range.startContainer, col < line.text.length ? col : line.text.length - 1);
//try {
//            var selection = window.getSelection();
//            var range = document.createRange();
//            range.setStart(line.dom, col);
//            range.collapse(true); 
//            selection.removeAllRanges(); 
//            selection.addRange(range);
//} catch (e) {
//    console.log(e);
//}
        }
    };

    /*
     * line
     */
    scope.Line = function(text) {
        var self = this;
        this.dom = document.createElement("div");
        addClass(this.dom, "line");
        this.text = text;
        this.render();
        this.on("click", function() {
            self.edit();
        });
        this.on("blur", function() {
            self.unedit();
        });
        this.on("keypress", function(evt) {
            //console.log(evt);
            if (evt.ctrlKey) {
                switch (evt.keyCode) {
                case 37: // left arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    self.editor.gotoLine(self, 0);
                    break;
                case 39: // right arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    console.log(self.text.length);
                    self.editor.gotoLine(self, 1);
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
                    self.openLine();
                    break;
                case 38: // up arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    //self.edit();
                    self.editor.gotoLine(self.prevLine, getPosition());
                    break;
                case 40: // down arrow
                    evt.preventDefault();
                    evt.stopPropagation();
                    //self.edit();
                    self.editor.gotoLine(self.nextLine, getPosition());
                    break;
                default:
                    //console.log(evt.keyCode);
                }
            }
        });
    };
    scope.Line.prototype.edit = function() {
        addClass(this.dom, "edit");
        var col = getPosition();
        this.dom.textContent = this.text;
        this.dom.contentEditable = true;
        this.dom.focus();
        this.editor.gotoLine(this, col);
        return this;
    };
    scope.Line.prototype.unedit = function() {
        removeClass(this.dom, "edit");
        this.dom.contentEditable = false;
        this.text = this.dom.textContent;
        this.render();
        return this;
    };
    scope.Line.prototype.on = function(evt, callback) {
        this.dom.addEventListener(evt, callback, false);
        return this;
    };
    scope.Line.prototype.openLine = function() {
        var range = window.getSelection().getRangeAt(0);
        var curr = this.dom.textContent.substr(0, range.startOffset);
        var next = this.dom.textContent.substr(range.startOffset);
        this.dom.innerHTML = curr;
        var nextLine = new scope.Line(next);
        var temp = this.nextLine
        this.nextLine = nextLine;
        nextLine.prevLine = this;
        if (temp) {
            nextLine.nextLine = temp;
            temp.prevLine = nextLine;
        }
        this.editor.add(nextLine, this);
        nextLine.edit();
        return this;
    }
    scope.Line.prototype.render = function() {
        var prevText = this.prevLine ? this.prevLine.text : null;
        this.dom.innerHTML = parse(this.text, prevText);
    }

    return scope;
})();
