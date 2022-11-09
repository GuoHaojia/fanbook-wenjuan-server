/*
 Navicat Premium Data Transfer

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80027
 Source Host           : localhost:3306
 Source Schema         : dev_tduck

 Target Server Type    : MySQL
 Target Server Version : 80027
 File Encoding         : 65001

 Date: 09/11/2022 14:07:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for scheduled_task
-- ----------------------------
DROP TABLE IF EXISTS `scheduled_task`;
CREATE TABLE `scheduled_task`  (
  `id` bigint(20) unsigned NOT NULL,
  `key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '项目key',
  `time` datetime DEFAULT NULL COMMENT '执行时间',
  `param` json DEFAULT NULL COMMENT '参数',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型 1：stop 2:推送',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态 0：执行中 1：执行成功 2：执行失败',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `flag` tinyint(1) DEFAULT NULL COMMENT '是否删除 0：存在 1：删除',
  `pid` bigint(20) DEFAULT NULL COMMENT '推送id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '定时任务' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of scheduled_task
-- ----------------------------
