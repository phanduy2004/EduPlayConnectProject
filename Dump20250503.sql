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
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `parent_comment_id` bigint DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_comment_parent` (`parent_comment_id`),
  KEY `FK_comment_post` (`post_id`),
  KEY `FK_comment_user` (`user_id`),
  CONSTRAINT `FK_comment_parent` FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`id`),
  CONSTRAINT `FK_comment_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `FK_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,'d','2025-05-01 12:27:51.291000','2025-05-01 12:27:51.291000',NULL,1,5),(2,'h','2025-05-01 12:28:04.016000','2025-05-01 12:28:04.016000',1,1,5),(3,'hello','2025-05-01 12:36:32.498000','2025-05-01 12:36:32.498000',NULL,1,6),(4,'how arre you','2025-05-01 12:36:41.864000','2025-05-01 12:36:41.864000',3,1,6),(5,'s','2025-05-01 12:42:40.399000','2025-05-01 12:42:40.399000',NULL,1,5),(6,'Heei, duy','2025-05-01 12:59:29.273000','2025-05-01 12:59:29.273000',5,1,6),(7,'haha','2025-05-01 12:59:40.702000','2025-05-01 12:59:40.702000',5,1,5),(8,'d','2025-05-01 17:52:45.705000','2025-05-01 17:52:45.705000',NULL,7,5),(9,'ddd','2025-05-01 17:53:08.929000','2025-05-01 17:53:08.929000',NULL,8,5),(10,'xin chao','2025-05-01 19:48:58.198000','2025-05-01 19:48:58.198000',NULL,11,5),(11,'taoday','2025-05-01 19:49:14.503000','2025-05-01 19:49:14.503000',10,11,5),(12,'ds','2025-05-01 19:54:43.112000','2025-05-01 19:54:43.112000',NULL,11,5),(13,'đuy','2025-05-01 19:58:03.949000','2025-05-01 19:58:03.949000',12,11,5),(14,'nn','2025-05-01 19:58:15.830000','2025-05-01 19:58:15.830000',NULL,11,5),(15,'hehee','2025-05-01 20:00:10.894000','2025-05-01 20:00:10.894000',14,11,6),(16,'hehee','2025-05-01 20:10:39.753000','2025-05-01 20:10:39.753000',NULL,11,5),(17,'d','2025-05-02 04:35:24.839000','2025-05-02 04:35:24.839000',NULL,8,5),(18,'s','2025-05-02 04:35:49.535000','2025-05-02 04:35:49.535000',NULL,13,5);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversation_members`
--

LOCK TABLES `conversation_members` WRITE;
/*!40000 ALTER TABLE `conversation_members` DISABLE KEYS */;
INSERT INTO `conversation_members` VALUES (1,2,5),(2,2,6),(3,5,6),(4,5,5),(5,5,7),(6,6,6),(7,6,5),(8,6,7),(9,7,5),(10,7,7),(11,7,8),(12,7,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `conversations`
--

LOCK TABLES `conversations` WRITE;
/*!40000 ALTER TABLE `conversations` DISABLE KEYS */;
INSERT INTO `conversations` VALUES (1,'2025-04-14 11:42:46.264000',_binary '\0',NULL),(2,'2025-04-14 12:07:44.059000',_binary '\0',NULL),(3,'2025-04-21 03:34:15.330000',_binary '\0','Nhom1'),(4,'2025-04-21 03:36:14.296000',_binary '\0','nhom 1'),(5,'2025-04-21 03:40:58.086000',_binary '','hello'),(6,'2025-04-21 03:43:45.154000',_binary '','nhom2'),(7,'2025-04-22 06:07:23.013000',_binary '','Hellochipboi');
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friendships`
--

LOCK TABLES `friendships` WRITE;
/*!40000 ALTER TABLE `friendships` DISABLE KEYS */;
INSERT INTO `friendships` VALUES (10,'Accepted',5,6);
/*!40000 ALTER TABLE `friendships` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `game_room_players`
--

DROP TABLE IF EXISTS `game_room_players`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `game_room_players` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_ready` bit(1) DEFAULT NULL,
  `score` bigint DEFAULT NULL,
  `game_room_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKf5eg4r0hfyuhqhyub87rrmr0c` (`game_room_id`),
  KEY `FK51c96ybdr8ul1rbcdc2w3dw74` (`user_id`),
  CONSTRAINT `FK51c96ybdr8ul1rbcdc2w3dw74` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKf5eg4r0hfyuhqhyub87rrmr0c` FOREIGN KEY (`game_room_id`) REFERENCES `game_rooms` (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_room_players`
--

LOCK TABLES `game_room_players` WRITE;
/*!40000 ALTER TABLE `game_room_players` DISABLE KEYS */;
INSERT INTO `game_room_players` VALUES (7,_binary '',0,171,5),(8,_binary '',0,171,6),(13,_binary '',0,174,6),(14,_binary '',0,174,5),(15,_binary '',0,175,5),(16,_binary '',0,175,6),(20,_binary '\0',0,176,6),(21,_binary '',2,177,6),(22,_binary '',0,177,5),(24,_binary '',1,179,5),(25,_binary '',0,179,6),(26,_binary '',2,180,5),(27,_binary '',0,180,6),(28,_binary '',2,181,5),(29,_binary '',0,181,6),(30,_binary '',2,182,5),(31,_binary '',0,182,6);
/*!40000 ALTER TABLE `game_room_players` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=183 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `game_rooms`
--

LOCK TABLES `game_rooms` WRITE;
/*!40000 ALTER TABLE `game_rooms` DISABLE KEYS */;
INSERT INTO `game_rooms` VALUES (3,_binary '\0',4,1,5),(4,_binary '\0',4,3,5),(5,_binary '\0',4,3,5),(6,_binary '\0',4,3,5),(7,_binary '\0',4,3,5),(8,_binary '\0',4,4,5),(9,_binary '\0',4,4,5),(10,_binary '\0',4,2,5),(11,_binary '\0',4,4,5),(12,_binary '\0',4,4,5),(13,_binary '\0',4,4,5),(14,_binary '\0',4,3,5),(15,_binary '\0',4,3,5),(16,_binary '\0',4,3,5),(17,_binary '\0',4,4,5),(18,_binary '\0',4,4,5),(19,_binary '\0',4,4,5),(20,_binary '\0',4,3,5),(21,_binary '\0',4,4,5),(22,_binary '\0',4,4,5),(23,_binary '\0',4,4,5),(24,_binary '\0',4,4,5),(25,_binary '\0',4,4,5),(26,_binary '\0',4,4,5),(27,_binary '\0',4,4,5),(28,_binary '\0',4,4,5),(29,_binary '\0',4,4,5),(30,_binary '\0',4,4,5),(31,_binary '\0',4,4,5),(32,_binary '\0',4,4,5),(33,_binary '\0',4,4,5),(34,_binary '\0',4,4,5),(35,_binary '\0',4,4,5),(36,_binary '\0',4,4,5),(37,_binary '\0',4,4,5),(38,_binary '\0',4,3,5),(39,_binary '\0',4,4,5),(40,_binary '\0',4,4,5),(41,_binary '\0',4,4,5),(42,_binary '\0',4,4,5),(43,_binary '\0',4,3,5),(44,_binary '\0',4,3,6),(45,_binary '\0',4,3,5),(46,_binary '\0',4,3,5),(47,_binary '\0',4,4,5),(48,_binary '\0',4,4,5),(49,_binary '\0',4,3,5),(50,_binary '\0',4,4,5),(51,_binary '\0',4,3,5),(52,_binary '\0',4,3,5),(53,_binary '\0',4,3,5),(55,_binary '\0',4,4,5),(56,_binary '\0',4,4,5),(57,_binary '\0',4,4,5),(58,_binary '\0',4,4,5),(59,_binary '\0',4,4,5),(60,_binary '\0',4,4,5),(61,_binary '\0',4,4,5),(62,_binary '\0',4,3,5),(63,_binary '\0',4,4,5),(64,_binary '\0',4,4,5),(65,_binary '\0',4,4,5),(66,_binary '\0',4,4,5),(67,_binary '\0',4,4,5),(68,_binary '\0',4,4,5),(69,_binary '\0',4,4,5),(70,_binary '\0',4,4,5),(71,_binary '\0',4,4,5),(72,_binary '\0',4,4,5),(73,_binary '\0',4,4,5),(74,_binary '\0',4,4,5),(75,_binary '\0',4,4,5),(76,_binary '\0',4,4,5),(77,_binary '\0',4,4,5),(78,_binary '\0',4,4,5),(79,_binary '\0',4,4,5),(80,_binary '\0',4,4,5),(81,_binary '\0',4,4,5),(82,_binary '\0',4,4,5),(83,_binary '\0',4,4,5),(84,_binary '\0',4,4,5),(85,_binary '\0',4,4,5),(86,_binary '\0',4,3,5),(87,_binary '\0',4,4,5),(88,_binary '\0',4,4,5),(89,_binary '\0',4,4,5),(90,_binary '\0',4,4,5),(91,_binary '\0',4,4,5),(92,_binary '\0',4,4,5),(93,_binary '\0',4,3,5),(94,_binary '\0',4,4,5),(95,_binary '\0',4,4,5),(96,_binary '\0',4,3,5),(97,_binary '\0',4,4,5),(98,_binary '\0',4,3,5),(99,_binary '\0',4,4,5),(100,_binary '\0',4,4,5),(101,_binary '\0',4,4,5),(102,_binary '\0',4,4,5),(103,_binary '\0',4,3,5),(104,_binary '\0',4,4,5),(105,_binary '\0',4,4,5),(106,_binary '\0',4,4,5),(107,_binary '\0',4,4,5),(108,_binary '\0',4,4,5),(109,_binary '\0',4,4,5),(110,_binary '\0',4,4,5),(111,_binary '\0',4,4,5),(112,_binary '\0',4,3,5),(113,_binary '\0',4,4,5),(114,_binary '\0',4,4,5),(115,_binary '\0',4,4,5),(116,_binary '\0',4,3,5),(117,_binary '\0',4,4,5),(118,_binary '\0',4,4,5),(119,_binary '\0',4,4,5),(120,_binary '\0',4,4,5),(121,_binary '\0',4,4,5),(122,_binary '\0',4,4,5),(123,_binary '\0',4,4,5),(124,_binary '\0',4,4,5),(125,_binary '\0',4,3,5),(126,_binary '\0',4,3,5),(127,_binary '\0',4,3,5),(128,_binary '\0',4,4,5),(129,_binary '\0',4,4,6),(130,_binary '\0',4,4,5),(131,_binary '\0',4,4,5),(132,_binary '\0',4,4,6),(133,_binary '\0',4,4,5),(134,_binary '\0',4,3,6),(135,_binary '\0',4,4,6),(136,_binary '\0',4,4,5),(137,_binary '\0',4,4,5),(138,_binary '\0',4,4,5),(139,_binary '\0',4,4,6),(140,_binary '\0',4,2,6),(141,_binary '\0',4,4,6),(142,_binary '\0',4,4,6),(143,_binary '\0',4,3,5),(144,_binary '\0',4,4,5),(145,_binary '\0',4,4,5),(146,_binary '\0',4,3,5),(147,_binary '\0',4,3,5),(148,_binary '\0',4,4,5),(149,_binary '\0',4,4,5),(150,_binary '\0',4,4,5),(151,_binary '\0',4,4,5),(152,_binary '\0',4,4,5),(153,_binary '\0',4,4,5),(154,_binary '\0',4,4,6),(155,_binary '\0',4,4,5),(156,_binary '\0',4,4,6),(157,_binary '',4,4,5),(158,_binary '',4,4,5),(159,_binary '',4,4,5),(160,_binary '',4,4,6),(161,_binary '',4,4,5),(162,_binary '',4,4,5),(163,_binary '',4,2,6),(164,_binary '',4,2,5),(165,_binary '\0',4,4,5),(166,_binary '\0',4,4,5),(167,_binary '',4,4,5),(168,_binary '',4,4,6),(169,_binary '',4,3,5),(170,_binary '',4,3,6),(171,_binary '',4,3,5),(172,_binary '',4,4,5),(173,_binary '',4,4,5),(174,_binary '\0',4,4,6),(175,_binary '\0',4,4,5),(176,_binary '\0',4,3,5),(177,_binary '\0',4,4,6),(178,_binary '\0',4,4,5),(179,_binary '\0',4,2,5),(180,_binary '\0',4,4,5),(181,_binary '\0',4,4,5),(182,_binary '\0',4,2,5);
/*!40000 ALTER TABLE `game_rooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `post_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_like_post_user` (`post_id`,`user_id`),
  KEY `FK_like_user` (`user_id`),
  CONSTRAINT `FK_like_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `FK_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (3,'2025-05-01 13:00:55.862000',3,5),(4,'2025-05-01 13:27:55.896000',4,6),(6,'2025-05-01 15:11:50.743000',5,6),(19,'2025-05-01 19:46:01.447000',1,5),(96,'2025-05-02 04:35:03.687000',9,5),(103,'2025-05-02 04:35:20.021000',8,5),(107,'2025-05-03 05:46:04.235000',13,5),(109,'2025-05-03 05:58:54.745000',10,5),(110,'2025-05-03 05:59:32.217000',11,5);
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (1,'1s','2025-04-14 19:07:49.000000',2,5),(2,'hehe','2025-04-14 19:08:10.000000',2,6),(3,'helklo','2025-04-20 17:18:30.000000',2,6),(4,'t day','2025-04-20 17:20:12.000000',2,5),(5,'dang lam gi do','2025-04-20 17:20:27.000000',2,5),(6,'tao day ne','2025-04-22 13:06:59.000000',2,6),(7,'1','2025-04-22 20:50:06.000000',2,5),(8,'2','2025-04-22 20:50:08.000000',2,5),(9,'3','2025-04-22 20:50:09.000000',2,5),(10,'4','2025-04-22 20:50:10.000000',2,5),(11,'.','2025-05-02 01:51:49.000000',2,5),(12,'2','2025-05-02 01:52:57.000000',2,6),(13,'3','2025-05-02 01:53:01.000000',2,6),(14,'s','2025-05-02 02:22:14.000000',2,5),(15,'b','2025-05-02 03:06:20.000000',2,5),(16,'.\n.\n.\n.','2025-05-02 03:06:35.000000',2,5),(17,'222','2025-05-02 03:07:43.000000',2,6),(18,'sdasdasd','2025-05-02 03:07:59.000000',2,5),(19,'ggg','2025-05-02 03:08:32.000000',2,5),(20,',..','2025-05-02 03:09:56.000000',7,5);
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_media`
--

DROP TABLE IF EXISTS `post_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_media` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `display_order` int NOT NULL,
  `media_type` enum('IMAGE','VIDEO') NOT NULL,
  `media_url` varchar(255) NOT NULL,
  `post_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_post_media_post` (`post_id`),
  CONSTRAINT `FK_post_media_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_media`
--

LOCK TABLES `post_media` WRITE;
/*!40000 ALTER TABLE `post_media` DISABLE KEYS */;
INSERT INTO `post_media` VALUES (1,'2025-05-01 06:26:15.722000',1,'IMAGE','/uploads/c24c3e01-3677-41a8-bad9-ad06662d86df_post_image_1746080774961.jpg',1),(2,'2025-05-01 06:26:15.788000',2,'IMAGE','/uploads/2dc0a9eb-20d5-4097-b4c4-4153405d1f3f_post_image_1746080775211.jpg',1),(3,'2025-05-01 13:00:02.661000',1,'IMAGE','/uploads/86b7fd5d-b344-4504-9518-b03acd4c1acc_post_image_1746104397297.jpg',2),(4,'2025-05-01 13:00:42.607000',1,'IMAGE','/uploads/9343d283-6c70-4bfa-8bdd-8fa3747b3ea8_post_image_1746104437191.jpg',3),(5,'2025-05-01 13:00:42.608000',2,'IMAGE','/uploads/58eb171e-05e4-4b39-bdac-0b861c9edb82_post_image_1746104437252.jpg',3),(6,'2025-05-01 13:00:42.610000',3,'IMAGE','/uploads/3853b29f-bc20-4712-80ea-84487cfed06a_post_image_1746104437297.jpg',3),(7,'2025-05-01 13:27:38.833000',1,'IMAGE','/uploads/0105ac8e-595b-49a3-a7d8-457d9afcb42a_post_image_1746106052814.jpg',4),(8,'2025-05-01 13:27:38.840000',2,'IMAGE','/uploads/988e5261-8f63-4eea-ac41-fce929b1c17a_post_image_1746106053468.jpg',4),(9,'2025-05-01 15:11:28.621000',1,'IMAGE','/uploads/5fbeff57-1f15-4721-9e23-e2f7dd96d8fa_post_image_1746112283168.jpg',5),(10,'2025-05-01 15:22:02.575000',1,'IMAGE','/uploads/f6bd4881-d1b8-4b1a-9092-aa03c0327748_post_image_1746112917056.jpg',6),(11,'2025-05-01 17:52:35.290000',1,'IMAGE','/uploads/6c399c62-5b43-4cb8-a388-bd9fac55ffd4_post_image_1746121951293.jpg',7),(12,'2025-05-01 17:53:04.163000',1,'IMAGE','/uploads/e7d74d61-5af1-400f-9b17-40f69c353972_post_image_1746121980231.jpg',8),(13,'2025-05-01 17:53:04.165000',2,'IMAGE','/uploads/2a51567d-775b-469a-b8cb-d1f8b8e5a193_post_image_1746121980325.jpg',8),(14,'2025-05-01 17:56:37.401000',1,'IMAGE','/uploads/ea736be3-b386-475c-8dac-ef207b5d106d_post_image_1746122193469.jpg',9),(15,'2025-05-01 18:07:50.321000',1,'IMAGE','/uploads/3f20df44-7126-4321-9d59-60b4d2ba5396_post_image_1746122869041.jpg',10),(16,'2025-05-01 18:07:50.321000',2,'IMAGE','/uploads/8d7790cf-5943-4a00-acfd-af6961e62578_post_image_1746122869133.jpg',10),(17,'2025-05-01 18:07:50.324000',3,'IMAGE','/uploads/9f354b2a-74fc-48ff-8edd-cdc4cf258d73_post_image_1746122869171.jpg',10),(18,'2025-05-01 18:07:50.324000',4,'IMAGE','/uploads/e4c168d9-f896-47f3-a237-2674853c8551_post_image_1746122869222.jpg',10),(19,'2025-05-01 18:07:50.325000',5,'IMAGE','/uploads/7bc99900-44f1-4e5d-8f3f-982f19956205_post_image_1746122869258.jpg',10),(20,'2025-05-01 19:46:15.728000',1,'IMAGE','/uploads/ed8e1f0e-df3c-4ff1-bce3-93a50c23cdfd_post_image_1746128773460.jpg',11),(21,'2025-05-01 19:46:15.732000',2,'IMAGE','/uploads/1b4ef4b4-32c8-4e15-b171-f4550ec01f84_post_image_1746128773506.jpg',11),(22,'2025-05-02 01:47:39.667000',1,'IMAGE','/uploads/c512873d-1364-4b07-8939-d16273222a12_post_image_1746150457474.jpg',12),(23,'2025-05-02 04:35:43.410000',1,'IMAGE','/uploads/56f9a1f2-bd43-4585-91d8-703d963130db_post_image_1746160542806.jpg',13);
/*!40000 ALTER TABLE `post_media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text,
  `created_at` datetime(6) DEFAULT NULL,
  `privacy` enum('FRIENDS','PRIVATE','PUBLIC') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_post_user` (`user_id`),
  CONSTRAINT `FK_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (1,'','2025-05-01 06:26:15.671000','PUBLIC','2025-05-01 06:26:15.671000',5),(2,'','2025-05-01 13:00:02.651000','PUBLIC','2025-05-01 13:00:02.651000',5),(3,'','2025-05-01 13:00:42.603000','PUBLIC','2025-05-01 13:00:42.603000',5),(4,'','2025-05-01 13:27:38.806000','PUBLIC','2025-05-01 13:27:38.806000',6),(5,'','2025-05-01 15:11:28.540000','PUBLIC','2025-05-01 15:11:28.540000',6),(6,'','2025-05-01 15:22:02.539000','PUBLIC','2025-05-01 15:22:02.539000',6),(7,'','2025-05-01 17:52:35.229000','PUBLIC','2025-05-01 17:52:35.229000',5),(8,'ddd','2025-05-01 17:53:04.159000','PUBLIC','2025-05-01 17:53:04.159000',5),(9,'','2025-05-01 17:56:37.370000','PUBLIC','2025-05-01 17:56:37.370000',5),(10,'','2025-05-01 18:07:50.314000','PUBLIC','2025-05-01 18:07:50.314000',5),(11,'','2025-05-01 19:46:15.723000','PUBLIC','2025-05-01 19:46:15.723000',5),(12,'','2025-05-02 01:47:39.633000','PUBLIC','2025-05-02 01:47:39.633000',5),(13,'','2025-05-02 04:35:43.401000','PUBLIC','2025-05-02 04:35:43.401000',5);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
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
INSERT INTO `user` VALUES (5,'2025-04-04 14:04:59.000000','user1@example.com',_binary '',_binary '\0','123456','123',_binary '','duy',NULL,'/uploads/383a9e59-9710-4f54-ba06-5721214c8b41_image.jpg'),(6,'2025-04-04 14:04:59.000000','user2@example.com',_binary '',_binary '','654321','123',_binary '\0','duong',NULL,'/uploads/5e4fdf8d-b58e-400d-bc85-5189b522b764_image.jpg'),(7,'2025-04-04 14:04:59.000000','user3@example.com',_binary '\0',_binary '\0',NULL,'123',_binary '','user3',NULL,NULL),(8,'2025-04-04 14:04:59.000000','user4@example.com',_binary '',_binary '','987654','123',_binary '\0','user4',NULL,NULL);
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

-- Dump completed on 2025-05-03 13:03:52
