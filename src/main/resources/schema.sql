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

-- 9. 兼容已有数据库:如果members表缺少新字段,自动添加
ALTER TABLE members ADD COLUMN IF NOT EXISTS user_id BIGINT DEFAULT NULL COMMENT '关联用户ID';
ALTER TABLE members ADD COLUMN IF NOT EXISTS phone VARCHAR(20) DEFAULT NULL COMMENT '手机号';
ALTER TABLE members ADD COLUMN IF NOT EXISTS email_contact VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱';
ALTER TABLE members ADD COLUMN IF NOT EXISTS skills TEXT DEFAULT NULL COMMENT '技能标签';
ALTER TABLE members ADD COLUMN IF NOT EXISTS bio TEXT DEFAULT NULL COMMENT '个人简介';
