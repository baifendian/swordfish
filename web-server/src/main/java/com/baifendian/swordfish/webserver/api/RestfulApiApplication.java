/*
 * Create Author  : dsfan
 * Create Date    : 2016-7-20
 * File Name      : RestfulApiApplication.java
 */

package com.baifendian.swordfish.webserver.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Restful Api 应用
 * <p>
 * 
 * @author : dsfan
 * @date : 2016-7-20
 */
@SpringBootApplication(exclude = { MongoAutoConfiguration.class })
@ComponentScan("com.baifendian.swordfish")
public class RestfulApiApplication {
    /**
     * main
     * <p>
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(RestfulApiApplication.class);
    }
}
