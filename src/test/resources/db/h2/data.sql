-- user_info 表语句
INSERT INTO user_info (id, userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole,
                       createTime, updateTime, isDelete)
VALUES (1816001696590692353, 'cong', '3a20abef00881f3a9549f50d5d507f99', null, null, '葱皮',
        'https://p4.itc.cn/q_70/images03/20220316/2b2fbf81affb4dc1a04e5a6d8ca26c99.jpeg', null, 'admin',
        '2024-07-24 06:45:18', '2024-08-02 14:10:43', 0);

INSERT INTO user_info (id, userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole,
                       createTime, updateTime, isDelete)
VALUES (1816992328458883074, 'congg', 'a05154021e1d346599f3042b879d3c04', null, null, null, null, null, 'user',
        '2024-07-27 00:21:43', '2024-07-27 00:21:43', 0);

INSERT INTO user_info (id, userAccount, userPassword, unionId, mpOpenId, userName, userAvatar, userProfile, userRole,
                       createTime, updateTime, isDelete)
VALUES (1818965526718836738, 'shing', '3a20abef00881f3a9549f50d5d507f99', null, null, 'shing',
        'http://124.70.210.130:9000/lbc/sp.jpg', null, 'admin', '2024-08-01 11:02:30', '2024-08-01 11:02:49', 0);

--table_info 表语句
INSERT INTO table_info(id, name, content, reviewStatus, reviewMessage, userId, createTime, updateTime, isDelete)
VALUES (1820740567387107329, '测试测试表', '[{"id":"12345"},{"name":"shing"},{"age":"18"}]', 0, '',
        1816001696590692353, '2024-08-06 08:35:53', '2024-08-06 08:35:53', 0);

INSERT INTO table_info(id, name, content, reviewStatus, reviewMessage, userId, createTime, updateTime, isDelete)
VALUES (1820740567387107328, '测试测试表-2', '[{"id":"12345"},{"name":"shing"},{"age":"18"}]', 0, '',
        1816001696590692353, '2024-08-06 08:35:53', '2024-08-06 08:35:53', 0);

-- topic_level 表语句
INSERT INTO topic_level(id, title, initSQL, mdContent, defaultSQL, answer, hint, type, preLevelId, nextLevelId, userId, createTime, updateTime, isDelete)
VALUES (100, '测试测试表', 'select * from table_info', 'test', 'select * from table_info','select * from table_info', 'test', 'custom', 99, 101, 1818965526718836738, '2024-08-06 08:35:53', '2024-08-06 08:35:53', 0);
INSERT INTO topic_level(id, title, initSQL, mdContent, defaultSQL, answer, hint, type, preLevelId, nextLevelId, userId, createTime, updateTime, isDelete)
VALUES (101, '测试测试表', 'select * from table_info', 'test', 'select * from table_info','select * from table_info', 'test', 'custom', 99, 101, 1818965526718836738, '2024-08-06 08:35:53', '2024-08-06 08:35:53', 0);