package com.shorindo.docs.markdown;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileReader;

import org.junit.Test;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;

public class FlexMarkTest {

    @Test
    public void test() throws Exception {
        FileReader reader = new FileReader(new File("C:\\Users\\kazm\\git\\shorindocs\\README.md"));
        StringBuilder sb = new StringBuilder();
        char[] c = new char[4096];
        int len;
        while ((len = reader.read(c)) > 0) {
            sb.append(c, 0, len);
        }
        reader.close();
        Parser parser = Parser.builder().build();
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        Node document = parser.parse(sb.toString());
        long st = System.currentTimeMillis();
        String result = renderer.render(document);
        long et = System.currentTimeMillis();
        System.out.println(result);
        System.out.println("elapsed:" + (et - st));
    }

}
