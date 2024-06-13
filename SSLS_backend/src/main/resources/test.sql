DROP TABLE IF EXISTS contest;
CREATE TABLE contest
(
    id                       INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name                     VARCHAR(100) NOT NULL COMMENT '名称',
    year                     YEAR         NOT NULL COMMENT '年度',
    topic                    VARCHAR(100) NOT NULL DEFAULT '' COMMENT '主题',
    intro                    VARCHAR(500) NOT NULL DEFAULT '' COMMENT '介绍',
    starting_time            DATE         NOT NULL COMMENT '开始可以报名的时间',
    registration_deadline    DATE         NOT NULL COMMENT '报名截至日期',
    result_announcement_time DATE         NOT NULL COMMENT '结果公布日期',
    categord_id              INT          NOT NULL COMMENT 'FK分类id',
    create_time              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FULLTEXT INDEX `ft_contest(name)` (name) WITH PARSER `ngram` COMMENT '全文索引，按关键词模糊查询比赛名称',
    INDEX `idx_contest(year)` (year) COMMENT '优化按年份where的速度',
    INDEX `idx_contest(category_id)` (categord_id) COMMENT '优化join速度'
) COMMENT = '竞赛';


DROP TABLE IF EXISTS category;
CREATE TABLE category
(
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '类别名称',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT = '竞赛分类';


DROP TABLE IF EXISTS team;
CREATE TABLE team
(
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100) NOT NULL COMMENT '队名',
    captain_id  INT          NOT NULL COMMENT '队长用户id',
    contest_id  INT          NOT NULL COMMENT 'FK参加的竞赛id',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_team(captain_id)` (captain_id) COMMENT '优化WHERE查询速度',
    INDEX `idx_team(contest_id)` (contest_id) COMMENT '优化JOIN速度'
) COMMENT = '团队';


DROP TABLE IF EXISTS team_member;
CREATE TABLE team_member
(
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    team_id     INT      NOT NULL COMMENT 'FK团队的id',
    user_id     INT      NOT NULL COMMENT 'FK成员用户id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_team_member(team_id)` (team_id) COMMENT '优化JOIN速度',
    INDEX `idx_team_member(user_id)` (user_id) COMMENT '优化JOIN速度',
    UNIQUE `uk_team_member(team_id,user_id)` (team_id, user_id) COMMENT '确保数据正确'
) COMMENT = '团队和成员映射';


DROP TABLE IF EXISTS work;
CREATE TABLE work
(
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    title       VARCHAR(200) NOT NULL COMMENT '标题',
    likes       INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
    filename    VARCHAR(255) NOT NULL COMMENT '作品的文件名UUID',
    team_id     INT          NOT NULL COMMENT 'FK所属的团队',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_work(team_id)` (team_id) COMMENT '优化JOIN速度'
) COMMENT = '作品';


DROP TABLE IF EXISTS award;
CREATE TABLE award
(
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '奖项等级名称',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT = '奖项';


DROP TABLE IF EXISTS winning_work;
CREATE TABLE winning_work
(
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    contest_id  INT      NOT NULL COMMENT 'FK所属的比赛',
    work_id     INT      NOT NULL COMMENT 'FK关联到作品表的id',
    award_id    INT      NOT NULL COMMENT 'FK关联到获奖等级表的id',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_winning_work(contest_id)` (contest_id) COMMENT '优化JOIN速度',
    INDEX `idx_winning_work(work_id)` (work_id) COMMENT '优化JOIN速度',
    INDEX `idx_winning_work(award_id)` (award_id) COMMENT '优化JOIN速度'
) COMMENT = '获奖作品';


DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    id          INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    rule        TINYINT     NOT NULL DEFAULT 0 COMMENT '表示用户的权限，0代表普通用户，1代表管理员',
    uid         LONG        NOT NULL COMMENT '用户的唯一表示符由雪花算法生成的Snowflake ID',
    nickname    VARCHAR(50) NOT NULL DEFAULT 0 COMMENT '昵称',
    username    VARCHAR(50) NOT NULL COMMENT '用户名',
    pwd         VARCHAR(64) NOT NULL COMMENT '密码+盐值的哈希结果',
    salt        VARCHAR(32) NOT NULL COMMENT '随机盐值',
    phone       VARCHAR(11)          DEFAULT '' COMMENT '电话号码',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX `uk_user(username)` (username) COMMENT '确保数据正确'
) COMMENT = '用户';


