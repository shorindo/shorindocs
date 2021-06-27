var outlogger = (function() {
  function hasClass(node, clazz) {
    return arguments.length == 2 
      && (" " + node.className + " ").match(" " + clazz + " ");
  };

  function parse(dom) {
    var node = {};
    node.text = dom.querySelector(".outline-text").innerText;
    node.children = [];
    var children = dom.querySelectorAll(":scope > .outline");
    for (var i = 0; i < children.length; i++) {
      node.children.push(parse(children.item(i)));
    }
    return node;
  };
  
  function newline(outline) {
    var newline = outline.cloneNode(false);
    var icons = outline.querySelectorAll("span.material-icons");
    for (var i = 0; i < icons.length; i++) {
      newline.appendChild(icons.item(i).cloneNode(true));
    }
    var text = outline.querySelector(".outline-text").cloneNode(false);
    newline.appendChild(text);
    var next = outline.nextSibling;
    while (next) {
      if (next.nodeName.toUpperCase() == 'DIV') {
        break;
      }
      next = next.nextSibling;
     }
     if (next) {
       next.parentNode.insertBefore(newline, next);
     } else {
       outline.parentNode.appendChild(newline);
     }
     text.focus();
   };

  function indent(target) {
    var prev = target.previousSibling;
    while (prev && !(" " + prev.className + " ").match(" outline ")) {
      prev = prev.previousSibling;
    }
    if (prev) {
      if (!isOpen(prev)) {
        var classNames = " " + prev.className + " ";
        if (classNames.match(/ open /)) {
          prev.className = classNames.replaceAll(" open ", " close "); 
        } else {
          prev.className = classNames.replaceAll(" close ", " open "); 
        }
      }
      prev.appendChild(target);
    }
    target.querySelector(".outline-text").focus();
  };

  function outdent(target) {
    var parent = target.parentNode;
    if (parent && hasClass(parent, "outline")) {
      var next = parent.nextSibling;
      while (next && !(" " + next.className + " ").match(" outline ")) {
        next = next.nextSibling;
      }
      if (next) {
        next.parentNode.insertBefore(target, next);
      } else if (hasClass(parent, "outline")) {
        parent.parentNode.appendChild(target);
      } else {
        var top = document.querySelector("#outlogger-pane");
        top.appendChild(target);
      }
    }
    target.querySelector(".outline-text").focus();
  };

  function prevOutline(outline) {
    var prev = outline.previousSibling;
    while (prev && !hasClass(prev, "outline")) {
      prev = prev.previousSibling;
    }
    return prev;
  }

  function movePrev(outline) {
    var prev = prevOutline(outline);
    if (prev) {
      prev.parentNode.insertBefore(outline, prev);
      outline.querySelector(".outline-text").focus();
    }
  };

  function nextOutline(outline) {
    var next = outline.nextSibling;
    while (next && !hasClass(next, "outline")) {
      next = next.nextSibling;
    }
    return next;
  };

  function moveNext(outline) {
    var next = nextOutline(outline);
    if (next) {
      var nextnext = nextOutline(next);
      if (nextnext) {
        outline.parentNode.insertBefore(outline, nextnext);
      } else {
        outline.parentNode.appendChild(outline);
      }
      outline.querySelector(".outline-text").focus();
    }
  }

  function isOpen(el) {
    return hasClass(el, "open");
  };

  function toggleTree(outline) {
    var classNames = " " + outline.className + " ";
    if (classNames.match(/ open /)) {
      outline.className = classNames.replaceAll(" open ", " close "); 
    } else {
      outline.className = classNames.replaceAll(" close ", " open "); 
    }
  };

  function open(outline) {
    if (!isOpen(outline)) {
      toggleTree(outline);
    }
  };

  function close(outline) {
    if (isOpen(outline)) {
      toggleTree(outline);
    }
  };

  function deleteBackward(outline) {
    var prev = prevOutline(outline);
    if (prev) {
      outline.parentNode.removeChild(outline);
      prev.querySelector(".outline-text").focus();
    } else {
      var parent = outline.parentNode;
      if (hasClass(parent, "outline")) {
        outline.parentNode.removeChild(outline);
        parent.querySelector(".outline-text").focus();
      }
    }
  }

  function deleteForward(outline) {
    var next = nextOutline(outline);
    if (next) {
      outline.parentNode.removeChild(outline);
      next.querySelector(".outline-text").focus();
    } else {
      var parent = outline.parentNode;
      if (hasClass(parent, "outline")) {
        outline.parentNode.removeChild(outline);
        parent.querySelector(".outline-text").focus();
      }
    }
  }

  function stop(evt) {
    evt.preventDefault();
    evt.stopPropagation();
  }

  function createParam() {
    var param = {
      "title":document.querySelector("#document-title").innerText,
      "content":[]
    };
    var outlines = document.querySelectorAll("#outlogger-pane > div.outline");
    for (var i = 0; i < outlines.length; i++) {
      param.content.push(parse(outlines.item(i)));
    }
    return param;
  }

  return {
    STATUS: 0,

    toggleTree: function(evt) {
      toggleTree(evt.target.parentNode);
    },

    input: function(evt) {
      if (evt.ctrlKey) {
        if (evt.key != 'Control') {
          xuml.toast("CTRL+" + evt.key);
        }
        var outline = evt.target.parentNode;
        switch (evt.key) {
        case 'Enter': // 同じ階層に次のアウトラインを生成する
          newline(outline);
          break;
        case 'Backspace': // アウトラインが空なら削除して１つ前のアウトラインにカーソルを移動する
          if (!outline.querySelector(".outline-text").textContent) {
            stop(evt);
            deleteBackward(outline);
          }
          break;
        case 'Delete': // アウトラインが空なら削除して１つ後のアウトラインにカーソルを移動する
          if (!outline.querySelector(".outline-text").textContent) {
            stop(evt);
            deleteForward(outline);
          }
          break;
        case 's':
          stop(evt);
          outlogger.save();
          break;
        case 'ArrowUp': // アウトラインを１つ前に移動する
          stop(evt);
          movePrev(outline);
          break;
        case 'ArrowDown': // アウトラインを１つ後ろに移動する
          stop(evt);
          moveNext(outline);
          break;
        case 'ArrowRight': // アウトラインを一階層深くする
          indent(outline);
          break;
        case 'ArrowLeft': // アウトラインを一階層浅くする
          outdent(outline);
          break;
        case '[': // アウトラインをオープンする
          open(outline);
          break;
        case ']': // アウトラインをクローズする
          close(outline);
          break;
        case '@': // アウトラインをオープン・クローズする
          toggleTree(outline);
          break;
        }
      }
    },

    save: function() {
      docs.rpc("save", createParam(), function(json) {
        //console.log(json);
        xuml.toast("一時保存しました");
      }, function() {
        xuml.toast("一時保存に失敗しました");
      });
    },

    commit: function() {
      docs.rpc("commit", createParam(), function(json) {
        //console.log(json);
        xuml.toast("コミットしました");
      }, function() {
        xuml.toast("コミットに失敗しました");
      });
    },

    onPaste: function(evt) {
      let paste = (evt.clipboardData || window.clipboardData).getData('text');
      const selection = window.getSelection();
      if (!selection.rangeCount) return false;
      selection.deleteFromDocument();
      const range = selection.getRangeAt(0);
      range.insertNode(document.createTextNode(paste));
      range.setStart(range.endContainer, range.endOffset);
      evt.preventDefault();
    }
  }
})();

window.addEventListener("DOMContentLoaded", function(evt) {
  document.querySelector("#docs-save")
    .addEventListener("click", function(evt) {
      outlogger.save();
    });
  document.querySelector("#docs-commit")
    .addEventListener("click", function(evt) {
      outlogger.commit();
    });
});
