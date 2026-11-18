package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 新增/编辑权限请求参数。
 */
@Data
public class PermissionSaveRequest implements Serializable {

    /** 编辑时必传，新增时为空 */
    private Integer id;
    private String name;
    private String code;
    /** 0 表示顶级权限 */
    private Integer parentId = 0;
    /** 1-目录，2-菜单，3-按钮 */
    private Integer type = 1;
    private String module;
    private Integer sort = 0;
    private Integer status = 1;
}
