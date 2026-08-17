# 前端（xdzn-home）适配 Java 后端方案

> 场景：`xdzn-home/apps/web`（Next.js 展示站 + 管理后台）原对接 `apps/server`（NestJS + Prisma），现切换到 Java 后端 `xdzn_home_backend`。
> 目标：以**最小前端改动**完成对接，不改 Java 后端统一 `Result` 设计。

---

## 一、兼容性对照（现状）

| 维度 | NestJS（前端契约） | Java 后端 | 是否一致 |
|---|---|---|---|
| 路径前缀 | `/api/...` | `/api/...` | ✅ |
| 字段命名 | camelCase（graduationYear/currentCompany） | camelCase（Jackson 默认） | ✅ |
| `user.role` | 需返回 `"admin"` | `StpInterface` 查 users.role | ✅ |
| join `status` 枚举 | pending/contacted/accepted/rejected | 同枚举 | ✅ |
| CORS | 允许 FRONTEND_URL | `CorsConfig` 允许 `app.frontend-url` | ✅ |
| **响应包装** | **裸数据**（数组/对象直接返回） | **`Result{code,msg,data}`** | ❌ **需适配** |
| **认证** | **JWT + refresh_token Cookie + `/api/auth/refresh`** | **Sa-Token 无 refresh_token，active-timeout 滑动续期** | ❌ **需适配** |
| `/api/auth/refresh` | 前端调用 | **不存在** | ❌ **需适配** |
| id 类型 | 前端 `id: string` | Java `Long`（JSON number） | ⚠️ JS 运行时兼容 |
| 未知字段（如 `order`） | POST 会带 | Spring Boot 默认忽略未知字段 | ✅ |
| `GET /api/joins` | 管理后台需 admin | 当前**公开**（安全隐患） | ❌ **需补保护** |

---

## 二、核心差异与适配方案

### 差异 1：响应包装（最影响前端）

前端期望裸数据，Java 统一返回 `{code, msg, data}`。解包后**契约完全一致**：

```
GET  /api/members      → 前端拿 Member[]      （Java data 是 List<Member>）✓
POST /api/auth/login   → 前端拿 {access_token, user}（Java data 是 {access_token, user}）✓
GET  /api/admin/dashboard → 前端拿 {stats, recentJoins}（Java data 是 DashboardVO）✓
```

**适配**：前端 `lib/api.ts` 统一解包（见下），**不动 Java**。

### 差异 2：认证（Sa-Token 无 refresh_token）

前端目前依赖：
- `Authorization: Bearer <access_token>`（Java 一致 ✅）
- httpOnly Cookie `refresh_token` + `POST /api/auth/refresh`（Java **无** ❌）

**适配（改前端）**：
- 去掉 `refreshAccessToken()` 与 `/api/auth/refresh` 调用；
- `fetchApiWithAuth` 遇 **401 直接登出**（清会话 → 跳 /login）；
- 依赖 Sa-Token `active-timeout`（30 天）：用户 30 天内活跃 token 永续，**无需 refresh**；401 意味着 30 天不活跃，重新登录即可，体验可接受。
- `login`/`me` 响应结构与 Java 完全一致，无需改。

---

## 三、前端改动点（`xdzn-home/apps/web`）

### 1. `src/lib/api.ts`（核心：解包 + 去 refresh）

```ts
// ① 统一解包 Result
async function parseJson<T>(res: Response): Promise<T> {
  const body = await res.json();
  // Java 统一响应 {code, msg, data}
  if (body && typeof body === "object" && "code" in body) {
    if (body.code === 200) return body.data as T;
    throw new ApiError(body.code, body.msg ?? "请求失败");
  }
  return body as T; // 兜底（原裸数据）
}

// ② fetchApiWithAuth：去掉 refreshAccessToken，401 直接登出
export async function fetchApiWithAuth<T>(endpoint, options): Promise<T> {
  const res = await requestWithAuth(endpoint, options, tokenRef.current);
  if (res.status === 401) {
    clearAuthAndRedirect();           // 复用原有 onAuthFailure 逻辑
    throw new ApiError(401, "Unauthorized");
  }
  return parseJson<T>(res);
}
```
> 删除原 `refreshAccessToken()`（含 Promise 去重逻辑）。

### 2. `src/context/AuthContext.tsx`（去 refresh 依赖）

- `login()`：保留（响应 `{access_token, user}` 一致）；
- `logout()`：保留（`POST /api/auth/logout`，Java 有）；
- `restoreAuth()`：用 `GET /api/auth/me`（Java 返回 `{access_token, user}`，一致）；
- **删除** `refresh()` 与任何对 `/api/auth/refresh` 的引用。

### 3. `src/actions/join.ts`（报名结果解包）

前端期望 `{ success, message }`；Java `POST /api/joins` 返回 `Result{code, data}`。解包后：
```ts
const res = await fetchApi<{ id: string }>("/api/joins", { method: "POST", body });
return { success: true, message: "报名成功" };   // 由 code==200 判定，不再依赖响应字段
```

---

## 四、Java 后端改动点（少量配合）

### 1. `SaTokenConfig`：`GET /api/joins` 加 admin 保护

目前 `GET /api/joins`（报名列表）对匿名公开，管理后台才应可见：
```java
SaRouter.match("/api/joins")
        .matchMethod("GET", "PATCH", "DELETE")
        .check(r -> StpUtil.checkLogin())
        .check(r -> StpUtil.checkRole("admin"));
```
> `POST /api/joins`（报名）仍保持匿名（现有 `notMatch("/api/joins")`）。

### 2.（可选）时间序列化格式
前端用 `new Date(createdAt)` 解析；Java `LocalDateTime` 当前按 `yyyy-MM-dd HH:mm:ss` 输出（`application.yml`）。建议改用 **ISO 格式**（`LocalDateTime` 默认 ISO 或加 `@JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")`），确保所有浏览器可解析。

---

## 五、验证清单（切换后）

- [ ] 首页 5 个公开 GET 正常渲染（members/timeline/testimonials/projects/tech-stack）
- [ ] 前台报名 `POST /api/joins` 成功、管理后台可见
- [ ] `admin@xdzn.dev / admin123` 登录 → 跳转 /admin
- [ ] 管理后台 6 个页面 CRUD 正常（members/projects/tech-stack/testimonials/timeline/joins）
- [ ] 仪表盘 `GET /api/admin/dashboard` 统计 + 最近报名展示
- [ ] token 过期（清 Redis 会话）→ 接口 401 → 前端跳 /login（不再走 refresh）
- [ ] `GET /api/joins` 匿名访问返回 401/403（admin 保护生效）

---

## 六、实施建议

1. **先改前端** `lib/api.ts` + `AuthContext.tsx`（解包 + 去 refresh），这是核心；
2. **同时加** Java 的 `GET /api/joins` 保护（一行规则）；
3. 联调按第五节清单回归；
4. 时间格式、id 类型等细节在联调中按报错微调。

> 改动量评估：前端核心 2 个文件（api.ts、AuthContext.tsx）+ join action 微调；Java 1 处规则。整体**低侵入**。

---

## 七、接口需求 vs 实现对照表（前端调用 → Java 后端状态）

> ✅ = Java 已实现；❌ = 未实现；⚠️ = 已实现但需注意。

| 前端调用（页面/组件） | Java 后端接口 | 状态 | 差异 / 适配 |
|---|---|---|---|
| GET `/api/members`（首页/成员页） | `MemberController.findAll` | ✅ | 解包 `Result.data` |
| GET `/api/members/:id` | `findById` | ✅ | 解包 |
| POST/PUT/DELETE `/api/members`（后台成员管理） | `create/update/delete`（MemberDto） | ✅ | 解包；前端带 `order` 字段，Spring 默认忽略未知字段 |
| GET `/api/projects`（首页） | `ProjectController.findAll` | ✅ | 解包；`techStack:string[]` 一致 |
| POST/PUT/DELETE `/api/projects`（后台） | CRUD（ProjectDto，techStackIds 可选） | ✅ | 解包 |
| GET `/api/tech-stack` / CRUD | `TechStackController` | ✅ | 解包 |
| GET `/api/testimonials` / CRUD | `TestimonialController` | ✅ | 解包 |
| GET `/api/timeline` / CRUD | `TimelineController` | ✅ | 解包 |
| POST `/api/joins`（前台报名，公开） | `JoinController.create` | ✅ | 解包；报名结果按 code==200 判 success |
| GET `/api/joins`（后台列表，admin） | `findAll` | ✅ **本次修复** | 解包；**已补 admin 保护**（原公开） |
| PATCH `/api/joins/:id`（后台改状态） | `updateStatus` | ✅ **本次修复** | 解包；**已补 admin 保护**（原匿名漏洞） |
| DELETE `/api/joins/:id`（后台删除） | `delete` | ✅ **本次修复** | 解包；**已补 admin 保护** |
| POST `/api/auth/login`（登录页） | `AuthController.login` | ✅ | 响应 `{access_token,user}` 一致；解包 |
| POST `/api/auth/logout`（导航/后台顶栏） | `logout` | ✅ | 解包 |
| GET `/api/auth/me`（会话恢复） | `me` | ✅ | 响应 `{access_token,user}` 一致；解包 |
| **POST `/api/auth/refresh`**（AuthContext 刷新） | **无** | ❌ **未实现** | **前端移除**（Sa-Token 无 refresh_token，active-timeout 续期） |
| GET `/api/admin/dashboard`（后台仪表盘） | `AdminDashboardController` | ✅ | 解包；`stats/recentJoins` 字段一致 |
| GET `/api/members/page` 等分页 | `findAllByPage` | ✅ 已实现 | 前端管理后台暂未用分页（用全量），可忽略 |
| GET `/api/members/export` 等 Excel | `MemberExcelController` | ✅ 已实现 | 前端未对接，后端增强项 |

## 八、需求内容差别清单

| # | 差别 | 处理 |
|---|---|---|
| 1 | **响应包装**：前端裸数据 vs Java `Result{code,msg,data}` | 前端 `lib/api.ts` 统一解包 |
| 2 | **认证机制**：JWT+refresh Cookie vs Sa-Token 无 refresh | 前端移除 `/api/auth/refresh`，401 直接登出；靠 active-timeout 续期 |
| 3 | **join 管理权限**：GET/PATCH/DELETE 应 admin | ✅ 本次后端已修复 |
| 4 | **id 类型**：前端 `string` vs Java `Long`(number) | JS 运行时兼容（数字自动转字符串），联调确认 |
| 5 | **时间格式**：前端 `new Date()` 解析 | Java LocalDateTime 默认 ISO，前端可解析；联调确认 |
| 6 | **未知字段**：前端 POST 带 `order` 等 | Spring Boot 默认忽略，无需处理 |
| 7 | **mock 兜底**：前端 services 层有 mock fallback | 联调时确认走真实接口 |

## 九、前端暂未对接的后端已有能力（可选补充）

- 各模块 `/page` 分页接口（管理后台可优化为分页，当前全量）
- 成员 Excel 导出 / 导入模板 / 批量导入（后台可加"导出名单"按钮）
- `POST /api/auth/register`（公开注册，前端无注册页）
