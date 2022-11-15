/*
Navicat MySQL Data Transfer

Source Server         : 问卷
Source Server Version : 80029
Source Host           : 43.138.162.112:3306
Source Database       : tduck

Target Server Type    : MYSQL
Target Server Version : 80029
File Encoding         : 65001

Date: 2022-11-15 11:45:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ac_permission
-- ----------------------------
DROP TABLE IF EXISTS `ac_permission`;
CREATE TABLE `ac_permission` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL,
  `action` varchar(64) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '0：失效 1:生效',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of ac_permission
-- ----------------------------
INSERT INTO `ac_permission` VALUES ('5', '问卷外观模板权限', '/project', '1', null, null);
INSERT INTO `ac_permission` VALUES ('6', '问卷操作权限', '/user', '1', null, null);
INSERT INTO `ac_permission` VALUES ('7', '权限相关操作权限', '/admin', '1', null, null);

-- ----------------------------
-- Table structure for ac_role
-- ----------------------------
DROP TABLE IF EXISTS `ac_role`;
CREATE TABLE `ac_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '0：失效 1：生效',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色表';

-- ----------------------------
-- Records of ac_role
-- ----------------------------
INSERT INTO `ac_role` VALUES ('1', '超管', '1', null, null);
INSERT INTO `ac_role` VALUES ('3', '表单管理员', '1', null, null);

-- ----------------------------
-- Table structure for ac_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `ac_role_permission`;
CREATE TABLE `ac_role_permission` (
  `role_id` int NOT NULL,
  `permission_id` int NOT NULL,
  `create_time` datetime DEFAULT NULL,
  KEY `role_permission_index` (`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of ac_role_permission
-- ----------------------------
INSERT INTO `ac_role_permission` VALUES ('1', '5', null);
INSERT INTO `ac_role_permission` VALUES ('1', '6', null);
INSERT INTO `ac_role_permission` VALUES ('1', '7', null);
INSERT INTO `ac_role_permission` VALUES ('3', '5', null);
INSERT INTO `ac_role_permission` VALUES ('3', '6', null);

-- ----------------------------
-- Table structure for ac_user
-- ----------------------------
DROP TABLE IF EXISTS `ac_user`;
CREATE TABLE `ac_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '姓名',
  `avatar` varchar(256) NOT NULL DEFAULT '' COMMENT '头像',
  `gender` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别0未知 1男2女',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(11) DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `reg_channel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '注册渠道',
  `last_login_channel` tinyint DEFAULT NULL COMMENT '最后登录渠道',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录Ip',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fb_user` varchar(255) DEFAULT NULL,
  `fb_username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `fanbookid` (`fb_user`)
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='用户';

-- ----------------------------
-- Records of ac_user
-- ----------------------------
INSERT INTO `ac_user` VALUES ('201', 'Tduck001', 'https://oss.smileyi.top/757b505cfd34c64c85ca5b5690ee5293/4c415e1c1af04af68d740e667819bece.png', '0', 'test@tduck.com', null, 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', '1', '2', '2021-06-24 11:08:21', '111.30.56.26', '0', '2020-11-12 11:50:50', '2021-06-24 11:08:21', null, null);
INSERT INTO `ac_user` VALUES ('202', '奇奇在哪里', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/f514dab6c62a4e062c97a14ab28fd9db', '0', null, null, null, null, '5', '2022-11-10 10:52:52', '172.16.32.6', '0', '2022-10-09 09:58:50', '2022-11-10 10:52:52', '416075786152312832', '3821331');
INSERT INTO `ac_user` VALUES ('203', '拉风得宅男', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/83438fb5ebf64cbd6c520b81b5fdff53', '0', null, null, null, null, '5', '2022-11-14 12:28:34', '172.16.32.6', '0', '2022-10-09 09:59:38', '2022-11-14 12:28:34', '416120040304148480', '3904464');
INSERT INTO `ac_user` VALUES ('204', '故事还长', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/7a8694d352c5657b04410c53870d1c03', '0', null, null, null, null, '5', '2022-11-14 18:05:46', '172.16.32.6', '0', '2022-10-10 01:41:43', '2022-11-14 18:05:46', '419333827232530432', '5799062');
INSERT INTO `ac_user` VALUES ('205', '无路可走', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/070606ac4d229541d38e9f827f8b7ab8', '0', null, null, null, null, '5', '2022-11-15 09:38:15', '172.16.32.6', '0', '2022-10-13 04:39:31', '2022-11-15 09:38:15', '419417293445922816', '4232801');
INSERT INTO `ac_user` VALUES ('206', 'howe123', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/558d8f4673d7de5aa0d94aee4d42c234', '0', null, null, null, null, '5', '2022-10-26 07:38:16', '172.16.32.6', '0', '2022-10-14 07:58:03', '2022-10-26 07:38:16', '233558858419671040', '601174');
INSERT INTO `ac_user` VALUES ('207', '答题问卷', 'https://fanbook-gamescluster-1251001060.cos.ap-shanghai.myqcloud.com/open-fanbook/pro/BotAvatar-26fb45ceb5963a35c13d564851b13721.png?v=1665564176000', '0', null, null, null, null, null, null, null, '0', '2022-10-17 01:58:21', '2022-10-17 01:58:21', '420861870266646528', '7863268');
INSERT INTO `ac_user` VALUES ('208', '测试111', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/22c85522733ec9f6139a865e76f1e431', '0', null, null, null, null, '5', '2022-11-09 15:13:23', '172.16.32.6', '0', '2022-10-17 06:21:53', '2022-11-09 15:13:23', '419764126097604608', '9586682');
INSERT INTO `ac_user` VALUES ('209', 'nicole', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/757949c93df9857377873ea1ceab4426.jpg', '0', null, null, null, null, '5', '2022-11-14 15:25:32', '172.16.32.6', '0', '2022-10-17 06:42:08', '2022-11-14 15:25:32', '256346942576001024', '145860');
INSERT INTO `ac_user` VALUES ('210', '', '', '0', null, null, null, null, '5', '2022-10-19 10:02:29', '172.16.32.6', '0', '2022-10-19 10:02:29', '2022-10-19 10:02:29', '423418577467473920', '4665746');
INSERT INTO `ac_user` VALUES ('211', '????????????', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/b0848a11af0500098b5beecc4e927709', '0', null, null, null, null, '5', '2022-10-29 10:15:38', '172.16.32.6', '0', '2022-10-26 11:13:54', '2022-10-29 10:15:38', '256783668259848192', '570289');
INSERT INTO `ac_user` VALUES ('212', 'carson', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/eaae74f60f78d9f1d53648f686baa242.jpg', '0', null, null, null, null, '5', '2022-11-11 14:57:24', '172.16.32.6', '0', '2022-10-26 11:15:14', '2022-11-11 14:57:24', '190821850975047680', '727062');
INSERT INTO `ac_user` VALUES ('213', '活动任务机器人', 'https://fanbook-gamescluster-1251001060.cos.ap-shanghai.myqcloud.com/open-fanbook/pro/BotAvatar-bc558ff275a38a3da0af3ef9aedc6c7a.png?v=1655288347000', '0', null, null, null, null, null, null, null, '0', '2022-10-29 11:13:51', '2022-10-29 11:13:51', '374546854160891904', '7027135');
INSERT INTO `ac_user` VALUES ('214', '测试机器人', 'https://fanbook-gamescluster-1251001060.cos.ap-shanghai.myqcloud.com/open-fanbook/pro/BotAvatar-d44b2e926b4371d132c57a24e7a8b007.jpg?v=1665564179000', '0', null, null, null, null, null, null, null, '0', '2022-10-29 11:13:51', '2022-10-29 11:13:51', '420861887761088512', '5580140');
INSERT INTO `ac_user` VALUES ('215', '积分任务机器人', 'https://fanbook-gamescluster-1251001060.cos.ap-shanghai.myqcloud.com/open-fanbook/pro/BotAvatar-e645203e29b29ec77bd7d4d7248e3ea3.png?v=1664194909000', '0', null, null, null, null, null, null, null, '0', '2022-10-29 11:13:51', '2022-10-29 11:13:51', '368332684671320064', '1362342');
INSERT INTO `ac_user` VALUES ('216', '大长腿', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/4346b1ecb0afb9e6fdaa323400a96647.jpg', '0', null, null, null, null, '5', '2022-11-14 19:16:15', '172.16.32.6', '0', '2022-10-31 17:39:23', '2022-11-14 19:16:15', '244306439814778880', '422603');
INSERT INTO `ac_user` VALUES ('217', 'wiley，', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/e9c4cb6db7e99adcea1db2db8fd44e26.jpg', '0', null, null, null, null, '5', '2022-11-14 17:54:24', '172.16.32.6', '0', '2022-10-31 17:40:39', '2022-11-14 17:54:24', '151565088581488640', '543490');
INSERT INTO `ac_user` VALUES ('218', 'juju吖', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/aa66e26bb752755024a7c1720fb0a9bb', '0', null, null, null, null, '5', '2022-11-14 19:14:41', '172.16.32.6', '0', '2022-10-31 17:47:57', '2022-11-14 19:14:41', '391478335680544771', '4639549');
INSERT INTO `ac_user` VALUES ('219', '小涂', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/849a6a802cc4d628823e0ac130c2c2e9.jpg', '0', null, null, null, null, '5', '2022-11-14 17:24:20', '172.16.32.6', '0', '2022-10-31 17:50:41', '2022-11-14 17:24:20', '337772422595883008', '1682702');
INSERT INTO `ac_user` VALUES ('220', 'Serena', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/09c19fe101921b9b1456ca861faea7e7', '0', null, null, null, null, '5', '2022-11-01 14:18:26', '172.16.32.6', '0', '2022-10-31 18:04:15', '2022-11-01 14:18:26', '144018526846320640', '680749');
INSERT INTO `ac_user` VALUES ('221', '我叫小贱贱', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/68f485b40639ee91686f35264ab1c6ed', '0', null, null, null, null, '5', '2022-11-02 13:39:38', '172.16.32.6', '0', '2022-11-01 14:30:40', '2022-11-02 13:39:38', '155149659315113984', '422004');
INSERT INTO `ac_user` VALUES ('222', '蟹黄堡', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/ce243cd4090d865ff12de18b67666a97', '0', null, null, null, null, '5', '2022-11-01 15:51:53', '172.16.32.6', '0', '2022-11-01 15:51:53', '2022-11-01 15:51:53', '255223909111042048', '327832');
INSERT INTO `ac_user` VALUES ('223', 'lky', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/beef29abd75491bb327c33a8d9fcb907', '0', null, null, null, null, '5', '2022-11-14 17:25:05', '172.16.32.6', '0', '2022-11-14 15:25:11', '2022-11-14 17:25:05', '306625924885909504', '1130390');
INSERT INTO `ac_user` VALUES ('224', '小明', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/a01b8c221940e1508cdc6ec753998f61', '0', null, null, null, null, '5', '2022-11-14 17:58:21', '172.16.32.6', '0', '2022-11-14 16:04:41', '2022-11-14 17:58:21', '175793136587509760', '755099');
INSERT INTO `ac_user` VALUES ('225', 'ᅟᅠ        ‌‍‎‏', 'https://fb-cdn.fanbook.mobi/fanbook/app/files/service/headImage/7c1ffe3a53c35638f2acfc1f732ec1f2.jpg', '0', null, null, null, null, '5', '2022-11-14 19:52:07', '172.16.32.6', '0', '2022-11-14 19:52:07', '2022-11-14 19:52:07', '351579791901601792', '1344550');

-- ----------------------------
-- Table structure for ac_user_authorize
-- ----------------------------
DROP TABLE IF EXISTS `ac_user_authorize`;
CREATE TABLE `ac_user_authorize` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) NOT NULL COMMENT '第三方平台类型',
  `app_id` varchar(150) NOT NULL COMMENT '平台AppId',
  `open_id` varchar(150) NOT NULL COMMENT '平台OpenId',
  `user_name` varchar(255) NOT NULL COMMENT '平台用户名',
  `user_id` bigint DEFAULT NULL COMMENT '用户Id',
  `user_info` json DEFAULT NULL COMMENT '平台用户信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='第三方用户授权信息';

-- ----------------------------
-- Records of ac_user_authorize
-- ----------------------------

-- ----------------------------
-- Table structure for ac_user_role
-- ----------------------------
DROP TABLE IF EXISTS `ac_user_role`;
CREATE TABLE `ac_user_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT '1启用 0禁用',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_role_index` (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of ac_user_role
-- ----------------------------
INSERT INTO `ac_user_role` VALUES ('6', '206', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('7', '203', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('8', '204', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('11', '207', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('20', '208', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('21', '202', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('22', '205', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('29', '213', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('34', '214', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('35', '215', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('43', '217', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('44', '212', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('46', '216', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('47', '211', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('48', '219', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('49', '218', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('75', '220', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('144', '221', '3', '1', null);
INSERT INTO `ac_user_role` VALUES ('163', '222', '3', '1', null);

-- ----------------------------
-- Table structure for pr_project_prize
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_prize`;
CREATE TABLE `pr_project_prize` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) DEFAULT NULL,
  `project_key` varchar(100) DEFAULT NULL,
  `count` int DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '启用禁用',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=148 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for pr_project_prize_item
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_prize_item`;
CREATE TABLE `pr_project_prize_item` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1积分 0CDK',
  `project_key` varchar(100) DEFAULT NULL,
  `fanbookid` varchar(255) DEFAULT NULL,
  `nickname` varchar(32) DEFAULT NULL,
  `phone_number` varchar(11) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1启用 0禁用',
  `get_time` datetime DEFAULT NULL COMMENT '获取时间',
  `prize` varchar(255) DEFAULT NULL,
  `prizeid` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1148 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='奖品表';


-- ----------------------------
-- Table structure for pr_project_prize_setting
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_prize_setting`;
CREATE TABLE `pr_project_prize_setting` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '发奖方式 1立即发 0问卷结束发',
  `project_key` varchar(100) DEFAULT NULL,
  `probability` int DEFAULT '1' COMMENT '中奖率分母',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1启用0禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COMMENT='奖品设置';


-- ----------------------------
-- Table structure for pr_project_template
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_template`;
CREATE TABLE `pr_project_template` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '模板唯一标识',
  `cover_img` varchar(100) DEFAULT NULL COMMENT '封面图',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `describe` text COMMENT '项目描述',
  `like_count` int DEFAULT '0' COMMENT '喜欢数',
  `category_id` int NOT NULL COMMENT '项目类型',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  `guild_id` varchar(255) DEFAULT NULL,
  `fb_user` varchar(255) DEFAULT NULL,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code` (`key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目表';


-- ----------------------------
-- Table structure for pr_project_template_category
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_template_category`;
CREATE TABLE `pr_project_template_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '主题名称',
  `sort` int DEFAULT NULL COMMENT '排序',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目模板分类';

-- ----------------------------
-- Records of pr_project_template_category
-- ----------------------------

-- ----------------------------
-- Table structure for pr_project_template_item
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_template_item`;
CREATE TABLE `pr_project_template_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '项目key',
  `form_item_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单项Id',
  `type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '表单项类型 ',
  `label` varchar(255) NOT NULL COMMENT '表单项标题',
  `show_label` tinyint(1) NOT NULL COMMENT '是否显示标签',
  `default_value` json DEFAULT NULL COMMENT '表单项默认值',
  `required` tinyint(1) NOT NULL COMMENT '是否必填',
  `placeholder` varchar(255) DEFAULT NULL COMMENT '输入型提示文字',
  `sort` bigint DEFAULT '0' COMMENT '排序',
  `span` int NOT NULL DEFAULT '24' COMMENT '栅格宽度',
  `expand` json DEFAULT NULL COMMENT '扩展字段 表单项独有字段',
  `reg_list` json DEFAULT NULL COMMENT '正则表达式 ',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_display_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '展示类型组件',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Records of pr_project_template_item
-- ----------------------------

-- ----------------------------
-- Table structure for pr_project_theme
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_theme`;
CREATE TABLE `pr_project_theme` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '主题名称',
  `style` json NOT NULL COMMENT '主题风格\r\n',
  `head_img_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '头部图片',
  `color` json NOT NULL COMMENT '颜色代码',
  `btns_color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '按钮颜色',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目主题外观模板';

-- ----------------------------
-- Records of pr_project_theme
-- ----------------------------

-- ----------------------------
-- Table structure for pr_role
-- ----------------------------
DROP TABLE IF EXISTS `pr_role`;
CREATE TABLE `pr_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '1启用 0禁用',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色表';

-- ----------------------------
-- Records of pr_role
-- ----------------------------

-- ----------------------------
-- Table structure for pr_user_project
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project`;
CREATE TABLE `pr_user_project` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key` varchar(50) NOT NULL COMMENT '项目code',
  `source_id` varchar(255) DEFAULT NULL COMMENT '来源Id',
  `source_type` tinyint DEFAULT NULL COMMENT '来源类型',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `describe` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '项目描述',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` tinyint DEFAULT NULL COMMENT '项目类型',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fb_user` varchar(255) DEFAULT NULL,
  `guild_id` int DEFAULT NULL,
  `answer_num` int NOT NULL DEFAULT '0' COMMENT '答卷数量',
  `publish_num` int NOT NULL DEFAULT '0' COMMENT '发布次数',
  `project_share_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `main_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `links` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code` (`key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=344 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表';

-- ----------------------------
-- Table structure for pr_user_project_item
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_item`;
CREATE TABLE `pr_user_project_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '项目key',
  `form_item_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单项Id',
  `type` varchar(25) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '表单项类型 ',
  `label` varchar(255) NOT NULL COMMENT '表单项标题',
  `is_display_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '展示类型组件',
  `show_label` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否显示标签',
  `default_value` json DEFAULT NULL COMMENT '表单项默认值',
  `required` tinyint(1) NOT NULL COMMENT '是否必填',
  `placeholder` varchar(255) DEFAULT NULL COMMENT '输入型提示文字',
  `sort` bigint DEFAULT '0' COMMENT '排序',
  `span` int NOT NULL DEFAULT '24' COMMENT '栅格宽度',
  `expand` json DEFAULT NULL COMMENT '扩展字段 表单项独有字段',
  `reg_list` json DEFAULT NULL COMMENT '正则表达式 ',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `text_type` varchar(255) DEFAULT NULL,
  `title_tip` tinyint(1) NOT NULL DEFAULT '0',
  `title_tip_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1870 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_user_project_logic
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_logic`;
CREATE TABLE `pr_user_project_logic` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '逻辑Id',
  `project_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '项目key',
  `form_item_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '问题Id',
  `expression` tinyint(1) NOT NULL COMMENT '条件选项 ',
  `condition_list` json NOT NULL COMMENT '条件列表',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1显示逻辑 2跳题逻辑 3角色分配',
  `role_type` tinyint(1) DEFAULT '0' COMMENT 'false直接分配  true逻辑分配',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目逻辑';


-- ----------------------------
-- Table structure for pr_user_project_result
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_result`;
CREATE TABLE `pr_user_project_result` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '项目key',
  `serial_number` int DEFAULT NULL COMMENT '序号',
  `original_data` json DEFAULT NULL COMMENT '填写结果',
  `process_data` json DEFAULT NULL COMMENT '填写结果',
  `submit_ua` json DEFAULT NULL COMMENT '提交ua',
  `submit_os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '提交系统',
  `submit_browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '提交浏览器',
  `submit_request_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '请求ip',
  `submit_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '提交地址',
  `complete_time` int DEFAULT NULL COMMENT '完成时间 毫秒',
  `wx_open_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '微信openId',
  `wx_user_info` json DEFAULT NULL COMMENT '微信用户信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `fb_userid` bigint DEFAULT NULL,
  `fb_username` varchar(255) DEFAULT NULL,
  `guild_id` bigint DEFAULT NULL,
  `guild_name` varchar(255) DEFAULT NULL,
  `publish_time` varchar(50) DEFAULT NULL,
  `chat_id` bigint DEFAULT NULL,
  `fb_nickname` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_user_project_setting
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_setting`;
CREATE TABLE `pr_user_project_setting` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '项目key',
  `submit_prompt_img` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '提交提示图片',
  `submit_prompt_text` varchar(255) DEFAULT NULL COMMENT '提交提示文字',
  `submit_jump_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '提交跳转连接',
  `is_public_result` tinyint(1) DEFAULT NULL COMMENT '公开提交结果',
  `is_wx_write` tinyint(1) DEFAULT NULL COMMENT '只在微信填写  只在fanbook填写',
  `empower` tinyint(1) DEFAULT NULL COMMENT '填写时授权fanbookid',
  `is_wx_write_once` tinyint(1) DEFAULT NULL COMMENT '每个fanbookid只填写一次',
  `is_everyone_write_once` int DEFAULT NULL COMMENT '每人只能填写一次',
  `is_everyone_day_write_once` tinyint(1) DEFAULT NULL COMMENT '每人每天只能填写一次',
  `write_once_prompt_text` varchar(255) DEFAULT NULL COMMENT '填写之后提示',
  `new_write_notify_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '新反馈通知邮件',
  `new_write_notify_wx` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '新反馈通知微信',
  `is_record_wx_user` tinyint(1) DEFAULT NULL COMMENT '记录微信用户个人信息',
  `timed_collection_begin_time` datetime DEFAULT NULL COMMENT '定时收集开始时间',
  `timed_collection_end_time` datetime DEFAULT NULL COMMENT '定时收集结束时间',
  `timed_not_enabled_prompt_text` varchar(100) DEFAULT NULL COMMENT '定时未启动提示文字',
  `timed_deactivate_prompt_text` varchar(100) DEFAULT NULL COMMENT '定时停用会提示文字',
  `timed_quantitative_quantity` int DEFAULT NULL COMMENT '定时定量数量',
  `timed_end_prompt_text` varchar(100) DEFAULT NULL COMMENT '定时定量完成提示',
  `share_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分享图片',
  `share_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分享标题',
  `share_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分享描述',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=129 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_user_project_theme
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_theme`;
CREATE TABLE `pr_user_project_theme` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '项目key',
  `theme_id` bigint DEFAULT NULL COMMENT '主题Id',
  `submit_btn_text` varchar(20) DEFAULT NULL COMMENT '提交按钮文字',
  `logo_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'logo图片',
  `logo_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'logo位置',
  `background_color` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '背景颜色',
  `background_img` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '背景图片',
  `show_title` tinyint(1) DEFAULT '1' COMMENT '是否显示标题',
  `show_describe` tinyint(1) DEFAULT '1' COMMENT '是否显示描述语',
  `show_number` tinyint(1) DEFAULT '0' COMMENT '显示序号',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='项目表单项';



-- ----------------------------
-- Table structure for pr_user_publish
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_publish`;
CREATE TABLE `pr_user_publish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `guild_id` bigint NOT NULL COMMENT '服务器id',
  `guild_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `fb_channel` bigint DEFAULT NULL COMMENT '频道id',
  `fb_channel_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL COMMENT '1:推送成功；2：推送失败',
  `publish_time` varchar(50) DEFAULT NULL,
  `answer_num` int NOT NULL DEFAULT '0' COMMENT '答卷数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=253 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='推送消息表';


-- ----------------------------
-- Table structure for pr_user_role
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_role`;
CREATE TABLE `pr_user_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '1启用 0禁用',
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `user_role_index` (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='角色表';

-- ----------------------------
-- Records of pr_user_role
-- ----------------------------
INSERT INTO `pr_user_role` VALUES ('38', '1', '1', '1', null);

-- ----------------------------
-- Table structure for scheduled_task
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_task`;
CREATE TABLE `scheduled_task` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '项目key',
  `time` datetime DEFAULT NULL COMMENT '执行时间',
  `param` json DEFAULT NULL COMMENT '参数',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型 1：stop 2：推送',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 0：执行中 1：执行成功 2：执行失败',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `flag` tinyint(1) DEFAULT NULL COMMENT '是否删除 0：存在 1：删除',
  `pid` bigint DEFAULT NULL COMMENT '推送id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='定时任务';


-- ----------------------------
-- Table structure for wx_mp_user
-- ----------------------------
DROP TABLE IF EXISTS `wx_mp_user`;
CREATE TABLE `wx_mp_user` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `appid` varchar(255) NOT NULL COMMENT '公众号AppId',
  `nickname` varchar(255) DEFAULT NULL COMMENT '昵称',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别',
  `head_img_url` varchar(255) NOT NULL COMMENT '头像',
  `union_id` varchar(150) DEFAULT NULL,
  `open_id` varchar(150) NOT NULL,
  `country` varchar(255) DEFAULT NULL COMMENT '国家',
  `province` varchar(255) DEFAULT NULL COMMENT '省',
  `city` varchar(255) DEFAULT NULL COMMENT '城市',
  `is_subscribe` tinyint(1) DEFAULT '1' COMMENT '是否关注',
  `user_id` bigint DEFAULT NULL COMMENT '用户Id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `wx_union_id` (`head_img_url`(191)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='微信公众号用户 ';

-- ----------------------------
-- Records of wx_mp_user
-- ----------------------------
