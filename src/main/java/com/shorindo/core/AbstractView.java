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
package com.shorindo.core;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 */
public abstract class AbstractView {
    private static final Logger LOG = Logger.getLogger(AbstractView.class);
    protected ActionContext context;

    public abstract String getContentType();
    public abstract String getContent() throws IOException;

    public AbstractView(ActionContext context) {
        this.context = context;
    }

    protected InputStream filter(InputStream is) {
        return new VariableExpandFilter(is);
    }

    protected String expandVars(String in) {
        String type = in.substring(0, 1);
        String name = in.substring(2, in.length() - 1);
        String value = in;
        if ("$".equals(type)) {
            return BeanManager.getValue(context.getAttributes(), name, in).toString();
//            Matcher m1 = p1.matcher(name);
//            if (!m1.matches()) {
//                return in;
//            }
//            String beanName = m1.group(1);
//            Object bean = attrMap.get(beanName);
//            if (bean == null) {
//                LOG.warn("'" + beanName + "' not found.");
//            }
//            int start = 0;
//            Matcher m2 = p2.matcher(m1.group(2));
//            while (m2.find(start)) {
//                String valueName = m2.group(1);
//                bean = BeanManager.getProperty(bean, valueName);
//                if (bean == null) {
//                    break;
//                }
//                start = m2.end();
//            }
//            if (bean == null) {
//                LOG.warn("bean '" + name + "' not found.");
//                value = in;
//            } else {
//                value = bean.toString();
//            }
        } else if ("#".equals(type)) {
            try {
                value = context.getMessage(name);
            } catch (Exception e) {
                e.printStackTrace();
                value = in;
            }
        }
        return value;
    }

    protected class VariableExpandFilter extends FilterInputStream {
        Queue<Byte> queue;

        protected VariableExpandFilter(InputStream in) {
            super(in);
            queue = new LinkedList<Byte>();
        }

        @Override
        public int read() throws IOException {
            int state = 0;
            if (queue.size() > 0) {
                return queue.poll();
            }
            while (true) {
                int c = super.read();
                switch (state) {
                case 0:
                    switch (c) {
                    case -1:
                        return -1;
                    case '$':
                    case '#':
                        queue.add((byte)c);
                        state = 1;
                        break;
                    default:
                        return c;
                    }
                    break;
                case 1:
                    switch (c) {
                    case '{':
                        queue.add((byte)c);
                        state = 2;
                        break;
                    default:
                        queue.add((byte)c);
                        state = 0;
                        return queue.poll();
                    }
                    break;
                case 2:
                    switch (c) {
                    case '}':
                        queue.add((byte)c);
                        state = 0;
                        byte buf[] = new byte[queue.size()];
                        int i = 0;
                        while (queue.size() > 0) {
                            buf[i++] = queue.poll();
                        }
                        String expanded = expandVars(new String(buf));
                        for (byte b : expanded.getBytes()) {
                            queue.add(b);
                        }
                        return queue.poll();
                    default:
                        queue.add((byte)c);
                        break;
                    }
                    break;
                }
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int c = this.read();
            if (c == -1) {
                return -1;
            } else {
                b[off] = (byte)c;
                return 1;
            }
        }
    }

//    public static void main(String[] args) {
//        AbstractView view = new AbstractView() {
//            @Override
//            public String getContentType() {
//                return null;
//            }
//
//            @Override
//            public String getContent() throws IOException {
//                return null;
//            }
//        };
//        view.setAttribute("abc", "ABC");
//        try {
//            String s = "123${abc}456";
//            InputStream is = new ByteArrayInputStream(s.getBytes());
//            int c;
//            while ((c = is.read()) != -1) {
//                System.out.write((byte)c);
//            }
//            System.out.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
