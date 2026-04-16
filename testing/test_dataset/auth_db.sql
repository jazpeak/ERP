-- Backup for database auth_db
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `users_auth`;
CREATE TABLE `users_auth` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `role` enum('Admin','Instructor','Student') DEFAULT NULL,
  `password_hash` varchar(100) DEFAULT NULL,
  `status` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users_auth` VALUES ('1','admin1','Admin','$2a$10$7ZaQfqiY259mv7orHPfqQemkxMBU.eEHV4irj56oJZa2.TOWCQ/RC','active');
INSERT INTO `users_auth` VALUES ('2','sambuddho','Instructor','$2a$10$/CvxT7GmyT03i/HUeoBVNuNamTBKoV/2FMJ3luyFuZfSuYhNFFwtG','active');
INSERT INTO `users_auth` VALUES ('3','subhajit','Instructor','$2a$10$xJz8bCsSn0HssjMuCAf6guUIszG8XyqB56H5CBZF2z19f8uA5Xjny','active');
INSERT INTO `users_auth` VALUES ('4','pravesh','Instructor','$2a$10$7MjCAuP410sB1Em9M47kQeiMF544T9.1aR/asFTALp6ag4N2pJlr.','active');
INSERT INTO `users_auth` VALUES ('5','paro','Instructor','$2a$10$3qejzF89YiUobxklLZbz/uNpLnBAl39BInYSe21rEg2hgr5CzBZZe','active');
INSERT INTO `users_auth` VALUES ('6','sonal','Instructor','$2a$10$iZ8SZW8FmzddOifzqoob/eoqdwDQJS74bGBciIX2k.ZUNBt1nzacy','active');
INSERT INTO `users_auth` VALUES ('7','amit','Student','$2a$10$EGAOk4LE71pFgwvSJvf2IuXmbvar0kJbMh.MU.BghFjc9sB6wUVQe','active');
INSERT INTO `users_auth` VALUES ('8','bhavya','Student','$2a$10$AvtjEXNBHOzOs5do/1rkSeSwzZf.mluqnAzIM9r9sssyDDxTFQmjO','active');
INSERT INTO `users_auth` VALUES ('9','celine','Student','$2a$10$HnGebapzIR6icwDHOyFg7uauYFlGtlu4DnPcYKp.ltgrQUn8w669G','active');
INSERT INTO `users_auth` VALUES ('10','devyaansh','Student','$2a$10$1VpuB9rBEjvLPASZjUBKkePrsanTfzxqrhvIOOfjA4zaphYwVqH0e','active');
INSERT INTO `users_auth` VALUES ('11','esha','Student','$2a$10$UuoFU0De.1FqXYm6hfrog.ZC5FmHSpv/bYCO8TgGgDcXEnVfybt46','active');
INSERT INTO `users_auth` VALUES ('12','inst1','Instructor','$2a$10$lc2KKKwGl7KIPeD.AAsc2.27DjeS9KWYoaOEhl3s0IegmHkmj16y.','active');
INSERT INTO `users_auth` VALUES ('13','stu1','Student','$2a$10$ZjLMU2pYHFvVHaOU.d4osu1JOvBqq8HKJk5iGjD1mO9mxVrvKKitq','active');
INSERT INTO `users_auth` VALUES ('14','stu2','Student','$2a$10$jJvP0pX0TA0DBAWzk/ilUuI6o5haWaUw1Xi/N7CaUw.61m3t3XABe','active');
SET FOREIGN_KEY_CHECKS=1;
