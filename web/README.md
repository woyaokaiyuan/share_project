# Share Admin 后台管理系统

基于 Vue 3 + Vite + TypeScript + Element Plus + Pinia 的后台管理框架（vue-element-plus-admin 风格），包含登录、动态路由、RBAC 权限与系统管理示例页面。

## 环境要求

- Node.js **22 LTS**（最低 18）
- 包管理器 **pnpm**（推荐，也可用 npm）

## 启动方式

```bash
npm install -g pnpm

# 进入项目目录
cd d:\share_project\web

# 安装依赖
pnpm install

# 启动开发服务器（默认 http://localhost:5173）
pnpm dev
```

当前 `.env.development` 中 `VITE_USE_MOCK=true`，为 **Mock 演示模式**，任意账号密码即可登录，无需后端。

## 对接后端

1. 将 `.env.development` 中的 `VITE_USE_MOCK` 改为 `false`；
2. `vite.config.ts` 已将 `/api` 代理到 `http://localhost:8080`（对应 Spring Boot 后端），按需修改 `target`；
3. 后端需提供以下接口：
   - `POST /auth/login` —— 登录，返回 `{ code, message, data: { token, id, username, nickname, roles, permissions } }`
   - `GET /auth/userInfo` —— 获取当前用户信息
   - `POST /auth/logout` —— 退出登录

## 其他命令

```bash
pnpm typecheck   # TypeScript 类型检查
pnpm build       # 生产构建
pnpm preview     # 预览生产构建
```

## 目录结构

```
src/
├── api/        # 请求封装与接口
├── router/     # 路由表与鉴权守卫
├── store/      # Pinia：user / permission
├── layout/     # 后台整体布局
├── views/      # 登录、首页、系统管理页面
├── utils/      # token 工具
└── types/      # 类型定义
```

> 注：`vite.config.uniapp.js.bak` 是原有 uni-app 项目配置的备份，如需恢复 uni-app 开发，将其改回 `vite.config.js` 并把 `vite.config.ts` 移走即可（两者不能同时生效）。
