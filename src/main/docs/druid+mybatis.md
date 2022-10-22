# SpringBoot学习(一)

## SpringBoot 整合Druid和mybatis框架

### 整合Druid
alibaba开源的数据库连接池,相比于Hikari数据源，加入了日志监控

```java
<!-- https://mvnrepository.com/artifact/com.alibaba/druid -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.8</version>
</dependency>

```
**自定义数据源**

Spring Boot 2.0 以上默认使用 com.zaxxer.hikari.HikariDataSource 数据源，
但可以 通过 spring.datasource.type 自定义数据源。
```yaml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/react_blog?useSSL=false&characterEncoding=utf-8&useUnicode=true&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource # 自定义数据源
```
**配置属性**

```yaml

spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://localhost:3306/react_blog?useSSL=false&characterEncoding=utf-8&useUnicode=true&serverTimezone=Asia/Shanghai
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource # 自定义数据源

    #Spring Boot 默认是不注入这些属性值的，需要自己绑定
    #druid 数据源专有配置
    initialSize: 5
    minIdle: 5
    maxActive: 20
    maxWait: 60000
    timeBetweenEvictionRunsMillis: 60000
    minEvictableIdleTimeMillis: 300000
    validationQuery: SELECT 1 FROM DUAL
    testWhileIdle: true
    testOnBorrow: false
    testOnReturn: false
    poolPreparedStatements: true

    #配置监控统计拦截的filters，stat:监控统计、log4j：日志记录、wall：防御sql注入
    #如果允许时报错  java.lang.ClassNotFoundException: org.apache.log4j.Priority
    #则导入 log4j 依赖即可，Maven 地址：https://mvnrepository.com/artifact/log4j/log4j
    filters: stat,wall,log4j
    maxPoolPreparedStatementPerConnectionSize: 20
    useGlobalDataSourceStat: true
    connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
```

log4j
```xml
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```
自定义Druid数据源初始化配置

```java
package com.qingxuan.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Author luo
 * @Date 2022/10/15 - 0:01
 */
@Configuration
public class DruidConfig {
    /*
   将自定义的 Druid数据源添加到容器中，不再让 Spring Boot 自动创建
   绑定全局配置文件中的 druid 数据源属性到 com.alibaba.druid.pool.DruidDataSource从而让它们生效
   @ConfigurationProperties(prefix = "spring.datasource")：作用就是将 全局配置文件中
   前缀为 spring.datasource的属性值注入到 com.alibaba.druid.pool.DruidDataSource 的同名参数中
 */
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource DruidDataSource(){
        return new DruidDataSource();
    }
}

```

测试
```test
package com.qingxuan.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author luo
 * @Date 2022/10/15 - 0:05
 */
@SpringBootTest
public class DruidConfigTest {
    @Autowired
    DataSource dataSource;
    @Test
    void contextLoads() throws SQLException {
        System.out.println("默认数据源"+ dataSource.getClass());

        Connection connection = dataSource.getConnection();
        System.out.println(connection);
        DruidDataSource druidDataSource = (DruidDataSource) dataSource;

        System.out.println("最大连接数： " + druidDataSource.getMaxActive());
        System.out.println("初始化连接数: " +druidDataSource.getInitialSize());
        connection.close();
    }
}

```


>配置Druid数据源监控

创建ServletRegistrationBean对象不加入"/druid/*"参数会无限重定向

```java

//配置 Druid 监控管理后台的Servlet；
//内置 Servlet 容器时没有web.xml文件，所以使用 Spring Boot 的注册 Servlet 方式
@Bean
public ServletRegistrationBean StatViewServlet(){
    ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(),"/druid/*");

    // 这些参数可以在 com.alibaba.druid.support.http.StatViewServlet
    // 的父类 com.alibaba.druid.support.http.ResourceServlet 中找到
    Map<String, String> initParams = new HashMap<>();
    //后台管理界面账号和密码
    initParams.put("loginUsername", "admin");
    initParams.put("loginPassword", "940618");
    //后台允许谁可以访问
    //initParams.put("allow", "localhost")：表示只有本机可以访问
    //initParams.put("allow", "")：为空或者为null时，表示允许所有访问
    initParams.put("allow","");
    //deny：Druid 后台拒绝谁访问
    //initParams.put("sxzz", "192.168.1.20");表示禁止此ip访问

    //设置初始化参数
    bean.setInitParameters(initParams);
    return bean;
}
```

>配置 Druid web 监控 filter 过滤器

```java
@Bean
public FilterRegistrationBean WebStatFilter(){
    FilterRegistrationBean<Filter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new WebStatFilter());
    //exclusions：设置哪些请求进行过滤排除掉，从而不进行统计
    HashMap<String, String> initParams = new HashMap<>();
    initParams.put("exclusions", "*.js,*.css,/druid/*,/jdbc/*");

    bean.setInitParameters(initParams);
    //"/*" 表示过滤所有请求
    bean.setUrlPatterns(Arrays.asList("/*"));
    return bean;
}
```

### 整合MyBatis

>导入 MyBatis 所需要的依赖

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.4</version>
</dependency>
<!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.24</version>
    <scope>provided</scope>
</dependency>
```

```yaml
# 整合mybatis
mybatis:
  type-aliases-package: com.qingxuan.pojo
  mapper-locations: classpath:mybatis/mapper/*.xml
```

>创建实体类

```java
package com.qingxuan.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author luo
 * @Date 2022/10/15 - 16:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String avatar;
    private String autograph;
    private String post;
    private String address;
    private String tags;
    private String mobile;
    private String wxReward;
    private String zfbReward;
    private String cover;
    private String wbUid;
    private Integer songId;
}

```

>创建mapper目录以及对应的Mapper接口和sql

```java
package com.qingxuan.mapper;

import com.qingxuan.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author luo
 * @Date 2022/10/15 - 16:26
 */
@Mapper
@Repository
public interface UserMapper {
    // 获取所有用户信息
    List<User> getUserList();

    // 通过id获得用户
    User getUserInfo(Integer id);
}

```

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >

<mapper namespace="com.qingxuan.mapper.UserMapper">
    <select id="getUserList" resultType="User">
        select * from user
    </select>

    <select id="getUserInfo" parameterType="int" resultType="User">
        select * from user where id = #{id}
    </select>
</mapper>
```

>maven配置资源过滤问题 pom.xml

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

>contrller验证,直接测试，就不另外写测试类了

```java
package com.qingxuan.controller;

import com.qingxuan.mapper.UserMapper;
import com.qingxuan.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author luo
 * @Date 2022/10/15 - 18:20
 */
@RestController
public class UserController {
    @Autowired
    UserMapper userMapper;

    @GetMapping("/users")
    public List<User> getUserList(){
        List<User> userlist = userMapper.getUserList();
        return userlist;
    }

    @GetMapping("/user/{id}")
    public User get(@PathVariable("id") Integer id){
        return userMapper.getUserInfo(id);
    }
}

```