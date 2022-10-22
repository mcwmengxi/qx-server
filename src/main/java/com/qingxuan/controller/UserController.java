package com.qingxuan.controller;

import com.qingxuan.mapper.UserMapper;
import com.qingxuan.pojo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

@Api(tags = "用户模块")
@RestController
public class UserController {
    @Autowired
    UserMapper userMapper;

    @ApiOperation("查询用户列表")
    @GetMapping("/users")
    public List<User> getUserList(){
        List<User> userlist = userMapper.getUserList();
        return userlist;
    }
    @ApiOperation("查询用户信息")
    @ApiImplicitParam(name = "id", value = "用户id", required = true)
    @GetMapping("/user/{id}")
    public User getUserInfo( @PathVariable("id") Integer id){
        return userMapper.getUserInfo(id);
    }
}
