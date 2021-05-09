/*
 * Copyright 2016 Shorindo, Inc.
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
package com.shorindo.xuml;

import static com.shorindo.xuml.XumlMessages.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.shorindo.docs.action.ActionContext;
import com.shorindo.docs.action.ActionLogger;
import com.shorindo.docs.view.View;
import com.shorindo.xuml.XumlParser.RootStatement;
import com.shorindo.xuml.XumlParser.Statement;

/**
 * 
 */
public class XumlView implements View {
    private static final ActionLogger LOG = ActionLogger.getLogger(XumlView.class);
    private static final String CONTENT_TYPE = "text/html;charset=UTF-8";
    private static final Map<String,XumlView> viewMap = new ConcurrentHashMap<>();
    private Statement statement;

    public static XumlView create(String name) {
        XumlView view = viewMap.get(name);
        if (view == null) {
            view = new XumlView(name);
            viewMap.put(name, view);
        }
        return view;
    }

    private XumlView(String name) {
        String[] names = name.split("#", 2);
        try (InputStream is = XumlView.class.getClassLoader().getResourceAsStream(names[0])) {
            Reader reader = new InputStreamReader(is, "UTF-8");
            int len = 0;
            char[] c = new char[2048];
            StringBuilder sb = new StringBuilder();
            while ((len = reader.read(c)) > 0) {
                sb.append(c, 0, len);
            }
            statement = XumlParser.compile(sb.toString());
            if (names.length > 1 && statement instanceof RootStatement) {
                statement = ((RootStatement)statement).getTemplate(names[1]);
            }
        } catch (IOException e) {
            LOG.error(XUML_5400, e, name);
        }
    }

    @Override
    public void render(ActionContext context, OutputStream os) throws IOException {
        long st = System.currentTimeMillis();
        LOG.debug(XUML_1001, "render");
        statement.execute(os, context.getModel());
        LOG.debug(XUML_1002, "render", System.currentTimeMillis() - st);
    }

    @Override
    public Map<String, String> getMetaData() {
        Map<String,String> meta = new HashMap<>();
        meta.put("Content-Type", CONTENT_TYPE);
        return meta;
    }

}
