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
