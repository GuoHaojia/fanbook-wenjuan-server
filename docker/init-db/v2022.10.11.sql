/*
Navicat MySQL Data Transfer

Source Server         : make
Source Server Version : 50737
Source Host           : 127.0.0.1:3306
Source Database       : dev_tduck

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2022-10-10 17:51:08
*/

SET FOREIGN_KEY_CHECKS=0;


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
   `guild_id` varchar(255) DEFAULT NULL,
   `fb_user` varchar(255) DEFAULT NULL,
   `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE KEY `code` (`key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='项目表';


-- ----------------------------
-- Table structure for ac_user_role
-- ----------------------------
DROP TABLE IF EXISTS `ac_user_role`;
CREATE TABLE `ac_user_role` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `user_id` int(11) NOT NULL,
    `role_id` int(11) NOT NULL,
    `status` tinyint(1) DEFAULT NULL COMMENT '1启用 0禁用',
    `create_time` datetime DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `user_role_index` (`user_id`,`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;


alter table `pr_user_project` add column `answer_num` int(10) not null default '0' comment '答卷数量' after `guild_id`;
alter table `pr_user_project` add column `publish_num` int(10) not null default '0' comment '发布次数' after `answer_num`;

alter table `pr_user_publish` add column `publish_time` datetime DEFAULT NULL comment '推送时间' after `status`;
alter table `pr_user_publish` add column `answer_num` int(10) NOT NULL DEFAULT 0 comment '答卷数量' after `publish_time`;
