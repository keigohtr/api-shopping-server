package com.apitore.api.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author Keigo Hattori
 */
@ComponentScan
@EnableDiscoveryClient
@SpringBootApplication
public class ShoppingAppMain {

  public static void main(String[] args) {
    SpringApplication.run(ShoppingAppMain.class, args);
  }

}
