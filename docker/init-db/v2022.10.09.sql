/*
Navicat MySQL Data Transfer

Source Server         : make
Source Server Version : 50737
Source Host           : 127.0.0.1:3306
Source Database       : dev_tduck

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2022-10-09 17:48:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for ac_permission
-- ----------------------------
DROP TABLE IF EXISTS `ac_permission`;
CREATE TABLE `ac_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(50) DEFAULT NULL,
  `action` varchar(64) DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '0：失效 1:生效',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ac_role
-- ----------------------------
DROP TABLE IF EXISTS `ac_role`;
CREATE TABLE `ac_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '0：失效 1：生效',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色表';

-- ----------------------------
-- Table structure for ac_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `ac_role_permission`;
CREATE TABLE `ac_role_permission` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  KEY `role_permission_index` (`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for ac_user
-- ----------------------------
DROP TABLE IF EXISTS `ac_user`;
CREATE TABLE `ac_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL DEFAULT '' COMMENT '姓名',
  `avatar` varchar(256) NOT NULL DEFAULT '' COMMENT '头像',
  `gender` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别0未知 1男2女',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(11) DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `reg_channel` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '注册渠道',
  `last_login_channel` tinyint(4) DEFAULT NULL COMMENT '最后登录渠道',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录Ip',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fb_user` varchar(255) DEFAULT NULL,
  `fb_username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户';

-- ----------------------------
-- Table structure for ac_user_authorize
-- ----------------------------
DROP TABLE IF EXISTS `ac_user_authorize`;
CREATE TABLE `ac_user_authorize` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) NOT NULL COMMENT '第三方平台类型',
  `app_id` varchar(150) NOT NULL COMMENT '平台AppId',
  `open_id` varchar(150) NOT NULL COMMENT '平台OpenId',
  `user_name` varchar(255) NOT NULL COMMENT '平台用户名',
  `user_id` bigint(11) DEFAULT NULL COMMENT '用户Id',
  `user_info` json DEFAULT NULL COMMENT '平台用户信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='第三方用户授权信息';

-- ----------------------------
-- Table structure for ac_user_role
-- ----------------------------
DROP TABLE IF EXISTS `ac_user_role`;
CREATE TABLE `ac_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_role_index` (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for pr_project_template
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_template`;
CREATE TABLE `pr_project_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '模板唯一标识',
  `cover_img` varchar(100) DEFAULT NULL COMMENT '封面图',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `describe` text COMMENT '项目描述',
  `like_count` int(10) DEFAULT '0' COMMENT '喜欢数',
  `category_id` int(2) NOT NULL COMMENT '项目类型',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code` (`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表';

-- ----------------------------
-- Table structure for pr_project_template_category
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_template_category`;
CREATE TABLE `pr_project_template_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '主题名称',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目模板分类';

-- ----------------------------
-- Table structure for pr_project_template_item
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_template_item`;
CREATE TABLE `pr_project_template_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '项目key',
  `form_item_id` varchar(11) CHARACTER SET utf8 DEFAULT NULL COMMENT '表单项Id',
  `type` varchar(25) CHARACTER SET utf8 NOT NULL COMMENT '表单项类型 ',
  `label` varchar(255) NOT NULL COMMENT '表单项标题',
  `show_label` tinyint(1) NOT NULL COMMENT '是否显示标签',
  `default_value` json DEFAULT NULL COMMENT '表单项默认值',
  `required` tinyint(1) NOT NULL COMMENT '是否必填',
  `placeholder` varchar(255) DEFAULT NULL COMMENT '输入型提示文字',
  `sort` bigint(20) DEFAULT '0' COMMENT '排序',
  `span` int(10) NOT NULL DEFAULT '24' COMMENT '栅格宽度',
  `expand` json DEFAULT NULL COMMENT '扩展字段 表单项独有字段',
  `reg_list` json DEFAULT NULL COMMENT '正则表达式 ',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_display_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '展示类型组件',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_project_theme
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_theme`;
CREATE TABLE `pr_project_theme` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '主题名称',
  `style` json NOT NULL COMMENT '主题风格\r\n',
  `head_img_url` varchar(255) CHARACTER SET utf8 NOT NULL COMMENT '头部图片',
  `color` json NOT NULL COMMENT '颜色代码',
  `btns_color` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '按钮颜色',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目主题外观模板';

-- ----------------------------
-- Table structure for pr_role
-- ----------------------------
DROP TABLE IF EXISTS `pr_role`;
CREATE TABLE `pr_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '1启用 0禁用',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1 COMMENT='用户角色表';

-- ----------------------------
-- Table structure for pr_user_project
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project`;
CREATE TABLE `pr_user_project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(50) NOT NULL COMMENT '项目code',
  `source_id` varchar(255) DEFAULT NULL COMMENT '来源Id',
  `source_type` tinyint(5) DEFAULT NULL COMMENT '来源类型',
  `name` varchar(100) NOT NULL COMMENT '项目名称',
  `describe` text CHARACTER SET utf8mb4 COMMENT '项目描述',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `type` tinyint(2) DEFAULT NULL COMMENT '项目类型',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `fb_user` varchar(255) DEFAULT NULL,
  `guild_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `code` (`key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='项目表';

-- ----------------------------
-- Table structure for pr_user_project_item
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_item`;
CREATE TABLE `pr_user_project_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '项目key',
  `form_item_id` varchar(11) CHARACTER SET utf8 DEFAULT NULL COMMENT '表单项Id',
  `type` varchar(25) CHARACTER SET utf8 NOT NULL COMMENT '表单项类型 ',
  `label` varchar(255) NOT NULL COMMENT '表单项标题',
  `is_display_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '展示类型组件',
  `show_label` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否显示标签',
  `default_value` json DEFAULT NULL COMMENT '表单项默认值',
  `required` tinyint(1) NOT NULL COMMENT '是否必填',
  `placeholder` varchar(255) DEFAULT NULL COMMENT '输入型提示文字',
  `sort` bigint(20) DEFAULT '0' COMMENT '排序',
  `span` int(10) NOT NULL DEFAULT '24' COMMENT '栅格宽度',
  `expand` json DEFAULT NULL COMMENT '扩展字段 表单项独有字段',
  `reg_list` json DEFAULT NULL COMMENT '正则表达式 ',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_user_project_logic
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_logic`;
CREATE TABLE `pr_user_project_logic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '逻辑Id',
  `project_key` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '项目key',
  `form_item_id` varchar(11) CHARACTER SET utf8 NOT NULL COMMENT '问题Id',
  `expression` tinyint(1) NOT NULL COMMENT '条件选项 ',
  `condition_list` json NOT NULL COMMENT '条件列表',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目逻辑';

-- ----------------------------
-- Table structure for pr_user_project_result
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_result`;
CREATE TABLE `pr_user_project_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '项目key',
  `serial_number` int(11) DEFAULT NULL COMMENT '序号',
  `original_data` json DEFAULT NULL COMMENT '填写结果',
  `process_data` json DEFAULT NULL COMMENT '填写结果',
  `submit_ua` json DEFAULT NULL COMMENT '提交ua',
  `submit_os` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '提交系统',
  `submit_browser` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '提交浏览器',
  `submit_request_ip` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '请求ip',
  `submit_address` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '提交地址',
  `complete_time` int(11) DEFAULT NULL COMMENT '完成时间 毫秒',
  `wx_open_id` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '微信openId',
  `wx_user_info` json DEFAULT NULL COMMENT '微信用户信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `fb_userid` int(11) DEFAULT NULL,
  `fb_username` varchar(255) DEFAULT NULL,
  `guild_id` int(11) DEFAULT NULL,
  `guild_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_user_project_setting
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_setting`;
CREATE TABLE `pr_user_project_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '项目key',
  `submit_prompt_img` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '提交提示图片',
  `submit_prompt_text` varchar(255) DEFAULT NULL COMMENT '提交提示文字',
  `submit_jump_url` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '提交跳转连接',
  `is_public_result` tinyint(1) DEFAULT NULL COMMENT '公开提交结果',
  `is_wx_write` tinyint(1) DEFAULT NULL COMMENT '只在微信填写',
  `is_wx_write_once` tinyint(1) DEFAULT NULL,
  `is_everyone_write_once` tinyint(1) DEFAULT NULL COMMENT '每人只能填写一次',
  `is_everyone_day_write_once` tinyint(1) DEFAULT NULL COMMENT '每人每天只能填写一次',
  `write_once_prompt_text` varchar(255) DEFAULT NULL COMMENT '填写之后提示',
  `new_write_notify_email` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '新反馈通知邮件',
  `new_write_notify_wx` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '新反馈通知微信',
  `is_record_wx_user` tinyint(1) DEFAULT NULL COMMENT '记录微信用户个人信息',
  `timed_collection_begin_time` datetime DEFAULT NULL COMMENT '定时收集开始时间',
  `timed_collection_end_time` datetime DEFAULT NULL COMMENT '定时收集结束时间',
  `timed_not_enabled_prompt_text` varchar(100) DEFAULT NULL COMMENT '定时未启动提示文字',
  `timed_deactivate_prompt_text` varchar(100) DEFAULT NULL COMMENT '定时停用会提示文字',
  `timed_quantitative_quantity` int(100) DEFAULT NULL COMMENT '定时定量数量',
  `timed_end_prompt_text` varchar(100) DEFAULT NULL COMMENT '定时定量完成提示',
  `share_img` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '分享图片',
  `share_title` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '分享标题',
  `share_desc` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '分享描述',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_user_project_theme
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_project_theme`;
CREATE TABLE `pr_user_project_theme` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_key` varchar(100) CHARACTER SET utf8 NOT NULL COMMENT '项目key',
  `theme_id` bigint(20) DEFAULT NULL COMMENT '主题Id',
  `submit_btn_text` varchar(20) DEFAULT NULL COMMENT '提交按钮文字',
  `logo_img` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT 'logo图片',
  `logo_position` varchar(10) CHARACTER SET utf8 DEFAULT NULL COMMENT 'logo位置',
  `background_color` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '背景颜色',
  `background_img` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '背景图片',
  `show_title` tinyint(1) DEFAULT '1' COMMENT '是否显示标题',
  `show_describe` tinyint(1) DEFAULT '1' COMMENT '是否显示描述语',
  `show_number` tinyint(1) DEFAULT '0' COMMENT '显示序号',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

-- ----------------------------
-- Table structure for pr_user_publish
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_publish`;
CREATE TABLE `pr_user_publish` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) DEFAULT NULL,
  `guild_id` int(11) DEFAULT NULL,
  `guild_name` varchar(255) DEFAULT NULL,
  `fb_channel` int(11) DEFAULT NULL,
  `fb_channel_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Table structure for pr_user_role
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_role`;
CREATE TABLE `pr_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `user_role_index` (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色表';

-- ----------------------------
-- Table structure for wx_mp_user
-- ----------------------------
DROP TABLE IF EXISTS `wx_mp_user`;
CREATE TABLE `wx_mp_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键',
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
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户Id',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `wx_union_id` (`head_img_url`(191)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='微信公众号用户 ';

INSERT INTO `tduck`.`ac_user`(`id`, `name`, `avatar`, `gender`, `email`, `phone_number`, `password`, `reg_channel`,
                              `last_login_channel`, `last_login_time`, `last_login_ip`, `deleted`, `create_time`,
                              `update_time`)
VALUES (201, 'Tduck001',
        'https://oss.smileyi.top/757b505cfd34c64c85ca5b5690ee5293/4c415e1c1af04af68d740e667819bece.png', 0,
        'test@tduck.com', NULL, 'ef797c8118f02dfb649607dd5d3f8c7623048c9c063d532cc95c5ed7a898a64f', '1', 2,
        '2021-06-24 11:08:21', '111.30.56.26', 0, '2020-11-12 11:50:50', '2021-06-24 11:08:21');