# 虚动智能官网后端 · 开发计划表（2026 年 8 月）

> 周期：4 周（8 月初 → 8 月底，9 月前完成）
> 团队：2 人（已有 Spring Boot + Redis 基础）+ 负责人（评审/带教）
> 范围：展示主页功能优化改造 + 内部团队管理新模块；部署不作为独立任务
> 前端不归本计划负责

---

## 一、目标架构

```
xdzn_home_backend
├── 展示主页（Public）—— 现有内容管理，优化改造
│   └── members(公开视图) / projects / tech-stack / timeline / testimonials / 招新报名
└── 内部团队管理（Internal）—— 本轮新增
    ├── 成员自助信息录入（统一档案表 + VO 双视角）
    ├── 任务布置与跟踪
    ├── 公告发布 + 站外通信（邮件通知，预留 Webhook 抽象）
    └── 经费收支管理
```

---

## 二、现有功能优化改造清单

| # | 现状问题 | 改造方向 | 优先级 |
|---|---|---|---|
| 1 | 写接口直接收实体（member/project/techStack/testimonial/timeline 的 create/update 无 DTO、无校验） | 全面 **DTO 化 + `@Valid` 参数校验**（参照现有 `LoginDto`/`CreateJoinDto`） | 高 |
| 2 | 成员由管理员单向录入，成员无法维护自己信息 | 改造为「**成员自助 + 管理员管理**」双模式（见新功能 A） | 高 |
| 3 | 列表全量返回，无分页/搜索 | MyBatis-Plus 分页插件（当前未配置）+ 统一 `PageResult` + 关键词/状态筛选 | 中 |
| 4 | 角色仅 admin/member，内部接口无成员本人/管理员隔离 | 扩展 Sa-Token 鉴权规则，内部接口按「本人 / 管理员」隔离 | 高 |
| 5 | 招新报名与正式成员不打通 | 报名状态 accepted 后可创建/关联成员档案 | 中 |
| 6 | 展示内容缓存未区分内部数据 | 内部数据（任务/经费/公告）不缓存；展示页缓存保留 | 中 |
| 7 | `data.sql` admin 密码注释与实际 hash 不符；无 README | 修正 + 补开发文档 | 低 |

---

## 三、新功能分析与表设计

### A. 成员自助信息（统一档案表 + VO 双视角）

**设计**：成员档案统一存 `members` 一张表，**通过 VO 区分内外部展示信息**。

- `members` 表新增字段：`user_id`（关联 users，可空）、`phone`、`email_contact`、`skills`、`bio`
- **公开 VO（官网）** `MemberPublicVO`：id / name / avatar / direction / graduation_year / current_company / current_role / order
- **内部 VO（管理端/本人）** `MemberVO`：公开字段 + user_id / phone / email_contact / skills / bio
- 接口：
  - 官网列表/详情 → 返回 `MemberPublicVO`
  - `GET/PUT /api/members/me`（本人读改）→ `MemberVO`
  - 管理员 `GET/PUT /api/members/{id}` → `MemberVO`
- 权限：本人只能改自己；管理员可管理全部

### B. 任务布置

**表 `tasks`**：`id, title, description, assignee_id(→members), creator_id(→users), priority(high/medium/low), status(todo/in_progress/done), due_date, created_at, updated_at`

- 管理员：`POST/PUT/DELETE /api/tasks`、按成员/状态筛选列表
- 成员：`GET /api/tasks/me`（自己负责的）、`PATCH /api/tasks/{id}/status`

### C. 公告发布 + 站外通信

**表 `announcements`**：`id, title, content, is_top, status(draft/published), published_at, created_by(→users), created_at, updated_at`

**站外通信（通知抽象）**：
```
NotificationService（接口，预留扩展点）
 ├─ EmailNotificationService（本轮实现：JavaMailSender + @Async，失败不阻塞主流程）
 └─ WebhookNotificationService（预留：后续钉钉/企业微信/飞书机器人）
```
- 发布公告 / 布置任务时异步通知目标成员邮箱
- 成员：`GET /api/announcements`（已发布列表，置顶优先）

### D. 经费管理

**表 `finance_records`**：`id, type(income/expense), category, amount(DECIMAL), description, operator_id(→users), occurred_at, created_at, updated_at`

- 收支明细 CRUD（管理员/成员提交）
- 统计接口：本月收入/支出/结余、按类别汇总
- 内部看板展示经费概览

> 四张新表加入 `schema.sql`，并补充 `data.sql` 种子数据。

---

## 四、4 周开发计划表（2 人并行）

> 节奏：每周二对齐目标、周五提交 PR，负责人 Review 后合并。
> 约定：`main` + `develop` + `feature/*`，禁止直推 develop；提交信息 `feat(member): xxx` 风格。

### 第 1 周：规范基线 + 校验优化 + 成员档案统一化
| 成员 | 任务 | 子项 | 验收标准 |
|---|---|---|---|
| **A** | 现有写接口 DTO 化 + 分页基础 | ① 内容模块 create/update 改 DTO + `@Valid` ② 分页插件 + `PageResult`，members 列表分页先行 | 写接口均有参数校验；分页生效 |
| **B** | 成员档案统一化 + VO 双视角 | ① `members` 加 `user_id`/`phone`/`email_contact`/`skills`/`bio` ② `MemberPublicVO`/`MemberVO` ③ 官网返回公开 VO ④ `GET/PUT /api/members/me` 自助 | 官网只见公开字段；成员登录能维护自己档案；管理员可管理全部 |

### 第 2 周：任务管理 + 权限体系
| 成员 | 任务 | 子项 | 验收标准 |
|---|---|---|---|
| **A** | 任务模块 | ① `tasks` 表 ② CRUD + 指派 + 状态流转 + 优先级/截止 ③ `GET /api/tasks/me` | 管理员派任务 → 成员登录看到并更新状态 |
| **B** | 权限细化 + 分页补全 | ① Sa-Token 规则扩展：成员本人/管理员隔离 ② projects/joins 分页筛选 ③ 接口文档骨架（SpringDoc） | 越权访问 403；分页筛选生效 |

### 第 3 周：公告 + 站外通信
| 成员 | 任务 | 子项 | 验收标准 |
|---|---|---|---|
| **A** | 公告模块 | ① `announcements` 表 ② 管理员 CRUD + 置顶 + 草稿/发布 ③ 成员已发布列表 | 发公告 → 站内可见、置顶生效 |
| **B** | 站外通信 | ① `NotificationService` 抽象 + `EmailNotificationService`（@Async）② 发布公告/布置任务触发通知 ③ `spring.mail.*` 配置外置 | 公告/任务发布后成员邮箱收到通知 |

### 第 4 周：经费管理 + 收尾联调
| 成员 | 任务 | 子项 | 验收标准 |
|---|---|---|---|
| **A** | 经费模块 | ① `finance_records` 表 ② 收支 CRUD + 类别 ③ 统计接口 | 录入收支 → 统计正确 |
| **B** | 内部看板 + 联调收尾 | ① 看板扩展：任务/公告/经费统计 ② 全接口回归 + 修遗留 bug（含 `data.sql` 密码注释）③ README 补内部模块 | 看板汇总内部数据；基本功能全部可用 |

> 部署：不单列任务。若收尾顺利，Docker 化（Dockerfile + docker-compose）作为第 4 周可选加分项。

---

## 五、上线 Checklist（第 4 周末）
- [ ] 生产环境变量外置（`JWT_SECRET`、`DB_PASSWORD`、`spring.mail.*`、`app.frontend-url`）
- [ ] `schema.sql` 四张新表 + 种子数据可迁移
- [ ] 内部接口权限回归（越权 403）
- [ ] 成员自助修改不影响官网展示（VO 脱敏确认）
