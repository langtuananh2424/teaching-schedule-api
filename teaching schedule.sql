create database teaching_schedule_db
CREATE TABLE `Assignments` (
	`assignment_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`subject_id` INTEGER NOT NULL,
	`class_id` INTEGER NOT NULL,
	`lecturer_id` INTEGER NOT NULL,
	`theory_sessions` INTEGER NOT NULL,
	`practice_sessions` INTEGER NOT NULL,
	PRIMARY KEY(`assignment_id`)
);


CREATE TABLE `Students` (
	`student_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`student_code` VARCHAR(20) NOT NULL UNIQUE,
	`full_name` VARCHAR(100) NOT NULL,
	`class_id` INTEGER NOT NULL,
	PRIMARY KEY(`student_id`)
);


CREATE TABLE `Departments` (
	`department_id` INTEGER NOT NULL UNIQUE,
	`department_name` VARCHAR(100) NOT NULL,
	PRIMARY KEY(`department_id`)
);


CREATE TABLE `Lecturers` (
	`lecturer_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`lecturer_code` VARCHAR(20) NOT NULL UNIQUE,
	`full_name` VARCHAR(100) NOT NULL,
	`email` VARCHAR(100) NOT NULL UNIQUE,
	`password` VARCHAR(255) NOT NULL,
	`department_id` INTEGER NOT NULL,
	`role` ENUM('lecturer', 'admin') NOT NULL,
	PRIMARY KEY(`lecturer_id`)
);


CREATE INDEX `Lecturers_index_0`
ON `Lecturers` (`department_id`);
CREATE TABLE `Subjects` (
	`subject_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`subject_code` VARCHAR(20) NOT NULL UNIQUE,
	`subject_name` VARCHAR(100) NOT NULL,
	`department_id` INTEGER NOT NULL,
	`credits` INTEGER NOT NULL,
	PRIMARY KEY(`subject_id`)
);


CREATE TABLE `Classes` (
	`class_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`class_code` VARCHAR(20) NOT NULL,
	`class_name` VARCHAR(100) NOT NULL,
	`semester` VARCHAR(20),
	PRIMARY KEY(`class_id`)
);


CREATE TABLE `Schedules` (
	`session_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`assignment_id` INTEGER NOT NULL,
	`session_date` DATE NOT NULL,
	`lesson_order` INTEGER NOT NULL,
	`start_period` INTEGER NOT NULL,
	`end_period` INTEGER NOT NULL,
	`classroom` VARCHAR(20) NOT NULL,
	`content` TEXT(65535),
	`status` ENUM('NOT_TAUGHT', 'TAUGHT', 'ABSENT_APPROVED', 'ABSENT_UNAPPROVED', 'MAKEUP_TAUGHT') NOT NULL,
	`notes` TEXT(65535),
	PRIMARY KEY(`session_id`)
);


CREATE TABLE `Attendance` (
	`attendance_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`session_id` INTEGER NOT NULL,
	`student_id` INTEGER NOT NULL,
	`is_present` BOOLEAN NOT NULL,
	`timestamp` TIMESTAMP,
	PRIMARY KEY(`attendance_id`)
);


CREATE TABLE `AbsenceRequests` (
	`request_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`session_id` INTEGER NOT NULL UNIQUE,
	`reason` TEXT(65535) NOT NULL,
	`approval_status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL,
	`created_at` DATETIME NOT NULL,
	`approver_id` INTEGER NOT NULL,
	`lecturer_id` INTEGER NOT NULL,
	PRIMARY KEY(`request_id`)
);


CREATE TABLE `MakeupSessions` (
	`makeup_session_id` INTEGER NOT NULL AUTO_INCREMENT UNIQUE,
	`absent_session_id` INTEGER NOT NULL UNIQUE,
	`makeup_date` DATE NOT NULL,
	`makeup_start_period` INTEGER NOT NULL,
	`makeup_end_period` INTEGER NOT NULL,
	`makeup_classroom` VARCHAR(20) NOT NULL,
	`approval_status` ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL,
	`approver_id` INTEGER NOT NULL,
	PRIMARY KEY(`makeup_session_id`)
);


ALTER TABLE `Lecturers`
ADD FOREIGN KEY(`department_id`) REFERENCES `Departments`(`department_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Schedules`
ADD FOREIGN KEY(`assignment_id`) REFERENCES `Assignments`(`assignment_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `MakeupSessions`
ADD FOREIGN KEY(`absent_session_id`) REFERENCES `Schedules`(`session_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Subjects`
ADD FOREIGN KEY(`department_id`) REFERENCES `Departments`(`department_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Assignments`
ADD FOREIGN KEY(`lecturer_id`) REFERENCES `Lecturers`(`lecturer_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Assignments`
ADD FOREIGN KEY(`subject_id`) REFERENCES `Subjects`(`subject_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Students`
ADD FOREIGN KEY(`class_id`) REFERENCES `Classes`(`class_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Assignments`
ADD FOREIGN KEY(`class_id`) REFERENCES `Classes`(`class_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Attendance`
ADD FOREIGN KEY(`session_id`) REFERENCES `Schedules`(`session_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `Attendance`
ADD FOREIGN KEY(`student_id`) REFERENCES `Students`(`student_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `MakeupSessions`
ADD FOREIGN KEY(`approver_id`) REFERENCES `Lecturers`(`lecturer_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `AbsenceRequests`
ADD FOREIGN KEY(`lecturer_id`) REFERENCES `Lecturers`(`lecturer_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `AbsenceRequests`
ADD FOREIGN KEY(`session_id`) REFERENCES `Schedules`(`session_id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;