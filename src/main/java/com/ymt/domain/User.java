package com.ymt.domain;

import lombok.Data;

@Data
public class User {
    private Long id;

    private String openId;

    private String nickName;

    private String avatarUrl;

    /**
     * 用户总积分，用于兑换
     */
    private Long totalScore;

    /**
     * 绑定的另一半用户id
     */
    private Long bindId;
}