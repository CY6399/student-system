# CLAUDE.md

## 项目概述

本项目是**教学数字平台的管理系统**，为教育机构提供数字化教学管理功能。

## 技术选型

### 前端
- **HTML + CSS + JavaScript**（原生三件套）
- **Vue 3**（通过 CDN 引入，使用 Options API 或 Composition API 均可）
- **Element Plus**（UI 组件库，通过 CDN 引入）
- **ECharts**（图表库，通过 CDN 引入）

> 所有前端依赖均通过 CDN 引入，**禁止使用 npm / webpack / vite 等构建工具**。前端代码为纯静态文件，直接在浏览器中运行。

### 后端
- **Java**（JDK 版本在 `pom.xml` 中指定）
- **Spring Boot**（自动配置、内嵌 Tomcat）
- **MyBatis**（持久层框架，使用 XML Mapper 或注解）
- **Maven**（构建与依赖管理）

### 数据库
- **MySQL**

## 项目结构

```
dll/
├── CLAUDE.md          # 本文件，项目开发约定与说明
├── doc/               # 文档（需求文档、设计文档、接口文档等）
├── front/             # 前端页面（纯静态 HTML / CSS / JS 文件）
└── backend/           # 后端代码（Maven 项目，Spring Boot + MyBatis）
```

## 后端开发规范

### Java 代码规范

- 严格遵循**阿里巴巴 Java 开发手册**规范，包括但不限于：
  - 命名规范：类名 UpperCamelCase，方法名/变量名 lowerCamelCase，常量 UPPER_SNAKE_CASE
  - 单方法不超过 80 行，单类不超过 1000 行
  - 所有 POJO 类属性使用包装类型，RPC 方法返回值禁止使用枚举
  - equals 方法使用 Objects.equals() 避免 NPE
  - 循环体内字符串拼接使用 StringBuilder
- 分层架构：`controller` → `service` → `mapper`（DAO 层）
- Controller 层负责参数校验和路由，Service 层负责业务逻辑，Mapper 层负责数据访问
- 禁止在 Controller 中直接调用 Mapper，必须经过 Service 层

### 接口规范

- 所有 REST 接口地址统一以 `/api/` 开头
- 统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- 成功时 `code` 为 `200`，失败时 `code` 为对应错误码（如 `400` 参数错误、`500` 服务器错误），`message` 描述失败原因
- 分页查询返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],
    "total": 100,
    "page": 1,
    "pageSize": 10
  }
}
```

### 参数校验

- 后端接口**必须**进行参数校验，使用 `@Valid` / `@Validated` 注解 + JSR-303 校验注解（`@NotNull`、`@NotBlank`、`@Size` 等）
- 禁止在业务代码中手动进行基础参数 null/空 判断，应使用校验框架统一处理

### 数据库操作

- **所有 SQL 查询必须使用参数化方式（MyBatis 的 `#{}`），禁止使用 `${}` 拼接用户输入**
- 仅在排序字段、表名等非用户输入的动态场景下才可使用 `${}`
- 禁止在 Java 代码中拼接 SQL 字符串

### 数据库表设计规范

- 每张表**必须**包含以下 4 个字段：

| 字段名       | 类型          | 说明               |
| ------------ | ------------- | ------------------ |
| id           | BIGINT        | 主键，自增         |
| create_time  | DATETIME      | 创建时间           |
| update_time  | DATETIME      | 更新时间           |
| is_deleted   | TINYINT       | 逻辑删除（0=正常，1=已删除） |

- 数据库操作使用逻辑删除（`is_deleted = 1`），禁止物理删除

## 前端开发规范

### 页面结构

- 每个页面为独立的 HTML 文件，内嵌 `<style>` 和 `<script>` 或通过相对路径引用同目录下的 CSS/JS 文件
- 使用 Vue 3 + Element Plus 构建 UI
- 图表面板使用 ECharts

### 输入校验

- **前端表单必须做输入校验**，在提交前验证必填项、格式、长度等
- 校验规则参考 Element Plus 的 Form Validation
- 常见校验项：必填、最大/最小长度、手机号格式、邮箱格式、数字范围等

### CDN 引入规范

- Vue 3、Element Plus、ECharts 均通过 CDN 引入（如 unpkg / jsdelivr）
- CDN 链接固定在页面 `<head>` 或 `<body>` 底部统一引入
- 禁止在页面中混用不同版本的 CDN 资源

## 开发约定

### 修改原则

- **一次只改一个文件**：每次提交 / 修改聚焦在单个文件上，便于代码审查和回滚
- 修改完成后要确保前后端接口对齐

### 接口开发流程

1. 在 `doc/` 中编写接口设计文档（请求参数、返回字段、业务逻辑说明）
2. 后端实现 Controller → Service → Mapper
3. 前端实现页面和 API 调用
4. 前后端联调验证

### 注释规范

- 仅在业务逻辑复杂、存在隐式约束、或代码意图不直观时添加注释
- 注释说明 **WHY**（为什么这样做），而不是 **WHAT**（做了什么）
- 禁止大段无意义的 JavaDoc 或多行注释块

## 强制检查点规则

**每完成一个独立任务后，必须暂停并与开发者确认以下三项，获得批准后方可继续：**

1. 刚才产出了什么（文件清单、关键代码片段摘要）
2. 开发者确认了哪些内容（需求理解是否正确、实现方式是否符合预期）
3. 是否批准进入下一步任务

> 在开发者明确批准之前，**禁止自行开始下一步工作**。此规则适用于所有开发任务，无一例外。
