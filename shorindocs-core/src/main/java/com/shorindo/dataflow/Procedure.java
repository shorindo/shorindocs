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

/**
 * 
 */
public class Procedure {
    public void doCall() {}
    public void doIf() {}
    public void doFor() {}
    public void doWhile() {}

    public Procedure begin() {
        return this;
    }
    public Procedure end() {
        return this;
    }
    public Procedure sequence(Procedure...procs) {
        return new Sequence(procs);
    }
    public Procedure choice(Procedure...procs) {
        return new Choice(procs);
    }
    public Procedure loop(Procedure...procs) {
        return new Loop(procs);
    }

    public static class Sequence extends Procedure {
        protected Sequence(Procedure...procs) {
        }
    }

    public static class Choice extends Procedure {
        protected Choice(Procedure...procs) {
        }
    }

    public static class Loop extends Procedure {
        protected Loop(Procedure...procs) {
        }
    }

    public static void main(String[] args) {
        Procedure proc = new Procedure();
        proc.begin()
            .end();
    }
}
