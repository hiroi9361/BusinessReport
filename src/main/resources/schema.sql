SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `task_log` CASCADE;
DROP TABLE IF EXISTS `report` CASCADE;
DROP TABLE IF EXISTS `user` CASCADE;
DROP TABLE IF EXISTS `team` CASCADE;
DROP TABLE IF EXISTS `feedback` CASCADE;
DROP TABLE IF EXISTS `assignment` CASCADE;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS `user` (
    `employee_code` int NOT NULL PRIMARY KEY,
    `name` varchar(50) NOT NULL,
    `password` char(64) NOT NULL,
    `role` varchar(255) NOT NULL,
    `icon` mediumblob
);

CREATE TABLE IF NOT EXISTS `report` (
    `report_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `employee_code` int NOT NULL,
    `condition_rate` int NOT NULL,
    `condition` varchar(50) NOT NULL,
    `impressions` text NOT NULL,
    `tomorrow_schedule` text NOT NULL,
    `date` date NOT NULL,
    `start_time` time NOT NULL,
    `end_time` time NOT NULL,
    `is_lateness` tinyint NOT NULL,
    `lateness_reason` text,
    `is_left_early` tinyint NOT NULL
--    FOREIGN KEY (`employee_code`) REFERENCES user(`employee_code`)
);

CREATE TABLE IF NOT EXISTS `task_log` (
    `task_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `report_id` int NOT NULL,
    `name` varchar(100) NOT NULL,
    `progress_rate` tinyint DEFAULT 0,
    FOREIGN KEY (`report_id`) REFERENCES report(`report_id`)
);

CREATE TABLE IF NOT EXISTS `team` (
    `team_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS `feedback` (
    `feedback_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` varchar(50) NOT NULL,
    `rating` int NOT NULL,
    `comment` text NOT NULL,
    `report_id` int NOT NULL
);

CREATE TABLE IF NOT EXISTS `assignment` (
    `assignment_id` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `is_manager` tinyint NOT NULL,
    `team_id` int NOT NULL,
    `employee_code` int NOT NULL
);

alter table assignment add constraint FKdetrh6pu9ojx5htmct8jirhof foreign key (team_id) references team (team_id);
alter table assignment add constraint FKrot3v731ri6t8i0aycum0gw5p foreign key (employee_code) references user (employee_code);
alter table feedback add constraint FK1vm3ocsdcjgqi526qcvwbqin4 foreign key (report_id) references report (report_id);
alter table report add constraint FKob2bc600lvaudiydtnssb0c17 foreign key (employee_code) references user (employee_code);