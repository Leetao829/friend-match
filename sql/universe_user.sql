create table user
(
    id            bigint auto_increment comment '用户id'
        primary key,
    user_name     varchar(255)                       null comment '昵称',
    user_account  varchar(255)                       null comment '昵称',
    avatarUrl     varchar(1024)                      null comment '头像',
    gender        tinyint                            null comment '性别',
    user_password varchar(255)                       not null comment '密码',
    phone         varchar(255)                       null comment '电话',
    email         varchar(255)                       null comment '邮箱',
    user_status   int                                null comment '状态，0-正常',
    createTime    datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint  default 0                 null comment '是否删除',
    user_role     int      default 0                 not null comment '用户角色 0-普通用户 1-管理员',
    planet_code   varchar(255)                       null comment '星球编号',
    tags          varchar(1024)                      null comment '标签列表',
    profile       varchar(255)                       null comment '用户描述'
)
    comment '用户表';

INSERT INTO universe.user (id, user_name, user_account, avatarUrl, gender, user_password, phone, email, user_status, createTime, updateTime, isDelete, user_role, planet_code, tags, profile) VALUES (1, 'dogLT', '123', 'https://touxiang', 0, 'xxx', '123', '456', null, '2024-01-10 01:36:53', '2024-02-02 22:59:30', 0, 1, null, '["c++","java","python"]', null);
INSERT INTO universe.user (id, user_name, user_account, avatarUrl, gender, user_password, phone, email, user_status, createTime, updateTime, isDelete, user_role, planet_code, tags, profile) VALUES (2, 'dogLT', '123', 'https://touxiang', 0, 'xxx', '123', '456', null, '2024-01-10 10:09:25', '2024-01-18 23:06:15', 0, 1, null, null, null);
INSERT INTO universe.user (id, user_name, user_account, avatarUrl, gender, user_password, phone, email, user_status, createTime, updateTime, isDelete, user_role, planet_code, tags, profile) VALUES (3, 'xxx', 'dogLT', 'xxx', 0, 'xxx', 'xxx', 'xxx', null, '2024-01-14 23:08:20', '2024-02-20 13:28:57', 0, 1, '1', 'java', 'xxx');
INSERT INTO universe.user (id, user_name, user_account, avatarUrl, gender, user_password, phone, email, user_status, createTime, updateTime, isDelete, user_role, planet_code, tags, profile) VALUES (4, null, 'lee tao', null, null, 'afffbaae4918d9f9bc7c5dc20d192007', null, null, null, '2024-01-18 19:53:28', '2024-02-26 10:49:03', 0, 1, '2', '[''c++'',''java'']', null);