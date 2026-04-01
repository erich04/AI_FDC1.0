# REST API 命名与设计规范（真相来源）

> 用途：本项目所有 **HTTP API** 的全局约定。以下条款**优先遵从技术专家组制定的 API 规范**；具体业务资源路径、字段与错误码枚举在 **各功能规格**（`/.docs/features/**`）中按功能落地。本文件定义**如何写**，不写完整业务端点清单。

---

## 1. 基础约定

| 项 | 约定 | 说明 |
|---|---|---|
| 基址占位 | `FDC_URL` | 网关/服务前缀，实现时替换为实际域名与 context-path |
| 版本 | 可选 | 若启用：`FDC_URL/v1/...`；版本策略在本文件或发布说明中统一 |
| 内容类型 | `application/json` | 请求/响应体默认 UTF-8 JSON |
| 时间格式 | ISO 8601 | 建议 `yyyy-MM-dd'T'HH:mm:ss.SSSXXX` 或团队统一时区约定 |

---

## 2. 资源 URI 命名约定（专家组）

- **使用小写和中横线**：使用 `/warehouse-areas` ✅，不使用 `/warehouseAreas` ❌ 或 `/warehouse_areas` ❌。
- **使用名词命名资源**：使用 `/warehouses` ✅，不使用 `/create-warehouses` ❌ 或 `/delete-warehouses` ❌。  
  > HTTP 方法 `GET`、`POST`、`PUT`、`PATCH`、`DELETE` 已表达动作含义。
- **集合 URI 使用复数名词**：使用 `/warehouses/{id}` ✅，不使用 `/warehouse/{id}` ❌。
- **保持关系简单、资源尽量扁平**：使用 `/warehouse-areas/{id}` ✅，避免过深嵌套，例如不使用 `/warehouses/{warehouseId}/areas/{areaId}` ❌。  
  > 例如 **库房 → 区域 → 档案柜 → 库位**：避免 `/warehouses/{warehouseId}/areas/{areaId}/cabinets/{cabinetId}/slots` ❌ 或 `/warehouses/areas/cabinets/slots` ❌ 等冗长或模糊路径；优先采用扁平资源名，如 `/warehouse-areas` ✅、`/warehouse-cabinets` ✅、`/warehouse-slots` ✅，便于维护与扩展。

### 2.1 路径段与 `{id}` 冲突说明（本项目）

若存在 `/resources/{id}` 与固定路径段（如 `/resources/template`）理论上可能冲突：**当前项目约定资源主键 `id` 均为数字**，固定功能路径使用非纯数字段名（如 `template`、`search`），故**不按字符串 id 设计**，无需额外规避。

---

## 3. HTTP 响应状态码规范（专家组）

| 响应状态码 | 类别 | 场景 |
|---|---|---|
| 200 OK | 成功响应 | 请求成功。 |
| 400 Bad Request | 客户端错误 | 请求数据语法错误、数据校验失败等。 |
| 401 Unauthorized | 客户端错误 | 未登录、登录凭据失效等。 |
| 403 Forbidden | 客户端错误 | 已登录但无访问权限。 |
| 404 Not Found | 客户端错误 | 访问的资源不存在。 |
| 429 Too Many Requests | 客户端错误 | 请求频率超出限制。 |
| 500 Internal Server Error | 服务端错误 | 处理请求出现异常等。 |

> 其他状态码（如 `204 No Content`）若使用，须在功能规格或本文档修订记录中说明，避免与上表语义冲突。

---

## 4. HTTP 请求方法规范（专家组）

以下以资源 `/warehouses` 为例说明**全项目复用的路径与方法模式**；其他业务资源将 `warehouses` 替换为对应的**小写 + 中横线 + 复数**集合名即可。

| 资源 | 请求方法 | 说明 |
|---|---|---|
| `/warehouses` | GET | 获取列表；简单筛选：`/warehouses?name={name}` |
| `/warehouses` | POST | 新增单个或列表 |
| `/warehouses` | PUT | 修改列表 |
| `/warehouses` | DELETE | 删除列表；简单筛选：`/warehouses?ids={id0}&ids={id1}&ids={id2}` |
| `/warehouses/{id}` | GET | 获取单个详情 |
| `/warehouses/{id}` | PUT | 修改单个 |
| `/warehouses/{id}` | DELETE | 删除单个 |
| `/warehouses/page` | GET | 列表 + 简单筛选 + 分页：`/warehouses?pageNumber={pageNumber}&pageSize={pageSize}&filter.name={name}`（`filter.*` 与实现约定一致） |
| `/warehouses/search` | POST | 列表，**复杂筛选** |
| `/warehouses/search-page` | POST | 列表，**复杂筛选 + 分页** |
| `/warehouses/save` | POST | 保存列表：**新增、修改、删除混合** |
| `/warehouses/delete` | POST | 删除列表，**复杂筛选**场景 |
| `/warehouses/template` | GET | 获取导入模板 |
| `/warehouses/import` | POST | 导入列表 |
| `/warehouses/export` | POST | 导出列表 |

> **PATCH**：单资源**部分更新**若采用 `PATCH /warehouses/{id}`，须在对应功能规格中单独约定请求体与幂等策略；未列出的资源默认以专家组上表模式为准。

---

## 5. HTTP 请求体规范（专家组）

### 5.1 筛选与分页同时使用

筛选条件应作为**分页查询对象的一个属性**，与分页字段并列，而非扁平混在顶层（与后端 `PaginationQuery` 体系一致）。

示例（Java 示意）：

```java
public class WarehousePaginationQuery extends PaginationQuery {
    /**
     * 筛选条件
     */
    private WarehouseQuery filter;
    // ...
}
```

功能规格中应写明：具体资源对应的 `*Query`、`*PaginationQuery` 字段名及 JSON 映射。

### 5.2 批量保存（新增 / 修改 / 删除混合）

请求体格式：

```json
{
    "toCreateList": [],
    "toUpdateList": [],
    "toDeleteList": []
}
```

### 5.3 列表类新增、修改、保存

请求中须携带**数据序号**（如行号、客户端生成序号等），以便校验失败时服务端返回**精准错误定位**（见第 6 节）。

---

## 6. HTTP 响应体规范（专家组）

### 6.1 按状态码区分

- **成功（2xx）**：直接返回**资源数据**（对象、列表或分页包装等，在功能规格中定义具体形状）。
- **失败（非 2xx）**：使用统一错误体：

```json
{
    "code": "{code}",
    "message": "{message}",
    "errors": []
}
```

- `errors`：**可选**；用于字段级、行级或多条校验错误列表。
- 可与 `/.docs/03_Security.md` 约定补充 `traceId` / `requestId`（若与安全审计一致，放在 Header 或错误体扩展字段，并在功能规格中统一）。

### 6.2 新增、修改、删除、保存列表

- 响应体**可以为空**（如 `200` + 空 body，或团队约定的空对象）。
- 若请求数据异常，须返回**精准错误定位**（与请求中的数据序号、字段路径对应），便于前端定位行/列。

---

## 7. 查询、分页与排序（与专家组对齐）

- **简单筛选 + 分页**：优先 `GET .../page` + Query（含 `pageNumber`、`pageSize` 及 `filter.*` 等）。
- **复杂筛选**：`POST .../search`；**复杂筛选 + 分页**：`POST .../search-page`。
- 排序、默认页大小等若在 Query 或 body 中传递，须在功能规格中写清参数名。

---

## 8. 请求头与安全

| Header | 说明 |
|---|---|
| `Authorization` | 鉴权（如 Bearer Token） |
| `X-Request-Id` / `traceparent` | 链路追踪 |
| `Idempotency-Key` | 导入、导出、批量保存等**建议**支持幂等（与实现协商） |

权限、脱敏、审计与 `/.docs/03_Security.md` 对齐。

---

## 9. 与功能文档的职责边界

| 文档 | 职责 |
|---|---|
| `05_API_Conventions.md`（本文） | URI 命名、状态码、方法/路径模式、请求/响应体格式 |
| `/.docs/features/<Fx>/...` | 各资源具体路径片段、`*Query`/DTO 字段、成功响应 JSON 示例、业务 `code` 与 `errors` 结构 |

---

## 10. 修订记录

| 版本 | 日期 | 变更摘要 |
|---|---|---|
| v0.1 | 2026-03-28 | 骨架初稿 |
| v0.2 | 2026-03-28 | 并入技术专家组：URI、状态码、方法路径表、请求/响应体规范 |
