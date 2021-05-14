/*
 * Copyright 2021 Shorindo, Inc.
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
 var docs = (function() {
    function rpc(method, params, onload) {
        var rpc = new XMLHttpRequest();
        rpc.onreadystatechange = function() {
            if (rpc.readyState == 4) {
                if (rpc.status == 200) {
                    onload(rpc.responseText);
                } else {
                    console.error("status = " + rpc.status);
                }
            }
        };
        rpc.open("POST", "");
        rpc.setRequestHeader("Content-Type" , "application/json");
        rpc.send(JSON.stringify({
            "method":method,
            "id":new Date().getTime(),
            "param":params
        }));
    }

    function parseVDOM(vnode) {
        if (!vnode.name) {
            return document.createTextNode(vnode);
        }
        var node = document.createElement(vnode.name);
        for (var name in vnode.attr) {
            node.setAttribute(name, vnode.attr[name]);
        }
        for (var i = 0; vnode.child && i < vnode.child.length; i++) {
            node.appendChild(parseVDOM(vnode.child[i]));
        }
        return node;
    }

    return {
        "select":function() {
            rpc("select", null, function(json) {
                var data = JSON.parse(json);
                var html = document.createElement("div");
                var root = parseVDOM(data.result);
                var nodes = [];
                for (var i = 0; i < root.childNodes.length; i++) {
                    nodes.push(root.childNodes.item(i));
                }
                for (var i = 0; i < nodes.length; i++) {
                    html.appendChild(nodes[i]);
                }
                document.body.appendChild(html);
            });
        },
        "create":function(docType, title) {
            rpc("create", {"docType":docType, "title":title}, function(json) {
                console.log(json);
                var data = JSON.parse(json);
                var html = document.createElement("div");
                var root = parseVDOM(data.result);
                var nodes = [];
                for (var i = 0; i < root.childNodes.length; i++) {
                    nodes.push(root.childNodes.item(i));
                }
                for (var i = 0; i < nodes.length; i++) {
                    html.appendChild(nodes[i]);
                }
                console.log(html);
                document.body.appendChild(html);
            });
        },
//        "save":function() {
//            rpc("save", null, function(json) {
//            });
//        },
        "flexHeight":function(selector) {
            var node = document.querySelector(selector);
            var height = window.innerHeight - node.offsetTop
                - document.querySelector("#footer-pane").offsetHeight;
            node.style.height = height + "px";
            window.onresize = function() {
                docs.flexHeight(selector);
            };
        },
        "rpc":rpc
    };
 })();

 