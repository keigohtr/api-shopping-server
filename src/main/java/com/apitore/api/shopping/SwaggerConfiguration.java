package com.apitore.api.shopping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


/**
 * @author Keigo Hattori
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.apitore.api.shopping.controler"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(metadata());
  }

  private ApiInfo metadata() {
    return new ApiInfoBuilder().title("Shopping APIs").build();
  }

}
