package com.qingxuan.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
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
@ApiModel("用户实体")
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String avatar;
    @ApiModelProperty("签名")
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
