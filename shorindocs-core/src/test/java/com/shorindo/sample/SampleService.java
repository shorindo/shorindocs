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
package com.shorindo.sample;

import java.math.BigDecimal;

/**
 * 
 */
public interface SampleService {

    public Output sum(Input input);
    
    public static class Input {
        private BigDecimal a;
        private BigDecimal b;

        public Input(BigDecimal a, BigDecimal b) {
            this.a = a;
            this.b = b;
        }

        public Input(String a, String b) {
            this.a = new BigDecimal(a);
            this.b = new BigDecimal(b);
        }

        public BigDecimal getA() {
            return a;
        }
        public void setA(BigDecimal a) {
            this.a = a;
        }
        public BigDecimal getB() {
            return b;
        }
        public void setB(BigDecimal b) {
            this.b = b;
        }
    }

    public static class Output {
        private BigDecimal c;

        public BigDecimal getC() {
            return c;
        }
        public void setC(BigDecimal c) {
            this.c = c;
        }
    }
}
