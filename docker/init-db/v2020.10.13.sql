/*
Navicat MySQL Data Transfer

Source Server         : make
Source Server Version : 50737
Source Host           : 127.0.0.1:3306
Source Database       : dev_tduck

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2022-10-13 16:05:06
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

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
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `status` tinyint(1) DEFAULT '1' COMMENT '0：失效 1：生效',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='角色表';

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
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  KEY `role_permission_index` (`role_id`,`permission_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of ac_role_permission
-- ----------------------------
INSERT INTO `ac_role_permission` VALUES ('1', '5', null);
INSERT INTO `ac_role_permission` VALUES ('1', '6', null);
INSERT INTO `ac_role_permission` VALUES ('1', '7', null);
INSERT INTO `ac_role_permission` VALUES ('3', '5', null);
INSERT INTO `ac_role_permission` VALUES ('3', '6', null);


ALTER TABLE `ac_user`
ADD UNIQUE INDEX `fanbookid` (`fb_user`) USING HASH ;


ALTER TABLE `pr_user_project_logic`
ADD COLUMN `type`  tinyint(1) NOT NULL DEFAULT 1 COMMENT '1显示逻辑 2跳题逻辑 3角色分配' AFTER `update_time`;


ALTER TABLE `pr_user_project_result`
MODIFY COLUMN `fb_userid`  bigint(20) NULL DEFAULT NULL AFTER `update_time`,
MODIFY COLUMN `guild_id`  bigint(20) NULL DEFAULT NULL AFTER `fb_username`,
AUTO_INCREMENT=2;