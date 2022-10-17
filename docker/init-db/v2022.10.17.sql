ALTER TABLE `pr_user_project_setting`
MODIFY COLUMN `is_wx_write`  tinyint(1) NULL DEFAULT NULL COMMENT '只在微信填写  只在fanbook填写' AFTER `is_public_result`,
MODIFY COLUMN `is_wx_write_once`  tinyint(1) NULL DEFAULT NULL COMMENT '每个fanbookid只填写一次' AFTER `is_wx_write`,
ADD COLUMN `empower`  tinyint(1) NULL COMMENT '填写时授权fanbookid' AFTER `is_wx_write`,
ADD COLUMN `start_time`  datetime NULL AFTER `create_time`,
ADD COLUMN `end_time`  datetime NULL AFTER `start_time`;


/*
Navicat MySQL Data Transfer

Source Server         : make
Source Server Version : 50737
Source Host           : 127.0.0.1:3306
Source Database       : dev_tduck

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2022-10-17 16:45:33
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pr_project_prize_item
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_prize_item`;
CREATE TABLE `pr_project_prize_item` (
  `id` int(11) DEFAULT NULL,
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1积分 0CDK',
  `project_key` varchar(100) DEFAULT NULL,
  `fanbookid` varchar(255) DEFAULT NULL,
  `nickname` varchar(32) DEFAULT NULL,
  `phone_number` varchar(11) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1启用 0禁用',
  `get_time` datetime DEFAULT NULL COMMENT '获取时间',
  `prize` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖品表';

-- ----------------------------
-- Table structure for pr_project_prize_setting
-- ----------------------------
DROP TABLE IF EXISTS `pr_project_prize_setting`;
CREATE TABLE `pr_project_prize_setting` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '发奖方式 1立即发 0问卷结束发',
  `project_key` varchar(100) DEFAULT NULL,
  `probability` int(11) DEFAULT '1' COMMENT '中奖率分母',
  `count` int(11) DEFAULT NULL COMMENT '奖品总数',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1启用0禁用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='奖品设置';
