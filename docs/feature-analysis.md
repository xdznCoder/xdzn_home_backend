# 虚动智能官网后端 · 功能深度分析清单


---

## 一、当前已实现

| 模块 | 能力 | 关键实现 |
|---|---|---|
| 认证 Auth | 注册/登录/登出/me；Sa-Token JWT + `active-timeout` 滑动续期；会话存 Redis；角色经 StpInterface 查库 | `AuthController` / `AuthServiceImpl` / `StpInterfaceImpl` |
| 成员 Member | CRUD + 分页 + DTO 校验 + **Excel 导入导出/模板** | `MemberController` / `MemberExcelController` |
| 项目 Project | CRUD + 分页 + 技术栈多对多关联 + 缓存 | `ProjectController` / `ProjectServiceImpl` |
| 技术栈 / 时间线 / 评价 | CRUD + 分页 + 缓存 | 对应 Controller/Service |
| 招新报名 Join | 提交 / 列表 / 状态流转 / 删除（**未分页**） | `JoinController` |
| 管理看板 Dashboard | 各表统计 + 最近 5 条报名；缓存 5min + 写操作切面失效 | `AdminDashboardServiceImpl` |
| 接口文档 | Knife4j，全部接口 `@Tag/@Operation/@Parameter` | `Knife4jConfig` |
| Excel 通用能力 | `ExcelService` 封装（导出/导入/字节级） | `common/excel/ExcelService` |
| 基础设施 | 全局异常、参数校验、CORS、RedisService、分页插件、Sa-Token 路由鉴权 | `common/config/*` |

---

## 二、已实现 · 可继续优化（按价值排序）

### 1. 招新报名分页 + 状态筛选
- **现状**：`GET /api/joins` 全量返回，无分页、无筛选。
- **优化**：报名列表分页，支持 `status` 筛选（pending/contacted/...）、按时间倒序。
- **实现路径**：`JoinSubmissionService` 加 `findAllByPage(page,size,status)`（LambdaQueryWrapper + Page）；`JoinController` 加 `GET /page` + `@RequestParam status`；复用已有 `PageResult`。
- **开发方式**： 标准分页 + 条件查询，与现有模块模式完全一致，学习点：`QueryWrapper` 动态条件。

### 2. 时间字段自动填充（MetaObjectHandler）
- **现状**：无自动填充，`created_at/updated_at` 依赖 DB `DEFAULT CURRENT_TIMESTAMP`（insert 有值，**update 不自动刷新 updated_at**）。
- **优化**：MyBatis-Plus 自动填充，insert/update 自动维护审计时间。
- **实现路径**：新建 `MyMetaObjectHandler implements MetaObjectHandler`；实体时间字段加 `@TableField(fill = FieldFill.INSERT / INSERT_UPDATE)`；`application.yml` 已配 `logic-delete`，再确认 `update-strategy`。
- **开发方式**： 全局能力，改动小收益大。

### 3. 登录失败封禁（防暴力破解闭环）
- **现状**：`LOGIN_FAIL` 计数已实现，但**无阈值封禁**，只计数不限制。
- **优化**：连续失败 ≥5 次，锁定该邮箱 N 分钟，返回 403 + 剩余解锁时间。
- **实现路径**：`login` 密码错误时 `incr` 后判断 ≥5 → 抛 BusinessException(403,"尝试过多，请 N 分钟后再试")；登录成功/锁定过期自动清除。可用 Redis TTL 做锁定窗口（`RedisService.set` 带过期）。
- **开发方式**： 复用 RedisService，逻辑简单。

### 4. 逻辑删除覆盖不全
- **现状**：仅 `User` 有 `@TableLogic`；members/projects 等为物理删除，误删不可恢复。
- **优化**：内容表加 `deleted` 字段 + 实体 `@TableLogic`（`application.yml` 已配全局逻辑删除）。
- **实现路径**：schema 加列 → 实体加字段 → 无需改 Service（MyBatis-Plus 自动拼接条件）。
- **开发方式**： 理解逻辑删除原理。

### 5. 分页插件加单页上限
- **现状**：`PaginationInnerInterceptor` 未设 maxLimit，`size` 可传超大值。
- **优化**：`.setMaxLimit(200)` 限制单页大小，防止恶意大查询。
- **实现路径**：`MybatisPlusConfig` 加一行。
- **开发方式**：一行改动。

### 6. 角色查询性能优化
- **现状**：`StpInterfaceImpl.getRoleList` 每次 `checkRole` 都查一次 DB。
- **优化**：角色结果短时缓存（Redis 或登录会话），降低鉴权 DB 压力。
- **实现路径**：在 `getRoleList` 内用 `RedisService` 缓存 `role:{userId}`（TTL 短，如 5min）；用户角色变更时主动失效。⚠️ 注意：之前 session 存 roleList 因 `List.of()` 不可反序列化失败，改用 Redis 短缓存 + ArrayList 或直接查库兜底。
- **开发方式**：进阶任务（负责人或能力强的）。

### 7. 缓存一致性与失效面
- **现状**：`CacheInvalidationAspect` 匹配 `service.*.create/update/delete`，DTO 化后方法名仍为 create/update/delete（可用）；但新模块（任务/经费/公告）需纳入失效面。
- **优化**：验证现有切面对内容更新的 dashboard 失效；新增模块时统一走切面或 `@CacheEvict`。
- **开发方式**：负责人核查。

### 8. 配置多环境隔离
- **现状**：`JWT_SECRET` 有 dev 默认值、`DB_PASSWORD` 默认空、无 `application-prod.yml`。
- **优化**：`application-prod.yml` + `spring.profiles.active`；生产强制覆盖密钥与 CORS 来源。
- **开发方式**：负责人，上线前必做。

### 9. 接口字段校验补全
- **现状**：内容模块 DTO 化完成，但部分字段校验偏弱（如 `order` 范围、`direction` 枚举、字符串长度）。
- **优化**：补 `@Size/@Min/@Max/@Pattern`。
- **实现路径**：各 DTO 加注解（Controller 已 `@Valid`）。
- **开发方式**： A/B 空闲时补。

---

## 三、未实现 · 可新增功能

### A. 内部团队管理（计划内，见 development-plan 第 2-4 周）

| 功能 | 价值 | 表设计 | 核心接口 | 实现路径 / 开发方式 |
|---|---|---|---|---|
| **A1 成员自助信息** | 成员自己维护档案，管理员减负 | `members` 加 `user_id/phone/skills/bio` | `GET/PUT /api/members/me`；官网返回 `MemberPublicVO`（脱敏） | 统一表 + VO 双视角；权限：本人/管理员隔离。 B，第 1 周已规划 |
| **A2 任务管理** | 布置/跟踪成员任务 | `tasks(id,title,desc,assignee_id,creator_id,priority,status,due_date)` | 管理员 CRUD + `GET /api/tasks/me` + `PATCH status` | 标准 CRUD + 权限隔离； A |
| **A3 公告** | 站内通知 | `announcements(id,title,content,is_top,status,published_at,created_by)` | 管理员 CRUD + 成员已发布列表 | CRUD + 置顶排序； A |
| **A4 站外通信** | 公告/任务推送到邮箱/群 | `NotificationService` 抽象 + `EmailNotificationService`(@Async) + 预留 Webhook | 发布时异步触发 | 抽象接口 + 多实现；`spring.mail.*` 配置外置； B |
| **A5 经费管理** | 收支透明 | `finance_records(id,type,category,amount,desc,operator_id,occurred_at)` | 收支 CRUD + 统计（本月收支/结余/按类汇总） | CRUD + 聚合统计（`@Select`/QueryWrapper groupBy）； A |
| **A6 内部看板** | 管理视图 | 复用各表 count | 任务/经费/公告统计聚合 | 扩展 `AdminDashboardServiceImpl`； B |

### B. 创意功能（让官网/团队更"活"，按吸引力排序）

| 功能 | 亮点 | 实现路径 | 难度 |
|---|---|---|---|
| **B1 实时动态流（SSE）** | 官网滚动展示"成员完成任务/新报名/新项目"，极客感 | `SseEmitter` + `ApplicationEventPublisher`；写操作发事件，SSE 广播 | 中 |
| **B2 Git 提交贡献统计** | 拉取成员 GitHub 提交，贡献热力图/排行，激励写码 | `@Scheduled` 定时调 GitHub API（已有 gh/token）→ 存表 → 排行接口 | 中 |
| **B3 钉钉/飞书 Webhook 通知** | 补全 A4 的 Webhook 实现，推群比邮件即时 | `WebhookNotificationService`（POST 机器人 URL）+ 配置项 | 低 |
| **B4 任务逾期提醒 + 周报** | 定时扫描逾期任务发通知；周五生成完成率周报 | `@EnableScheduling` + `@Scheduled` + 复用通知服务 | 中 |
| **B5 访客留言墙** | 官网互动，管理员审核后展示 | `messages` 表 + 匿名提交 + 审核状态 | 低（友好） |
| **B6 成员积分/成就** | 完成任务积分、徽章，游戏化激励 | `points/achievements` 表 + 事件加分 | 低-中 |
| **B7 招新报名倒计时 + 进度** | 招新季转化核心 | Redis 计数器 + 配置截止时间 + 前端进度条 | 低 |
| **B8 团队数据大屏** | 管理后台投屏统计，指挥中心感 | 看板聚合接口 + 前端图表 | 中 |
| **B9 全局搜索** | 搜索框搜遍成员/项目/公告 | MySQL `LIKE`（轻量）或 Elasticsearch（重） | 低-高 |
| **B10 Excel 报表扩展** | 报名/经费导出（复用 `ExcelService`） | 仿 `MemberExcelController` 模式 | 低 |

### C. 工程化与质量

| 项 | 现状 | 目标 | 实现路径 |
|---|---|---|---|
| **C1 单元测试** | 仅 Excel + contextLoads | 核心 Service 单测（Mockito） | `spring-boot-starter-test` 已含 Mockito；为 Auth/Member/Excel 补用例 |
| **C2 Docker 部署** | 无 | 一键启动 app+mysql+redis | Dockerfile（多阶段）+ docker-compose + 数据卷 + 健康检查 |
| **C3 CI 流水线** | 无 | push 自动构建/测试 | GitHub Actions：maven build + test |
| **C4 接口限流** | 无 | 防刷（登录/报名） | Sa-Token 限流注解或 Guava RateLimiter / Redis 计数 |
| **C5 操作日志** | 无 | 记录管理员操作 | AOP 切面 + `operation_logs` 表 |

---

## 四、技术债与质量风险（建议优先处理）

1. **业务逻辑无测试覆盖** —— 回归全靠手工，模块增多后风险大。
2. **无自动填充** —— `updated_at` 更新不准，审计混乱。
3. **逻辑删除不全** —— 内容误删不可恢复。
4. **配置无环境隔离** —— 生产密钥默认值存在安全隐患。
5. **分页无上限 + 登录无封禁** —— 安全/性能隐患。
6. **角色查库无缓存** —— 高并发鉴权压力。

---

## 五、落地优先级建议

**第一优先级（9 月上线前，安全/体验兜底）**
- 二.3 登录封禁、二.5 分页上限、二.2 自动填充、二.8 生产配置隔离
- 三-A1~A6 内部团队管理核心（计划第 2-4 周）

**第二优先级（体验提升，招新季加分）**
- 二.1 报名分页筛选、三-B1 SSE 动态流、三-B7 报名倒计时、三-B10 报名/经费导出

**第三优先级（长期建设）**
- 三-B2 Git 贡献、B6 积分、B8 大屏、C1 测试、C2/C3 Docker+CI

> 建议排期：先按 development-plan 完成内部管理（A1-A6），期间穿插二类的安全/体验优化（3、5、2），上线后进入创意功能（B 类）。
