-- `user` table
DROP TABLE If Exists `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'user id',
  `name` varchar(64) NOT NULL COMMENT 'user name',
  `email` varchar(64) NOT NULL COMMENT 'user email',
  `desc` varchar(64) DEFAULT NULL COMMENT 'description information of user',
  `phone` varchar(20) DEFAULT NULL COMMENT 'user phone number',
  `password` varchar(32) NOT NULL COMMENT 'user password for login, md5-value',
  `role` tinyint(4) NOT NULL COMMENT '0 means administrator, others means normal user',
  `proxy_users` text DEFAULT NULL COMMENT 'allow proxy user list',
  `create_time` datetime NOT NULL COMMENT 'create time of user',
  `modify_time` datetime NOT NULL COMMENT 'last modify time of user information',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Create a admin user, password is '123456'
INSERT INTO `user`(`name`, `email`, `desc`, `phone`, `password`, `role`, `create_time`, `modify_time`) VALUES('admin', 'admin@baifendian.com', 'administrator user', '13800000000', 'e10adc3949ba59abbe56e057f20f883e', 0, now(),now());

-- `project` table
DROP TABLE If Exists `project`;
CREATE TABLE `project` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'project id',
  `name` varchar(64) NOT NULL COMMENT 'project name',
  `desc` varchar(256) DEFAULT NULL COMMENT 'project description',
  `owner` int(11) NOT NULL COMMENT 'owner of the project',
  `create_time` datetime NOT NULL COMMENT 'create time of the project',
  `modify_time` datetime NOT NULL COMMENT 'last modify time of the project',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  FOREIGN KEY (`owner`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `project_user` table
DROP TABLE If Exists `project_user`;
CREATE TABLE `project_user` (
  `project_id` int(11) NOT NULL COMMENT 'project id',
  `user_id` int(11) NOT NULL COMMENT 'user id',
  `perm` int(11) NOT NULL COMMENT 'permission, w/r/x, 0x04-w, 0x02-r, 0x01-x',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `modify_time` datetime NOT NULL COMMENT 'last modify time',
  UNIQUE KEY `project_user` (`project_id`, `user_id`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `session` table
DROP TABLE If Exists `session`;
CREATE TABLE `session` (
  `id` varchar(64) NOT NULL COMMENT 'session id',
  `user_id` int(11) NOT NULL COMMENT 'user id',
  `ip` varchar(32) NOT NULL COMMENT 'ip address of login on',
  `last_login_time` datetime NOT NULL COMMENT 'last login time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_ip` (`user_id`, `ip`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `resources` table
DROP TABLE If Exists `resources`;
CREATE TABLE `resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'resource id',
  `name` varchar(64) NOT NULL COMMENT 'resources name',
  `suffix` varchar(20) NOT NULL COMMENT 'suffix of the file',
  `origin_filename` varchar(64) NOT NULL COMMENT 'file name of the orgin file',
  `desc` varchar(256) DEFAULT NULL COMMENT 'resources description',
  `owner` int(11) NOT NULL COMMENT 'owner id of the resource',
  `project_id` int(11) NOT NULL COMMENT 'project id of this resource',
  `create_time` datetime NOT NULL COMMENT 'resource create time',
  `modify_time` datetime NOT NULL COMMENT 'resource last modify time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_resname` (`project_id`, `name`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`owner`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `datasource` table
DROP TABLE If Exists `datasource`;
CREATE TABLE `datasource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'datasource id',
  `name` varchar(64) NOT NULL COMMENT 'datasource name',
  `desc` varchar(256) DEFAULT NULL COMMENT 'datasource description',
  `type` int(11) NOT NULL COMMENT 'datasource type',
  `owner` int(11) NOT NULL COMMENT 'owner id of the datasource.',
  `project_id` int(11) NOT NULL COMMENT 'project id of the datasource.',
  `params` text NOT NULL COMMENT 'datasource params',
  `create_time` datetime NOT NULL COMMENT 'create time of the datasource',
  `modify_time` datetime NOT NULL COMMENT 'modify time of the datasource',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_dsname` (`project_id`, `name`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`owner`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `project_flows` table
DROP TABLE If Exists `project_flows`;
CREATE TABLE `project_flows` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'project_flows id',
  `name` varchar(64) NOT NULL COMMENT 'project_flows name',
  `project_id` int(20) NOT NULL COMMENT 'project id of the project_flows',
  `create_time` datetime NOT NULL COMMENT 'create time of the project_flows',
  `modify_time` datetime NOT NULL COMMENT 'modify time of the project_flows',
  `last_modify_by` int(20) NOT NULL COMMENT 'last modify user id of the project_flows',
  `owner` int(20) NOT NULL COMMENT 'owner id of the project_flows.',
  `proxy_user` varchar(64) NOT NULL COMMENT 'proxy user of the project_flows.',
  `user_defined_params` text DEFAULT NULL COMMENT 'user defined params of the project_flows.',
  `extras` text DEFAULT NULL COMMENT 'extends of the project_flows',
  `queue` varchar(64) DEFAULT NULL COMMENT 'queue of the project_flows',
  PRIMARY KEY (`id`),
  UNIQUE KEY `project_flowname` (`project_id`, `name`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`owner`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `flows_nodes` table
DROP TABLE If Exists `flows_nodes`;
CREATE TABLE `flows_nodes` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'flow nodes id',
  `name` varchar(64) NOT NULL COMMENT 'flow nodes name',
  `flow_id` int(11) NOT NULL COMMENT 'project flow id of the flow nodes',
  `desc` VARCHAR(512) NOT NULL COMMENT 'create time of the flow nodes',
  `create_time` datetime NOT NULL COMMENT 'create time of the flow nodes',
  `modify_time` datetime NOT NULL COMMENT 'modify time of the flow nodes',
  `last_modify_by` int(11) NOT NULL COMMENT 'last modify user id of the flow nodes',
  `type` int(11) NOT NULL COMMENT 'type of the flow nodes',
  `param` text DEFAULT NULL COMMENT 'param of the flow nodes.',
  `extras` text DEFAULT NULL COMMENT 'extends of the flow nodes',
  `dep` VARCHAR(512) DEFAULT NULL COMMENT 'dep of the flow nodes',
  PRIMARY KEY (`id`),
  UNIQUE KEY `flows_nodename` (`flow_id`, `name`),
  FOREIGN KEY (`flow_id`) REFERENCES `project_flows`(`id`) ON DELETE CASCADE,
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `ad_hocs` table
DROP TABLE If Exists `ad_hocs`;
CREATE TABLE `ad_hocs` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'exec id of ad hoc query',
  `project_id` int(11) NOT NULL COMMENT 'project id of the ad hoc',
  `owner` int(11) NOT NULL COMMENT 'owner id of the ad hoc',
  `stms` text NOT NULL COMMENT 'statements of this ad hoc query',
  `number` int(11) NOT NULL COMMENT 'results number of this ad hoc query',
  `proxy_user` varchar(30) NOT NULL COMMENT 'proxy user name',
  `queue` varchar(40) NOT NULL COMMENT 'queue name',
  `status` tinyint(4) NOT NULL COMMENT 'status, refer https://github.com/baifendian/swordfish/wiki/workflow-exec%26maintain',
  `create_time` datetime NOT NULL COMMENT 'create time of the ad hoc query',
  `start_time` datetime DEFAULT NULL COMMENT 'start time of this exec',
  `end_time` datetime DEFAULT NULL COMMENT 'end time of this exec',
  `job_id` varchar(64) DEFAULT NULL COMMENT 'job id of this exec',
  PRIMARY KEY (`id`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`owner`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `ad_hoc_results` table
DROP TABLE If Exists `ad_hoc_results`;
CREATE TABLE `ad_hoc_results` (
  `exec_id` int(11) NOT NULL COMMENT 'exec id of ad hoc query',
  `index` int(11) NOT NULL COMMENT 'index of the stm',
  `stm` text NOT NULL COMMENT 'sql clause',
  `result` text DEFAULT NULL COMMENT 'result of this exec',
  `status` tinyint(4) NOT NULL COMMENT 'status of this exec',
  `create_time` datetime NOT NULL COMMENT 'create time of the records',
  `start_time` datetime DEFAULT NULL COMMENT 'start time of this exec',
  `end_time` datetime DEFAULT NULL COMMENT 'end time of this exec',
  PRIMARY KEY (`exec_id`,`index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- `master_server` table
DROP TABLE If Exists `master_server`;
CREATE TABLE `master_server` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'master id',
  `host` varchar(20) NOT NULL COMMENT 'ip address of the master',
  `port` int(11) NOT NULL COMMENT 'port of the master',
  `create_time` datetime NOT NULL COMMENT 'create time of the records',
  `modify_time` datetime NOT NULL COMMENT 'last modify time of the records',
  PRIMARY KEY (`id`),
  UNIQUE KEY `host_port` (`host`, `port`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ad_hoc_result table
DROP TABLE IF EXISTS `ad_hoc_results`;
CREATE TABLE `ad_hoc_results` (
  `ad_hoc_id` bigint(20) NOT NULL,
  `index` int(11) NOT NULL,
  `stm` text NOT NULL,
  `result` text,
  `status` tinyint(4) NOT NULL,
  `create_time` datetime NOT NULL,
  PRIMARY KEY (`ad_hoc_id`, `index`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ad_hocs`;
CREATE TABLE `ad_hocs` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `params` text NOT NULL,
  `proxy_user` varchar(30) NOT NULL,
  `queue` varchar(40) DEFAULT NULL,
  `status` tinyint(4) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `owner` int(11) DEFAULT NULL,
  `last_modify_by` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `job_id` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `schedules`;
CREATE TABLE `schedules` (
  `flow_id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `modify_time` datetime NOT NULL,
  `last_modify_by` int(11) NOT NULL,
  `schedule_status` tinyint(4) NOT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `schedule_type` tinyint(4) DEFAULT NULL,
  `crontab_str` varchar(256) DEFAULT NULL,
  `next_submit_time` datetime DEFAULT NULL,
  `dep_workflows` varchar(2048) DEFAULT NULL,
  `dep_policy` tinyint(4) DEFAULT NULL,
  `failure_policy` tinyint(4) NOT NULL,
  `max_try_times` tinyint(4) NOT NULL,
  `notify_type` tinyint(4) NOT NULL,
  `notify_mails` varchar(512) DEFAULT NULL,
  `timeout` int(11) DEFAULT NULL,
  PRIMARY KEY (`flow_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `execution_flows`;
CREATE TABLE `execution_flows` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `flow_id` int(11) NOT NULL,
  `worker` varchar(100) DEFAULT NULL,
  `status` tinyint(4) NOT NULL,
  `submit_user` int(11) NOT NULL,
  `submit_time` datetime NOT NULL,
  `proxy_user` varchar(64) NOT NULL,
  `schedule_time` datetime DEFAULT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `workflow_data` text NOT NULL,
  `type` tinyint(4) NOT NULL,
  `max_try_times` tinyint(4) DEFAULT NULL,
  `timeout` int(4) DEFAULT NULL,
  `error_code` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `execution_nodes`;
CREATE TABLE `execution_nodes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `attempt` tinyint(4) NOT NULL,
  `end_time` datetime DEFAULT NULL,
  `exec_id` bigint(20) NOT NULL,
  `flow_id` int(11) NOT NULL,
  `job_id` varchar(64) NOT NULL,
  `node_id` int(11) NOT NULL,
  `start_time` datetime NOT NULL,
  `status` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;