package com.shorindo.xuml;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.Test;

public class XumlParserTest {

    @Test
    public void testLiteral() throws Exception {
        assertXuml("xuml/test-literal.xuml");
    }

    @Test
    public void testTemplate() throws Exception {
        assertXuml("xuml/test-template.xuml");
    }

    @Test
    public void testApply() throws Exception {
        assertXuml("xuml/test-apply.xuml");
    }

    @Test
    public void testAll() throws Exception {
        assertXuml("xuml/test-all.xuml");
    }

    private void assertXuml(String fileName) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        try (Reader reader = new InputStreamReader(is, "UTF-8")) {
            StringBuffer text = new StringBuffer();
            char[] buff = new char[2048];
            int len = 0;
            while ((len = reader.read(buff)) > 0) {
                text.append(buff, 0, len);
            }
            XumlParser parser = new XumlParser();
            parser.parse(text.toString());
        }
    }
}
