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
package com.shorindo.docs.repository;

/**
 * 
 */
public class SqlBuilder {
    public void test() {
        select()
            .column("*").alias("x")
            .from("table")
            .where()
                .and()
                .eq()
                .eq();
    }

    public Select select() {
        return new Select();
    }

    public static class Select {
        public Column column(String name) {
            return new Column(name);
        }
    }

    public static class Column {
        private String name;

        public Column(String name) {
            this.name = name;
        }

        public Column column(String name) {
            return new Column(name);
        }

        public Column alias(String name) {
            return this;
        }

        public Table from(String name) {
            return new Table(name);
        }
    }

    public static class Table {
        public Table(String name) {
        }

        public Expr where() {
            return new Expr();
        }
    }

    public static class Expr {
        public Expr eq() {
            return this;
        }
        public Expr ne() {
            return this;
        }
        public Expr lt() {
            return this;
        }
        public Expr le() {
            return this;
        }
        public Expr gt() {
            return this;
        }
        public Expr ge() {
            return this;
        }
        public Expr and() {
            return this;
        }
        public Expr or() {
            return this;
        }
    }
}
