# share_project

前后端分离的后台管理系统项目。

## 技术栈与环境要求

### 后端（server/）

| 项目 | 版本 |
|------|------|
| JDK | **21** |
| Spring Boot | **3.5.1** |
| Maven | 3.9.16（推荐稳定版） |
| 数据库 | MySQL 8.4 |

后端为多模块 Maven 工程：
- `sys_common` —— 公共模块
- `sys_manager` —— 业务模块（含启动类 `com.luwq.SysManagerApplication`）

### 前端（web/）

| 项目 | 版本 |
|------|------|
| Node.js | **22 LTS**（最低 18） |
| Vue | 3 |
| 构建工具 | Vite |
| UI 框架 | Element Plus（vue-element-plus-admin 风格） |
| 包管理器 | pnpm（推荐，也可用 npm） |

前端详细说明见 [`web/README.md`](web/README.md)。

## 目录结构

```
share_project/
├── server/     # 后端（Spring Boot 多模块 Maven 工程）
│   ├── sys_common/
│   └── sys_manager/
├── web/        # 前端（Vue3 后台管理）
├── sql/        # 数据库脚本
├── LICENSE     # MIT 开源协议
└── README.md   # 本文件
```

## 开源协议

本项目采用 [MIT License](LICENSE)。
