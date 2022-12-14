/*
Navicat MySQL Data Transfer

Source Server         : make
Source Server Version : 50737
Source Host           : 127.0.0.1:3306
Source Database       : dev_tduck

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2022-10-27 18:55:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pr_user_publish
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
  `fb_userid`  bigint(20) DEFAULT NULL,
  `fb_username` varchar(255) DEFAULT NULL,
  `guild_id`  bigint(20) DEFAULT NULL,
  `guild_name` varchar(255) DEFAULT NULL,
  `publish_time` varchar(255) DEFAULT NULL,
  `chat_id`  bigint(20) DEFAULT NULL,
  `fb_Nickname` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `phone_number` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `project_key` (`project_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表单项';

alter table `pr_user_publish` modify column `publish_time` varchar(50) DEFAULT NULL;
alter table `pr_user_project_result` modify column `publish_time` varchar(50) DEFAULT NULL;
alter table `pr_user_publish` modify column `fb_channel_name` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL;

alter table `pr_user_project` add column `project_share_img` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL after `publish_num`;
alter table `pr_user_project` add column `main_text` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL after `project_share_img`;
alter table `pr_user_project` add column `description` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL after `main_text`;
alter table `pr_user_project` add column `links` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL after `description`;