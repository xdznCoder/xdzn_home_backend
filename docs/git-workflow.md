# 虚动智能官网后端 · Git 协作规范

> 面向团队成员（含新人）的 Git 操作指南，覆盖分支模型、日常开发流程、提交规范、PR 流程与常见问题处理。
> 仓库：`https://github.com/xdznCoder/xdzn_home_backend.git`

---

## 一、分支模型

```
main ───────────────────────────── 稳定版，只有可上线代码（受保护，禁止直接 push）
  │
  └── develop ──────────────────── 集成分支，日常开发的主干线（受保护）
        │
        ├── feature/xxx ─────────── 功能分支：每个任务开一个
        ├── feature/xxx ─────────── 并行开发互不干扰
        └── ...
```

| 分支 | 用途 | 谁能写 |
|---|---|---|
| `main` | 生产/稳定版本，仅保存可上线的提交 | 仅负责人通过 PR 合入 |
| `develop` | 日常集成分支，所有已完成功能的汇合点 | 仅负责人通过 PR 合入 |
| `feature/*` | 个人开发分支，一个任务一个分支 | 开发者在自己的分支上自由提交 |

> **铁律**：任何人不直接 push `main` / `develop`，一律走 Pull Request，由负责人 Review 后合并。

---

## 二、首次准备

```bash
# 1. 克隆仓库
git clone https://github.com/xdznCoder/xdzn_home_backend.git
cd xdzn_home_backend

# 2. 配置身份（让提交显示你的名字）
git config user.name "你的名字"
git config user.email "你的邮箱"

# 3. 拉取所有远程分支
git fetch --all
```

> 若克隆后 `git branch -a` 看不到 develop，执行 `git fetch --prune`。

---

## 三、日常开发流程（标准 7 步）

以「做成员自助信息接口」为例：

```bash
# ① 同步最新的 develop
git checkout develop
git pull origin develop

# ② 从最新的 develop 拉出功能分支（分支名见第四节规范）
git checkout -b feature/member-self-service

# ③ 开发代码……写完后查看改动
git status            # 看哪些文件被改了
git diff              # 看改动内容

# ④ 暂存并提交（提交信息见第五节规范）
git add src/main/java/...   # 只 add 本次相关的文件
git commit -m "feat(member): 新增成员自助信息接口"

# ⑤ 推送功能分支到远程
git push -u origin feature/member-self-service

# ⑥ 在 GitHub 上发起 Pull Request：feature/xxx → develop
#    （详见第六节）

# ⑦ Review 合并后，删除本地已合并的分支
git checkout develop
git pull origin develop
git branch -d feature/member-self-service
```

> 一次任务只开一个分支；**一个功能完成后**再切回 develop 同步，不要跨功能混在一个分支上。

---

## 四、分支命名规范

| 分支类型 | 命名规则 | 示例 |
|---|---|---|
| 功能分支 | `feature/<功能名>` | `feature/member-self-service` |
| 修复分支 | `fix/<问题名>` | `fix/admin-password` |
| 文档分支 | `docs/<主题>` | `docs/git-workflow` |
| 实验分支 | `try/<想法>` | `try/redis-lock` |

- 使用小写 + 中划线 `-`，不要用空格/大写/中文。
- 用你自己的前缀区分是谁的分支（可选）：`chen/feature/member-self-service`。

---

## 五、提交信息规范（Conventional Commits）

```
<type>(<模块>): <中文描述>
```

| type | 含义 | 示例 |
|---|---|---|
| `feat` | 新功能 | `feat(member): 成员自助信息接口` |
| `fix` | 修 bug | `fix(data): 修正 admin 种子密码 hash` |
| `docs` | 文档 | `docs: 新增环境搭建指南` |
| `refactor` | 重构（不改行为） | `refactor(redis): 统一 Redis 操作入口` |
| `perf` | 性能优化 | `perf(project): 项目列表缓存` |
| `test` | 测试 | `test(auth): 登录接口单测` |
| `chore` | 构建/工具/配置 | `chore: 升级 mybatis-plus 版本` |

模块参考：`auth / member / project / tech-stack / timeline / testimonial / join / task / announcement / finance / redis / data / config`。

**规则**：
- 描述用中文、一句话说清做了什么。
- 每个逻辑改动一次提交，**不要一次性提交所有文件**。
- 用 `git add <文件>` 精确暂存，避免把无关改动混进一次提交。

---

## 六、Pull Request 与 Code Review

### 发起 PR
1. 推送功能分支后，GitHub 会提示 Compare & pull request，点它。
2. 确认：
   - **base = `develop`**（不是 main）
   - **compare = 你的功能分支**
3. 标题用提交信息风格：`feat(member): 成员自助信息接口`
4. 描述里写清：改了什么 / 怎么验证（附测试结果或 curl 示例）。

### Review 与合并
- 负责人 Review，提出修改意见 → 你在功能分支上继续提交，PR 自动更新。
- 通过后由**负责人**合并（Squash and merge 或 Rebase and merge），保持历史干净。
- 合并后删除远程功能分支。

---

## 七、常用命令速查

| 场景 | 命令 |
|---|---|
| 查看状态/改动 | `git status` / `git diff` / `git log --oneline` |
| 建/切分支 | `git checkout -b <分支>` / `git checkout <分支>` |
| 提交 | `git add <文件>` → `git commit -m "feat(x): ..."` |
| 推送 | `git push -u origin <分支>`（首次） / `git push` |
| 同步 develop | `git checkout develop && git pull origin develop` |
| 合并 develop 到功能分支 | `git checkout feature/xxx && git merge develop` |
| 删除分支 | `git branch -d <分支>`（本地） / `git push origin --delete <分支>`（远程） |
| 暂存未提交改动 | `git stash`（保存）/ `git stash pop`（恢复） |

---

## 八、场景处理

### 1. 功能分支落后于 develop（有更新）
```bash
git checkout feature/xxx
git merge develop        # 把 develop 新改动合并进来，解决冲突后提交
```

### 2. 合并冲突
- 冲突文件会标记 `<<<<<<< / ======= / >>>>>>>`，手动选择保留哪边，删除标记。
- 解决后：
```bash
git add <冲突文件>
git commit -m "fix: 解决与 develop 的合并冲突"
```

### 3. 提交信息写错了（还没推送）
```bash
git commit --amend -m "feat(member): 正确的描述"
```

### 4. 不小心把文件 add 进暂存区
```bash
git restore --staged <文件>    # 取消暂存（保留改动）
```

### 5. 改动做错了，想丢弃
```bash
git restore <文件>             # 丢弃工作区改动（⚠️ 不可恢复，谨慎）
```

### 6. 误提交到 develop / main
- **不要 force push**。立即告知负责人，由负责人用 `git revert` 处理：
```bash
git revert <commitId>   # 生成一个反向提交，保留历史
```

### 7. 想临时放下手上的活
```bash
git stash        # 保存未提交改动
git stash list   # 查看
git stash pop    # 恢复
```

---

## 九、注意事项

1. **禁止**直接 push `main` / `develop`；禁止 `git push -f`（force push）。
2. 提交前**先编译确认通过**：`./mvnw compile`。
3. `.gitignore` 已排除 `target/`、`.idea/`、`HELP.md` 等，**不要把本地生成物 add 进仓库**。
4. 生产敏感信息（密码、密钥）只走环境变量，**绝不提交到代码/配置文件**。
5. 每周五前把完成的功能提 PR，避免堆积到周末。
6. 遇到不确定的操作先问负责人，不要擅自做不可逆操作（force push / 删远程分支）。

---

> 相关文档：`docs/development-plan.md`（4 周开发计划）、`docs/setup-guide.md`（环境搭建）。
