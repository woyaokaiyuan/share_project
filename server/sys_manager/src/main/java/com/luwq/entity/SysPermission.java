package com.luwq.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统权限实体，对应 sys_permission 表。
 */
@Data
public class SysPermission implements Serializable {

    private Integer id;
    private String name;
    private String code;
    private Integer parentId;
    private Integer type;
    private String module;
    private Integer sort;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
