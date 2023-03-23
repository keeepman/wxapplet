package com.ymt.domain;

import lombok.Data;

@Data
public class ListType {
    private Integer id;

    private String name;

    private String iconUrl;

    /**
     * 积分，用来记录兑换
     */
    private Integer score;
}