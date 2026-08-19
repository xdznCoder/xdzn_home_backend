-- ============================================
-- 虚动智能官网数据库建表脚本
-- ============================================

-- 1. users 管理员用户表
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    email       VARCHAR(255)  NOT NULL COMMENT '登录邮箱',
    password    VARCHAR(255)  NOT NULL COMMENT 'BCrypt 加密密文',
    name        VARCHAR(255)  NOT NULL COMMENT '用户名',
    role        VARCHAR(32)   NOT NULL DEFAULT 'member' COMMENT '角色: admin / member',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 2. members 团队成员表
CREATE TABLE IF NOT EXISTS members (
    id                BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    name              VARCHAR(255)  NOT NULL COMMENT '姓名',
    avatar            VARCHAR(512)  NOT NULL DEFAULT '' COMMENT '头像URL',
    direction         VARCHAR(255)  NOT NULL COMMENT '方向（前端/后端/算法/设计/运维）',
    graduation_year   INT           NOT NULL COMMENT '毕业年份',
    current_company   VARCHAR(255)  DEFAULT NULL COMMENT '现就职公司',
    current_role      VARCHAR(255)  DEFAULT NULL COMMENT '现职位',
    user_id           BIGINT        DEFAULT NULL COMMENT '关联用户ID',
    phone             VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
    email_contact     VARCHAR(100)  DEFAULT NULL COMMENT '联系邮箱',
    skills            TEXT          DEFAULT NULL COMMENT '技能标签',
    bio               TEXT          DEFAULT NULL COMMENT '个人简介',
    `order`           INT           NOT NULL DEFAULT 0 COMMENT '排序',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_order (`order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队成员表';

-- 3. timeline_events 团队历程表
CREATE TABLE IF NOT EXISTS timeline_events (
    id          BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    year        VARCHAR(32)   NOT NULL COMMENT '年份（如 2018）',
    title       VARCHAR(255)  NOT NULL COMMENT '标题',
    description TEXT          NOT NULL COMMENT '描述',
    `order`     INT           NOT NULL DEFAULT 0 COMMENT '排序',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_order (`order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队历程表';

-- 4. testimonials 学长学姐说表
CREATE TABLE IF NOT EXISTS testimonials (
    id                BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    name              VARCHAR(255)  NOT NULL COMMENT '姓名',
    avatar            VARCHAR(512)  NOT NULL DEFAULT '' COMMENT '头像URL',
    direction         VARCHAR(255)  NOT NULL COMMENT '方向',
    quote             TEXT          NOT NULL COMMENT '语录',
    graduation_year   INT           NOT NULL COMMENT '毕业年份',
    `order`           INT           NOT NULL DEFAULT 0 COMMENT '排序',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_order (`order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学长学姐说表';

-- 5. projects 项目表
CREATE TABLE IF NOT EXISTS projects (
    id          BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    title       VARCHAR(255)  NOT NULL COMMENT '项目名称',
    description TEXT          NOT NULL COMMENT '项目描述',
    color       VARCHAR(32)   NOT NULL COMMENT '主题色（如 #006FEE）',
    link        VARCHAR(512)  DEFAULT NULL COMMENT '项目链接',
    image       VARCHAR(512)  DEFAULT NULL COMMENT '项目图片',
    `order`     INT           NOT NULL DEFAULT 0 COMMENT '排序',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_order (`order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 6. tech_stack_items 技术栈表
CREATE TABLE IF NOT EXISTS tech_stack_items (
    id          BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    name        VARCHAR(255)  NOT NULL COMMENT '技术名（如 React）',
    color       VARCHAR(32)   NOT NULL COMMENT '主题色（如 #61DAFB）',
    count       INT           NOT NULL COMMENT '掌握人数',
    `desc`      VARCHAR(512)  NOT NULL COMMENT '一句话描述',
    `order`     INT           NOT NULL DEFAULT 0 COMMENT '排序',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name),
    KEY idx_order (`order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技术栈表';

-- 7. project_tech_stack 项目-技术栈关联表
CREATE TABLE IF NOT EXISTS project_tech_stack (
    project_id      BIGINT  NOT NULL COMMENT '项目ID',
    tech_stack_id   BIGINT  NOT NULL COMMENT '技术栈ID',
    PRIMARY KEY (project_id, tech_stack_id),
    KEY idx_project_id (project_id),
    KEY idx_tech_stack_id (tech_stack_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目-技术栈关联表';

-- 8. join_submissions 招新报名表
CREATE TABLE IF NOT EXISTS join_submissions (
    id          BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    name        VARCHAR(255)  NOT NULL COMMENT '姓名',
    grade       VARCHAR(255)  NOT NULL COMMENT '年级',
    direction   VARCHAR(255)  NOT NULL COMMENT '方向',
    status      VARCHAR(32)   NOT NULL DEFAULT 'pending' COMMENT 'pending/contacted/accepted/rejected',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_status (status),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招新报名表';

-- 兼容已有数据库:如果members表缺少新字段,自动添加
ALTER TABLE members ADD COLUMN IF NOT EXISTS user_id BIGINT DEFAULT NULL COMMENT '关联用户ID';
ALTER TABLE members ADD COLUMN IF NOT EXISTS phone VARCHAR(20) DEFAULT NULL COMMENT '手机号';
ALTER TABLE members ADD COLUMN IF NOT EXISTS email_contact VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱';
ALTER TABLE members ADD COLUMN IF NOT EXISTS skills TEXT DEFAULT NULL COMMENT '技能标签';
ALTER TABLE members ADD COLUMN IF NOT EXISTS bio TEXT DEFAULT NULL COMMENT '个人简介';

-- 9. tasks 任务表
CREATE TABLE IF NOT EXISTS tasks (
    id          BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    title       VARCHAR(255)  NOT NULL COMMENT '任务标题',
    description TEXT          DEFAULT NULL COMMENT '任务描述',
    attachment  VARCHAR(512)  DEFAULT NULL COMMENT '附件URL(预留文件上传)',
    creator_id  BIGINT        NOT NULL COMMENT '创建人ID(关联users)',
    start_date  DATETIME      DEFAULT NULL COMMENT '起始时间',
    due_date    DATETIME      DEFAULT NULL COMMENT '截止时间',
    status      VARCHAR(32)   NOT NULL DEFAULT 'todo' COMMENT '状态: todo/in_progress/done',
    priority    VARCHAR(16)   NOT NULL DEFAULT 'medium' COMMENT '优先级: high/medium/low',
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_creator (creator_id),
    KEY idx_status (status),
    KEY idx_due_date (due_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 10. task_assignees 任务-成员关联表
CREATE TABLE IF NOT EXISTS task_assignees (
    task_id     BIGINT  NOT NULL COMMENT '任务ID',
    member_id   BIGINT  NOT NULL COMMENT '成员ID',
    PRIMARY KEY (task_id, member_id),
    KEY idx_member (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务-成员关联表';

-- 11. files 文件表（通用文件上传服务）
CREATE TABLE IF NOT EXISTS files (
    id            BIGINT        NOT NULL PRIMARY KEY COMMENT '雪花ID',
    original_name VARCHAR(255)  NOT NULL COMMENT '原始文件名',
    stored_name   VARCHAR(255)  NOT NULL COMMENT '存储文件名(UUID+扩展名)',
    content_type  VARCHAR(128)  DEFAULT NULL COMMENT 'MIME类型',
    size          BIGINT        NOT NULL COMMENT '文件大小(字节)',
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- 12. finance_records 经费收支表
CREATE TABLE IF NOT EXISTS finance_records (
    id           BIGINT         NOT NULL PRIMARY KEY COMMENT '雪花ID',
    type         VARCHAR(16)    NOT NULL COMMENT '收支类型: income(进账)/expense(支出)',
    amount       DECIMAL(12,2)  NOT NULL COMMENT '金额(元,保留两位)',
    category     VARCHAR(32)    DEFAULT NULL COMMENT '分类(如 团费/报销/赞助/物资/活动)',
    description  VARCHAR(255)   DEFAULT NULL COMMENT '说明',
    operator_id  BIGINT         DEFAULT NULL COMMENT '操作人ID(关联users)',
    occurred_at  DATETIME       DEFAULT NULL COMMENT '发生时间',
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_type (type),
    KEY idx_occurred_at (occurred_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='经费收支表';
