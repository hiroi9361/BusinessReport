drop table IF EXiSTS `task_log`;
drop table IF EXiSTS `report`;
drop table IF EXiSTS `user`;

CREATE TABLE IF NOT EXISTS `user` (
    `employee_code` int NOT NULL PRIMARY KEY,
    `name` varchar(255) NOT NULL,
    `password` char(64) NOT NULL,
    `role` varchar(255) NOT NULL,
    `icon` mediumblob
);

CREATE TABLE IF NOT EXISTS `report` (
    `id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_code` int NOT NULL,
    `condition` varchar(10) NOT NULL,
    `impressions` text NOT NULL,
    `tomorrow_schedule` text NOT NULL,
    `date` date NOT NULL,
    `start_time` time NOT NULL,
    `end_time` time NOT NULL,
    `is_lateness` tinyint NOT NULL,
    `lateness_reason` text,
    `is_left_early` tinyint NOT NULL,
    FOREIGN KEY (`employee_code`) REFERENCES user(`employee_code`)
);

CREATE TABLE IF NOT EXISTS `task_log` (
    `report_id` int NOT NULL,
    `name` varchar(255) NOT NULL,
    `progress_rate` tinyint NOT NULL,
    FOREIGN KEY (`report_id`) REFERENCES report(`id`)
);

