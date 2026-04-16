-- Backup for database erp_db
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
  `code` varchar(10) NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `credits` int DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `courses` VALUES ('COM101','Communication Skills','4');
INSERT INTO `courses` VALUES ('CSE101','Introduction to Programming','4');
INSERT INTO `courses` VALUES ('DES102','Introduction to HCI','4');
INSERT INTO `courses` VALUES ('ECE111','Digital Circuits','4');
INSERT INTO `courses` VALUES ('MTH100','Maths I','4');

DROP TABLE IF EXISTS `enrollments`;
CREATE TABLE `enrollments` (
  `enroll_id` int NOT NULL AUTO_INCREMENT,
  `student_id` int DEFAULT NULL,
  `section_id` int DEFAULT NULL,
  `status` varchar(20) DEFAULT 'enrolled',
  PRIMARY KEY (`enroll_id`),
  UNIQUE KEY `student_id` (`student_id`,`section_id`),
  KEY `section_id` (`section_id`),
  CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `enrollments` VALUES ('1','7','2','enrolled');
INSERT INTO `enrollments` VALUES ('2','7','3','enrolled');
INSERT INTO `enrollments` VALUES ('3','8','3','enrolled');
INSERT INTO `enrollments` VALUES ('4','8','5','enrolled');
INSERT INTO `enrollments` VALUES ('5','9','6','enrolled');
INSERT INTO `enrollments` VALUES ('6','9','4','enrolled');
INSERT INTO `enrollments` VALUES ('7','10','5','enrolled');
INSERT INTO `enrollments` VALUES ('8','10','1','enrolled');
INSERT INTO `enrollments` VALUES ('9','11','1','enrolled');
INSERT INTO `enrollments` VALUES ('10','11','6','enrolled');
INSERT INTO `enrollments` VALUES ('11','13','5','enrolled');
INSERT INTO `enrollments` VALUES ('12','13','2','enrolled');
INSERT INTO `enrollments` VALUES ('13','13','4','enrolled');
INSERT INTO `enrollments` VALUES ('14','14','6','enrolled');
INSERT INTO `enrollments` VALUES ('15','14','4','enrolled');
INSERT INTO `enrollments` VALUES ('16','14','1','enrolled');
INSERT INTO `enrollments` VALUES ('18','13','3','enrolled');
INSERT INTO `enrollments` VALUES ('19','7','1','enrolled');
INSERT INTO `enrollments` VALUES ('20','7','5','enrolled');
INSERT INTO `enrollments` VALUES ('21','7','4','enrolled');
INSERT INTO `enrollments` VALUES ('22','8','6','enrolled');
INSERT INTO `enrollments` VALUES ('23','8','4','enrolled');
INSERT INTO `enrollments` VALUES ('24','8','1','enrolled');
INSERT INTO `enrollments` VALUES ('25','9','1','enrolled');
INSERT INTO `enrollments` VALUES ('26','9','5','enrolled');
INSERT INTO `enrollments` VALUES ('27','9','3','enrolled');
INSERT INTO `enrollments` VALUES ('28','10','6','enrolled');
INSERT INTO `enrollments` VALUES ('29','10','4','enrolled');
INSERT INTO `enrollments` VALUES ('30','10','3','enrolled');
INSERT INTO `enrollments` VALUES ('31','11','3','enrolled');
INSERT INTO `enrollments` VALUES ('32','11','4','enrolled');
INSERT INTO `enrollments` VALUES ('33','11','5','enrolled');
INSERT INTO `enrollments` VALUES ('34','14','5','enrolled');
INSERT INTO `enrollments` VALUES ('35','14','3','enrolled');

DROP TABLE IF EXISTS `grades`;
CREATE TABLE `grades` (
  `enroll_Id` int NOT NULL,
  `component` varchar(30) NOT NULL,
  `score` decimal(5,2) DEFAULT NULL,
  `final_grade` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`enroll_Id`,`component`),
  CONSTRAINT `grades_ibfk_1` FOREIGN KEY (`enroll_Id`) REFERENCES `enrollments` (`enroll_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `grades` VALUES ('1','Endsem','75.00','A');
INSERT INTO `grades` VALUES ('1','Midsem','90.00','A');
INSERT INTO `grades` VALUES ('1','Quiz','80.00','A');
INSERT INTO `grades` VALUES ('2','Endsem','55.00','C');
INSERT INTO `grades` VALUES ('2','Midsem','80.00','C');
INSERT INTO `grades` VALUES ('2','Quiz','80.00','C');
INSERT INTO `grades` VALUES ('3','Endsem','95.00','A+');
INSERT INTO `grades` VALUES ('3','Midsem','85.00','A+');
INSERT INTO `grades` VALUES ('3','Quiz','90.00','A+');
INSERT INTO `grades` VALUES ('4','Endsem','40.00','F');
INSERT INTO `grades` VALUES ('4','Midsem','40.00','F');
INSERT INTO `grades` VALUES ('4','Quiz','50.00','F');
INSERT INTO `grades` VALUES ('5','Endsem','60.00','C');
INSERT INTO `grades` VALUES ('5','Midsem','65.00','C');
INSERT INTO `grades` VALUES ('5','Quiz','70.00','C');
INSERT INTO `grades` VALUES ('6','Endsem','70.00','B');
INSERT INTO `grades` VALUES ('6','Midsem','75.00','B');
INSERT INTO `grades` VALUES ('6','Quiz','70.00','B');
INSERT INTO `grades` VALUES ('7','Endsem','55.00','F');
INSERT INTO `grades` VALUES ('7','Midsem','30.00','F');
INSERT INTO `grades` VALUES ('7','Quiz','30.00','F');
INSERT INTO `grades` VALUES ('8','Endsem','85.00','A');
INSERT INTO `grades` VALUES ('8','Midsem','80.00','A');
INSERT INTO `grades` VALUES ('8','Quiz','90.00','A');
INSERT INTO `grades` VALUES ('9','Endsem','90.00','A');
INSERT INTO `grades` VALUES ('9','Midsem','90.00','A');
INSERT INTO `grades` VALUES ('9','Quiz','70.00','A');
INSERT INTO `grades` VALUES ('10','Endsem','55.00','C');
INSERT INTO `grades` VALUES ('10','Midsem','80.00','C');
INSERT INTO `grades` VALUES ('10','Quiz','75.00','C');
INSERT INTO `grades` VALUES ('11','Endsem','80.00','B');
INSERT INTO `grades` VALUES ('11','Midsem','75.00','B');
INSERT INTO `grades` VALUES ('11','Quiz','70.00','B');
INSERT INTO `grades` VALUES ('12','Endsem','85.00','A');
INSERT INTO `grades` VALUES ('12','Midsem','80.00','A');
INSERT INTO `grades` VALUES ('12','Quiz','75.00','A');
INSERT INTO `grades` VALUES ('13','Endsem','65.00','B');
INSERT INTO `grades` VALUES ('13','Midsem','85.00','B');
INSERT INTO `grades` VALUES ('13','Quiz','90.00','B');
INSERT INTO `grades` VALUES ('14','Endsem','80.00','A');
INSERT INTO `grades` VALUES ('14','Midsem','80.00','A');
INSERT INTO `grades` VALUES ('14','Quiz','85.00','A');
INSERT INTO `grades` VALUES ('15','Endsem','70.00','C');
INSERT INTO `grades` VALUES ('15','Midsem','60.00','C');
INSERT INTO `grades` VALUES ('15','Quiz','70.00','C');
INSERT INTO `grades` VALUES ('16','Endsem','85.00','A');
INSERT INTO `grades` VALUES ('16','Midsem','85.00','A');
INSERT INTO `grades` VALUES ('16','Quiz','75.00','A');
INSERT INTO `grades` VALUES ('18','Endsem','65.00','B');
INSERT INTO `grades` VALUES ('18','Midsem','80.00','B');
INSERT INTO `grades` VALUES ('18','Quiz','68.00','B');
INSERT INTO `grades` VALUES ('19','Endsem','69.00','B');
INSERT INTO `grades` VALUES ('19','Midsem','72.00','B');
INSERT INTO `grades` VALUES ('19','Quiz','77.00','B');
INSERT INTO `grades` VALUES ('20','Endsem','60.00','D');
INSERT INTO `grades` VALUES ('20','Midsem','58.00','D');
INSERT INTO `grades` VALUES ('20','Quiz','40.00','D');
INSERT INTO `grades` VALUES ('21','Endsem','65.00','C');
INSERT INTO `grades` VALUES ('21','Midsem','54.00','C');
INSERT INTO `grades` VALUES ('21','Quiz','65.00','C');
INSERT INTO `grades` VALUES ('22','Endsem','70.00','C');
INSERT INTO `grades` VALUES ('22','Midsem','60.00','C');
INSERT INTO `grades` VALUES ('22','Quiz','80.00','C');
INSERT INTO `grades` VALUES ('23','Endsem','81.00','B');
INSERT INTO `grades` VALUES ('23','Midsem','78.00','B');
INSERT INTO `grades` VALUES ('23','Quiz','67.00','B');
INSERT INTO `grades` VALUES ('24','Endsem','74.00','B');
INSERT INTO `grades` VALUES ('24','Midsem','66.00','B');
INSERT INTO `grades` VALUES ('24','Quiz','73.00','B');
INSERT INTO `grades` VALUES ('25','Endsem','70.00','B');
INSERT INTO `grades` VALUES ('25','Midsem','75.00','B');
INSERT INTO `grades` VALUES ('25','Quiz','68.00','B');
INSERT INTO `grades` VALUES ('26','Endsem','65.00','C');
INSERT INTO `grades` VALUES ('26','Midsem','70.00','C');
INSERT INTO `grades` VALUES ('26','Quiz','45.00','C');
INSERT INTO `grades` VALUES ('27','Endsem','90.00','A');
INSERT INTO `grades` VALUES ('27','Midsem','75.00','A');
INSERT INTO `grades` VALUES ('27','Quiz','69.00','A');
INSERT INTO `grades` VALUES ('28','Endsem','75.00','B');
INSERT INTO `grades` VALUES ('28','Midsem','75.00','B');
INSERT INTO `grades` VALUES ('28','Quiz','55.00','B');
INSERT INTO `grades` VALUES ('29','Endsem','68.00','C');
INSERT INTO `grades` VALUES ('29','Midsem','64.00','C');
INSERT INTO `grades` VALUES ('29','Quiz','57.00','C');
INSERT INTO `grades` VALUES ('30','Endsem','88.00','B');
INSERT INTO `grades` VALUES ('30','Midsem','64.00','B');
INSERT INTO `grades` VALUES ('30','Quiz','56.00','B');
INSERT INTO `grades` VALUES ('31','Endsem','60.00','C');
INSERT INTO `grades` VALUES ('31','Midsem','76.00','C');
INSERT INTO `grades` VALUES ('31','Quiz','55.00','C');
INSERT INTO `grades` VALUES ('32','Endsem','60.00','C');
INSERT INTO `grades` VALUES ('32','Midsem','65.00','C');
INSERT INTO `grades` VALUES ('32','Quiz','75.00','C');
INSERT INTO `grades` VALUES ('33','Endsem','33.00','F');
INSERT INTO `grades` VALUES ('33','Midsem','51.00','F');
INSERT INTO `grades` VALUES ('33','Quiz','49.00','F');
INSERT INTO `grades` VALUES ('34','Endsem','42.00','F');
INSERT INTO `grades` VALUES ('34','Midsem','45.00','F');
INSERT INTO `grades` VALUES ('34','Quiz','50.00','F');
INSERT INTO `grades` VALUES ('35','Endsem','85.00','B');
INSERT INTO `grades` VALUES ('35','Midsem','80.00','B');
INSERT INTO `grades` VALUES ('35','Quiz','20.00','B');

DROP TABLE IF EXISTS `instructors`;
CREATE TABLE `instructors` (
  `user_id` int NOT NULL,
  `dept` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `instructors_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `auth_db`.`users_auth` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `instructors` VALUES ('2','CSE');
INSERT INTO `instructors` VALUES ('3','MTH');
INSERT INTO `instructors` VALUES ('4','ECE');
INSERT INTO `instructors` VALUES ('5','SOC');
INSERT INTO `instructors` VALUES ('6','DES');
INSERT INTO `instructors` VALUES ('12','ECE');

DROP TABLE IF EXISTS `section_weights`;
CREATE TABLE `section_weights` (
  `weight_id` int NOT NULL AUTO_INCREMENT,
  `section_id` int NOT NULL,
  `component` varchar(50) NOT NULL,
  `weight` int NOT NULL,
  PRIMARY KEY (`weight_id`),
  UNIQUE KEY `unique_weight` (`section_id`,`component`),
  CONSTRAINT `section_weights_ibfk_1` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `section_weights` VALUES ('1','1','Quiz','30');
INSERT INTO `section_weights` VALUES ('2','1','Midsem','30');
INSERT INTO `section_weights` VALUES ('3','1','Endsem','40');
INSERT INTO `section_weights` VALUES ('4','2','Quiz','20');
INSERT INTO `section_weights` VALUES ('5','2','Midsem','40');
INSERT INTO `section_weights` VALUES ('6','2','Endsem','40');

DROP TABLE IF EXISTS `sections`;
CREATE TABLE `sections` (
  `section_id` int NOT NULL AUTO_INCREMENT,
  `course_code` varchar(10) DEFAULT NULL,
  `inst_id` int DEFAULT NULL,
  `day` varchar(20) DEFAULT NULL,
  `time_slot` varchar(20) DEFAULT NULL,
  `room` varchar(20) DEFAULT NULL,
  `cap` int DEFAULT NULL,
  `sem` varchar(10) DEFAULT NULL,
  `year` int DEFAULT NULL,
  `seats_left` int DEFAULT NULL,
  `reg_deadline` datetime DEFAULT NULL,
  UNIQUE KEY `section_id` (`section_id`),
  KEY `course_code` (`course_code`),
  CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`course_code`) REFERENCES `courses` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `sections` VALUES ('1','COM101','5','Mon','09:00–10:00','A101','30','Winter','2025','24','2025-12-30T00:00');
INSERT INTO `sections` VALUES ('2','CSE101','2','Tue','10:00–11:00','B201','30','Winter','2025','28','2025-12-30T00:00');
INSERT INTO `sections` VALUES ('3','DES102','6','Wed','11:00–12:00','D105','30','Winter','2025','23','2025-12-30T00:00');
INSERT INTO `sections` VALUES ('4','ECE111','4','Thu','12:00–13:00','E303','30','Winter','2025','23','2025-12-30T00:00');
INSERT INTO `sections` VALUES ('5','MTH100','3','Fri','13:00–14:00','C404','30','Winter','2025','23','2025-12-30T00:00');
INSERT INTO `sections` VALUES ('6','CSE101','12','Tue','10:00–11:00','B006','30','Winter','2025','25','2025-12-30T00:00');

DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
  `key` varchar(50) NOT NULL,
  `value` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `settings` VALUES ('maintenance','off');
INSERT INTO `settings` VALUES ('maintenance_mode','OFF');
INSERT INTO `settings` VALUES ('notice','hello there');

DROP TABLE IF EXISTS `students`;
CREATE TABLE `students` (
  `user_id` int NOT NULL,
  `roll_no` varchar(20) DEFAULT NULL,
  `prog` varchar(50) DEFAULT NULL,
  `year` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `roll_no` (`roll_no`),
  CONSTRAINT `students_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `auth_db`.`users_auth` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `students` VALUES ('7','2025001','CSE','2025');
INSERT INTO `students` VALUES ('8','2025002','CSAM','2025');
INSERT INTO `students` VALUES ('9','2025003','CSD','2025');
INSERT INTO `students` VALUES ('10','2025004','CSSS','2025');
INSERT INTO `students` VALUES ('11','2025005','ECE','2025');
INSERT INTO `students` VALUES ('13','2025006','CSAI','2025');
INSERT INTO `students` VALUES ('14','2025007','CSE','2025');
SET FOREIGN_KEY_CHECKS=1;
