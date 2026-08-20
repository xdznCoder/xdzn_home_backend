-- ============================================
-- 虚动智能官网种子数据
-- ============================================

-- 管理员账号: admin@xdzn.dev / admin123 (BCrypt $2a$10)
INSERT IGNORE INTO users (id, email, password, name, role) VALUES
(1, 'admin@xdzn.dev', '$2a$10$Mn8L5lhnpSJtgD8kBC54yupY6lLOSknfPvvI9fSI7Gdp1bScAVK2S', '管理员', 'admin');

-- timeline_events
INSERT IGNORE INTO timeline_events (id, year, title, description, `order`) VALUES
(1, '2018', '社团创立', '虚动智能正式成立，开启极客之旅', 1),
(2, '2019', '首次参赛', '团队首次参加全国大学生竞赛，斩获佳绩', 2),
(3, '2020', '开源启航', '团队第一个开源项目发布', 3),
(4, '2021', '规模扩大', '成员人数突破 20 人，方向更多元', 4),
(5, '2022', '技术突破', '多个项目获得省级以上奖项', 5),
(6, '2023', '社区建设', '开始举办校际技术分享会', 6),
(7, '2024', '全新官网上线', '你正在看的这个网站', 7);

-- tech_stack_items
INSERT IGNORE INTO tech_stack_items (id, name, color, count, `desc`, `order`) VALUES
(1, 'React', '#61DAFB', 12, '核心前端框架', 1),
(2, 'Next.js', '#F5A623', 8, '全栈 React 框架', 2),
(3, 'TypeScript', '#3178C6', 15, '类型安全的 JavaScript', 3),
(4, 'Three.js', '#049EF4', 5, '3D 图形渲染引擎', 4),
(5, 'Vue.js', '#42B883', 4, '渐进式前端框架', 5),
(6, 'Tailwind CSS', '#38BDF8', 10, '原子化 CSS 框架', 6),
(7, 'Node.js', '#339933', 11, '服务端 JavaScript 运行时', 7),
(8, 'NestJS', '#E0234E', 6, '企业级 Node.js 框架', 8),
(9, 'Python', '#3776AB', 9, '通用编程语言', 9),
(10, 'Go', '#00ADD8', 5, '高并发系统语言', 10),
(11, 'PostgreSQL', '#4169E1', 7, '关系型数据库', 11),
(12, 'MongoDB', '#47A248', 6, '文档型数据库', 12),
(13, 'Docker', '#2496ED', 8, '容器化部署', 13),
(14, 'Kubernetes', '#326CE5', 4, '容器编排平台', 14),
(15, 'Git', '#F05032', 15, '版本控制系统', 15),
(16, 'Figma', '#F24E1E', 7, '协作设计工具', 16),
(17, 'Rust', '#CE422B', 3, '系统级安全语言', 17),
(18, 'WASM', '#654FF0', 2, 'Web 高性能运行时', 18);

-- projects
INSERT IGNORE INTO projects (id, title, description, color, link, `order`) VALUES
(1, '智能校园导航', '基于 A* 算法的校园实时导航系统，支持 AR 路线指引', '#006FEE', 'https://github.com', 1),
(2, '深度学习竞赛平台', '面向高校学生的 AI 竞赛评测系统，支持多框架模型提交', '#8B5CF6', NULL, 2),
(3, '虚拟实验室', 'WebGL 驱动的在线化学实验模拟平台', '#EC4899', NULL, 3),
(4, '开源组件库', '极客风格的 React UI 组件库，暗黑模式优先', '#F59E0B', NULL, 4),
(5, '即时通讯系统', '基于 WebSocket 的实时通讯平台，支持端到端加密', '#10B981', NULL, 5),
(6, '数据可视化引擎', '高性能 Canvas 渲染引擎，支持百万级数据点实时绘制', '#EF4444', NULL, 6);

-- project_tech_stack
INSERT IGNORE INTO project_tech_stack (project_id, tech_stack_id) VALUES
(1, 1), (1, 7), (1, 12),
(2, 2), (2, 9), (2, 13),
(3, 4), (3, 1),
(4, 1), (4, 3),
(5, 5), (5, 10),
(6, 3);

-- members
INSERT IGNORE INTO members (id, name, direction, graduation_year, major, team_role, `order`) VALUES
(1, '林星河', '前端', 2024, '计算机科学与技术', '负责人', 1),
(2, '陈墨', '后端', 2023, '软件工程', '核心成员', 2),
(3, '苏晚', '算法', 2024, '数据科学与大数据技术', '核心成员', 3),
(4, '张涵', '设计', 2025, '数字媒体技术', '成员', 4),
(5, '李遥', '前端', 2025, '计算机科学与技术', '成员', 5),
(6, '周然', '运维', 2023, '网络工程', '核心成员', 6),
(7, '王澈', '后端', 2024, '软件工程', '成员', 7),
(8, '赵麟', '算法', 2025, '计算机科学与技术', '成员', 8);

-- 为admin用户关联member记录(用于测试/me接口)
UPDATE members SET user_id = 1 WHERE id = 1;

-- testimonials
INSERT IGNORE INTO testimonials (id, name, direction, quote, graduation_year, `order`) VALUES
(1, '林星河', '前端', '在虚动智能的日子是我大学最珍贵的回忆。这里教会我的不只是技术，还有解决问题的思维方式和一群志同道合的伙伴。', 2024, 1),
(2, '陈墨', '后端', '社团给了我从 0 到 1 搭建系统的机会，比课堂上的项目磨练更多。', 2023, 2),
(3, '苏晚', '算法', '在这里认识了一群真正热爱技术的人，一起打比赛、做项目，是最有收获的时光。', 2024, 3),
(4, '周然', '运维', '从踩坑部署到自动化运维，虚动智能让我真正理解了什么是「工程化」。', 2023, 4);

-- tasks
INSERT IGNORE INTO tasks (id, title, description, creator_id, start_date, due_date, status, priority) VALUES
(1, '完成官网招新页面优化', '优化招新报名页面的移动端适配', 1, '2026-08-01 09:00:00', '2026-08-20 18:00:00', 'in_progress', 'high');

-- task_assignees
INSERT IGNORE INTO task_assignees (task_id, member_id) VALUES
(1, 1), (1, 2);
