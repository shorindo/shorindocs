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

/**
 * 
 */
public class SampleServiceImpl implements SampleService {

    @Override
    public Output sum(Input input) {
        Output output = new Output();
        output.setC(input.getA().add(input.getB()));
        return output;
    }

    public static void main(String[] args) {
        SampleService service = new SampleServiceImpl();
        Output output = service.sum(new Input("123.0", "456"));
        System.out.println(output.getC());
    }
}
