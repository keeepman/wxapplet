package com.ymt.domain;

import lombok.Data;

@Data
public class Audit {
    private Long id;

    /**
     * 提交时间
     */
    private String subTime;

    /**
     * 审核时间
     */
    private String auditTime;

    private String mediaBaseInfoId;

    /**
     * 0:未审核，1:审核通过，2:审核未通过
     */
    private Integer status;

    private Long userId;

    private Integer typeId;
}