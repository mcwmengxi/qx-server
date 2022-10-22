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
