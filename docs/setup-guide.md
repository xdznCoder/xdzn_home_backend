# 虚动智能官网后端 · 环境搭建指南

> 目标：新人克隆仓库后 **30 分钟内**在本地跑通项目。
> 适用平台：Windows（本项目主要开发环境）。

---

## 一、环境要求

| 依赖 | 版本 | 说明 |
|---|---|---|
| JDK | **17** | 项目编译运行版本（可用 IDEA 自带或独立安装） |
| Maven | 3.8+（可选） | 项目自带 `mvnw`，不装 Maven 也能构建 |
| MySQL | 8.0 | 主数据库（本地 3306 端口） |
| Redis | 6.0+ | Sa-Token 会话存储 + 业务缓存（本地 6379 端口） |
| Git | 任意 | 克隆与协作 |

> 建议使用 IDEA（IntelliJ IDEA）打开项目，自带 JDK/Maven 配置引导。

---

## 二、克隆项目

```bash
git clone https://github.com/xdznCoder/xdzn_home_backend.git
cd xdzn_home_backend
```

---

## 三、初始化数据库

1. 启动本地 MySQL，用客户端（或命令行）创建数据库并导入脚本：

```bash
# 在项目根目录执行
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS xdzn DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
mysql -u root -p xdzn < src/main/resources/schema.sql   # 建表（8 张）
mysql -u root -p xdzn < src/main/resources/data.sql     # 种子数据
```

2. 启动本地 Redis（默认 6379 端口，无密码）。

> 如 Redis 设了密码，需在下面的环境变量中配置 `REDIS_PASSWORD`。

---

## 四、配置环境变量

项目默认配置在 `src/main/resources/application.yml`，敏感信息全部走环境变量（有默认值兜底）：

| 环境变量 | 默认值 | 说明 |
|---|---|---|
| `DB_USER` | `root` | MySQL 用户名 |
| `DB_PASSWORD` | 空 | MySQL 密码（**本机有密码时必须设置**） |
| `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD` | `localhost` / `6379` / 空 | Redis 连接 |
| `JWT_SECRET` | `dev-secret-change-in-production` | Sa-Token JWT 密钥（生产必须更换） |
| `FRONTEND_URL` | `http://localhost:3000` | 允许跨域的前端地址 |
| `JOIN_NOTIFY_EMAIL` | 空 | 招新报名通知邮箱（邮件模块上线后使用） |

**IDEA 运行配置**（推荐方式）：
Run/Debug Configurations → 选中 `XdznHomeBackendApplication` → Environment variables 填入：

```
DB_PASSWORD=你的密码;JWT_SECRET=你的自定义密钥
```

---

## 五、启动项目

**方式一：IDEA**
运行主类 `src/main/java/com/xdzn/xdzn_home_backend/XdznHomeBackendApplication.java`

**方式二：命令行**
```bash
DB_PASSWORD=你的密码 ./mvnw spring-boot:run
```

启动成功标志：控制台出现
```
Tomcat started on port 3001 (http) with context path '/'
Started XdznHomeBackendApplication in X.X seconds
```

---

## 六、验证

项目启动后，验证接口：

```bash
# 登录管理员（种子账号）
curl -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@xdzn.dev","password":"admin123"}'
```

预期返回：
```json
{"code":200,"msg":"ok","data":{"access_token":"...","user":{"id":1,"name":"管理员","email":"admin@xdzn.dev","role":"admin"}}}
```

再验证公共接口：
```bash
curl http://localhost:3001/api/members     # 成员列表（公开）
curl http://localhost:3001/api/projects    # 项目列表（公开，带技术栈）
```

---

## 七、项目结构

```
src/main/java/com/xdzn
├── aspect/          # AOP 切面（如缓存失效）
├── common/          # 通用：Result 响应、业务异常、全局异常、CORS/Redis/SaToken 配置
├── controller/      # 接口层
├── mapper/          # MyBatis-Plus Mapper
├── model/
│   ├── dto/         # 请求/响应 DTO（含参数校验）
│   └── entity/      # 数据库实体
├── redis/           # RedisService 统一封装 + key 前缀定义
├── service/         # 业务层（接口 + impl）
└── xdzn_home_backend/ # 启动类
src/main/resources/
├── application.yml  # 配置
├── schema.sql       # 建表脚本
└── data.sql         # 种子数据
docs/                # 团队文档（开发计划、本指南）
```

**关键约定**（写代码前必读）：
- 接口统一返回 `Result<T>`：`{code, msg, data}`
- 手动 Redis 操作走 `com.xdzn.redis.RedisService`，key 前缀定义在 `redis/key/` 下
- 实体放 `model/entity`，DTO 放 `model/dto`（写接口必须用 DTO + `@Valid` 校验）
- 认证基于 Sa-Token：`Authorization: Bearer <token>`，管理员角色 `admin`

---

## 八、常见问题（FAQ）

**1. 报 `Access denied for user 'root'`**
→ 没设置 `DB_PASSWORD` 环境变量，或密码不对。核对第 4 节。

**2. 报连接 Redis 失败 / 接口 500**
→ 确认本地 Redis 已启动（`redis-cli ping` 返回 `PONG`）。

**3. 登录返回 401「密码错误」**
→ 确认使用的是种子账号 `admin@xdzn.dev / admin123`（新版已修正 hash 与注释一致）。
   若数据库是从旧版导入的，重新执行 `data.sql` 覆盖种子用户。

**4. 端口 3001 被占用**
→ `netstat -ano | findstr 3001` 找到 PID，`taskkill /PID <pid> /F` 后重启。

**5. 换行符 warning（LF→CRLF）**
→ Git 在 Windows 下的正常提示，不影响运行。

---

## 九、Git 协作规范（重要）

分支模型：`main`（稳定） + `develop`（集成） + `feature/*`（个人功能分支）

```bash
# 拉取并切到 develop
git checkout develop
git pull

# 每次开发前新建功能分支（用你自己的前缀，如 xd/、chen/）
git checkout -b feature/member-self-service
# ... 开发、提交 ...
git push -u origin feature/member-self-service
```

**规则**：
- **禁止直接 push `main` / `develop`**，一律通过 Pull Request 合入，由负责人 Review。
- 提交信息用 `feat(模块): 描述` 风格，例如 `feat(member): 成员自助信息接口`、`fix(data): 修正种子密码`。
- 每完成一个小任务就提交一次（不要攒一堆）。
- 每周五前提交 PR，负责人 Review 后合并。

详细的分周任务见 `docs/development-plan.md`。
