package com.qingxuan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @Author luo
 * @Date 2022/10/16 - 18:00
 */
// @Configuration //配置类
// @EnableSwagger2 // 开启Swagger2的自动配置
// public class SwaggerConfig {
//     // Swagger实例Bean是Docket
//     @Bean
//     public Docket docket(Environment environment){
//         // 设置要显示swagger的环境
//         Profiles of = Profiles.of("dev", "test", "prev","default");
//         // 判断当前是否处于该环境
//         boolean flag = environment.acceptsProfiles(of);
//         return new Docket(DocumentationType.SWAGGER_2)
//                 .apiInfo(apiInfo())
//                 .groupName("用户")
//                 .enable(flag) // 配置是否启用Swagger
//                 .select() // 配置怎么扫描接口
//                 .apis(RequestHandlerSelectors.basePackage("com.qingxuan.controller"))
//                 .paths(PathSelectors.ant("/user/**"))
//                 .build();
//     }
//
//     //配置文档信息
//     private Contact contact = new Contact("QingXuan", "https://inangong.top", "1395568275@qq.com");
//     private ApiInfo apiInfo(){
//         return new ApiInfo(
//                 "QingXuan 的接口文档",
//                 "QingXuan 的接口文档--Swagger",
//                 "1.0",
//                 "http://terms.service.url/组织链接",
//                  contact,
//                 "Apache 2.0许可",
//                 "许可链接",
//                  new ArrayList());
//     }
// }
