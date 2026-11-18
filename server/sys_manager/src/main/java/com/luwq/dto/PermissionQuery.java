package com.luwq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PermissionQuery implements Serializable {

    private String name;
    private String code;
}
