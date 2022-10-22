# SpringBoot学习(二)

## SpringBoot 搭建Swagger接口文档

### 搭建环境

>添加依赖
```xml
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger2 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<!-- https://mvnrepository.com/artifact/io.springfox/springfox-swagger-ui -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```
>SwaggerConfig配置类

```java
package com.qingxuan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @Author luo
 * @Date 2022/10/16 - 18:00
 */
@Configuration //配置类
@EnableSwagger2 // 开启Swagger2的自动配置
public class SwaggerConfig {
    // Swagger实例Bean是Docket
    @Bean
    public Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select() // 配置怎么扫描接口
                .apis(RequestHandlerSelectors.basePackage("com.qingxuan.controller"))
                .build();
    }

    //配置文档信息
    private Contact contact = new Contact("QingXuan", "https://inangong.top", "1395568275@qq.com");
    private ApiInfo apiInfo(){
        return new ApiInfo(
                "QingXuan 的接口文档",
                "QingXuan 的接口文档--Swagger",
                "1.0",
                "http://terms.service.url/组织链接",
                 contact,
                "Apache 2.0许可",
                "许可链接",
                 new ArrayList());
    }
}

```

```text
RequestHandlerSelectors配置扫描接口方式

any() // 扫描所有，项目中的所有接口都会被扫描到
none() // 不扫描接口
// 通过方法上的注解扫描，如withMethodAnnotation(GetMapping.class)只扫描get请求
withMethodAnnotation(final Class<? extends Annotation> annotation)
// 通过类上的注解扫描，如.withClassAnnotation(Controller.class)只扫描有controller注解的类中的接口
withClassAnnotation(final Class<? extends Annotation> annotation)
basePackage(final String basePackage) // 根据包路径扫描接口
```

>配置接口扫描过滤

```text
PathSelectors 配置接口扫描过滤

any() // 任何请求都扫描
none() // 任何请求都不扫描
regex(final String pathRegex) // 通过正则表达式控制
ant(final String antPattern) // 通过ant()控制
```

>配置Swagger开关

enable()方法配置是否启用swagger

```java
@Bean
public Docket docket(Environment environment){
    // 设置要显示swagger的环境
    Profiles of = Profiles.of("dev", "test", "prev");
    // 判断当前是否处于该环境
    boolean flag = environment.acceptsProfiles(of);

    return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .enable(flag) // 配置是否启用Swagger
            .select() // 配置怎么扫描接口
            .apis(RequestHandlerSelectors.basePackage("com.qingxuan.controller"))
            .paths(PathSelectors.ant("/user/**"))
            .build();
}
```

>配置API分组

每一个docket实例代表一个分组，通过groupName()方法控制分组名称

>文档注释配置

**实体类注释配置**
```java
@ApiModel("用户实体")
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String avatar;
    @ApiModelProperty("签名")
    private String autograph;
    ...
}
```

>接口描述信息配置

```java
@ApiOperation("查询用户信息")
@GetMapping("/user/{id}")
public User get(@ApiParam("用户id") @PathVariable("id") Integer id){
    return userMapper.getUserInfo(id);
}
```

**常用的Swagger注解**


|   Swagger注解   |   简单说明   |
| ---- | ---- |
| @Api(tags = "xxx模块说明")   |   作用在模块类上   |
| @ApiOperation("xxx接口说明")  |  作用在接口方法上 |
| @ApiModel("xxxPOJO说明")	| 作用在模型类上：如VO、BO |
| @ApiModelProperty(value = "xxx属性说明",hidden = true)  |  作用在类方法和属性上，hidden设置为true可以隐藏该属性 |
| @ApiParam("xxx参数说明")  |作用在参数、方法和字段上，类似@ApiModelProperty |

>Swagger生成Api文档的增强解决方案

Knife4j-spring-boot-starter

添加依赖
```xml
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-spring-boot-starter</artifactId>
    <version>2.0.9</version>
</dependency>
```

配置knife4j或者swaggerui基本配置信息
```java
// Knife4jConfig.java 配置类
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Knife4jConfig {
    @Bean
    public Docket docket(Environment environment) {
        // 添加接口请求头参数配置 没有的话 可以忽略
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("token")
                .description("令牌")
                .defaultValue("")
                .modelRef(new ModelRef("string"))
                .parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //是否启动swagger 默认启动
                .enable(true)
                //所在分组
                .groupName("用户")
                .select()
                //指定扫描的包路径
                .apis(RequestHandlerSelectors.basePackage("com.qingxuan.controller"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .globalOperationParameters(pars);
    }

    private ApiInfo apiInfo() {
        Contact author = new Contact("QingXuan", "地址", "邮箱");
        return new ApiInfo(
                "QingXuan文档",
                "QingXuan文档",
                "1.0",
                "",
                author,
                "",
                "",
                new ArrayList()
        );

    }

}
```

放行Knife4j请求
```java
package com.qingxuan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author luo
 * @Date 2022/10/16 - 20:20
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        // registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        //registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

        /** 配置knife4j 显示文档 */
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        /**
         * 配置swagger-ui显示文档
         */
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        /** 公共部分内容 */
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
```