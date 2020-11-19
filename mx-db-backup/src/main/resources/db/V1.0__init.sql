/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50505
Source Host           : localhost:3306
Source Database       : db_backup

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2020-11-13 10:28:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for db_config
-- ----------------------------
DROP TABLE IF EXISTS `db_config`;
CREATE TABLE `db_config` (
  `dsId` bigint(11) DEFAULT NULL COMMENT '数据源id',
  `name` varchar(200) DEFAULT '' COMMENT '连接名称',
  `dbType` varchar(200) DEFAULT '',
  `adress` varchar(200) DEFAULT '' COMMENT 'ip',
  `port` varchar(200) DEFAULT NULL COMMENT '端口',
  `password` varchar(200) DEFAULT '' COMMENT '端口',
  `username` varchar(200) DEFAULT '',
  `database` varchar(255) DEFAULT NULL COMMENT '指定备份数据库',
  `service_name` varchar(255) DEFAULT '' COMMENT '服务名'
) ;

-- ----------------------------
-- Records of db_config
-- ----------------------------

-- ----------------------------
-- Table structure for db_type
-- ----------------------------
DROP TABLE IF EXISTS `db_type`;
CREATE TABLE `db_type` (
  `id` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL
) ;

-- ----------------------------
-- Records of db_type
-- ----------------------------
INSERT INTO `db_type` VALUES ('1', 'mysql');
INSERT INTO `db_type` VALUES ('2', 'oracle');

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule` (
  `sId` bigint(20) DEFAULT NULL COMMENT '定时任务id',
  `dsId` bigint(20) DEFAULT NULL COMMENT '数据源id',
  `cron` varchar(255) DEFAULT NULL COMMENT 'cron表达式',
  `status` int(11) DEFAULT 0 COMMENT '状态'
) ;

-- ----------------------------
-- Records of schedule
-- ----------------------------

-- ----------------------------
-- Table structure for task_record
-- ----------------------------
DROP TABLE IF EXISTS `task_record`;
CREATE TABLE `task_record` (
  `rid` bigint(20) DEFAULT NULL COMMENT '主键',
  `sId` bigint(20) DEFAULT NULL COMMENT '定时任务id',
  `backupTime` datetime DEFAULT NULL COMMENT '备份时间',
  `backupDuration` bigint(20) DEFAULT NULL COMMENT '备份耗时',
  `logFile` varchar(255) DEFAULT '' COMMENT '日志文件',
  `dataFileSize` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `dataFile` varchar(255) DEFAULT '' COMMENT '文件路径',
  `status` int(11) DEFAULT NULL COMMENT '状态'
) ;

-- ----------------------------
-- Records of task_record
-- ----------------------------
