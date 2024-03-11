create table team
(
    id          bigint auto_increment comment 'id'
        primary key,
    name        varchar(256) charset utf8          null,
    description varchar(256) charset utf8          null,
    maxNum      int      default 1                 not null comment '最大人数',
    expireTime  datetime                           null comment '过期时间',
    userId      bigint                             null comment '用户id',
    status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password    varchar(512)                       null comment '密码',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';

INSERT INTO universe.team (id, name, description, maxNum, expireTime, userId, status, password, createTime, updateTime, isDelete) VALUES (25, '鱼皮小队', '测试描述', 3, '2024-03-25 11:23:29', 4, 0, null, '2024-02-25 11:47:44', '2024-02-26 13:19:08', 0);
INSERT INTO universe.team (id, name, description, maxNum, expireTime, userId, status, password, createTime, updateTime, isDelete) VALUES (26, '鱼皮小队', '测试描述', 3, '2024-03-25 11:23:29', 4, 0, null, '2024-02-25 11:47:56', '2024-02-25 11:47:56', 0);
INSERT INTO universe.team (id, name, description, maxNum, expireTime, userId, status, password, createTime, updateTime, isDelete) VALUES (27, '鱼皮小队', '测试描述', 3, '2024-03-25 11:23:29', 4, 0, null, '2024-02-25 11:47:59', '2024-02-25 11:47:59', 0);
INSERT INTO universe.team (id, name, description, maxNum, expireTime, userId, status, password, createTime, updateTime, isDelete) VALUES (28, '鱼皮小队', '测试描述', 3, '2024-03-25 11:23:29', 4, 0, null, '2024-02-25 11:48:00', '2024-02-26 13:43:39', 1);
