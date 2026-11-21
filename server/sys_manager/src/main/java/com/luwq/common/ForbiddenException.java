package com.luwq.common;

/**
 * 无权限异常，由权限切面抛出，全局异常处理器转换为业务码 403。
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
