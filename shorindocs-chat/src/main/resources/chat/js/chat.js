function adjust(evt) {
  var target = evt.target;
  target.style.height = update(target) + "px";
}

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
    console.log(scaff.offsetHeight);
    return Math.max(scaff.offsetHeight, style.offsetHeight);
  }
}

var chat = {
  send: function(evt) {
    var rpc = new XMLHttpRequest();
    var textarea = document.querySelector("textarea");
    rpc.onreadystatechange = function() {
      if (rpc.readyState == 4) {
        //console.log(rpc.responseText);
        if (rpc.status == 200) {
          //success();
          var response = JSON.parse(rpc.responseText);
          var part = docs.parseVDOM(response.result);
          console.log(part);
          document.querySelector("#chat-list")
            .appendChild(part);
          textarea.value = "\u200b";
          textarea.disabled = false;
        } else {
          //error();
          textarea.disabled = false;
        }
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
    textarea.disabled = true;
  },
  recv: function(text) {
        }
};

//document.querySelector("textarea").focus();
