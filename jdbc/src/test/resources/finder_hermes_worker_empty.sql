-- --------------------------------------------------------
-- 主机:                           192.168.1.8
-- 服务器版本:                        5.7.35-log - MySQL Community Server (GPL)
-- 服务器操作系统:                      Linux
-- HeidiSQL 版本:                  11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 导出 finder_worker 的数据库结构
CREATE DATABASE IF NOT EXISTS `finder_worker` /*!40100 DEFAULT_PROFILE CHARACTER SET utf8 */;
USE `finder_worker`;

-- 导出  表 finder_worker.camera 结构
CREATE TABLE IF NOT EXISTS `camera` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `rtsp_addr` varchar(250) NOT NULL,
  `private_port` int(10) DEFAULT NULL,
  `topic` varchar(250) DEFAULT 'company',
  `framerate` int(11) DEFAULT '1',
  `grab` tinyint(4) DEFAULT '1',
  `tracking` tinyint(4) DEFAULT '0',
  `name` varchar(250) DEFAULT NULL,
  `camera_id` int(11) NOT NULL,
  `grab_choice` varchar(45) DEFAULT 'rtsp',
  `brand` varchar(45) DEFAULT NULL,
  `threshold` float DEFAULT '80',
  `face_size` int(11) DEFAULT NULL,
  `roi` json DEFAULT NULL,
  `recording` tinyint(4) DEFAULT '0',
  `api_address` varchar(255) DEFAULT NULL COMMENT '视频流获取地址',
  `web_hook` varchar(255) DEFAULT NULL COMMENT '告警回调地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.camera 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `camera` DISABLE KEYS */;
/*!40000 ALTER TABLE `camera` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_01 结构
CREATE TABLE IF NOT EXISTS `faces_01` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_01 的数据：0 rows
/*!40000 ALTER TABLE `faces_01` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_01` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_02 结构
CREATE TABLE IF NOT EXISTS `faces_02` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_02 的数据：0 rows
/*!40000 ALTER TABLE `faces_02` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_02` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_03 结构
CREATE TABLE IF NOT EXISTS `faces_03` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_03 的数据：0 rows
/*!40000 ALTER TABLE `faces_03` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_03` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_04 结构
CREATE TABLE IF NOT EXISTS `faces_04` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_04 的数据：0 rows
/*!40000 ALTER TABLE `faces_04` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_04` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_05 结构
CREATE TABLE IF NOT EXISTS `faces_05` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_05 的数据：0 rows
/*!40000 ALTER TABLE `faces_05` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_05` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_06 结构
CREATE TABLE IF NOT EXISTS `faces_06` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_06 的数据：0 rows
/*!40000 ALTER TABLE `faces_06` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_06` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_07 结构
CREATE TABLE IF NOT EXISTS `faces_07` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_07 的数据：0 rows
/*!40000 ALTER TABLE `faces_07` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_07` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_08 结构
CREATE TABLE IF NOT EXISTS `faces_08` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_08 的数据：0 rows
/*!40000 ALTER TABLE `faces_08` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_08` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_09 结构
CREATE TABLE IF NOT EXISTS `faces_09` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_09 的数据：0 rows
/*!40000 ALTER TABLE `faces_09` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_09` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_10 结构
CREATE TABLE IF NOT EXISTS `faces_10` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_10 的数据：0 rows
/*!40000 ALTER TABLE `faces_10` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_10` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_11 结构
CREATE TABLE IF NOT EXISTS `faces_11` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=34345 DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_11 的数据：0 rows
/*!40000 ALTER TABLE `faces_11` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_11` ENABLE KEYS */;

-- 导出  表 finder_worker.faces_12 结构
CREATE TABLE IF NOT EXISTS `faces_12` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(45) DEFAULT NULL,
  `location` json DEFAULT NULL,
  `landmark` json DEFAULT NULL,
  `frame` varchar(45) DEFAULT NULL,
  `timestamp` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `camera_id` int(11) NOT NULL DEFAULT '0',
  `camera_name` varchar(45) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `in_time` datetime DEFAULT NULL,
  `out_time` datetime DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(4) DEFAULT NULL,
  `expression` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.faces_12 的数据：0 rows
/*!40000 ALTER TABLE `faces_12` DISABLE KEYS */;
/*!40000 ALTER TABLE `faces_12` ENABLE KEYS */;

-- 导出  表 finder_worker.record_videos 结构
CREATE TABLE IF NOT EXISTS `record_videos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `camera_id` int(11) DEFAULT NULL,
  `filename` varchar(45) DEFAULT NULL,
  `timestamp` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `camera_id` (`camera_id`,`filename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.record_videos 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `record_videos` DISABLE KEYS */;
/*!40000 ALTER TABLE `record_videos` ENABLE KEYS */;

-- 导出  表 finder_worker.system_config 结构
CREATE TABLE IF NOT EXISTS `system_config` (
  `name` varchar(250) NOT NULL,
  `data` json DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.system_config 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `system_config` DISABLE KEYS */;
INSERT IGNORE INTO `system_config` (`name`, `data`) VALUES
	('faces_expire_time', '31536000');
/*!40000 ALTER TABLE `system_config` ENABLE KEYS */;

-- 导出  表 finder_worker.t_analyse_schedule 结构
CREATE TABLE IF NOT EXISTS `t_analyse_schedule` (
  `ChannelId` int(32) NOT NULL,
  `ChannelName` varchar(128) DEFAULT NULL,
  `DeviceId` int(32) NOT NULL,
  `DeviceName` varchar(128) DEFAULT NULL,
  `DeviceType` varchar(128) DEFAULT NULL,
  `FunctionId` int(32) NOT NULL,
  `DeviceUrl` varchar(512) DEFAULT NULL,
  `RecordEnable` int(8) DEFAULT NULL,
  `FrameCaptureNodeId` varchar(128) DEFAULT NULL,
  `FrameCaptureExtra` varchar(512) DEFAULT NULL,
  `Switch` int(8) NOT NULL,
  `FrameRate` int(32) NOT NULL,
  `FrameTime` int(32) NOT NULL,
  `Roi` varchar(512) NOT NULL,
  `Type` varchar(128) DEFAULT NULL,
  `Scheme` varchar(1024) DEFAULT NULL,
  `ImageProcessNodeId` varchar(128) DEFAULT NULL,
  `ImageProcessExtra` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`ChannelId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_analyse_schedule 的数据：0 rows
/*!40000 ALTER TABLE `t_analyse_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_analyse_schedule` ENABLE KEYS */;

-- 导出  表 finder_worker.t_black_list 结构
CREATE TABLE IF NOT EXISTS `t_black_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` varchar(255) DEFAULT NULL COMMENT '人员类型',
  `username` varchar(255) DEFAULT NULL COMMENT '姓名',
  `gender` varchar(255) DEFAULT NULL COMMENT '性别  0-男， 1 -女',
  `age` varchar(255) DEFAULT NULL COMMENT '年龄',
  `id_card` varchar(255) DEFAULT NULL COMMENT '身份证号',
  `state` varchar(255) DEFAULT NULL,
  `domicile` varchar(255) DEFAULT NULL COMMENT '户籍行政区域',
  `case_category` varchar(255) DEFAULT NULL COMMENT '案件类别',
  `case_time` date DEFAULT NULL COMMENT '立案日期  年-月-日',
  `track` int(255) DEFAULT '0' COMMENT '是否为追踪库，1表示追踪，追踪库实际不存在',
  `status` int(255) DEFAULT '0' COMMENT '数据状态  0未删除 1表示删除 ',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据最新更新时间',
  `effective_date` datetime DEFAULT NULL COMMENT '生效时间',
  `expiration_date` datetime DEFAULT NULL COMMENT '失效时间',
  `description` text COMMENT '描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_black_list 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `t_black_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_black_list` ENABLE KEYS */;

-- 导出  表 finder_worker.t_camera 结构
CREATE TABLE IF NOT EXISTS `t_camera` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `type` varchar(56) DEFAULT NULL,
  `address` varchar(100) DEFAULT NULL,
  `api_address` varchar(255) DEFAULT NULL COMMENT '获取视频流地址接口',
  `web_hook` varchar(255) DEFAULT NULL COMMENT '告警推送接口',
  `status` int(11) DEFAULT NULL,
  `gpsx` double DEFAULT NULL,
  `gpsy` double DEFAULT NULL,
  `gpsz` double DEFAULT NULL,
  `install_time` datetime DEFAULT NULL,
  `manufacturer` varchar(100) DEFAULT NULL,
  `reserved1` varchar(255) DEFAULT NULL,
  `reserved2` varchar(255) DEFAULT NULL,
  `reserved3` varchar(255) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `region_id` bigint(20) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(255) DEFAULT NULL,
  `regionId` bigint(20) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `grab_choice` varchar(255) DEFAULT NULL COMMENT '抓拍方式',
  `private_port` int(11) DEFAULT NULL COMMENT 'rtsp私有端口',
  `threshold` float(7,4) DEFAULT NULL COMMENT '阈值',
  `framerate` int(11) DEFAULT NULL COMMENT '帧率',
  `face_size` int(11) DEFAULT '80',
  `tracking` tinyint(4) DEFAULT '0',
  `library_id` bigint(20) DEFAULT NULL COMMENT '名单组ID',
  `recording` int(11) DEFAULT '0' COMMENT '是否选择录播   1：是   0：否    默认为0',
  `door_id` bigint(20) DEFAULT NULL COMMENT '关联门id',
  `description` varchar(255) DEFAULT NULL,
  `roi` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_camera 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `t_camera` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_camera` ENABLE KEYS */;

-- 导出  表 finder_worker.t_camera_access 结构
CREATE TABLE IF NOT EXISTS `t_camera_access` (
  `DeviceId` int(32) NOT NULL,
  `Type` varchar(16) DEFAULT NULL,
  `Status` int(32) DEFAULT NULL,
  `RtspUrl` varchar(512) DEFAULT NULL,
  `Ip` varchar(16) DEFAULT NULL,
  `Account` varchar(32) DEFAULT NULL,
  `Password` varchar(32) DEFAULT NULL,
  `Port` int(32) DEFAULT NULL,
  PRIMARY KEY (`DeviceId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_camera_access 的数据：0 rows
/*!40000 ALTER TABLE `t_camera_access` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_camera_access` ENABLE KEYS */;

-- 导出  表 finder_worker.t_camera_schedule 结构
CREATE TABLE IF NOT EXISTS `t_camera_schedule` (
  `ChannelId` int(32) NOT NULL,
  `DeviceId` int(32) NOT NULL,
  `DeviceName` varchar(128) DEFAULT NULL,
  `RtspUrl` varchar(512) DEFAULT NULL,
  `RecordEnable` int(8) DEFAULT NULL,
  `FrameCaptureNodeId` varchar(128) DEFAULT NULL,
  `FrameCaptureExtra` varchar(128) DEFAULT NULL,
  `Switch` int(8) NOT NULL,
  `FrameRates` int(32) NOT NULL,
  `Type` varchar(128) DEFAULT NULL,
  `Scheme` varchar(512) DEFAULT NULL,
  `ImageProcessNodeId` varchar(128) DEFAULT NULL,
  `ImageProcessExtra` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ChannelId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_camera_schedule 的数据：0 rows
/*!40000 ALTER TABLE `t_camera_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_camera_schedule` ENABLE KEYS */;

-- 导出  表 finder_worker.t_camera_task 结构
CREATE TABLE IF NOT EXISTS `t_camera_task` (
  `ChannelId` int(32) NOT NULL,
  `DeviceId` int(32) NOT NULL,
  `DeviceName` varchar(128) DEFAULT NULL,
  `RtspUrl` varchar(512) DEFAULT NULL,
  `RecordEnable` int(8) DEFAULT NULL,
  `FrameCaptureExtra` varchar(128) DEFAULT NULL,
  `Switch` int(8) NOT NULL,
  `FrameRates` int(32) NOT NULL,
  `Type` varchar(128) DEFAULT NULL,
  `Scheme` varchar(512) DEFAULT NULL,
  `ImageProcessExtra` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`ChannelId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_camera_task 的数据：0 rows
/*!40000 ALTER TABLE `t_camera_task` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_camera_task` ENABLE KEYS */;

-- 导出  表 finder_worker.t_camera_t_people_group 结构
CREATE TABLE IF NOT EXISTS `t_camera_t_people_group` (
  `group_id` bigint(20) NOT NULL,
  `camera_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_camera_t_people_group 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `t_camera_t_people_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_camera_t_people_group` ENABLE KEYS */;

-- 导出  表 finder_worker.t_image_url 结构
CREATE TABLE IF NOT EXISTS `t_image_url` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `black_list_id` bigint(20) DEFAULT NULL COMMENT '名单id',
  `image_url` varchar(255) DEFAULT NULL COMMENT '人员照片的地址',
  `status` int(11) DEFAULT '0' COMMENT '数据状态  0未删除 1表示删除 ',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '数据最新更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_black_list_id` (`id`,`black_list_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_image_url 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `t_image_url` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_image_url` ENABLE KEYS */;

-- 导出  表 finder_worker.t_people_group_t_black_list 结构
CREATE TABLE IF NOT EXISTS `t_people_group_t_black_list` (
  `group_id` bigint(20) NOT NULL,
  `people_id` bigint(20) NOT NULL,
  KEY `group_id` (`group_id`) USING BTREE,
  KEY `people_id` (`people_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_people_group_t_black_list 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `t_people_group_t_black_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_people_group_t_black_list` ENABLE KEYS */;

-- 导出  表 finder_worker.t_video_analysis 结构
CREATE TABLE IF NOT EXISTS `t_video_analysis` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_url` varchar(255) NOT NULL COMMENT '文件URL',
  `time` mediumtext NOT NULL COMMENT '时长',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `analysis_status` int(11) NOT NULL DEFAULT '0',
  `file_md5` varchar(64) NOT NULL,
  `rate` float NOT NULL DEFAULT '1',
  `media_type` varchar(256) NOT NULL,
  `ruid` varchar(128) NOT NULL,
  `failure` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `file_md5` (`file_md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.t_video_analysis 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `t_video_analysis` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_video_analysis` ENABLE KEYS */;

-- 导出  表 finder_worker.video 结构
CREATE TABLE IF NOT EXISTS `video` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(250) DEFAULT NULL,
  `timestamp` datetime DEFAULT CURRENT_TIMESTAMP,
  `format` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT 'pending',
  `callback` varchar(250) DEFAULT NULL,
  `speedup` int(11) DEFAULT NULL,
  `length` varchar(45) DEFAULT NULL,
  `pivot` int(11) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.video 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `video` DISABLE KEYS */;
/*!40000 ALTER TABLE `video` ENABLE KEYS */;

-- 导出  表 finder_worker.video_analysis 结构
CREATE TABLE IF NOT EXISTS `video_analysis` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `face` varchar(250) DEFAULT NULL,
  `frame` varchar(250) DEFAULT NULL,
  `timestamp` int(11) DEFAULT NULL,
  `glasses` tinyint(4) DEFAULT NULL,
  `video_name` varchar(250) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL,
  `hair` tinyint(4) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 正在导出表  finder_worker.video_analysis 的数据：~0 rows (大约)
/*!40000 ALTER TABLE `video_analysis` DISABLE KEYS */;
/*!40000 ALTER TABLE `video_analysis` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
