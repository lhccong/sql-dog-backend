/*
 IDEA mysql-2-h2插件 生成的SQL 脚本，请根据实际情况调整。

 Source Server Adjusted for: H2 Database Engine
*/

SET
MODE MySQL; -- 设置H2以模拟MySQL的模式，以便更好地兼容

-- 创建库
create database if not exists sql_dog;

-- 切换库
use sql_dog;

-- -- 设置H2以模拟MySQL的模式
-- SET MODE MySQL;
--
-- -- 创建并使用模式
-- CREATE SCHEMA IF NOT EXISTS sql_dog;
-- SET SCHEMA sql_dog;


-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
    ) comment '用户' collate = utf8mb4_unicode_ci;

-- 表信息表
create table if not exists table_info
(
    id            bigint auto_increment comment 'id' primary key,
    name          varchar(512)                       null comment '名称',
    content       text                               null comment '表信息（json）',
    reviewStatus  int      default 0                 not null comment '状态（0-待审核, 1-通过, 2-拒绝）',
    reviewMessage varchar(512)                       null comment '审核信息',
    userId        bigint                             not null comment '创建用户 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
    ) comment '表信息';

create index idx_name
    on table_info (name);

-- 字段信息表
create table if not exists field_info
(
    id            bigint auto_increment comment 'id' primary key,
    name          varchar(512)                       null comment '名称',
    fieldName     varchar(512)                       null comment '字段名称',
    content       text                               null comment '字段信息（json）',
    reviewStatus  int      default 0                 not null comment '状态（0-待审核, 1-通过, 2-拒绝）',
    reviewMessage varchar(512)                       null comment '审核信息',
    userId        bigint                             not null comment '创建用户 id',
    createTime    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 not null comment '是否删除'
    ) comment '字段信息';

create index idx_fieldName
    on field_info (fieldName);

create index idx_name
    on field_info (name);

-- SQL 执行记录表
create table if not exists level_info
(
    id               bigint auto_increment comment 'id' primary key,
    sqlContent       varchar(512)                       null comment '执行 SQL 的内容',
    sqlAnalyzeResult varchar(512)                       null comment 'SQL 分析结果',
    reviewStatus     int      default 0                 not null comment '状态（0-待审核, 1-通过, 2-拒绝）',
    reviewMessage    varchar(512)                       null comment '审核信息',
    userId           bigint                             not null comment '创建用户 id',
    createTime       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete         tinyint  default 0                 not null comment '是否删除'
    ) comment 'SQL执行记录';

-- 关卡题目表
create table if not exists topic_level
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(64)                        null comment '关卡中文名',
    initSQL    varchar(512)                       null comment '初始化 SQL',
    mdContent  text                               not null comment '关卡教程 Markdown 文档',
    defaultSQL varchar(512)                       null comment '关卡初始化后默认执行的语句，一般是查询全表',
    answer     varchar(512)                       null comment '关卡标准答案',
    hint       varchar(256)                       null comment '关卡提示',
    type       varchar(256)                       null comment '关卡类别，custom 自定义、system 系统',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
    ) comment '关卡题目';

-- SQL 执行记录表
CREATE TABLE IF NOT EXISTS execute_info (
                                            id               BIGINT AUTO_INCREMENT PRIMARY KEY, -- 自增 id
                                            sqlContent       VARCHAR(512),                      -- 执行 SQL 的内容
                                            sqlAnalyzeResult VARCHAR(512),                      -- SQL 分析结果
                                            reviewStatus     INT DEFAULT 0 NOT NULL,            -- 状态（0-待审核, 1-通过, 2-拒绝）
                                            reviewMessage    VARCHAR(512),                      -- 审核信息
                                            userId           BIGINT NOT NULL,                   -- 创建用户 id
                                            createTime       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 创建时间
                                            updateTime       TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, -- 更新时间
                                            isDelete         TINYINT DEFAULT 0 NOT NULL         -- 是否删除
);