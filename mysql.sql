CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `user_type` tinyint DEFAULT 0,
    `name` varchar(255) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `profile` text,
    `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `service_request` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` bigint(20) NOT NULL,
    `region_id` bigint(20) DEFAULT NULL,
    `service_type` varchar(255) DEFAULT NULL,
    `title` varchar(255) NOT NULL,
    `description` text,
    `image_files` varchar(1024) DEFAULT NULL,
    `video_file` varchar(255) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status` int(11) DEFAULT '0',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `service_response` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `request_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `description` text,
    `image_files` varchar(1024) DEFAULT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `status` int(11) DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `region` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) DEFAULT NULL,
    `city` varchar(255) DEFAULT NULL,
    `province` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `successful_response` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `request_id` bigint(20) NOT NULL,
    `request_user_id` bigint(20) NOT NULL,
    `response_id` bigint(20) NOT NULL,
    `response_user_id` bigint(20) NOT NULL,
    `accepted_date` date DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;