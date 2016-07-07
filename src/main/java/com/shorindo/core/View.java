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
public abstract class View {
    private static final Logger LOG = Logger.getLogger(View.class);
    protected ActionContext context;

    public abstract String getContentType();
    public abstract String getContent() throws IOException;

    public View(ActionContext context) {
        this.context = context;
    }

    protected InputStream filter(InputStream is) {
        return new VariableExpandFilter(is);
    }

    protected String expandVars(String in) {
        String type = in.substring(0, 1);
        String name = in.substring(2, in.length() - 1);
        if ("$".equals(type)) {
            return BeanManager.getValue(context.getAttributes(), name, in).toString();
        } else if ("#".equals(type)) {
            return context.getMessage(name);
        } else {
            return in;
        }
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

}
