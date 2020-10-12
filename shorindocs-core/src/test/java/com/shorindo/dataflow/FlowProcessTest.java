/*
 * Copyright 2019 Shorindo, Inc.
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
package com.shorindo.dataflow;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * 
 */
public class FlowProcessTest {

    @Test
    public void testExecute() throws Exception {
        List<FlowProcess<?,?>> processList = new ArrayList<FlowProcess<?,?>>();
        processList.add(new FlowProcess<Integer,Integer>() {
            @Override
            public Integer execute(Integer i) {
                return i + (i % 13);
            }
        });
        processList.add(new FlowProcess<Integer,Byte[]>() {
            @Override
            public Byte[] execute(Integer i) throws FlowException {
                try {
                    MessageDigest md5 = MessageDigest.getInstance("MD5");
                    byte[] b = new byte[] {
                        (byte) (0x000000ff & (i >>> 24)),
                        (byte) (0x000000ff & (i >>> 16)),
                        (byte) (0x000000ff & (i >>> 8)),
                        (byte) (0x000000ff & (i))
                    };
                    b = md5.digest(b);
                    Byte[] result = new Byte[b.length];
                    for (int pos = 0; pos < b.length; pos++) {
                        result[pos] = new Byte(b[pos]);
                    }
                    return result;
                } catch (NoSuchAlgorithmException e) {
                    throw new FlowException(e);
                }
            }
        });
        processList.add(new FlowProcess<Byte[],String>() {
            @Override
            public String execute(Byte[] b) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < b.length; i++) {
                    sb.append(String.format("%02x", b[i]));
                }
                return sb.toString();
            }
        });

        Object in = new Integer(123);
        for (FlowProcess<?,?> task : processList) {
            for (Method method : task.getClass().getDeclaredMethods()) {
                if ("execute".equals(method.getName()) &&
                        method.getParameterCount() == 1) {
                    System.out.println(method.getName() + "(" + in.getClass() + " " + in + ")");
                    in = method.invoke(task, in);
                    break;
                }
            }
        }
        System.out.println("-> " + in.getClass() + " " + in);
        assertEquals("919f0e2671e55c474253ef9546f4df23", in);
    }

}
