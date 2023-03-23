package com.ymt.domain;

import lombok.Data;

@Data
public class MediaBaseInfo {
    private String id;

    private String title;

    private String describe;

    private String date;

    private String address;

    private String addressDetail;

    /**
     * 资源类型 0:图 1:视频
     */
    private Integer mediaType;

    /**
     * 任务列表id
     */
    private Integer typeId;

    /**
     * user表id
     */
    private Long userId;
}