package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户列表查询条件。
 */
@Data
public class UserQuery implements Serializable {

    private String username;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;

    public int getOffset() {
        int currentPage = page == null || page < 1 ? 1 : page;
        int pageSize = size == null || size < 1 ? 10 : size;
        return (currentPage - 1) * pageSize;
    }
}
