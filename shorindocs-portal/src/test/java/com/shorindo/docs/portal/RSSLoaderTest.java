package com.shorindo.docs.portal;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class RSSLoaderTest {

    @Test
    public void test() throws Exception {
        //HttpURLConnection.setFollowRedirects(true);
        HttpURLConnection conn = (HttpURLConnection)
            new URL("http://www.techscore.com/rss/").openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("GET");
        conn.connect();
        System.out.println(conn.getHeaderField("location"));
        try (InputStream is = conn.getInputStream()) {
            int len = 0;
            byte[] buff = new byte[4096];
            while ((len = is.read(buff)) > 0) {
                System.out.write(buff, 0, len);
            }
        }
    }

}
