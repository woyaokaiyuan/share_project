-- =============================================================
-- 权限管理系统（sys_manager）表结构
-- 目标数据库：MySQL 8.4
--
-- 在原始 PostgreSQL 设计基础上做了如下完善：
--   1. 补充「用户-角色关联表」sys_user_role（原设计缺失，用户无法分配角色）
--   2. 所有表显式定义主键；关联表增加唯一约束防止重复授权
--   3. 补充外键约束（级联删除）与常用查询字段索引
--   4. 类型/函数改为 MySQL 语法：varchar、datetime、CURRENT_TIMESTAMP、
--      AUTO_INCREMENT、反引号标识符
--   5. status / user_type / type 使用 tinyint（枚举值范围小）
--
-- 说明：
--   - sys_user.id 沿用原设计的 varchar(32)，由应用层生成（如 UUID），不自增。
--   - sys_permission.parent_id 用 0 表示顶级节点，因此不建立自引用外键
--     （0 没有对应记录会导致外键校验失败），仅建索引由应用层保证层级。
--     如需强约束，可先插入 id=0 的虚拟根节点后再补上外键。
--   - 本脚本不包含 DROP TABLE，避免误删已有数据；如需重建请手动处理。
-- =============================================================

CREATE DATABASE IF NOT EXISTS `sys_manager`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `sys_manager`;


-- ----------------------------
-- 1. 系统用户表
-- ----------------------------
CREATE TABLE `sys_user` (
  `id`           varchar(32)  NOT NULL                COMMENT '主键，应用层生成（如 UUID）',
  `username`     varchar(50)  NOT NULL                COMMENT '用户名',
  `password`     varchar(255) NOT NULL                COMMENT '密码（BCrypt 等哈希值）',
  `nickname`     varchar(50)  DEFAULT NULL            COMMENT '昵称',
  `email`        varchar(100) DEFAULT NULL            COMMENT '邮箱',
  `phone`        varchar(20)  DEFAULT NULL            COMMENT '手机号',
  `status`       tinyint      DEFAULT 1               COMMENT '状态：1-启用，0-禁用',
  `create_time`  datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`  datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_type`    tinyint      DEFAULT 0               COMMENT '用户类型：0-普通用户，1-超级管理员',
  `perm_version` int          NOT NULL DEFAULT 0      COMMENT '权限版本号，权限变更时递增以触发缓存失效',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';


-- ----------------------------
-- 2. 系统角色表
-- ----------------------------
CREATE TABLE `sys_role` (
  `id`          int          NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `name`        varchar(50)  NOT NULL                  COMMENT '角色名称',
  `code`        varchar(50)  NOT NULL                  COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL              COMMENT '角色描述',
  `status`      tinyint      DEFAULT 1                 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';


-- ----------------------------
-- 3. 系统权限表（菜单 / 按钮）
-- ----------------------------
CREATE TABLE `sys_permission` (
  `id`          int          NOT NULL AUTO_INCREMENT   COMMENT '主键',
  `name`        varchar(100) NOT NULL                  COMMENT '权限名称',
  `code`        varchar(200) NOT NULL                  COMMENT '权限编码（如 system:user:list）',
  `parent_id`   int          NOT NULL DEFAULT 0        COMMENT '父级权限ID，0 表示顶级',
  `type`        tinyint      DEFAULT 1                 COMMENT '类型：1-目录，2-菜单，3-按钮',
  `module`      varchar(50)  DEFAULT NULL              COMMENT '所属模块',
  `sort`        int          DEFAULT 0                 COMMENT '排序值',
  `status`      tinyint      DEFAULT 1                 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`code`),
  KEY `idx_perm_parent` (`parent_id`),
  KEY `idx_perm_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统权限表';


-- ----------------------------
-- 4. 用户-角色关联表（原设计缺失）
-- ----------------------------
CREATE TABLE `sys_user_role` (
  `id`      int         NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` varchar(32) NOT NULL                 COMMENT '用户ID',
  `role_id` int         NOT NULL                 COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ur_user_role` (`user_id`, `role_id`),
  KEY `idx_ur_role` (`role_id`),
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-角色关联表';


-- ----------------------------
-- 5. 角色-权限关联表
-- ----------------------------
CREATE TABLE `sys_role_permission` (
  `id`            int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id`       int NOT NULL                 COMMENT '角色ID',
  `permission_id` int NOT NULL                 COMMENT '权限ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rp_role_perm` (`role_id`, `permission_id`),
  KEY `idx_rp_permission` (`permission_id`),
  CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rp_permission` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色-权限关联表';


-- ----------------------------
-- 6. 初始管理员账号（密码 123456，BCrypt 哈希）
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `email`, `status`, `user_type`, `perm_version`)
VALUES ('1', 'admin', '$2a$10$qmK0pxz2EegdpBFXemNpd.u1Yz85rmflPLHnTbOdjeeXbMD9pAu36', '系统管理员', 'admin@share.com', 1, 1, 0);
