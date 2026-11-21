package com.luwq.aspect;

import com.luwq.annotation.RequirePermission;
import com.luwq.common.ForbiddenException;
import com.luwq.common.PermissionChecker;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限切面：拦截带 @RequirePermission 的方法/类，执行前校验权限。
 */
@Aspect
@Component
public class PermissionAspect {

    private final PermissionChecker permissionChecker;

    public PermissionAspect(PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
    }

    @Before("@annotation(required) || @within(required)")
    public void check(JoinPoint joinPoint, RequirePermission required) {
        // 方法上的注解优先于类上的注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        if (annotation == null) {
            annotation = required;
        }
        if (annotation == null) {
            return;
        }
        if (!permissionChecker.hasPermission(annotation.value(), annotation.anyOf())) {
            throw new ForbiddenException("没有操作权限");
        }
    }
}
