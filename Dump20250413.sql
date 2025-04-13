CREATE DATABASE  IF NOT EXISTS `backupeduplay` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `backupeduplay`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: backupeduplay
-- ------------------------------------------------------
-- Server version	8.0.36

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image` text,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'	https://cdn-media.sforum.vn/storage/app/media/giakhanh/h%C3%ACnh%20n%E1%BB%81n%20powerpoint%20l%E1%BB%8Bch%20s%E1%BB%AD/hinh-nen-powerpoint-lich-su-thumbnail.jpg','Lịch Sử'),(2,'https://png.pngtree.com/png-clipart/20230814/original/pngtree-students-and-geography-subject-student-subject-child-vector-picture-image_10633069.png','Địa Lý'),(3,'https://images.saymedia-content.com/.image/ar_16:9%2Cc_fill%2Ccs_srgb%2Cfl_progressive%2Cq_auto:eco%2Cw_1200/MTc2MjQ0MDg0NDYwMjk5Njg5/culture-observation.jpg','Văn Hóa'),(4,'https://kenh14cdn.com/2016/photo-2-1477197771866.jpg','Ẩm Thực');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversation_members`
--

DROP TABLE IF EXISTS `conversation_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversation_members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKnxfbup81m9td8l03se3rg2icf` (`conversation_id`),
  KEY `FK55hljfeb9knxg2sfm1gh398qv` (`user_id`),
  CONSTRAINT `FK55hljfeb9knxg2sfm1gh398qv` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKnxfbup81m9td8l03se3rg2icf` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation_members`
--

LOCK TABLES `conversation_members` WRITE;
/*!40000 ALTER TABLE `conversation_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `conversation_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `conversations`
--

DROP TABLE IF EXISTS `conversations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `is_group` bit(1) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversations`
--

LOCK TABLES `conversations` WRITE;
/*!40000 ALTER TABLE `conversations` DISABLE KEYS */;
INSERT INTO `conversations` VALUES (1,NULL,_binary '\0','Conversation between Duy and Duong'),(2,NULL,_binary '','LamTac'),(3,NULL,_binary '\0','Conversation user3 vs Duy');
/*!40000 ALTER TABLE `conversations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friendships`
--

DROP TABLE IF EXISTS `friendships`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friendships` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` varchar(255) DEFAULT NULL,
  `receiver_id` bigint DEFAULT NULL,
  `sender_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_receiver_id` (`receiver_id`),
  KEY `FK_sender_id` (`sender_id`),
  CONSTRAINT `FK_receiver_id` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friendships`
--

LOCK TABLES `friendships` WRITE;
/*!40000 ALTER TABLE `friendships` DISABLE KEYS */;
INSERT INTO `friendships` VALUES (1,'Accepted',5,6);
/*!40000 ALTER TABLE `friendships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_rooms`
--

DROP TABLE IF EXISTS `game_rooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_rooms` (
  `room_id` bigint NOT NULL AUTO_INCREMENT,
  `game_started` bit(1) NOT NULL,
  `max_players` int NOT NULL,
  `category_id` bigint NOT NULL,
  `host_id` bigint NOT NULL,
  PRIMARY KEY (`room_id`),
  KEY `FKd5ce1kdf4ke3u2wr8ou2w24ja` (`category_id`),
  KEY `FKc9ij1wogkt53okbaxie6qoxjg` (`host_id`),
  CONSTRAINT `FKc9ij1wogkt53okbaxie6qoxjg` FOREIGN KEY (`host_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKd5ce1kdf4ke3u2wr8ou2w24ja` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=168 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_rooms`
--

LOCK TABLES `game_rooms` WRITE;
/*!40000 ALTER TABLE `game_rooms` DISABLE KEYS */;
INSERT INTO `game_rooms` VALUES (3,_binary '\0',4,1,5),(4,_binary '\0',4,3,5),(5,_binary '\0',4,3,5),(6,_binary '\0',4,3,5),(7,_binary '\0',4,3,5),(8,_binary '\0',4,4,5),(9,_binary '\0',4,4,5),(10,_binary '\0',4,2,5),(11,_binary '\0',4,4,5),(12,_binary '\0',4,4,5),(13,_binary '\0',4,4,5),(14,_binary '\0',4,3,5),(15,_binary '\0',4,3,5),(16,_binary '\0',4,3,5),(17,_binary '\0',4,4,5),(18,_binary '\0',4,4,5),(19,_binary '\0',4,4,5),(20,_binary '\0',4,3,5),(21,_binary '\0',4,4,5),(22,_binary '\0',4,4,5),(23,_binary '\0',4,4,5),(24,_binary '\0',4,4,5),(25,_binary '\0',4,4,5),(26,_binary '\0',4,4,5),(27,_binary '\0',4,4,5),(28,_binary '\0',4,4,5),(29,_binary '\0',4,4,5),(30,_binary '\0',4,4,5),(31,_binary '\0',4,4,5),(32,_binary '\0',4,4,5),(33,_binary '\0',4,4,5),(34,_binary '\0',4,4,5),(35,_binary '\0',4,4,5),(36,_binary '\0',4,4,5),(37,_binary '\0',4,4,5),(38,_binary '\0',4,3,5),(39,_binary '\0',4,4,5),(40,_binary '\0',4,4,5),(41,_binary '\0',4,4,5),(42,_binary '\0',4,4,5),(43,_binary '\0',4,3,5),(44,_binary '\0',4,3,6),(45,_binary '\0',4,3,5),(46,_binary '\0',4,3,5),(47,_binary '\0',4,4,5),(48,_binary '\0',4,4,5),(49,_binary '\0',4,3,5),(50,_binary '\0',4,4,5),(51,_binary '\0',4,3,5),(52,_binary '\0',4,3,5),(53,_binary '\0',4,3,5),(55,_binary '\0',4,4,5),(56,_binary '\0',4,4,5),(57,_binary '\0',4,4,5),(58,_binary '\0',4,4,5),(59,_binary '\0',4,4,5),(60,_binary '\0',4,4,5),(61,_binary '\0',4,4,5),(62,_binary '\0',4,3,5),(63,_binary '\0',4,4,5),(64,_binary '\0',4,4,5),(65,_binary '\0',4,4,5),(66,_binary '\0',4,4,5),(67,_binary '\0',4,4,5),(68,_binary '\0',4,4,5),(69,_binary '\0',4,4,5),(70,_binary '\0',4,4,5),(71,_binary '\0',4,4,5),(72,_binary '\0',4,4,5),(73,_binary '\0',4,4,5),(74,_binary '\0',4,4,5),(75,_binary '\0',4,4,5),(76,_binary '\0',4,4,5),(77,_binary '\0',4,4,5),(78,_binary '\0',4,4,5),(79,_binary '\0',4,4,5),(80,_binary '\0',4,4,5),(81,_binary '\0',4,4,5),(82,_binary '\0',4,4,5),(83,_binary '\0',4,4,5),(84,_binary '\0',4,4,5),(85,_binary '\0',4,4,5),(86,_binary '\0',4,3,5),(87,_binary '\0',4,4,5),(88,_binary '\0',4,4,5),(89,_binary '\0',4,4,5),(90,_binary '\0',4,4,5),(91,_binary '\0',4,4,5),(92,_binary '\0',4,4,5),(93,_binary '\0',4,3,5),(94,_binary '\0',4,4,5),(95,_binary '\0',4,4,5),(96,_binary '\0',4,3,5),(97,_binary '\0',4,4,5),(98,_binary '\0',4,3,5),(99,_binary '\0',4,4,5),(100,_binary '\0',4,4,5),(101,_binary '\0',4,4,5),(102,_binary '\0',4,4,5),(103,_binary '\0',4,3,5),(104,_binary '\0',4,4,5),(105,_binary '\0',4,4,5),(106,_binary '\0',4,4,5),(107,_binary '\0',4,4,5),(108,_binary '\0',4,4,5),(109,_binary '\0',4,4,5),(110,_binary '\0',4,4,5),(111,_binary '\0',4,4,5),(112,_binary '\0',4,3,5),(113,_binary '\0',4,4,5),(114,_binary '\0',4,4,5),(115,_binary '\0',4,4,5),(116,_binary '\0',4,3,5),(117,_binary '\0',4,4,5),(118,_binary '\0',4,4,5),(119,_binary '\0',4,4,5),(120,_binary '\0',4,4,5),(121,_binary '\0',4,4,5),(122,_binary '\0',4,4,5),(123,_binary '\0',4,4,5),(124,_binary '\0',4,4,5),(125,_binary '\0',4,3,5),(126,_binary '\0',4,3,5),(127,_binary '\0',4,3,5),(128,_binary '\0',4,4,5),(129,_binary '\0',4,4,6),(130,_binary '\0',4,4,5),(131,_binary '\0',4,4,5),(132,_binary '\0',4,4,6),(133,_binary '\0',4,4,5),(134,_binary '\0',4,3,6),(135,_binary '\0',4,4,6),(136,_binary '\0',4,4,5),(137,_binary '\0',4,4,5),(138,_binary '\0',4,4,5),(139,_binary '\0',4,4,6),(140,_binary '\0',4,2,6),(141,_binary '\0',4,4,6),(142,_binary '\0',4,4,6),(143,_binary '\0',4,3,5),(144,_binary '\0',4,4,5),(145,_binary '\0',4,4,5),(146,_binary '\0',4,3,5),(147,_binary '\0',4,3,5),(148,_binary '\0',4,4,5),(149,_binary '\0',4,4,5),(150,_binary '\0',4,4,5),(151,_binary '\0',4,4,5),(152,_binary '\0',4,4,5),(153,_binary '\0',4,4,5),(154,_binary '\0',4,4,6),(155,_binary '\0',4,4,5),(156,_binary '\0',4,4,6),(157,_binary '',4,4,5),(158,_binary '',4,4,5),(159,_binary '',4,4,5),(160,_binary '',4,4,6),(161,_binary '',4,4,5),(162,_binary '',4,4,5),(163,_binary '',4,2,6),(164,_binary '',4,2,5),(165,_binary '\0',4,4,5),(166,_binary '\0',4,4,5),(167,_binary '',4,4,5);
/*!40000 ALTER TABLE `game_rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `message` text NOT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `conversation_id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt492th6wsovh1nush5yl5jj8e` (`conversation_id`),
  KEY `FKip9clvpi646rirksmm433wykx` (`sender_id`),
  CONSTRAINT `FKip9clvpi646rirksmm433wykx` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKt492th6wsovh1nush5yl5jj8e` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questions`
--

DROP TABLE IF EXISTS `questions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `correct_answer` varchar(255) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `optiona` varchar(255) NOT NULL,
  `optionb` varchar(255) NOT NULL,
  `optionc` varchar(255) NOT NULL,
  `optiond` varchar(255) NOT NULL,
  `question` varchar(255) NOT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKctl6tuf74n8cufkb3ulj6b3fc` (`category_id`),
  CONSTRAINT `FKctl6tuf74n8cufkb3ulj6b3fc` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questions`
--

LOCK TABLES `questions` WRITE;
/*!40000 ALTER TABLE `questions` DISABLE KEYS */;
INSERT INTO `questions` VALUES (1,'Hùng Vương',NULL,'Lý Thái Tổ','Hùng Vương','Trần Nhân Tông','Quang Trung','Ai là vị vua đầu tiên của nước Việt Nam?',1),(2,'1954',NULL,'1945','1954','1975','1968','Trận Điện Biên Phủ diễn ra vào năm nào?',1),(3,'Quảng Ninh',NULL,'Hải Phòng','Quảng Ninh','Thanh Hóa','Nam Định','Vịnh Hạ Long thuộc tỉnh nào của Việt Nam?',2),(4,'Phanxipăng',NULL,'Phanxipăng','Bạch Mã','Ba Vì','Langbiang','Đỉnh núi cao nhất Việt Nam tên là gì?',2),(5,'Tết Nguyên Đán',NULL,'Lễ hội Chùa Hương','Lễ hội Gióng','Tết Nguyên Đán','Lễ hội Lim','Lễ hội nào lớn nhất Việt Nam?',3),(6,'Nón lá',NULL,'Nón cối','Nón lá','Mũ beret','Mũ bảo hiểm','Áo dài truyền thống của Việt Nam thường đi kèm với loại nón nào?',3),(7,'Hà Nội',NULL,'Huế','Hà Nội','Đà Nẵng','Hải Phòng','Phở là món ăn nổi tiếng của tỉnh nào?',4),(8,'Pháp',NULL,'Pháp','Trung Quốc','Hàn Quốc','Nhật Bản','Bánh mì Việt Nam có nguồn gốc từ nền ẩm thực nào?',4),(9,'Sông Mê Kông',NULL,'Sông Hồng','Sông Mê Kông','Sông Đồng Nai','Sông Cửu Long','Sông nào dài nhất Việt Nam?',2),(10,'Đà Lạt',NULL,'Hà Nội','Đà Lạt','Huế','Đà Nẵng','Thành phố nào được mệnh danh là \"thành phố ngàn hoa\"?',2),(11,'Cao nguyên Tây Nguyên',NULL,'Cao nguyên Mộc Châu','Cao nguyên Pleiku','Cao nguyên Lâm Viên','Cao nguyên Tây Nguyên','Cao nguyên nào lớn nhất Việt Nam?',2),(12,'Khánh Hòa',NULL,'Khánh Hòa','Bình Định','Quảng Nam','Nghệ An','Quần đảo Trường Sa thuộc tỉnh nào của Việt Nam?',2),(13,'Cà Mau',NULL,'Cà Mau','Bạc Liêu','Sóc Trăng','Kiên Giang','Cực Nam của Việt Nam thuộc tỉnh nào?',2);
/*!40000 ALTER TABLE `questions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `is_ready` bit(1) NOT NULL,
  `otp` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `status` bit(1) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `game_room_id` bigint DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`),
  KEY `FKf9bam05ql9x99jjm6kdq5t2y8` (`game_room_id`),
  CONSTRAINT `FKf9bam05ql9x99jjm6kdq5t2y8` FOREIGN KEY (`game_room_id`) REFERENCES `game_rooms` (`room_id`),
  CONSTRAINT `user_chk_1` CHECK ((`status` between 0 and 1))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (5,'2025-04-04 14:04:59.000000','user1@example.com',_binary '',_binary '\0','123456','123',_binary '','duy',NULL,NULL),(6,'2025-04-04 14:04:59.000000','user2@example.com',_binary '',_binary '','654321','123',_binary '\0','duong',NULL,NULL),(7,'2025-04-04 14:04:59.000000','user3@example.com',_binary '\0',_binary '\0',NULL,'123',_binary '','user3',NULL,NULL),(8,'2025-04-04 14:04:59.000000','user4@example.com',_binary '',_binary '','987654','123',_binary '\0','user4',NULL,NULL);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-13 11:20:26
