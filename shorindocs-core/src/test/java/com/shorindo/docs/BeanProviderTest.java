/*
 * Copyright 2018 Shorindo, Inc.
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
package com.shorindo.docs;

import static org.junit.Assert.*;

import org.junit.Test;

import com.shorindo.docs.annotation.ImplementedBy;

/**
 * 
 */
public class BeanProviderTest {

    @Test
    public void test() {
        ImplicitSample implicitSample = BeanProvider.inject(ImplicitSample.class);
        assertEquals("ImplicitSampleImpl", implicitSample.getName());

        BeanProvider.bind(Sample.class, SampleImpl.class);
        Sample sample = BeanProvider.inject(Sample.class);
        assertEquals("SampleImpl", sample.getName());
    }

    @ImplementedBy(SampleImpl.class)
    public static interface ImplicitSample {
        public String getName();
    }
    public static class ImplicitSampleImpl implements ImplicitSample {
        public String getName() {
            return "ImplicitSampleImpl";
        }
    }

    public static interface Sample {
        public String getName();
    }
    public static class SampleImpl implements Sample {
        @Override
        public String getName() {
            return "SampleImpl";
        }
    }
}
