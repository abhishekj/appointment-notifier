/*
SQLyog Community v9.51 
MySQL - 5.5.16-log : Database - appointment_project
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*Table structure for table `doctors` */

DROP TABLE IF EXISTS `doctors`;

CREATE TABLE `doctors` (
  `d_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `d_number` varchar(100) DEFAULT NULL,
  `d_calendar_id` varchar(255) NOT NULL,
  `d_refresh_token` text,
  `d_message` text,
  `d_enabled` int(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`d_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

/*Data for the table `doctors` */

insert  into `doctors`(`d_id`,`d_number`,`d_calendar_id`,`d_refresh_token`,`d_message`,`d_enabled`) values (1,'','xyz@ejain.com','','Dear __patient__, You have an appointment with Dr. Jain , at __time__.',1);

/*Table structure for table `sms_request` */

DROP TABLE IF EXISTS `sms_request`;

CREATE TABLE `sms_request` (
  `sr_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `sr_to` varchar(200) DEFAULT NULL,
  `sr_doctor_id` int(11) DEFAULT NULL,
  `sr_message` text,
  `sr_send_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sr_status` varchar(100) DEFAULT 'sent',
  `sr_enabled` int(1) NOT NULL DEFAULT '1',
  `sr_response` text,
  PRIMARY KEY (`sr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `sms_request` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
