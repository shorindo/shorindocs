package com.shorindo.docs.specout;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.shorindo.xuml.XumlParser;
import com.shorindo.xuml.XumlParser.AbstractStatement;
import com.shorindo.xuml.XumlParser.Statement;

@Ignore
public class ViewTest {

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
            Map<String,Object> scope = new HashMap<>();
            stmt.execute(System.err, scope);
        }
    }
}
