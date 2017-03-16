DROP TABLE If Exists `user`;
CREATE TABLE `user` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'user id',
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

DROP TABLE If Exists `project_user`;
CREATE TABLE `project_user` (
  `project_id` int(20) NOT NULL COMMENT 'project id',
  `user_id` int(20) NOT NULL COMMENT 'user id',
  `perm` int(11) NOT NULL COMMENT 'permission, w/r/x, 0x04-w, 0x02-r, 0x01-x',
  `create_time` datetime NOT NULL COMMENT 'create time',
  `modify_time` datetime NOT NULL COMMENT 'last modify time',
  UNIQUE KEY `project_user` (`project_id`, `user_id`),
  FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE If Exists `session`;
CREATE TABLE `session` (
  `id` varchar(64) NOT NULL COMMENT 'session id',
  `user_id` int(20) NOT NULL COMMENT 'user id',
  `ip` varchar(32) NOT NULL COMMENT 'ip address of login on',
  `last_login_time` datetime NOT NULL COMMENT 'last login time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_ip` (`user_id`, `ip`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

