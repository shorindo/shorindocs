package com.shorindo.xuml;

import static org.junit.Assert.*;
import static com.shorindo.xuml.XumlMessages.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.shorindo.xuml.XumlParser.AbstractStatement;
import com.shorindo.xuml.XumlParser.Statement;
import com.shorindo.xuml.XumlParser.XumlException;

import net.arnx.jsonic.JSON;

/**
 * xuml[namespace?, use?] := (label / import / template / apply / switch / each / var / literal)*
 * label[key] := text+
 * text[lang] := literal
 * import[file]
 * template[name] := (apply / switch / each / var / literal)*
 * apply[name] := template*
 * switch[value] := (case / default)+
 * case[eq / ne / lt / le / gt / ge] := (var / literal)*
 * each[item, value] := (apply / switch / each / var / literal)*
 */
public class XumlParserTest {

    /**
     * 0:空のソース
     */
    @Test
    public void case0001() throws Exception {
        try {
            assertRender("", null, null);
            fail("expect to faile, but success.");
        } catch (XumlException e) {
            assertEquals(XUML_5300, e.getType());
        }
    }

    /**
     * 空の<xuml:xuml>
     */
    @Test
    public void case0002() throws Exception {
        assertRender("<xuml:xuml></xuml:xuml>",
            "", null);
    }

    /**
     * <xuml:xuml>のnamespace属性
     */
    @Test
    public void case0003() throws Exception {
        assertRender("<xuml:xuml namespace=\"xxx\"></xuml:xuml>",
            "", null);
    }

    /**
     * <xuml:xuml>のuse属性
     */
    @Test
    public void case0004() throws Exception {
        assertRender("<xuml:xuml use=\"strict\"></xuml:xuml>",
            "", null);
    }

    /**
     * <xuml:xuml>に文字列
     */
    @Test
    public void case0005() throws Exception {
        assertRender("<xuml:xuml>XXX</xuml:xuml>",
            "XXX", null);
    }

    /**
     * <xuml:xuml>に変数
     */
    @Test
    public void case0006() throws Exception {
        assertRender("<xuml:xuml>${person.name}</xuml:xuml>",
            "daddy", createScope());
    }

    /**
     * 01:xuml
     * <xuml:xuml>
     *   <xuml:import file="..">
     */
    @Test
    public void case0101() throws Exception {
        assertRender("<xuml:xuml>" +
            "  <xuml:import file=\"xuml/case0101-1.xuml\"/>" +
            "  <xuml:apply name=\"case0101.text\"/>" +
            "</xuml:xuml>",
            "XXX", null);
        assertRender("<xuml:xuml>" +
            "  <xuml:import file=\"xuml/case0101-1.xuml\"/>" +
            "  <xuml:apply name=\"case0101.parent\"/>" +
            "</xuml:xuml>",
            "XXX", null);
        assertRender("<xuml:xuml>" +
            "  <xuml:import file=\"xuml/case0101-1.xuml\"/>" +
            "  <xuml:apply name=\"case0101.template\">" +
            "    <xuml:template name=\"param\">XXX</xuml:template>" +
            "  </xuml:apply>" +
            "</xuml:xuml>",
            "XXX", null);
    }

    /**
     * <xuml:xuml>
     *   <xuml:import>
     */
    @Test
    public void case0102() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:import/></xuml:xuml>",
                null, null);
            fail("'file' attribute not specified.");
        } catch (XumlException e) {
            assertEquals(XUML_3002, e.getType());
        }
    }

    /**
     * <xuml:xuml>
     *   <xuml:import name="..">
     */
    @Test
    public void case0103() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:import name=\"foo\"/></xuml:xuml>",
                null, null);
            fail("invalid attribute not specified.");
        } catch (XumlException e) {
            assertEquals(XUML_3003, e.getType());
        }
    }

    /**
     * 02:template
     * template - 空 -> SUCCESS
     */
    @Test
    public void case0201() throws Exception {
        assertRender("<xuml:xuml><xuml:template name=\"component\"></xuml:template></xuml:xuml>",
            "", null);
    }

    /**
     * template - name属性を含まない -> ERROR
     */
    @Test
    public void case0202() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:template></xuml:template></xuml:xuml>",
                "", null);
            fail();
        } catch (XumlException e) {
            assertEquals(XUML_3002, e.getType());
        }
    }


    /**
     * template - 不正な属性を含む -> ERROR
     */
    @Test
    public void case0203() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:template foo=\"bar\"></xuml:template></xuml:xuml>",
                "", null);
            fail();
        } catch (XumlException e) {
            assertEquals(XUML_3003, e.getType());
        }
    }

    /**
     * template - リテラルのみを含む
     */
    @Test
    public void case0204() throws Exception {
        assertRender("<xuml:xuml><xuml:template name=\"component\">" +
            "XXX" +
            "</xuml:template></xuml:xuml>",
            "", null);
    }

    /**
     * template - 変数のみを含む -> SUCCCESS
     */
    @Test
    public void case0205() throws Exception {
        assertRender("<xuml:xuml>" +
            "  <xuml:template name=\"component\">${name}</xuml:template>" +
            "</xuml:xuml>",
            "", createScope());
    }

    /**
     * template - importのみを含む -> ERROR
     */
    @Test
    public void case0206() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:template name=\"component\">" +
                "  <xuml:import file=\"xuml/test-all.xuml\"/></xuml:template>" +
                "</xuml:xuml>", null, null);
            fail();
        } catch (XumlException e) {
            assertEquals(XUML_5300, e.getType());
        }
    }

    /**
     * template - templateのみを含む -> ERROR
     */
    @Test
    public void case0207() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:template name=\"component\">" +
                "  <xuml:template name=\"child\"></xuml:template>>" +
                "</xuml:template></xuml:xuml>",
                null, null);
            fail();
        } catch (XumlException e) {
            assertEquals(XUML_5300, e.getType());
        }
    }

    /**
     * template - applyのみを含む -> SUCCESS
     */
    @Test
    public void case0208() throws Exception {
        assertRender("<xuml:xuml>" +
            "  <xuml:template name=\"component\">" +
            "    <xuml:apply name=\"child\"/>" +
            "  </xuml:template>" +
            "</xuml:xuml>",
            "", createScope());
    }

    /**
     * template - eachのみを含む -> SUCCESS
     */
    @Test
    public void case0209() throws Exception {
        assertRender("<xuml:xuml><xuml:template name=\"component\">" +
            "  <xuml:each item=\"child\" value=\"${children}\"></xuml:each>" +
            "</xuml:template></xuml:xuml>",
            "", createScope());
    }

    /**
     * template - switchのみを含む -> SUCCESS
     */
    @Test
    public void case0210() throws Exception {
        assertRender("<xuml:xuml><xuml:template name=\"component\">" +
            "  <xuml:switch value=\"${age}\">" +
            "    <xuml:default></xuml:default>" +
            "  </xuml:switch>" +
            "</xuml:template></xuml:xuml>",
            "", createScope());
    }

    /**
     * template - caseのみを含む -> ERROR
     */
    @Test
    public void case0211() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:template name=\"component\">" +
                "  <xuml:case eq=\"0\"></xuml:case>" +
                "</xuml:template></xuml:xuml>",
                "", createScope());
            fail();
        } catch (XumlException e) {
            assertEquals(XUML_5300, e.getType());
        }
    }

    /**
     * template - defaultのみを含む -> ERROR
     */
    @Test
    public void case0212() throws Exception {
        try {
            assertRender("<xuml:xuml><xuml:template name=\"component\">" +
                "  <xuml:default></xuml:default>" +
                "</xuml:template></xuml:xuml>",
                "", createScope());
            fail();
        } catch (XumlException e) {
            assertEquals(XUML_5300, e.getType());
        }
    }

    /**
     * 03:apply
     * apply - normal -> SUCCESS
     * apply - templateを含む -> SUCCESS
     * apply - リテラルを含む -> ERROR
     * apply - 変数を含む -> ERROR
     * apply - importを含む -> ERROR
     * apply - applyを含む -> ERROR
     * apply - switchを含む -> ERROR
     * apply - caseを含む -> ERROR
     * apply - defaultを含む -> ERROR
     * apply - eachを含む -> ERROR
     */
    @Test
    public void case0301() throws Exception {
        assertRender("<xuml:xuml>" +
            "  <xuml:template name=\"xxx\">XXX</xuml:template>" +
            "  <xuml:apply name=\"yyy\"/>" +
            "  <xuml:apply name=\"xxx\"/>" +
            "  <xuml:template name=\"yyy\">YYY</xuml:template>" +
            "</xuml:xuml>",
            "YYYXXX", createScope());
    }

    /**
     * apply - template差し替え
     */
    @Test
    public void case0302() throws Exception {
        assertRender("<xuml:xuml>" +
            "  <xuml:template name=\"AAA\">" +
            "    <xuml:apply name=\"BBB\"/>" +
            "  </xuml:template>" +
            "  <xuml:apply name=\"AAA\">" +
            "    <xuml:template name=\"BBB\">XXX</xuml:template>" +
            "  </xuml:apply>" +
            "</xuml:xuml>",
            "XXX", createScope());
    }

    /**
     * 05:switch
     */
    /**
     * 06:each
     */
    @Test
    public void case0601() throws Exception {
        assertRender("<xuml:xuml>" +
            "  <xuml:each item=\"item\" value=\"${person.children}\">" +
            "    ${item.name}" +
            "  </xuml:each>" +
            "</xuml:xuml>",
            "taro&lt;hanako&gt;tara", createScope());
    }

    /**
     * 07:label
     */
    @Test
    public void case0701() throws Exception {
        String source = "<xuml:xuml>" +
            "  <xuml:label key=\"language\">" +
            "    <xuml:text lang=\"ja\">日本語</xuml:text>" +
            "    <xuml:text lang=\"en\">English</xuml:text>" +
            "  </xuml:label>" +
            "#{language}" +
            "</xuml:xuml>";
        Map<String,Object> scope = createScope();
        assertRender(source, "日本語", scope);
        scope.put("lang", Locale.ENGLISH);
        assertRender(source, "English", scope);
    }

    /**
     * 08:var
     * ${..}
     * @{..}
     * #{..}
     * *{..}
     */
    @Test
    public void case0801() throws Exception {
        assertRender("<xuml:xuml>${person.name}</xuml:xuml>",
            "daddy", createScope());
        assertRender("<xuml:xuml>${person.children[0].name}</xuml:xuml>",
            "taro", createScope());
    }

    @Test
    public void case0802() throws Exception {
        assertRender("<xuml:xuml>${person.children[1].name}</xuml:xuml>",
            "&lt;hanako&gt;", createScope());
        assertRender("<xuml:xuml>*{person.children[1].name}</xuml:xuml>",
            "<hanako>", createScope());
    }

    private void assertRender(String source, String expect, Map<String, Object> scope) throws Exception {
        Statement stmt = XumlParser.compile(source);
        printStatement(stmt);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stmt.execute(baos, scope);
        baos.close();
        String output = new String(baos.toByteArray(), "UTF-8");
        assertEquals(expect, output);
    }

    private static Map<String,Object> createScope() {
        Map<String,Object> scope = new HashMap<>();
        Person person = Person.newPerson();
        scope.put("person", person);
        scope.put("lang", Locale.JAPANESE);
        return scope;
    }
    public static class Person {
        private String name = "daddy";
        private int age = 45;
        private float fare = 123.45f;
        private List<Person> children= new ArrayList<>();

        public static Person newPerson() {
            Person person = new Person();
            person.children.add(new Person()
                .name("taro")
                .age(12)
                .fare(0.0f));
            person.children.add(new Person()
                .name("<hanako>")
                .age(8)
                .fare(0.0f));
            person.children.add(new Person()
                .name("tara")
                .age(5)
                .fare(0.0f));
            return person;
        }
        private Person() {
        }
        public String getName() {
            return name;
        }
        public Person name(String name) {
            this.name = name;
            return this;
        }
        public int getAge() {
            return age;
        }
        public Person age(int age) {
            this.age = age;
            return this;
        }
        public float getFare() {
            return fare;
        }
        public Person fare(float fare) {
            this.fare = fare;
            return this;
        }
        public List<Person> getChildren() {
            return children;
        }
        public Person child(Person child) {
            this.children.add(child);
            return this;
        }
    }

//    @Test
//    public void testLiteral() throws Exception {
//        assertXuml("xuml/test-literal.xuml", "{}");
//    }
//
//    @Test
//    public void testLabel() throws Exception {
//        assertXuml("xuml/test-label.xuml", "{}");
//    }
//
//    @Test
//    public void testTemplate() throws Exception {
//        assertXuml("xuml/test-template.xuml", "{}");
//    }
//
//    @Test
//    public void testApply() throws Exception {
//    	assertXuml("xuml/test-apply.xuml", "{}");
//    }
//
//    @Test
//    public void testImport() throws Exception {
//    	assertXuml("xuml/test-import.xuml", "{}");
//    }
//
//    @Test
//    public void testSwitch() throws Exception {
//    	assertXuml("xuml/test-switch.xuml", "{'age':45}");
//    }
//
//    @Test
//    public void testEach() throws Exception {
//    	assertXuml("xuml/test-each.xuml", "{'list':[1, 2, 3]}");
//    }
//
//    @Test
//    public void testVar() throws Exception {
//    	assertXuml("xuml/test-var.xuml", "{'string':'こんにちわ！'}");
//    }
//
//    @Test
//    public void testXuml() throws Exception {
//    	assertXuml("xuml/test-xuml.xuml", "{}");
//    }
//
    @Test
    public void testAll() throws Exception {
        assertXuml("xuml/test-all.xuml", "{'title':'タイトル'}");
    }

    @Test
    public void testSpecout() throws Exception {
        assertXuml("specout/xuml/specout.xuml", "{}");
    }

    private void assertXuml(String fileName, String json) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        try (Reader reader = new InputStreamReader(is, "UTF-8")) {
            StringBuffer text = new StringBuffer();
            char[] buff = new char[2048];
            int len = 0;
            while ((len = reader.read(buff)) > 0) {
                text.append(buff, 0, len);
            }
            Statement stmt = XumlParser.compile(text.toString());
            printStatement(stmt);
            stmt.execute(System.err, createScope());
        }
    }

//    private XumlScope createScope(String json) {
//        XumlScope scope = new XumlScope();
//        Map<String,Object> map = JSON.decode(json);
//        for (Map.Entry<String,Object> entry : map.entrySet()) {
//            scope.put(entry.getKey(), entry.getValue());
//        }
//        return scope;
//    }

    private void printStatement(Statement stmt) {
        printStatement("", stmt);
    }

    private void printStatement(String indent, Statement stmt) {
        System.out.println(indent + stmt);
        if (stmt instanceof AbstractStatement) {
            for (AbstractStatement child : ((AbstractStatement)stmt).getChildList()) {
                printStatement(indent + "    ", child);
            }
        }
    }

//    public static class BeanSample {
//        private String string = "bar";
//        private String[] array = new String[] { "a", "b", "c" };
//        private List<Integer> list = new ArrayList<Integer>() {{
//            add(1); add(2); add(3);
//        }};
//    }
}
