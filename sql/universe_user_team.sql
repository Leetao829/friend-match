create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';

INSERT INTO universe.user_team (id, userId, teamId, joinTime, createTime, updateTime, isDelete) VALUES (1, 4, 25, '2024-02-25 11:47:45', '2024-02-25 11:47:44', '2024-02-26 13:19:18', 0);
INSERT INTO universe.user_team (id, userId, teamId, joinTime, createTime, updateTime, isDelete) VALUES (2, 4, 26, '2024-02-25 11:47:56', '2024-02-25 11:47:56', '2024-02-25 11:47:56', 0);
INSERT INTO universe.user_team (id, userId, teamId, joinTime, createTime, updateTime, isDelete) VALUES (3, 4, 27, '2024-02-25 11:48:00', '2024-02-25 11:47:59', '2024-02-25 11:47:59', 0);
INSERT INTO universe.user_team (id, userId, teamId, joinTime, createTime, updateTime, isDelete) VALUES (4, 4, 28, '2024-02-25 11:48:00', '2024-02-25 11:48:00', '2024-02-26 13:43:37', 1);
INSERT INTO universe.user_team (id, userId, teamId, joinTime, createTime, updateTime, isDelete) VALUES (5, 5, 25, '2024-02-26 10:52:55', '2024-02-26 10:52:56', '2024-02-26 10:52:56', 0);
INSERT INTO universe.user_team (id, userId, teamId, joinTime, createTime, updateTime, isDelete) VALUES (6, 5, 28, '2024-02-26 13:41:30', '2024-02-26 13:41:30', '2024-02-26 13:43:37', 1);
