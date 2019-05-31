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
package com.shorindo.docs;

import java.sql.SQLException;

import com.shorindo.docs.service.DocumentServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 */
@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {
    @Autowired
    private DocumentServiceImpl service;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//        startH2Server();
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
//        startH2Server();
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/")
    String index() {
        return docs("index");
    }

    @RequestMapping("/{docId}")
    String docs(@PathVariable("docId") String docId) {
        return "Hello " + docId + "!";
    }

//    private static void startH2Server() {
//        try {
//            Server h2server = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092").start();
//            if (h2server.isRunning(true)) {
//                System.out.println("H2 server is running.");
//            } else {
//                throw new RuntimeException("could'nt start H2.");
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("failed to start H2.");
//        }
//    }
}
