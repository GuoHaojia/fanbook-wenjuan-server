/*
Navicat MySQL Data Transfer

Source Server         : make
Source Server Version : 50737
Source Host           : 127.0.0.1:3306
Source Database       : dev_tduck

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2022-10-13 18:55:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pr_user_publish
-- ----------------------------
DROP TABLE IF EXISTS `pr_user_publish`;
CREATE TABLE `pr_user_publish`  (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `guild_id` bigint(20) DEFAULT NULL comment '服务器id',
  `guild_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `fb_channel` bigint(20) DEFAULT NULL comment '频道id',
  `fb_channel_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL comment '1:推送成功；2：推送失败',
  `publish_time` datetime DEFAULT NULL COMMENT '推送时间',
  `answer_num` int(10) NOT NULL DEFAULT 0 comment '答卷数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 ROW_FORMAT = Dynamic COMMENT '推送消息表';
