QUnit.extend(QUnit.assert, {
    dom: function(xpath, expect) {
        var context = document.getElementById("qunit-fixture");
        var result = document.evaluate(xpath, context, null, XPathResult.STRING_TYPE, null);
        this.equal(expect, result.stringValue);
    }
});

QUnit.test("HTML", function(assert) {
    var fixture = document.getElementById("qunit-fixture");
    var window = new xuml.Window(fixture)
        .add(new xuml.Html("table")
        .attr("border", "1")
        .style("width", "100%")
        .add(new xuml.Html("tr")
            .add(new xuml.Html("td").add(new xuml.Text("1")))
            .add(new xuml.Html("td").add(new xuml.Text("John"))))
        .add(new xuml.Html("tr")
            .add(new xuml.Html("td").add(new xuml.Text("2")))
            .add(new xuml.Html("td").add(new xuml.Text("Lisa"))))
        .add(new xuml.Html("tr")
            .add(new xuml.Html("td").add(new xuml.Text("3")))
            .add(new xuml.Html("td").add(new xuml.Text("Mike")))));
    assert.dom("table/tr[position()=3]/td[position()=2]/text()", "Mike");
});

QUnit.test("レイアウト", function(assert) {
    var fixture = document.getElementById("qunit-fixture");
    var window = new xuml.Window(fixture)
        .add(new xuml.VBox()
        .style("background", "#EEEEEE")
        .style("height", "100%")
        .style("display", "flex")
        .style("flex-direction", "column")
        .add(new xuml.Box()
            .style("background", "#FFEEEE")
            .style("height", "50px"))
        .add(new xuml.HBox()
            .style("background", "#EEFFEE")
            .style("flex", "1")
            .add(new xuml.VBox()
                .style("width", "300px")
                .style("background", "#EEEEFF"))
            .add(new xuml.VBox()
                .style("background", "#EEFFEE"))
            .add(new xuml.VBox()
                .style("background", "#FFEEFF")
                .style("width", "300px")))
         .add(new xuml.Box()
            .style("height", "50px")));
    assert.dom("//div[@class='xuml-vbox']/@class", "xuml-vbox");
});

QUnit.test("ダイアログ", function(assert) {
    var fixture = document.getElementById("qunit-fixture");
    var window = new xuml.Window(fixture)
        .add(new xuml.Dialog("タイトル")
            .add(new xuml.Label("ラベル"))
            .addButton(new xuml.Button("OK").style("width", "100px"))
            .addButton(new xuml.Button("CANCEL").style("width", "100px")));
    assert.dom("div//label/text()", "ラベル");
})