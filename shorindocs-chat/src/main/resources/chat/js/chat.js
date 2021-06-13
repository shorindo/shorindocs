
/*
var scaff;
function update(src) {
  if (scaff) {
    scaff.innerText = src.value  + '\u200b';
    return scaff.offsetHeight - 2;
  } else {
    var style = window.getComputedStyle(src);
    scaff = document.createElement("div");
    scaff.style.position = "absolute";
    scaff.style.left = "-200vw";
    scaff.style.top = "0";
    scaff.style.visibility = "hidden";
    scaff.style.fontSize = style.fontSize;
    scaff.style.fontFamily = style.fontFamily;
    scaff.style.width = src.offsetWidth + "px";
    scaff.innerText = src.value;
    document.body.appendChild(scaff);
    document.querySelector("#chat-wrapper .xuml-window-body").scrollTo(0, 9999);
    return Math.max(scaff.offsetHeight, style.offsetHeight);
  }
}
*/

var chat = {
  STATUS: 0,

  init: function() {
    var element = document.querySelector("#chat-wrapper .xuml-window-body");
    element.addEventListener('scroll', function(evt) {
      var e = evt.target;
      console.log(e.scrollHeight + "/" + e.clientHeight + "/" + e.scrollTop);
      if (e.scrollTop < e.clientHeight / 2) {
        chat.search();
      }
    });
  },

  adjust: function(evt) {
    var target = evt.target;
    target.style.height = chat.update(target) + "px";
  },

  update: function(src) {
    var scaff = chat.scaff;
    if (scaff) {
      scaff.innerText = src.value  + '\u200b';
      return scaff.offsetHeight - 2;
    } else {
      var style = window.getComputedStyle(src);
      var scaff = chat.scaff = document.createElement("div");
      scaff.style.position = "absolute";
      scaff.style.left = "-200vw";
      scaff.style.top = "0";
      scaff.style.visibility = "hidden";
      scaff.style.fontSize = style.fontSize;
      scaff.style.fontFamily = style.fontFamily;
      scaff.style.width = src.offsetWidth + "px";
      scaff.innerText = src.value;
      document.body.appendChild(scaff);
      document.querySelector("#chat-wrapper .xuml-window-body").scrollTo(0, 9999);
      return Math.max(scaff.offsetHeight, style.offsetHeight);
    }
  },

  send: function(evt) {
    if (chat.STATUS > 0) {
      return;
    }
    var rpc = new XMLHttpRequest();
    var textarea = document.querySelector("textarea");
    rpc.onreadystatechange = function() {
      if (rpc.readyState == 4) {
        if (rpc.status == 200) {
          var element = document.querySelector("#chat-wrapper .xuml-window-body");
          element.innerText = "";
          var chats = JSON.parse(rpc.responseText).result;
          for (var i = 0; i < chats.child.length; i++) {
            var part = docs.parseVDOM(chats.child[i]);
            element.appendChild(part);
          }
          element.scrollTo(0, 9999);
          textarea.value = "\u200b";
          textarea.disabled = false;
        } else {
          //error();
          textarea.disabled = false;
        }
        chat.update(textarea);
        textarea.focus();
        chat.STATUS = 0;
      }
    };
    rpc.open("POST", location);
    rpc.setRequestHeader("Content-Type" , "application/json");
    rpc.send(JSON.stringify({
      "method":"addMessage",
      "id":new Date().getTime(),
      "param": {
        "message":textarea.value
      }
    }));
    chat.STATUS = 1;
    textarea.disabled = true;
  },

  search: function() {
    if (chat.STATUS > 0) {
      return;
    }
    var chatId = document.querySelector(".chat-markdown").getAttribute("data-chatId");
    var rpc = new XMLHttpRequest();
    rpc.onreadystatechange = function() {
      if (rpc.readyState == 4) {
        if (rpc.status == 200) {
          var element = document.querySelector("#chat-wrapper .xuml-window-body");
          var chats = JSON.parse(rpc.responseText).result;
          var anchor = document.querySelector(".chat-markdown");
          if (anchor) {
            for (var i = chats.child.length - 1; i >= 0; i--) {
              var part = docs.parseVDOM(chats.child[i]);
              anchor.parentNode.insertBefore(part, anchor);
              anchor = part;
            }
          } else {
            for (var i = 0; i < chats.child.length; i++) {
              var part = docs.parseVDOM(chats.child[i]);
              element.appendChild(part);
            }
          }
        }
        chat.STATUS = 0;
      }
    };
    rpc.open("POST", location);
    rpc.setRequestHeader("Content-Type" , "application/json");
    rpc.send(JSON.stringify({
      "method":"searchMessage",
      "id":new Date().getTime(),
      "param": {
        "minChatId":chatId,
        "size":20
      }
    }));
    chat.STATUS = 1;
  },

  removeMessage: function(id) {
    //console.log("removeMessage(" + id  + ")");
    if (chat.STATUS > 0) {
      return;
    }
    if (!confirm("メッセージ[" + id + "]を削除しますか？")) {
      return;
    }
    var rpc = new XMLHttpRequest();
    rpc.onreadystatechange = function() {
      if (rpc.readyState == 4) {
        if (rpc.status == 200) {
          var element = document.querySelector("#chat-wrapper .xuml-window-body");
          element.innerText = "";
          var chats = JSON.parse(rpc.responseText).result;
          for (var i = 0; i < chats.child.length; i++) {
            var part = docs.parseVDOM(chats.child[i]);
            element.appendChild(part);
          }
        }
        chat.STATUS = 0;
      }
    };
    rpc.open("POST", location);
    rpc.setRequestHeader("Content-Type" , "application/json");
    rpc.send(JSON.stringify({
      "method":"removeMessage",
      "id":new Date().getTime(),
      "param": {
        "chatId":id
      }
    }));
    chat.STATUS = 1;
  },

  toggleMenu: function(evt) {
    evt.preventDefault();
    evt.stopPropagation();
    var el = evt.target.parentNode.querySelector(".popup-menu");
    if (window.getComputedStyle(el).display == "none") {
      el.style.display = "block";
    } else {
      el.style.display = "none";
    }
  },

  getAbsolutePosition: function(el) {
    var pos = { "left":el.offsetLeft, "top":el.offsetTop };
    var parent = el.offsetParent;
    while (parent) {
      console.log(pos);
      pos.left += parent.offsetLeft;
      pos.top += parent.offsetTop;
      parent = parent.offsetParent;
    }
    return pos;
  },

  fixHeight: function() {
    var control = document.querySelector(".chat-control");
    var controlHeight = control.offsetHeight;
    var window = document.querySelector("#chat-wrapper .xuml-window-body");
    var windowTop = 0;
    var parent = window;
    while (parent) {
      windowTop += parent.offsetTop;
      parent = parent.offsetParent;
    }
    var footerHeight = document.querySelector("#footer-pane").offsetHeight;
    var marginOffset = 6;
    //console.log(document.body.offsetHeight + "/" + windowTop + "/" + controlHeight + "/" + footerHeight);
    var windowHeight = document.body.offsetHeight - windowTop - controlHeight - footerHeight - marginOffset;
    window.style.maxHeight = windowHeight + "px";
  },

  refresh: function(evt) {
    chat.fixHeight();
    document.querySelector("#chat-wrapper .xuml-window-body").scrollTo(0, 999999);
    document.querySelector("textarea").focus();
  }
};
window.addEventListener('DOMContentLoaded', chat.init);
window.addEventListener('DOMContentLoaded', chat.refresh);
window.addEventListener('resize', chat.refresh);
