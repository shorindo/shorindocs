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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.shorindo.dataflow.DataFlowEngine;

/**
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@ComponentScan("com.shorindo.dataflow")
public class DataFlowEngineTest {
    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private DataFlowEngine engine;

    @Test
    public void test() throws DataFlowException {
        engine.load(null);
        assertTrue("done.", true);
        
        for (String string : ctx.getBeanDefinitionNames()) {
            if (string.indexOf("spring") == -1)
                System.err.println("BEAN DEFINITION:" + string);
        }
    }

}
