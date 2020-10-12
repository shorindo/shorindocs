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
package com.shorindo.xml;

import java.io.InputStream;

/**
 * 
 */
public class RelaxNG {

    public static RelaxNG parse(InputStream is) {
        return null;
    }

    public static class Grammer {
    }

    public static class Start {
    }

    public static class Ref {
        private String name;
    }

    public static class Define {
        private String name;
    }

    public static class Choice {
    }

    public static class Element {
        private String name;
    }

    public static class Attribute {
        private String name;
    }

    public static class Data {
        private String type;
    }

    public static class Optional {
    }

    public static class Text {
    }

    public static class Group {
    }

    public static class ZeroOrMore {
    }

    public static class OneOrMore {
    }

    public static class Interleave {
    }

    public static class AnyName {
    }
}
