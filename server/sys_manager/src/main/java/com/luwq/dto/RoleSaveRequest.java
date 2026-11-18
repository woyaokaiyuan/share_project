package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增/编辑角色请求参数。
 */
@Data
public class RoleSaveRequest implements Serializable {

    /** 编辑时必传，新增时为空 */
    private Integer id;
    private String name;
    private String code;
    private String description;
    private Integer status = 1;
}
