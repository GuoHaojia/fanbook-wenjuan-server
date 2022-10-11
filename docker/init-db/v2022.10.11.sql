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
