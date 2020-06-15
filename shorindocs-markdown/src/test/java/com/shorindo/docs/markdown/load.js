(function() {
    var code = document.createElement("textarea");
    code.setAttribute("position", "absolute");
    code.setAttribute("left", "0");
    code.setAttribute("top", "0");
    code.add = function(text) {
        code.value += text;
    };
    document.documentElement.appendChild(code);
    var examples = document.querySelectorAll("div.example");
    for (var i = 0; i < examples.length; i++) {
        var name = examples[i].id.replaceAll("-", "");
        var markdown = examples[i].querySelector("code.language-markdown")
            .textContent
            .replaceAll("\\", "\\\\")
            .replaceAll("\"", "\\\"")
            .replaceAll("→", "\\t")
            .replaceAll("\n", "\\n");
        var html = examples[i].querySelector("code.language-html")
            .textContent
            .replaceAll("\\", "\\\\")
            .replaceAll("\"", "\\\"")
            .replaceAll("→", "\\t")
            .replaceAll("\n", "\\n");
        code.add("@Test\n");
        code.add("public void " + name + "() throws Exception {\n");
        code.add("    GFM(\"" + markdown + "\", \"" + html + "\");\n");
        code.add("}\n");
    }
    return code;
})();

