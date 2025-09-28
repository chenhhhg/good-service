# Good-Service 后端项目

本项目是一个基于 Spring Boot 的 "好服务" 平台后端，旨在连接服务需求方和服务提供方。用户可以发布 "我需要"
的服务请求，也可以响应他人发布的需求，成为 "我服务" 的提供者。项目还包含了管理员角色，用于监控和统计平台数据。

## 功能特性

- **用户管理**: 用户注册、登录、信息查询与修改。
- **权限控制**: 基于 JWT (JSON Web Token) 的认证和授权机制，区分普通用户和管理员。
- **服务请求 (我需要)**: 用户可以创建、查询、修改、删除自己的服务需求。
- **服务响应 (我服务)**: 用户可以查询平台上的服务需求，并提交自己的服务方案；可以管理自己提交的响应。
- **文件上传**: 支持图片、视频等文件上传，用于丰富需求和响应的描述。
- **管理功能**: 管理员可以查询平台上的用户、服务请求、服务响应的列表。
- **统计分析**: 管理员可以按时间、地域等条件统计平台的服务发布和成功响应数据。

## 技术栈

- **核心框架**: Spring Boot
- **数据库**: MySQL
- **数据访问**: MyBatis
- **认证授权**: Spring Security, JWT
- **API 文档**: 本 README

---

## API 接口文档

所有接口的基路径为 `/api`。

### 1. 认证接口 (`/api/auth`)

#### **`POST /api/auth/register`**

- **功能**: 注册新用户。
- **请求体** (`application/json`):
  ```json
  {
    "username": "newuser",
    "password": "Password123",
    "name": "张三",
    "phone": "13800138000",
    "profile": "用户简介"
  }
  ```
- **成功响应** (`200 OK`): 返回创建的用户信息 (密码字段为空)。

#### **`POST /api/auth/login`**

- **功能**: 用户登录，获取 JWT Token。
- **请求体** (`application/json`):
  ```json
  {
    "username": "existinguser",
    "password": "Password123"
  }
  ```
- **成功响应** (`200 OK`):
  ```json
  {
    "token": "ey..."
  }
  ```

### 2. 用户接口 (`/api/users`)

- **认证要求**: 需要在请求头中携带有效的 JWT Token (`Authorization: Bearer <token>`)。

#### **`GET /api/users/me`**

- **功能**: 获取当前登录用户的信息。
- **成功响应** (`200 OK`): 返回当前用户的详细信息。

#### **`PUT /api/users/me`**

- **功能**: 更新当前登录用户的信息（目前支持电话、简介、密码）。
- **请求体** (`application/json`):
  ```json
  {
    "phone": "13900139000",
    "profile": "新的用户简介",
    "password": "NewPassword123"
  }
  ```
- **成功响应** (`200 OK`): 返回更新后的用户信息。

### 3. "我需要" 服务请求接口 (`/api/requests`)

- **认证要求**: 部分接口需要认证。

#### **`POST /api/requests`**

- **功能**: 创建新的服务请求。
- **认证要求**: 需要认证。
- **请求体**: `ServiceRequest` 对象 JSON。
- **成功响应** (`200 OK`): 返回创建成功的 `ServiceRequest` 对象。

#### **`GET /api/requests`**

- **功能**: 分页查询服务请求列表，可按服务类型、地域ID筛选。
- **查询参数**:
    - `serviceType` (String, 可选): 服务类型。
    - `regionId` (Long, 可选): 地域 ID。
    - `page` (int, 可选, 默认 1): 页码。
    - `size` (int, 可选, 默认 10): 每页数量。
- **成功响应** (`200 OK`): `ServiceRequest` 对象列表。

#### **`GET /api/requests/{id}`**

- **功能**: 根据 ID 查询单个服务请求的详情。
- **成功响应** (`200 OK`): `ServiceRequest` 对象。

#### **`GET /api/requests/user/{userId}`**

- **功能**: 根据用户 ID 分页查询该用户发布的所有服务请求。
- **成功响应** (`200 OK`): `ServiceRequest` 对象列表。

#### **`PUT /api/requests/{id}`**

- **功能**: 更新指定 ID 的服务请求。
- **认证要求**: 需要认证，且只能更新自己发布的、还未被响应的请求。
- **请求体**: `ServiceRequest` 对象 JSON。
- **成功响应** (`200 OK`): 返回更新后的 `ServiceRequest` 对象。

#### **`DELETE /api/requests/{id}`**

- **功能**: 删除指定 ID 的服务请求。
- **认证要求**: 需要认证，且只能删除自己发布的、还未被响应的请求。
- **成功响应** (`204 No Content`)。

#### **`GET /api/requests/types`**

- **功能**: 获取所有可用的服务类型列表。
- **成功响应** (`200 OK`): 字符串数组，例如 `["管道维修", "助老服务", ...]`。

### 4. "我服务" 服务响应接口 (`/api/responses`)

- **认证要求**: 需要认证。

#### **`POST /api/responses`**

- **功能**: 针对某个服务请求，创建新的服务响应。
- **请求体**: `ServiceResponse` 对象 JSON。
- **成功响应** (`200 OK`): 返回创建成功的 `ServiceResponse` 对象。

#### **`GET /api/responses/{id}`**

- **功能**: 根据 ID 查询单个服务响应的详情。
- **成功响应** (`200 OK`): `ServiceResponse` 对象。

#### **`GET /api/responses/request/{requestId}`**

- **功能**: 根据服务请求 ID，分页查询其收到的所有服务响应。
- **成功响应** (`200 OK`): `ServiceResponse` 对象列表。

#### **`GET /api/responses/user/{userId}`**

- **功能**: 根据用户 ID，分页查询该用户提交的所有服务响应。
- **成功响应** (`200 OK`): `ServiceResponse` 对象列表。

#### **`PUT /api/responses/{id}`**

- **功能**: 更新指定 ID 的服务响应。
- **认证要求**: 只能更新自己提交的、还未被接受的响应。
- **请求体**: `ServiceResponse` 对象 JSON。
- **成功响应** (`200 OK`): 返回更新后的 `ServiceResponse` 对象。

#### **`DELETE /api/responses/{id}`**

- **功能**: 删除指定 ID 的服务响应。
- **认证要求**: 只能删除自己提交的、还未被接受的响应。
- **成功响应** (`204 No Content`)。

#### **`PATCH /api/responses/{id}/status`**

- **功能**: 服务发布者接受或拒绝某个服务响应。
- **认证要求**: 只有服务请求的发布者才能操作。
- **查询参数**:
    - `rId` (Long, 必填): 对应的服务请求 ID。
    - `ts` (Integer, 必填): 目标状态。`1` 为同意, `2` 为拒绝。
- **成功响应** (`200 OK`): 返回更新状态后的 `ServiceResponse` 对象。

### 5. 文件上传接口 (`/api/files`)

#### **`POST /api/files/upload`**

- **功能**: 上传单个文件。
- **请求体**: `multipart/form-data`，包含一个名为 `file` 的文件。
- **成功响应** (`200 OK`):
  ```json
  {
    "fileName": "generated-uuid-filename.ext",
    "fileDownloadUri": "http://localhost:8080/api/files/download/generated-uuid-filename.ext"
  }
  ```

#### **`GET /api/files/download/{fileName}`**

- **功能**: 根据文件名下载文件。
- **成功响应**: 文件流。

### 6. 管理员接口 (`/api/admin`)

- **认证要求**: 需要管理员权限。

#### **`GET /api/admin/users`**

- **功能**: 分页查询所有用户信息。
- **成功响应** (`200 OK`): `User` 对象列表。

#### **`GET /api/admin/requests`**

- **功能**: 分页查询所有服务请求。
- **成功响应** (`200 OK`): `ServiceRequest` 对象列表。

#### **`GET /api/admin/responses`**

- **功能**: 分页查询所有服务响应。
- **成功响应** (`200 OK`): `ServiceResponse` 对象列表。

### 7. 统计接口 (`/api/admin/stats`)

- **认证要求**: 需要管理员权限。

#### **`GET /api/admin/stats/monthly`**

- **功能**: 获取月度统计数据。
- **查询参数**:
    - `startMonth` (String, 可选, 格式 `yyyy-MM`): 开始月份。
    - `endMonth` (String, 可选, 格式 `yyyy-MM`): 结束月份。
    - `region` (String, 可选): 地域名称。
    - `success` (boolean, 可选, 默认 `false`): 是否只看成功响应的。
- **成功响应** (`200 OK`): `MonthlyStats` 对象列表。
