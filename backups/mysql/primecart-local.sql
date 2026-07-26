-- MySQL dump 10.13  Distrib 9.6.0, for macos26.3 (arm64)
--
-- Host: 127.0.0.1    Database: product_db
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '2859af94-452f-11f1-8b93-c62257508ac2:1-551307';

--
-- Current Database: `product_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `product_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `product_db`;

--
-- Table structure for table `brands`
--

DROP TABLE IF EXISTS `brands`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brands` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brands`
--

LOCK TABLES `brands` WRITE;
/*!40000 ALTER TABLE `brands` DISABLE KEYS */;
INSERT INTO `brands` VALUES (1,'Apple','Apple Inc.',NULL,1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(2,'Samsung','Samsung Electronics',NULL,1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(3,'Nike','Sportswear',NULL,1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(4,'Adidas','Sportswear',NULL,1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(5,'Sony','Consumer Electronics',NULL,1,'2026-07-09 17:49:48','2026-07-09 17:49:48');
/*!40000 ALTER TABLE `brands` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Electronics','Electronic Products',1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(2,'Fashion','Clothing and Accessories',1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(3,'Home & Kitchen','Home Appliances',1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(4,'Books','Books and Stationery',1,'2026-07-09 17:49:48','2026-07-09 17:49:48'),(5,'Sports','Sports Equipment',1,'2026-07-09 17:49:48','2026-07-09 17:49:48');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create categories table','SQL','V1__create_categories_table.sql',845749921,'root','2026-07-09 17:49:48',7,1),(2,'2','create brands table','SQL','V2__create_brands_table.sql',1339915653,'root','2026-07-09 17:49:48',6,1),(3,'3','create products table','SQL','V3__create_products_table.sql',-122289285,'root','2026-07-09 17:49:48',9,1),(4,'4','insert categories','SQL','V4__insert_categories.sql',-401015491,'root','2026-07-09 17:49:48',2,1),(5,'5','insert brands','SQL','V5__insert_brands.sql',1961969062,'root','2026-07-09 17:49:48',2,1),(6,'6','insert sample products','SQL','V6__insert_sample_products.sql',585991780,'root','2026-07-09 17:51:25',4,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` text,
  `price` decimal(10,2) NOT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `sku` varchar(100) NOT NULL,
  `stock` int NOT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `category_id` bigint NOT NULL,
  `brand_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sku` (`sku`),
  KEY `fk_product_category` (`category_id`),
  KEY `fk_product_brand` (`brand_id`),
  CONSTRAINT `fk_product_brand` FOREIGN KEY (`brand_id`) REFERENCES `brands` (`id`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (4,'Nike Air Max 270','Men Running Shoes',12999.00,'https://picsum.photos/400?random=4','NIKE-AM270-BLK',120,1,2,3,'2026-07-09 17:51:25','2026-07-17 11:23:50'),(5,'Adidas Ultraboost 5','Running Shoes',14999.00,'https://picsum.photos/400?random=4','ADI-UB5-WHT',90,1,2,1,'2026-07-09 17:51:25','2026-07-18 16:24:46'),(81,'iPhone 16','Apple iPhone 16 128GB',79999.00,'https://picsum.photos/400?random=1','APL-IP16-128',25,1,3,1,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(82,'iPhone 16 Pro','Apple iPhone 16 Pro 256GB',124999.00,'https://picsum.photos/400?random=2','APL-IP16PRO-256',15,1,3,1,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(83,'Samsung Galaxy S25','Samsung Galaxy S25 256GB',85999.00,'https://picsum.photos/400?random=4','SMS-S25-256',20,1,3,2,'2026-07-17 11:21:29','2026-07-18 13:17:29'),(85,'Dell XPS 15','Dell XPS 15 Intel Core Ultra',169999.00,'https://picsum.photos/400?random=5','DLL-XPS15',10,1,2,3,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(86,'Dell Inspiron 15','Dell Inspiron 15 Ryzen 7',69999.00,'https://picsum.photos/400?random=6','DLL-INS15',18,1,2,3,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(88,'HP Spectre x360','HP Spectre Convertible Laptop',149999.00,'https://picsum.photos/400?random=8','HP-SPX360',8,1,2,4,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(89,'MacBook Air M4','Apple MacBook Air M4 13-inch',114999.00,'https://picsum.photos/400?random=9','APL-MBA-M4',14,1,2,1,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(91,'Sony WH-1000XM5','Noise Cancelling Headphones',29999.00,'https://picsum.photos/400?random=11','SNY-WHXM5',40,1,4,5,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(92,'Sony WF-1000XM5','Wireless Noise Cancelling Earbuds',22999.00,'https://picsum.photos/400?random=12','SNY-WFXM5',45,1,4,5,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(93,'Boat Rockerz 550','Wireless Bluetooth Headphones',2499.00,'https://picsum.photos/400?random=13','BOT-R550',80,1,4,5,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(94,'Boat Airdopes 311','True Wireless Earbuds',1799.00,'https://picsum.photos/400?random=14','BOT-AD311',95,1,4,3,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(95,'LG Smart TV 55\"','55 Inch 4K UHD Smart TV',62999.00,'https://picsum.photos/400?random=15','LG-TV55',12,1,5,3,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(96,'LG Washing Machine','Front Load 8KG Washing Machine',45999.00,'https://picsum.photos/400?random=16','LG-WM8KG',16,1,5,1,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(97,'LG Refrigerator 340L','Double Door Refrigerator',38999.00,'https://picsum.photos/400?random=17','LG-REF340',14,1,5,2,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(98,'Logitech MX Master 3S','Wireless Productivity Mouse',8999.00,'https://picsum.photos/400?random=18','LOG-MX3S',55,1,1,2,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(99,'Logitech MX Keys S','Wireless Keyboard',11999.00,'https://picsum.photos/400?random=19','LOG-MXKEYS',42,1,1,2,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(100,'Logitech C920 Webcam','Full HD Webcam',6999.00,'https://picsum.photos/400?random=20','LOG-C920',38,1,1,3,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(101,'Apple Magic Mouse','Rechargeable Wireless Mouse',7999.00,'https://picsum.photos/400?random=21','APL-MMOUSE',26,1,4,1,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(102,'Apple Magic Keyboard','Wireless Keyboard',9999.00,'https://picsum.photos/400?random=22','APL-MKEY',22,1,4,1,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(103,'Samsung 27 Monitor','27 Inch IPS Full HD Monitor',18999.00,'https://picsum.photos/400?random=23','SMS-MON27',24,1,1,2,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(104,'Dell UltraSharp 27','27 Inch QHD Professional Monitor',38999.00,'https://picsum.photos/400?random=24','DLL-U2724',18,1,1,3,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(105,'HP USB-C Dock','Universal USB-C Docking Station',14999.00,'https://picsum.photos/400?random=25','HP-DOCK',20,1,3,4,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(106,'Sony Bluetooth Speaker','Portable Bluetooth Speaker',7999.00,'https://picsum.photos/400?random=26','SNY-SPKR',44,1,4,5,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(107,'Boat Stone 1200','Portable Bluetooth Speaker',3999.00,'https://picsum.photos/400?random=27','BOT-ST1200',60,1,4,5,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(108,'Apple AirPods Pro 2','Wireless ANC Earbuds',24999.00,'https://picsum.photos/400?random=28','APL-APP2',32,1,4,1,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(109,'Samsung Galaxy Buds3 Pro','Premium Wireless Earbuds',16999.00,'https://picsum.photos/400?random=29','SMS-BUDS3P',36,1,4,2,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(110,'Dell Wireless Keyboard & Mouse','Combo Set',3499.00,'https://picsum.photos/400?random=30','DLL-KM216',75,1,5,3,'2026-07-17 11:21:29','2026-07-17 11:21:29'),(170,'iPhone 16','Apple iPhone 16 128GB',79999.00,'https://picsum.photos/400?random=1','APL-IP16-1281',25,1,3,1,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(171,'iPhone 16 Pro','Apple iPhone 16 Pro 256GB',124999.00,'https://picsum.photos/400?random=2','APL-IP16PRO-2562',15,1,3,1,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(172,'Samsung Galaxy S25','Samsung Galaxy S25 256GB',85999.00,'https://picsum.photos/400?random=3','SMS-S25-2563',20,1,3,2,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(173,'Samsung Galaxy A56','Samsung Galaxy A56 5G',32999.00,'https://picsum.photos/400?random=4','SMS-A56-1284',35,1,3,2,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(174,'OnePlus 13','OnePlus 13 256GB Smartphone',69999.00,'https://picsum.photos/400?random=5','OP-13-2565',22,1,3,3,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(175,'Dell Inspiron 15','Dell Inspiron 15 Laptop 16GB RAM',62999.00,'https://picsum.photos/400?random=6','DEL-INS156',12,1,2,4,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(176,'HP Pavilion 14','HP Pavilion 14 Laptop',58999.00,'https://picsum.photos/400?random=7','HP-PAV147',18,1,2,5,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(177,'Lenovo IdeaPad Slim 5','Lenovo IdeaPad Slim 5 Laptop',54999.00,'https://picsum.photos/400?random=8','LEN-SLIM58',14,1,2,4,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(178,'Apple MacBook Air M3','MacBook Air M3 13-inch',114999.00,'https://picsum.photos/400?random=9','APL-MBA-M39',10,1,2,1,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(179,'Sony WH-1000XM5','Sony Noise Cancelling Headphones',29999.00,'https://picsum.photos/400?random=10','SNY-XM510',30,1,5,2,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(180,'JBL Flip 6','Portable Bluetooth Speaker',9999.00,'https://picsum.photos/400?random=11','JBL-FLIP611',45,1,5,5,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(181,'Boat Rockerz 550','Wireless Over-Ear Headphones',2499.00,'https://picsum.photos/400?random=12','BOAT-R55120',60,1,5,3,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(182,'Samsung 55 Inch Smart TV','Samsung Crystal UHD Smart TV',55999.00,'https://picsum.photos/400?random=13','SMS-TV5225',8,1,4,2,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(183,'LG 43 Inch LED TV','LG Full HD Smart TV',34999.00,'https://picsum.photos/400?random=14','LG-TV4333',9,1,4,5,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(184,'Sony Bravia 65 Inch','Sony Bravia 4K Google TV',89999.00,'https://picsum.photos/400?random=15','SNY-BR3265',6,1,4,2,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(185,'Apple Watch Series 10','Apple Smart Watch GPS',42999.00,'https://picsum.photos/400?random=16','APL-WATCH4310',20,1,1,1,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(186,'Samsung Galaxy Watch 7','Samsung Smart Watch',28999.00,'https://picsum.photos/400?random=17','SMS-WATCH745',22,1,1,2,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(187,'Noise ColorFit Ultra','Noise Smart Watch',4999.00,'https://picsum.photos/400?random=18','NOISE-CFU45',50,1,1,3,'2026-07-18 11:44:27','2026-07-18 13:09:05'),(188,'Mi Smart Band 9','Fitness Smart Band',3499.00,'https://picsum.photos/400?random=19','MI-BAND945',70,1,1,3,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(189,'Canon EOS 200D','Canon DSLR Camera',52999.00,'https://picsum.photos/400?random=20','CAN-200D657',10,1,5,4,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(190,'Nikon D7500','Nikon DSLR Camera',71999.00,'https://picsum.photos/400?random=21','NIK-D750056',7,1,5,4,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(192,'Samsung Galaxy Buds 3','Samsung Wireless Earbuds',11999.00,'https://picsum.photos/400?random=23','SMS-BUDS334',40,0,5,2,'2026-07-18 11:44:27','2026-07-19 11:46:32'),(193,'Logitech MX Master 3S','Wireless Productivity Mouse',8999.00,'https://picsum.photos/400?random=24','LOG-MX3S45',28,1,1,4,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(194,'Dell 27 Inch Monitor','Dell IPS Full HD Monitor',18999.00,'https://picsum.photos/400?random=25','DEL-MON2767',16,1,4,4,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(195,'HP LaserJet MFP 136w','Wireless Laser Printer',16999.00,'https://picsum.photos/400?random=26','HP-LJ13456',13,1,4,5,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(196,'OnePlus Nord Buds 3','True Wireless Earbuds',2999.00,'https://picsum.photos/400?random=27','OP-BUDS387',55,1,5,3,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(197,'Lenovo Tab M11','Android Tablet 128GB',21999.00,'https://picsum.photos/400?random=28','LEN-TABM1145',18,1,2,4,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(198,'Apple iPad Air M3','Apple iPad Air 11-inch',69999.00,'https://picsum.photos/400?random=29','APL-IPADAIR45',11,1,2,1,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(199,'Samsung Galaxy Tab S10','Samsung Premium Android Tablet',64999.00,'https://picsum.photos/400?random=30','SMS-TABS1034',9,1,2,2,'2026-07-18 11:44:27','2026-07-18 11:44:27'),(200,'prod1','prod123456',23.00,'https://picsum.photos/400?random=28','prod-354-123',3,1,3,3,'2026-07-18 12:12:59','2026-07-18 12:12:59'),(201,'prod2','prod124564',3.00,'https://picsum.photos/400?random=29','lap-435',3,1,4,4,'2026-07-18 12:14:18','2026-07-18 12:14:18'),(202,'prod2','prod2345657',34.00,'https://picsum.photos/400?random=29','SKU-923-DFG',3,0,2,3,'2026-07-18 12:26:55','2026-07-18 12:26:55'),(203,'prod5','sddgdfgrdfgd',23.00,'https://picsum.photos/400?random=28','prod5werfsd',3,1,1,1,'2026-07-18 12:58:59','2026-07-18 12:58:59'),(204,'product5','product5- for new',1.00,'http://picsum.photos/400?random=28','sdfg',1,1,4,4,'2026-07-18 17:05:33','2026-07-19 11:47:45'),(205,'prod5','productdderw',1.00,'https://picsum.photos/400?random=30','wke-wee',1,1,4,4,'2026-07-19 11:47:05','2026-07-19 11:47:24'),(206,'product6','product6-34',1.00,'https://picsum.photos/400?random=28','product6',2,1,4,4,'2026-07-19 13:08:08','2026-07-19 13:08:08'),(207,'p1','p1',2.00,'image.com','string-0101',1,1,1,1,'2026-07-23 16:11:32','2026-07-23 16:11:32'),(208,'prod2','this is p2 proudct',1.00,'products/temporary/6408ffb1-cb72-48fd-90a1-50df0889b359.png','prod2-345',1,1,4,4,'2026-07-24 16:06:20','2026-07-24 16:06:20'),(209,'prod3','prod3- new product',1.00,'https://primecart-products.s3.eu-north-1.amazonaws.com/products/temporary/8c901621-afd1-4d4a-9922-22f27ce09eae.jpg','prod3-3453',1,1,4,4,'2026-07-24 16:17:06','2026-07-24 16:17:06'),(210,'prod4','sdfasdfsdfsdfs',1.00,'https://primecart-products.s3.eu-north-1.amazonaws.com/products/temporary/7fa0d52c-a118-4701-8910-f7e0e8615521.png','skfdasf',1,1,4,4,'2026-07-24 16:18:49','2026-07-24 16:18:49'),(211,'ASDASDA','ASDFASDFASD',1.00,'https://primecart-products.s3.eu-north-1.amazonaws.com/products/temporary/e74b4581-feec-49df-95eb-c85e815374cb.png','ASDFASFD',1,1,4,3,'2026-07-24 16:22:47','2026-07-24 16:22:47'),(212,'dvcxvxc','bxcvbxcvsdfsd',1.00,'https://primecart-products.s3.eu-north-1.amazonaws.com/products/temporary/06435e9c-360f-43e7-b09b-924077d7058d.jpg','xcvbxcbvx',1,1,1,1,'2026-07-24 16:28:11','2026-07-24 16:28:11');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'product_db'
--

--
-- Dumping routines for database 'product_db'
--

--
-- Current Database: `inventory_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `inventory_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `inventory_db`;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create inventory table','SQL','V1__create_inventory_table.sql',429240392,'root','2026-07-11 08:47:05',9,1),(2,'2','add sku to inventory','SQL','V2__add_sku_to_inventory.sql',1179008538,'root','2026-07-15 07:09:12',40,1),(3,'3','create processed events table','SQL','V3__create_processed_events_table.sql',784863680,'root','2026-07-15 09:08:03',16,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `sku` varchar(100) NOT NULL,
  `available_quantity` int NOT NULL,
  `reserved_quantity` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=97 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,1,'SKU-001',6,19,'2026-07-11 14:17:05','2026-07-16 18:40:35'),(2,2,'SKU-002',6,3,'2026-07-11 14:17:05','2026-07-17 11:48:22'),(3,3,'SKU-003',6,15,'2026-07-11 14:17:05','2026-07-18 16:29:50'),(4,4,'SKU-004',6,0,'2026-07-11 14:17:05','2026-07-22 13:24:35'),(5,5,'SKU-005',93,2,'2026-07-11 14:17:05','2026-07-22 14:26:14'),(6,6,'SKU-006',100,0,'2026-07-11 14:17:05','2026-07-11 14:17:05'),(7,7,'SKU-007',100,15,'2026-07-11 14:17:05','2026-07-11 14:17:05'),(8,8,'SKU-008',100,0,'2026-07-11 14:17:05','2026-07-11 14:17:05'),(9,9,'SKU-009',100,5,'2026-07-11 14:17:05','2026-07-11 14:17:05'),(10,10,'SKU-010',100,0,'2026-07-11 14:17:05','2026-07-11 14:17:05'),(12,11,'MAC-001-AIR2',100,0,'2026-07-15 07:11:01','2026-07-15 07:11:01'),(13,12,'MAC-001-AIR22',100,0,'2026-07-15 07:13:22','2026-07-15 07:13:22'),(14,14,'KBD',100,0,'2026-07-15 07:48:13','2026-07-15 07:48:13'),(15,15,'KBD1',100,0,'2026-07-15 08:07:33','2026-07-15 08:07:33'),(16,16,'KBD2',100,0,'2026-07-15 08:07:44','2026-07-15 08:07:44'),(17,17,'KBD3',100,0,'2026-07-15 08:10:59','2026-07-15 08:10:59'),(18,18,'KBD43',100,2,'2026-07-15 08:11:39','2026-07-15 15:58:12'),(19,19,'KBD45',100,0,'2026-07-15 09:08:37','2026-07-15 09:08:37'),(20,20,'KBD46',100,1,'2026-07-15 09:21:18','2026-07-15 15:44:48'),(21,200,'prod-354-123',100,0,'2026-07-18 12:12:59','2026-07-18 12:12:59'),(22,201,'lap-435',100,0,'2026-07-18 12:14:18','2026-07-18 12:14:18'),(23,202,'SKU-923-DFG',100,0,'2026-07-18 12:26:55','2026-07-18 12:26:55'),(24,203,'prod5werfsd',100,0,'2026-07-18 12:58:59','2026-07-18 12:58:59'),(25,204,'sdfg',100,0,'2026-07-18 17:05:34','2026-07-18 17:05:34'),(26,81,'APL-IP16-128',53,3,'2026-07-18 23:06:03','2026-07-25 13:48:08'),(27,82,'APL-IP16PRO-256',57,1,'2026-07-18 23:06:03','2026-07-25 13:48:08'),(28,83,'SMS-S25-256',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(29,85,'DLL-XPS15',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(30,86,'DLL-INS15',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(31,88,'HP-SPX360',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(32,89,'APL-MBA-M4',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(33,91,'SNY-WHXM5',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(34,92,'SNY-WFXM5',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(35,93,'BOT-R550',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(36,94,'BOT-AD311',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(37,95,'LG-TV55',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(38,96,'LG-WM8KG',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(39,97,'LG-REF340',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(40,98,'LOG-MX3S',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(41,99,'LOG-MXKEYS',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(42,100,'LOG-C920',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(43,101,'APL-MMOUSE',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(44,102,'APL-MKEY',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(45,103,'SMS-MON27',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(46,104,'DLL-U2724',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(47,105,'HP-DOCK',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(48,106,'SNY-SPKR',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(49,107,'BOT-ST1200',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(50,108,'APL-APP2',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(51,109,'SMS-BUDS3P',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(52,110,'DLL-KM216',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(53,170,'APL-IP16-1281',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(54,171,'APL-IP16PRO-2562',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(55,172,'SMS-S25-2563',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(56,173,'SMS-A56-1284',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(57,174,'OP-13-2565',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(58,175,'DEL-INS156',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(59,176,'HP-PAV147',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(60,177,'LEN-SLIM58',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(61,178,'APL-MBA-M39',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(62,179,'SNY-XM510',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(63,180,'JBL-FLIP611',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(64,181,'BOAT-R55120',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(65,182,'SMS-TV5225',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(66,183,'LG-TV4333',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(67,184,'SNY-BR3265',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(68,185,'APL-WATCH4310',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(69,186,'SMS-WATCH745',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(70,187,'NOISE-CFU45',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(71,188,'MI-BAND945',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(72,189,'CAN-200D657',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(73,190,'NIK-D750056',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(74,191,'APL-APP223',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(75,192,'SMS-BUDS334',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(76,193,'LOG-MX3S45',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(77,194,'DEL-MON2767',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(78,195,'HP-LJ13456',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(79,196,'OP-BUDS387',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(80,197,'LEN-TABM1145',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(81,198,'APL-IPADAIR45',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(82,199,'SMS-TABS1034',100,0,'2026-07-18 23:06:03','2026-07-18 23:06:03'),(89,205,'wke-wee',6,0,'2026-07-19 11:47:05','2026-07-19 11:47:05'),(90,206,'product6',2,0,'2026-07-19 13:08:08','2026-07-19 13:08:08'),(91,207,'string-0101',6,0,'2026-07-23 16:11:32','2026-07-23 16:11:32'),(92,208,'prod2-345',1,0,'2026-07-24 16:06:20','2026-07-24 16:06:20'),(93,209,'prod3-3453',1,0,'2026-07-24 16:17:06','2026-07-24 16:17:06'),(94,210,'skfdasf',1,0,'2026-07-24 16:18:49','2026-07-24 16:18:49'),(95,211,'ASDFASFD',1,0,'2026-07-24 16:22:47','2026-07-24 16:22:47'),(96,212,'xcvbxcbvx',1,0,'2026-07-24 16:28:11','2026-07-24 16:28:11');
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processed_events`
--

DROP TABLE IF EXISTS `processed_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `processed_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` varchar(36) NOT NULL,
  `event_type` varchar(100) NOT NULL,
  `processed_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_processed_events_event_id` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=137 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processed_events`
--

LOCK TABLES `processed_events` WRITE;
/*!40000 ALTER TABLE `processed_events` DISABLE KEYS */;
INSERT INTO `processed_events` VALUES (1,'2c7a5197-70c7-494a-af05-d9583cf77e25','PRODUCT_CREATED','2026-07-15 09:08:37'),(2,'2c7a5197-70c7-494a-af05-d9583cf77e26','PRODUCT_CREATED','2026-07-15 09:12:29'),(3,'2ec37360-1bed-4610-8815-12a0f97c1fc4','PRODUCT_CREATED','2026-07-15 09:21:18'),(4,'b4a11728-e987-496c-a2b5-2b57e7121ad9','ORDER_CREATED','2026-07-15 13:54:41'),(5,'42f80e1e-606b-4427-a98b-ff4b57caea16','ORDER_CREATED','2026-07-15 15:44:48'),(6,'08f5afa8-135e-46c3-ae65-374e058f62a6','ORDER_CREATED','2026-07-15 15:57:19'),(7,'e9d74c02-6edb-4093-8570-dbdc87aefde3','ORDER_CREATED','2026-07-15 15:58:12'),(8,'bdb52201-7f4b-40ae-8b9c-387c0c3026be','ORDER_CREATED','2026-07-15 16:54:53'),(9,'e5f717c0-5d4c-4654-bf12-997026f3c6fa','ORDER_CREATED','2026-07-15 16:55:04'),(10,'0d8be3a7-eb82-4574-a5d3-47ff9e065bb8','ORDER_CREATED','2026-07-15 16:57:48'),(11,'364026b0-2481-434e-8dc0-ebd527233f40','ORDER_CREATED','2026-07-15 17:02:49'),(12,'85a143ef-aa21-4f93-a93d-34c1c527eb7c','ORDER_CREATED','2026-07-15 17:18:22'),(13,'bf81683b-bc9e-4c44-8aa0-ce6dccfcc506','ORDER_CREATED','2026-07-15 17:19:58'),(14,'deb100d5-4047-493f-bbaf-62c8bf89becf','ORDER_CREATED','2026-07-16 09:15:10'),(15,'ed0c404c-2ab6-47d6-9203-db8ccc963f5f','ORDER_CREATED','2026-07-16 09:18:06'),(16,'86987553-65af-4325-b108-cbd70cabb580','ORDER_CREATED','2026-07-16 09:18:42'),(17,'2a6bbe1a-3008-41eb-b18c-2eebacc9ea3f','ORDER_CREATED','2026-07-16 09:20:23'),(18,'3434a3fe-b03a-4042-a9c5-2ff17d9a63a3','ORDER_CREATED','2026-07-16 09:24:03'),(19,'a3ddaad2-cf73-46eb-af31-3a73dfc68ca2','ORDER_CREATED','2026-07-16 09:27:13'),(20,'ac8d7473-0351-415d-824b-b5f439944e15','ORDER_CREATED','2026-07-16 09:29:34'),(21,'b53332c8-2531-459f-824a-8990b26c1bb8','ORDER_CREATED','2026-07-16 09:29:42'),(22,'9c83d6e3-90ad-4e8f-a6e6-a80cde9d2c15','ORDER_CREATED','2026-07-16 09:31:09'),(23,'f3dc0de6-fc72-4413-965f-bebe342d9d5c','ORDER_CREATED','2026-07-16 09:31:29'),(24,'a04d994a-ac2d-412b-8a47-a7417cbd7b4c','ORDER_CREATED','2026-07-16 09:31:42'),(25,'9df6f7a8-428e-4dfb-a1b2-6a454694d72b','ORDER_CREATED','2026-07-16 09:32:43'),(26,'47b63c00-9304-449f-8707-3fc34709799b','ORDER_CREATED','2026-07-16 09:33:09'),(27,'cc5cbc74-a4f5-4661-82f0-b84810422cd9','ORDER_CREATED','2026-07-16 11:56:21'),(28,'4d86dc86-eff1-48d9-bbf9-ab5bdafa22b0','ORDER_CREATED','2026-07-16 18:40:35'),(29,'0c286d01-f48d-4e49-8d25-e9bdcf631584','ORDER_CREATED','2026-07-17 05:53:55'),(30,'035eb938-8d0c-479f-a610-65bfc2b547b3','ORDER_CREATED','2026-07-17 05:55:50'),(31,'b675e18d-44d6-4819-9bfe-e921f87a2164','ORDER_CREATED','2026-07-17 06:02:37'),(32,'ed2598cb-a588-4c26-bd47-45fc88408247','ORDER_CREATED','2026-07-17 06:13:05'),(33,'325e6e80-1786-4564-aa07-11ed863d58a2','ORDER_CREATED','2026-07-17 07:18:53'),(34,'f85c41df-c2fb-4368-ba93-c10a3c3c945f','ORDER_CREATED','2026-07-17 09:49:31'),(35,'8371ab8a-8afd-48eb-83a9-f73f9d66c5cf','ORDER_CREATED','2026-07-17 11:48:22'),(36,'b978705d-11da-4b23-beab-6dc014cd7716','PRODUCT_CREATED','2026-07-18 12:12:59'),(37,'f0336f79-17ec-42c5-b244-5ed7358dffed','PRODUCT_CREATED','2026-07-18 12:14:18'),(38,'b2439c10-a6e6-46ea-8137-731e60cdadf6','PRODUCT_CREATED','2026-07-18 12:26:55'),(39,'98107820-76c8-4db7-8470-583a9ce01991','PRODUCT_CREATED','2026-07-18 12:58:59'),(40,'9a3cb52f-aadd-43b8-843c-12098913a24c','ORDER_CREATED','2026-07-18 16:29:50'),(41,'fde7bf4d-fcd4-4eb8-828a-ec39e53f552f','ORDER_CREATED','2026-07-18 16:38:43'),(42,'a4cc63ad-32da-4500-8698-fb6098d17721','ORDER_CREATED','2026-07-18 16:44:00'),(43,'9feb64fc-d788-43e1-815e-484ebbf78a60','ORDER_CREATED','2026-07-18 16:44:43'),(44,'f516cc0c-a8c6-42d7-9cd8-ece119f8668b','ORDER_CREATED','2026-07-18 16:48:35'),(45,'566ed9d2-cf55-415d-806d-a8669e08a446','ORDER_CREATED','2026-07-18 16:48:59'),(46,'b4f96998-8b85-4c2e-a5bb-bdf77b113cf0','PRODUCT_CREATED','2026-07-18 17:05:34'),(47,'b87e4004-741d-40a6-ac46-85aef3758d4a','ORDER_CREATED','2026-07-18 17:23:52'),(48,'c768835b-fd82-42fb-9003-ff301e28dc98','ORDER_CREATED','2026-07-18 17:29:51'),(49,'a76dec6b-26a2-4d9a-baf5-702574f3f7f8','ORDER_CREATED','2026-07-18 17:37:03'),(50,'ec141f13-bd9a-47ee-8923-3116db4c7486','ORDER_CREATED','2026-07-18 17:37:38'),(51,'ef0a178f-f452-4b05-ba3a-7e5c28026296','ORDER_CREATED','2026-07-18 17:57:35'),(52,'e5de1527-f61e-451b-99bc-b2ea181f645b','ORDER_CREATED','2026-07-19 06:28:00'),(53,'f36c6bc0-8de7-4cfd-9ff1-181a03248f5d','ORDER_CREATED','2026-07-19 07:01:43'),(54,'74a6ca9a-a217-4909-a33a-8903f9ef2015','ORDER_CREATED','2026-07-19 08:02:53'),(55,'4bb7685d-f2e2-43ce-8dd9-4c9dd9362080','ORDER_CREATED','2026-07-19 08:09:14'),(56,'72770eec-22be-4646-87a2-3a8f5c7e9960','ORDER_CREATED','2026-07-19 08:11:42'),(57,'324763be-a060-4382-bd0f-268a07233b5e','ORDER_CREATED','2026-07-19 08:11:54'),(58,'0f0b04fb-7a9a-4019-9f3d-4b33d30b56f0','ORDER_CREATED','2026-07-19 08:17:20'),(59,'f2b3bce0-bdc3-451d-bfe5-72fcb62684dc','ORDER_CREATED','2026-07-19 08:18:32'),(60,'d76b9722-457b-492f-95c2-441990c6aa1e','ORDER_CREATED','2026-07-19 08:33:07'),(61,'8ca040d4-750d-4edb-a1db-6be8dd43b749','ORDER_CREATED','2026-07-19 09:19:39'),(62,'7fdc5d61-46c7-4e04-b67f-7746a365039b','ORDER_CREATED','2026-07-19 09:33:12'),(63,'c519ba19-a9a2-4da3-b8ec-aee2aeb424b8','ORDER_CREATED','2026-07-19 09:33:41'),(64,'db45fe61-8f58-4502-ae65-753304bb92d6','ORDER_CREATED','2026-07-19 09:36:57'),(65,'34f445b2-8c00-487d-a845-1103fd411e16','ORDER_CREATED','2026-07-19 09:58:04'),(66,'0d0f3a42-a2a9-47a5-b57b-bd881739b009','ORDER_CREATED','2026-07-19 09:58:52'),(67,'46f5a3bb-3ebb-426c-a383-c31260a82c80','ORDER_CREATED','2026-07-19 10:03:46'),(68,'a72b14fe-ea6c-4ae1-bdac-353535772fce','ORDER_CREATED','2026-07-19 10:08:52'),(69,'793c8a3a-7111-4a51-af99-4ce40c48e900','ORDER_CREATED','2026-07-19 10:09:48'),(70,'93581c5a-f94b-495c-9e73-ec91ca61edbf','ORDER_CREATED','2026-07-19 10:34:22'),(71,'9cc95120-5410-4fb5-9942-a5b4f54315a6','ORDER_CREATED','2026-07-19 10:34:31'),(72,'440fa02e-f505-4473-96a9-b38c4c5489c0','ORDER_CREATED','2026-07-19 10:35:16'),(73,'fef8e2eb-fb4f-478b-8a50-c310614abbc3','ORDER_CREATED','2026-07-19 10:35:59'),(74,'2ca62c62-3967-4fd6-b358-4c36528d13c8','ORDER_CREATED','2026-07-19 10:38:56'),(75,'f6dbbca1-78ea-4074-9505-5d52aaf0c0a2','ORDER_CREATED','2026-07-19 10:57:10'),(76,'8f64d78e-7580-401d-9456-e07c62ca09d9','ORDER_CREATED','2026-07-19 10:58:05'),(77,'f330c61f-fd6d-4a1f-9da6-851790c046ae','ORDER_CREATED','2026-07-19 10:58:49'),(78,'eceac94c-1b6e-4be1-9e17-927ae031e993','ORDER_CREATED','2026-07-19 11:01:31'),(79,'67bc7395-8edd-4b1c-9794-7486f7ed2a7e','ORDER_CREATED','2026-07-19 11:04:13'),(80,'b8a97cb0-b800-4db5-a4b4-abcd45d9b3b8','ORDER_CREATED','2026-07-19 11:05:58'),(81,'c100456c-8970-4acd-8c2c-77e26dce6709','ORDER_CREATED','2026-07-19 11:08:56'),(82,'827e9e77-a2bc-4102-869c-03575ae5b7ec','ORDER_CREATED','2026-07-19 11:10:18'),(83,'e9f4340b-b92c-4249-a007-ed64bfe7a587','ORDER_CREATED','2026-07-19 11:12:39'),(84,'089eb753-2880-4b6a-ad82-fc0120938770','ORDER_CREATED','2026-07-19 11:30:05'),(85,'d5039600-79e3-4c3d-a61d-7dc4c1e0cce0','ORDER_CREATED','2026-07-19 11:30:19'),(86,'76b07c72-a769-4f8a-9a68-45922aba8ce2','ORDER_CREATED','2026-07-19 11:30:31'),(87,'3123ae51-3bb9-46b3-8ecc-38ed941137d2','PRODUCT_CREATED','2026-07-19 11:47:05'),(88,'aa1644e0-5a4a-4ef9-bdc4-673ecb8ef4db','ORDER_CREATED','2026-07-19 13:07:29'),(89,'0119319d-c517-4fc6-bc5c-38898b0d9d75','PRODUCT_CREATED','2026-07-19 13:08:08'),(90,'859377d5-1231-4847-a1f9-4eb76635cddb','ORDER_CREATED','2026-07-19 14:11:10'),(91,'c1919057-4fb5-4ec0-97ff-2721c8d68101','ORDER_CREATED','2026-07-19 16:27:02'),(92,'573785c3-7ad1-4171-882e-4b5e307be832','ORDER_CREATED','2026-07-19 16:32:17'),(93,'40433b2a-8f89-4430-a061-13f4f6de0b0c','ORDER_CREATED','2026-07-19 16:33:29'),(94,'e4abb1bd-d362-468b-b307-e6cd1c161673','ORDER_CREATED','2026-07-19 16:34:45'),(95,'e77bd2ee-b1a8-4736-8104-30339d339a32','ORDER_CREATED','2026-07-19 18:19:42'),(96,'507fbf20-8f2b-429f-8e3a-f2df66f05ae4','ORDER_CREATED','2026-07-19 18:19:51'),(97,'4208fc5a-9047-4c3e-93f7-0e1fa8b80dd1','ORDER_CREATED','2026-07-19 18:22:07'),(98,'6da236e8-2768-4987-b571-5c3fc64dae4f','ORDER_CREATED','2026-07-19 18:24:44'),(99,'219b2595-069d-459f-927c-1689423e6607','ORDER_CREATED','2026-07-20 16:01:47'),(100,'afcd2be3-7555-4064-8b76-957d9750f1e7','ORDER_CREATED','2026-07-20 16:03:00'),(101,'3ce19182-f7f1-4798-b2c7-60f2be76ca09','ORDER_CREATED','2026-07-20 18:21:33'),(102,'ad3762e5-121c-4230-927d-db8aab04b525','ORDER_CREATED','2026-07-21 17:52:44'),(103,'cd7443b2-284f-4735-9f17-f7a3ef4eae6a','ORDER_CREATED','2026-07-21 17:58:15'),(104,'4ba1c234-bf78-471f-8769-f3c14733fe69','ORDER_CREATED','2026-07-21 18:02:29'),(105,'6661bee6-d114-4c5e-9c1f-308d24647746','ORDER_CREATED','2026-07-21 18:22:23'),(106,'d1c894bc-0425-4f7f-b06b-b7b442d95784','ORDER_CREATED','2026-07-21 18:29:22'),(107,'44cd6f70-a7eb-4551-a3d1-9c7150d5a5bc','ORDER_CREATED','2026-07-21 18:30:40'),(108,'c0521db3-fcfb-4224-a975-d43a9656c5d9','ORDER_CREATED','2026-07-22 04:04:36'),(109,'9d9e8353-57ec-4510-b3e0-43336282ae20','ORDER_CREATED','2026-07-22 04:28:48'),(110,'4530656c-95d1-451b-b202-2683f7c759d8','ORDER_CREATED','2026-07-22 04:29:50'),(111,'d59fcd1e-2ab6-47cc-8eb3-b6d1cecbb2a1','ORDER_CREATED','2026-07-22 05:06:37'),(112,'5d860189-2ba7-4789-983b-1d76a2eb0bf9','ORDER_CREATED','2026-07-22 05:14:10'),(113,'94e82fb6-1190-4a47-9538-c37d103401df','ORDER_CREATED','2026-07-22 05:18:19'),(114,'07aa0473-52b7-4f81-83ae-bcb1225ab473','ORDER_CREATED','2026-07-22 05:27:05'),(115,'ca9231ee-dc33-4153-b344-cbce6052ccb4','ORDER_CREATED','2026-07-22 05:29:55'),(116,'387c1ed6-e3e5-4168-9c8a-e732b366934a','ORDER_CREATED','2026-07-22 05:29:58'),(117,'d1cb4da5-34df-4c3b-93c1-a8e2b96cb336','ORDER_CREATED','2026-07-22 05:52:23'),(118,'b799dd32-8c4c-4881-8895-d7b5953ff291','ORDER_CREATED','2026-07-22 05:54:52'),(119,'584845f9-ef74-4531-b13a-12883d9efdab','ORDER_CREATED','2026-07-22 07:04:25'),(120,'bd191515-a78b-4229-8ecc-9458a225db4b','ORDER_CREATED','2026-07-22 07:18:09'),(121,'631a396f-43b0-495b-976b-ab129de1ec4d','ORDER_CREATED','2026-07-22 07:33:14'),(122,'7da0559c-09b5-4aef-aa00-4c41607c2db3','ORDER_CREATED','2026-07-22 07:36:57'),(123,'0fcb0496-bc0c-426f-bb78-4f12a0cd1e3c','ORDER_CREATED','2026-07-22 08:06:34'),(124,'7122d63f-a0a8-433d-82f6-517c6c09f85c','ORDER_CREATED','2026-07-22 10:48:42'),(125,'05638afb-f5fa-4248-85c6-5cdd11d534e3','ORDER_CREATED','2026-07-22 13:24:35'),(126,'9e1d6f9b-75b7-463b-86a5-8d287bbcc09e','ORDER_CREATED','2026-07-22 13:31:44'),(127,'0553fadf-2f70-43b1-a7d5-9b93d27998e3','ORDER_CREATED','2026-07-22 14:26:14'),(128,'b8dd54b2-126c-45eb-a9ba-703b69083a28','ORDER_CREATED','2026-07-23 15:18:59'),(129,'a30f2e3a-5e69-4e98-89ad-234fae5fd1e6','PRODUCT_CREATED','2026-07-23 16:11:32'),(130,'dc662d55-d46b-4711-ba1e-406c7df8a67f','PRODUCT_CREATED','2026-07-24 16:06:21'),(131,'c28e2eee-58f2-4691-9301-91652ef43659','PRODUCT_CREATED','2026-07-24 16:17:06'),(132,'9a1a10b2-a5bf-4bca-b183-3cb9fad0166f','PRODUCT_CREATED','2026-07-24 16:18:49'),(133,'e362c352-8114-431f-8d46-4e687a3248ef','PRODUCT_CREATED','2026-07-24 16:22:47'),(134,'677eb55a-1640-4e2c-91c0-e0fbb8ed7df8','PRODUCT_CREATED','2026-07-24 16:28:11'),(135,'0ff4217c-f32d-47b9-997a-6625f392de6e','ORDER_CREATED','2026-07-24 18:14:25'),(136,'538d9131-aab7-4ac7-8f26-d60456026a91','ORDER_CREATED','2026-07-25 13:48:08');
/*!40000 ALTER TABLE `processed_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'inventory_db'
--

--
-- Dumping routines for database 'inventory_db'
--

--
-- Current Database: `customer_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `customer_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `customer_db`;

--
-- Table structure for table `customer_profiles`
--

DROP TABLE IF EXISTS `customer_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `keycloak_user_id` varchar(100) NOT NULL,
  `username` varchar(100) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `created_at` timestamp(6) NOT NULL,
  `updated_at` timestamp(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_profiles_keycloak_user_id` (`keycloak_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_profiles`
--

LOCK TABLES `customer_profiles` WRITE;
/*!40000 ALTER TABLE `customer_profiles` DISABLE KEYS */;
INSERT INTO `customer_profiles` VALUES (8,'d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150','dadaramjadhav','dadaramjadhav@gmail.com','dm','dm',NULL,'2026-07-21 13:04:48.040607','2026-07-21 13:04:48.040607'),(9,'301f4e17-6208-4845-8c82-07bf6ea473d0','admin','admin@gmail.com','admin','admin','4234567898','2026-07-21 23:34:22.841862','2026-07-25 08:42:34.069998'),(10,'83a0c8b2-104c-4fba-8460-36ae6050e400','user','user@gmail.com','u1','u1','9011044985','2026-07-25 08:45:36.475850','2026-07-25 08:45:36.475850'),(11,'1814b14e-1986-4bdf-a03d-c55a182a3861','dm1@gmail.com','dm1@gmail.com','d1','d1','9011044321','2026-07-25 08:49:00.131079','2026-07-25 08:49:00.131079');
/*!40000 ALTER TABLE `customer_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create customer profiles','SQL','V1__create_customer_profiles.sql',-1247225544,'root','2026-07-20 16:48:38',21,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'customer_db'
--

--
-- Dumping routines for database 'customer_db'
--

--
-- Current Database: `cart_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `cart_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `cart_db`;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_cart_items_cart_id` (`cart_id`),
  KEY `idx_cart_items_product_id` (`product_id`),
  CONSTRAINT `fk_cart_items_cart` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
INSERT INTO `cart_items` VALUES (1,1,101,'iPhone 16 Pro',1,69999.00,69999.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(2,2,102,'Samsung Galaxy S25',2,49999.00,99998.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(3,3,103,'MacBook Air M4',1,89999.00,89999.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(4,4,104,'Sony WH-1000XM5',3,1499.00,4497.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(5,5,105,'Apple Watch Series 10',2,2999.00,5998.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(6,6,106,'Dell XPS 15',1,79999.00,79999.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(7,7,107,'Logitech MX Master 3S',4,999.00,3996.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(8,8,108,'iPad Air M3',2,24999.00,49998.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(9,9,109,'Kindle Paperwhite',1,12999.00,12999.00,'2026-07-11 11:45:46','2026-07-11 11:45:46'),(10,10,110,'JBL Flip 7',5,799.00,3995.00,'2026-07-11 11:45:46','2026-07-11 11:45:46');
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(100) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
INSERT INTO `carts` VALUES (1,'user-1001','2026-07-11 11:45:46','2026-07-11 11:45:46'),(2,'user-1002','2026-07-11 11:45:46','2026-07-11 11:45:46'),(3,'user-1003','2026-07-11 11:45:46','2026-07-11 11:45:46'),(4,'user-1004','2026-07-11 11:45:46','2026-07-11 11:45:46'),(5,'user-1005','2026-07-11 11:45:46','2026-07-11 11:45:46'),(6,'user-1006','2026-07-11 11:45:46','2026-07-11 11:45:46'),(7,'user-1007','2026-07-11 11:45:46','2026-07-11 11:45:46'),(8,'user-1008','2026-07-11 11:45:46','2026-07-11 11:45:46'),(9,'user-1009','2026-07-11 11:45:46','2026-07-11 11:45:46'),(10,'user-1010','2026-07-11 11:45:46','2026-07-11 11:45:46'),(18,'564fe970-3e98-4628-8b3f-bd1d668b5595','2026-07-11 18:25:54','2026-07-11 18:25:54'),(19,'1814b14e-1986-4bdf-a03d-c55a182a3861','2026-07-12 17:33:12','2026-07-12 17:33:12'),(27,'301f4e17-6208-4845-8c82-07bf6ea473d0','2026-07-16 09:15:03','2026-07-16 09:15:03'),(28,'83a0c8b2-104c-4fba-8460-36ae6050e400','2026-07-19 08:02:50','2026-07-19 08:02:50'),(29,'d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150','2026-07-21 18:22:20','2026-07-21 18:22:20');
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create cart tables','SQL','V1__create_cart_tables.sql',-1326052449,'root','2026-07-11 06:15:46',26,1),(2,'2','insert sample cart data','SQL','V2__insert_sample_cart_data.sql',790878262,'root','2026-07-11 06:15:46',2,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'cart_db'
--

--
-- Dumping routines for database 'cart_db'
--

--
-- Current Database: `orderdb`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `orderdb` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `orderdb`;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create order tables','SQL','V1__create_order_tables.sql',-1829555169,'root','2026-07-10 06:50:55',38,1),(2,'2','insert sample orders','SQL','V2__insert_sample_orders.sql',-263172522,'root','2026-07-10 06:50:55',1,1),(3,'3','alter customer id to varchar','SQL','V3__alter_customer_id_to_varchar.sql',303449687,'root','2026-07-11 09:57:45',31,1),(4,'4','update order status for saga','SQL','V4__update_order_status_for_saga.sql',1974386920,'root','2026-07-15 12:37:56',15,1),(5,'6','create processed events','SQL','V6__create_processed_events.sql',1552723995,'root','2026-07-15 15:41:29',12,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `quantity` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_items_order_id` (`order_id`),
  KEY `idx_order_items_product_id` (`product_id`),
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,'Apple iPhone 16',999.99,2,1999.98),(2,1,5,'20W USB-C Charger',499.99,1,499.99),(3,2,2,'Samsung Galaxy S25',899.99,2,1799.98),(4,3,3,'Sony WH-1000XM6',999.99,1,999.99),(5,4,1,'Apple iPhone 16',79999.00,2,159998.00),(6,5,2,'Samsung Galaxy S25',74999.00,2,149998.00),(7,6,2,'Samsung Galaxy S25',74999.00,2,149998.00),(8,7,1,'Apple iPhone 16',79999.00,1,79999.00),(9,7,2,'Samsung Galaxy S25',74999.00,1,74999.00),(10,8,5,'Adidas Ultraboost 5',14999.00,1,14999.00),(11,9,1,'Apple iPhone 16',79999.00,5,399995.00),(12,9,3,'Sony WH-1000XM6',34999.00,5,174995.00),(13,9,4,'Nike Air Max 270',12999.00,2,25998.00),(14,10,5,'Adidas Ultraboost 5',14999.00,5,74995.00),(15,11,5,'Adidas Ultraboost 5',14999.00,5,74995.00),(16,12,1,'Apple iPhone 16',79999.00,5,399995.00),(17,13,3,'Sony WH-1000XM6',34999.00,3,104997.00),(18,13,5,'Adidas Ultraboost 5',14999.00,7,104993.00),(19,14,2,'Samsung Galaxy S25',74999.00,2,149998.00),(20,14,3,'Sony WH-1000XM6',34999.00,1,34999.00),(21,15,3,'Sony WH-1000XM6',34999.00,1,34999.00),(22,16,3,'Sony WH-1000XM6',34999.00,3,104997.00),(23,16,2,'Samsung Galaxy S25',74999.00,2,149998.00),(24,17,2,'Samsung Galaxy S25',74999.00,1,74999.00),(25,18,2,'Samsung Galaxy S25',74999.00,1,74999.00),(26,19,2,'Samsung Galaxy S25',74999.00,1,74999.00),(27,20,1,'Apple iPhone 16',79999.00,1,79999.00),(28,21,3,'Sony WH-1000XM6',34999.00,1,34999.00),(29,22,2,'Samsung Galaxy S25',74999.00,1,74999.00),(30,23,3,'Sony WH-1000XM6',34999.00,1,34999.00),(31,24,3,'Sony WH-1000XM6',34999.00,1,34999.00),(32,25,3,'Sony WH-1000XM6',34999.00,1,34999.00),(33,26,2,'Samsung Galaxy S25',74999.00,1,74999.00),(34,27,2,'Samsung Galaxy S25',74999.00,1,74999.00),(35,28,2,'Samsung Galaxy S25',74999.00,1,74999.00),(36,29,3,'Sony WH-1000XM6',34999.00,1,34999.00),(37,30,3,'Sony WH-1000XM6',34999.00,1,34999.00),(38,31,3,'Sony WH-1000XM6',34999.00,1,34999.00),(39,32,4,'Nike Air Max 270',12999.00,1,12999.00),(40,33,2,'Samsung Galaxy S25',74999.00,1,74999.00),(41,34,1,'new phone',2.00,1,2.00),(42,35,1,'new phone',2.00,1,2.00),(43,36,1,'new phone',2.00,1,2.00),(44,37,1,'new phone',2.00,1,2.00),(45,38,1,'new phone',2.00,1,2.00),(46,39,1,'new phone',2.00,1,2.00),(47,40,1,'new phone',2.00,1,2.00),(48,41,1,'new phone',2.00,1,2.00),(49,42,1,'new phone',2.00,1,2.00),(50,43,1,'new phone',2.00,1,2.00),(51,44,3,'Sony WH-1000XM6',34999.00,1,34999.00),(52,45,4,'Nike Air Max 270',12999.00,1,12999.00),(53,46,1,'new phone',2.00,3,6.00),(54,47,1,'new phone',2.00,4,8.00),(55,48,1,'new phone',2.00,4,8.00),(56,48,20,'keyboard6',500.00,1,500.00),(57,49,1,'new phone',2.00,4,8.00),(58,49,20,'keyboard6',500.00,1,500.00),(59,49,19,'keyboard5',500.00,100,50000.00),(60,50,1,'new phone',2.00,4,8.00),(61,50,20,'keyboard6',500.00,1,500.00),(62,50,19,'keyboard5',500.00,101,50500.00),(63,51,18,'keyboard4',500.00,1,500.00),(64,52,18,'keyboard4',500.00,1,500.00),(65,53,18,'keyboard4',500.00,200,100000.00),(66,54,5,'Adidas Ultraboost 5',14999.00,1,14999.00),(67,55,5,'Adidas Ultraboost 5',14999.00,1,14999.00),(68,56,3,'Sony WH-1000XM6',34999.00,1,34999.00),(69,57,3,'Sony WH-1000XM6',34999.00,1,34999.00),(70,58,18,'keyboard4',500.00,201,100500.00),(71,59,1,'new phone',2.00,1,2.00),(72,60,3,'Sony WH-1000XM6',34999.00,1,34999.00),(73,61,3,'Sony WH-1000XM6',34999.00,1,34999.00),(74,62,3,'Sony WH-1000XM6',34999.00,1,34999.00),(75,63,3,'Sony WH-1000XM6',34999.00,1,34999.00),(76,64,2,'new phone',123.00,1,123.00),(77,65,3,'Sony WH-1000XM6',34999.00,1,34999.00),(78,66,3,'Sony WH-1000XM6',34999.00,1,34999.00),(79,67,3,'Sony WH-1000XM6',34999.00,1,34999.00),(80,68,3,'Sony WH-1000XM6',34999.00,1,34999.00),(81,69,3,'Sony WH-1000XM6',34999.00,1,34999.00),(82,70,3,'Sony WH-1000XM6',34999.00,1,34999.00),(83,71,3,'Sony WH-1000XM6',34999.00,1,34999.00),(84,72,4,'Nike Air Max 270',12999.00,1,12999.00),(85,73,4,'Nike Air Max 270',12999.00,1,12999.00),(86,74,1,'new phone',2.00,1,2.00),(87,75,1,'new phone',2.00,1,2.00),(88,76,3,'Sony WH-1000XM6',34999.00,1,34999.00),(89,77,4,'Nike Air Max 270',12999.00,1,12999.00),(90,78,4,'Nike Air Max 270',12999.00,1,12999.00),(91,79,4,'Nike Air Max 270',12999.00,1,12999.00),(92,80,3,'Sony WH-1000XM6',34999.00,1,34999.00),(93,81,3,'Sony WH-1000XM6',34999.00,1,34999.00),(94,82,2,'new phone',123.00,1,123.00),(95,83,3,'Sony WH-1000XM6',34999.00,1,34999.00),(96,84,3,'Sony WH-1000XM6',34999.00,1,34999.00),(97,84,81,'iPhone 16',79999.00,1,79999.00),(98,85,82,'iPhone 16 Pro',124999.00,1,124999.00),(99,86,4,'Nike Air Max 270',12999.00,1,12999.00),(100,87,82,'iPhone 16 Pro',124999.00,1,124999.00),(101,88,82,'iPhone 16 Pro',124999.00,1,124999.00),(102,89,82,'iPhone 16 Pro',124999.00,1,124999.00),(103,90,81,'iPhone 16',79999.00,1,79999.00),(104,91,4,'Nike Air Max 270',12999.00,1,12999.00),(105,92,4,'Nike Air Max 270',12999.00,1,12999.00),(106,93,4,'Nike Air Max 270',12999.00,1,12999.00),(107,94,4,'Nike Air Max 270',12999.00,1,12999.00),(108,95,81,'iPhone 16',79999.00,2,159998.00),(109,96,81,'iPhone 16',79999.00,1,79999.00),(110,97,81,'iPhone 16',79999.00,1,79999.00),(111,98,82,'iPhone 16 Pro',124999.00,1,124999.00),(112,99,82,'iPhone 16 Pro',124999.00,1,124999.00),(113,100,4,'Nike Air Max 270',12999.00,1,12999.00),(114,101,4,'Nike Air Max 270',12999.00,1,12999.00),(115,102,82,'iPhone 16 Pro',124999.00,1,124999.00),(116,103,81,'iPhone 16',79999.00,5,399995.00),(117,104,81,'iPhone 16',79999.00,1,79999.00),(118,105,81,'iPhone 16',79999.00,2,159998.00),(119,106,81,'iPhone 16',79999.00,1,79999.00),(120,107,81,'iPhone 16',79999.00,1,79999.00),(121,108,81,'iPhone 16',79999.00,1,79999.00),(122,109,81,'iPhone 16',79999.00,2,159998.00),(123,110,82,'iPhone 16 Pro',124999.00,1,124999.00),(124,111,81,'iPhone 16',79999.00,1,79999.00),(125,112,82,'iPhone 16 Pro',124999.00,1,124999.00),(126,113,82,'iPhone 16 Pro',124999.00,2,249998.00),(127,114,81,'iPhone 16',79999.00,1,79999.00),(128,115,81,'iPhone 16',79999.00,1,79999.00),(129,116,81,'iPhone 16',79999.00,1,79999.00),(130,117,82,'iPhone 16 Pro',124999.00,1,124999.00),(131,118,81,'iPhone 16',79999.00,1,79999.00),(132,119,82,'iPhone 16 Pro',124999.00,1,124999.00),(133,120,82,'iPhone 16 Pro',124999.00,1,124999.00),(134,121,82,'iPhone 16 Pro',124999.00,1,124999.00),(135,122,82,'iPhone 16 Pro',124999.00,1,124999.00),(136,123,81,'iPhone 16',79999.00,1,79999.00),(137,124,82,'iPhone 16 Pro',124999.00,1,124999.00),(138,125,82,'iPhone 16 Pro',124999.00,1,124999.00),(139,126,82,'iPhone 16 Pro',124999.00,1,124999.00),(140,127,82,'iPhone 16 Pro',124999.00,1,124999.00),(141,128,82,'iPhone 16 Pro',124999.00,1,124999.00),(142,129,82,'iPhone 16 Pro',124999.00,1,124999.00),(143,130,82,'iPhone 16 Pro',124999.00,1,124999.00),(144,131,82,'iPhone 16 Pro',124999.00,1,124999.00),(145,132,82,'iPhone 16 Pro',124999.00,1,124999.00),(146,133,82,'iPhone 16 Pro',124999.00,1,124999.00),(147,134,82,'iPhone 16 Pro',124999.00,1,124999.00),(148,135,82,'iPhone 16 Pro',124999.00,1,124999.00),(149,136,82,'iPhone 16 Pro',124999.00,1,124999.00),(150,136,81,'iPhone 16',79999.00,1,79999.00),(151,137,81,'iPhone 16',79999.00,1,79999.00),(152,138,81,'iPhone 16',79999.00,2,159998.00),(153,139,82,'iPhone 16 Pro',124999.00,1,124999.00),(154,140,81,'iPhone 16',79999.00,1,79999.00),(155,141,81,'iPhone 16',79999.00,1,79999.00),(156,142,81,'iPhone 16',79999.00,1,79999.00),(157,143,81,'iPhone 16',79999.00,1,79999.00),(158,144,81,'iPhone 16',79999.00,5,399995.00),(159,144,5,'Adidas Ultraboost 5',14999.00,1,14999.00),(160,145,5,'Adidas Ultraboost 5',14999.00,3,44997.00),(161,146,81,'iPhone 16',79999.00,1,79999.00),(162,147,81,'iPhone 16',79999.00,1,79999.00),(163,148,81,'iPhone 16',79999.00,1,79999.00),(164,149,81,'iPhone 16',79999.00,1,79999.00),(165,150,82,'iPhone 16 Pro',124999.00,1,124999.00),(166,151,82,'iPhone 16 Pro',124999.00,1,124999.00),(167,152,82,'iPhone 16 Pro',124999.00,1,124999.00),(168,153,81,'iPhone 16',79999.00,1,79999.00),(169,154,5,'Adidas Ultraboost 5',14999.00,2,29998.00),(170,155,81,'iPhone 16',79999.00,1,79999.00),(171,156,81,'iPhone 16',79999.00,2,159998.00),(172,157,82,'iPhone 16 Pro',124999.00,1,124999.00),(173,158,82,'iPhone 16 Pro',124999.00,1,124999.00),(174,159,81,'iPhone 16',79999.00,1,79999.00),(175,160,82,'iPhone 16 Pro',124999.00,1,124999.00),(176,161,82,'iPhone 16 Pro',124999.00,1,124999.00),(177,162,82,'iPhone 16 Pro',124999.00,1,124999.00),(178,163,82,'iPhone 16 Pro',124999.00,1,124999.00),(179,164,82,'iPhone 16 Pro',124999.00,1,124999.00),(180,165,82,'iPhone 16 Pro',124999.00,1,124999.00),(181,165,81,'iPhone 16',79999.00,1,79999.00),(182,166,81,'iPhone 16',79999.00,1,79999.00),(183,167,81,'iPhone 16',79999.00,1,79999.00),(184,168,82,'iPhone 16 Pro',124999.00,1,124999.00),(185,169,82,'iPhone 16 Pro',124999.00,1,124999.00),(186,170,82,'iPhone 16 Pro',124999.00,1,124999.00),(187,171,82,'iPhone 16 Pro',124999.00,1,124999.00),(188,172,4,'Nike Air Max 270',12999.00,1,12999.00),(189,173,82,'iPhone 16 Pro',124999.00,1,124999.00),(190,174,82,'iPhone 16 Pro',124999.00,1,124999.00),(191,174,81,'iPhone 16',79999.00,1,79999.00),(192,175,82,'iPhone 16 Pro',124999.00,1,124999.00),(193,176,4,'Nike Air Max 270',12999.00,1,12999.00),(194,177,82,'iPhone 16 Pro',124999.00,1,124999.00),(195,178,5,'Adidas Ultraboost 5',14999.00,1,14999.00),(196,179,81,'iPhone 16',79999.00,1,79999.00),(197,180,82,'iPhone 16 Pro',124999.00,1,124999.00),(198,181,82,'iPhone 16 Pro',124999.00,1,124999.00),(199,181,81,'iPhone 16',79999.00,1,79999.00);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_number` varchar(50) NOT NULL,
  `customer_id` varchar(100) NOT NULL,
  `status` varchar(50) NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `idx_orders_order_number` (`order_number`),
  KEY `idx_orders_customer_id` (`customer_id`),
  KEY `idx_orders_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=182 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,'ORD-20260710-00001','101','CONFIRMED',2499.97,'2026-07-10 06:50:55','2026-07-10 06:50:55'),(2,'ORD-20260710-00002','102','CONFIRMED',1799.98,'2026-07-10 06:50:55','2026-07-10 06:50:55'),(3,'ORD-20260710-00003','103','CONFIRMED',999.99,'2026-07-10 06:50:55','2026-07-10 06:50:55'),(4,'db4fd54a-09b6-4f01-aba2-debcf7774377','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',159998.00,'2026-07-11 04:30:37','2026-07-11 05:56:57'),(5,'8c9c70b1-5046-4285-b558-947ecbf06a2d','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',149998.00,'2026-07-11 04:31:48','2026-07-11 05:57:10'),(6,'e2654b3b-8e15-4838-ac85-0d67f13f6618','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',149998.00,'2026-07-11 04:32:15','2026-07-11 05:56:29'),(7,'ORD-CC33F328CD77','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',154998.00,'2026-07-11 05:18:30','2026-07-11 05:45:18'),(8,'ORD-4DCBE5EFC777','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',14999.00,'2026-07-11 05:19:37','2026-07-11 05:54:39'),(9,'ORD-D7151B412629','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',600988.00,'2026-07-11 05:52:36','2026-07-11 05:53:34'),(10,'ORD-3A0E7BD8664F','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74995.00,'2026-07-11 06:30:56','2026-07-11 06:31:24'),(11,'ORD-B5F8427805BB','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74995.00,'2026-07-11 08:34:20','2026-07-11 08:36:36'),(12,'ORD-55AF5C734BC7','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',399995.00,'2026-07-11 09:01:00','2026-07-11 09:26:02'),(13,'ORD-35EF0A067244','564fe970-3e98-4628-8b3f-bd1d668b5595','CONFIRMED',209990.00,'2026-07-12 08:07:25','2026-07-12 08:43:31'),(14,'ORD-670740F44E03','564fe970-3e98-4628-8b3f-bd1d668b5595','CONFIRMED',184997.00,'2026-07-12 08:23:14','2026-07-12 08:44:07'),(15,'ORD-513A86CF8FB9','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-12 12:05:54','2026-07-12 12:05:57'),(16,'ORD-34759ECEFDE7','564fe970-3e98-4628-8b3f-bd1d668b5595','CONFIRMED',254995.00,'2026-07-12 12:09:17','2026-07-12 12:09:17'),(17,'ORD-BEF86046FAEC','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74999.00,'2026-07-12 12:29:05','2026-07-12 12:29:08'),(18,'ORD-3EB68A2C42BC','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74999.00,'2026-07-12 12:32:42','2026-07-12 12:32:45'),(19,'ORD-14685090F16C','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74999.00,'2026-07-12 12:38:42','2026-07-12 12:38:45'),(20,'ORD-613D1DF9E014','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',79999.00,'2026-07-12 12:47:08','2026-07-12 12:47:10'),(21,'ORD-474761B1D9D8','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-13 00:33:47','2026-07-13 00:33:50'),(22,'ORD-271C2CD8FD4F','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74999.00,'2026-07-13 00:44:19','2026-07-13 00:44:22'),(23,'ORD-BDF98590B188','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-13 01:00:17','2026-07-13 01:00:20'),(24,'ORD-7323980B6280','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',34999.00,'2026-07-13 01:04:43','2026-07-13 01:04:45'),(25,'ORD-C43239162727','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',34999.00,'2026-07-13 01:07:03','2026-07-13 01:07:05'),(26,'ORD-92203C5003F6','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',74999.00,'2026-07-13 01:11:26','2026-07-13 01:11:28'),(27,'ORD-906806235C3D','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',74999.00,'2026-07-13 01:19:20','2026-07-13 01:19:23'),(28,'ORD-3CD5B47DC9D9','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74999.00,'2026-07-13 01:21:54','2026-07-13 01:21:56'),(29,'ORD-93A239DCCAB3','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-13 01:24:06','2026-07-13 01:24:08'),(30,'ORD-A13B1E5FC504','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-13 01:26:32','2026-07-13 01:26:34'),(31,'ORD-F8494824E217','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-13 01:51:12','2026-07-13 01:51:15'),(32,'ORD-5094A8FEEE46','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-13 02:03:43','2026-07-13 02:03:45'),(33,'ORD-7865063D4E7A','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',74999.00,'2026-07-13 23:10:26','2026-07-13 23:10:28'),(34,'ORD-043C649B76C9','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 10:56:02','2026-07-14 10:56:02'),(35,'ORD-508C80BB4853','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:05:18','2026-07-14 11:05:18'),(36,'ORD-2AA67588BE24','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:23:33','2026-07-14 11:23:33'),(37,'ORD-25DE03722730','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:23:49','2026-07-14 11:23:49'),(38,'ORD-D8D80C367D51','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:24:12','2026-07-14 11:24:12'),(39,'ORD-1A52400F9745','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:25:25','2026-07-14 11:25:25'),(40,'ORD-D329DA404B47','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:25:29','2026-07-14 11:25:29'),(41,'ORD-82D50924EA39','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:25:33','2026-07-14 11:25:33'),(42,'ORD-981FBFD732A2','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:25:36','2026-07-14 11:25:36'),(43,'ORD-E806EFBB192C','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-14 11:42:37','2026-07-14 11:42:40'),(44,'ORD-21042E4E52F2','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-14 11:43:07','2026-07-14 11:43:09'),(45,'ORD-C1BE64FCC1B4','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-14 12:00:56','2026-07-14 12:00:59'),(46,'ORD-D10683E0527E','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',6.00,'2026-07-15 07:21:55','2026-07-15 07:21:55'),(47,'ORD-339F0952BD4F','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',8.00,'2026-07-15 08:24:41','2026-07-15 08:24:41'),(48,'ORD-7279D78F727D','108fa683-146b-4a63-ba63-0842c967c495','INVENTORY_RESERVED',508.00,'2026-07-15 10:14:48','2026-07-15 10:14:48'),(49,'ORD-93F1D0D1D14C','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',50508.00,'2026-07-15 10:18:54','2026-07-15 10:18:54'),(50,'ORD-A28B66B7506E','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',51008.00,'2026-07-15 10:24:08','2026-07-15 10:24:08'),(51,'ORD-5ECF427325A2','108fa683-146b-4a63-ba63-0842c967c495','INVENTORY_RESERVED',500.00,'2026-07-15 10:27:19','2026-07-15 10:27:19'),(52,'ORD-37E5A53005AF','108fa683-146b-4a63-ba63-0842c967c495','INVENTORY_RESERVED',500.00,'2026-07-15 10:28:12','2026-07-15 10:28:12'),(53,'ORD-5889E743A6F8','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',100000.00,'2026-07-15 10:29:22','2026-07-15 10:29:22'),(54,'ORD-323A072C551F','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',14999.00,'2026-07-15 11:24:53','2026-07-15 11:24:54'),(55,'ORD-CC6E9C3F17A2','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',14999.00,'2026-07-15 11:25:04','2026-07-15 11:25:04'),(56,'ORD-7BF86CC014BB','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',34999.00,'2026-07-15 11:27:48','2026-07-15 11:27:48'),(57,'ORD-855DF6AC33BB','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',34999.00,'2026-07-15 11:32:49','2026-07-15 11:32:49'),(58,'ORD-7F9B42BCFFBC','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',100500.00,'2026-07-15 11:46:24','2026-07-15 11:46:24'),(59,'ORD-1E52A90863E0','108fa683-146b-4a63-ba63-0842c967c495','CONFIRMED',2.00,'2026-07-15 11:48:22','2026-07-15 11:48:22'),(60,'ORD-633BB48B47CD','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-15 11:49:58','2026-07-15 11:49:58'),(61,'ORD-630E1793A6D3','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 03:45:10','2026-07-16 03:45:10'),(62,'ORD-18BE9B6B9B86','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',34999.00,'2026-07-16 03:48:06','2026-07-16 03:48:06'),(63,'ORD-317446A103EA','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',34999.00,'2026-07-16 03:48:42','2026-07-16 03:48:42'),(64,'ORD-1F627535F655','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',123.00,'2026-07-16 03:50:23','2026-07-16 03:50:23'),(65,'ORD-50B672EFE811','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 03:54:03','2026-07-16 03:54:10'),(66,'ORD-70A7588EC814','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 03:57:13','2026-07-16 06:37:39'),(67,'ORD-B1F3F319FB83','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 03:59:34','2026-07-16 06:37:05'),(68,'ORD-ED4F2E246594','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 03:59:42','2026-07-16 03:59:46'),(69,'ORD-D1FE5C262D8B','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 04:01:09','2026-07-16 04:01:11'),(70,'ORD-A837BC7781CF','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 04:01:29','2026-07-16 06:36:28'),(71,'ORD-282C58418133','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-16 04:01:42','2026-07-16 04:01:49'),(72,'ORD-9864F11DF1A7','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-16 04:02:43','2026-07-16 04:02:49'),(73,'ORD-1B82146BE2EB','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-16 04:03:09','2026-07-16 04:03:11'),(74,'ORD-1F08163769E2','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-16 06:26:21','2026-07-16 06:26:25'),(75,'ORD-F1B3C7C34C6C','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',2.00,'2026-07-16 13:10:35','2026-07-16 13:10:38'),(76,'ORD-DFDEA5F539ED','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-17 00:23:55','2026-07-17 00:25:31'),(77,'ORD-8FBB605031FC','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-17 00:25:50','2026-07-17 00:26:07'),(78,'ORD-77FCB37B3379','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-17 00:32:37','2026-07-17 00:32:40'),(79,'ORD-DE2C95B4FEDB','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-17 00:43:05','2026-07-17 00:43:08'),(80,'ORD-0581A89BA3EE','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-17 01:48:53','2026-07-17 01:49:14'),(81,'ORD-AC80B73E1F12','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',34999.00,'2026-07-17 04:19:31','2026-07-17 04:19:51'),(82,'ORD-1FA678FF4130','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',123.00,'2026-07-17 06:18:22','2026-07-17 06:18:25'),(83,'ORD-B65E5F4CBED8','1814b14e-1986-4bdf-a03d-c55a182a3861','PAYMENT_PENDING',34999.00,'2026-07-18 10:59:50','2026-07-18 10:59:50'),(84,'ORD-AEE850F4B980','1814b14e-1986-4bdf-a03d-c55a182a3861','PENDING',114998.00,'2026-07-18 11:05:05','2026-07-18 11:05:05'),(85,'ORD-C94429F3FEBD','1814b14e-1986-4bdf-a03d-c55a182a3861','PENDING',124999.00,'2026-07-18 11:05:55','2026-07-18 11:05:55'),(86,'ORD-424EA85437CC','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-18 11:08:43','2026-07-18 11:08:47'),(87,'ORD-36E2BD6BD23D','1814b14e-1986-4bdf-a03d-c55a182a3861','PENDING',124999.00,'2026-07-18 11:09:00','2026-07-18 11:09:00'),(88,'ORD-09FB4B2422A8','1814b14e-1986-4bdf-a03d-c55a182a3861','PENDING',124999.00,'2026-07-18 11:09:52','2026-07-18 11:09:52'),(89,'ORD-188FA1381B42','301f4e17-6208-4845-8c82-07bf6ea473d0','PENDING',124999.00,'2026-07-18 11:10:56','2026-07-18 11:10:56'),(90,'ORD-EA43DEF197F6','301f4e17-6208-4845-8c82-07bf6ea473d0','PENDING',79999.00,'2026-07-18 11:11:14','2026-07-18 11:11:14'),(91,'ORD-D37FE44B48F2','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-18 11:14:00','2026-07-18 11:14:05'),(92,'ORD-56B12A49EB8C','1814b14e-1986-4bdf-a03d-c55a182a3861','CONFIRMED',12999.00,'2026-07-18 11:14:43','2026-07-18 11:16:46'),(93,'ORD-35F587F47729','1814b14e-1986-4bdf-a03d-c55a182a3861','CONFIRMED',12999.00,'2026-07-18 11:18:35','2026-07-18 11:18:38'),(94,'ORD-F1A3E7B6CB19','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-18 11:18:59','2026-07-18 11:19:02'),(95,'ORD-15F2615AA1C1','301f4e17-6208-4845-8c82-07bf6ea473d0','PENDING',159998.00,'2026-07-18 11:47:45','2026-07-18 11:47:45'),(96,'ORD-A38C2FDBCF72','301f4e17-6208-4845-8c82-07bf6ea473d0','PENDING',79999.00,'2026-07-18 11:48:17','2026-07-18 11:48:17'),(97,'ORD-4B8FBF789129','301f4e17-6208-4845-8c82-07bf6ea473d0','PENDING',79999.00,'2026-07-18 11:52:10','2026-07-18 11:52:10'),(98,'ORD-1B8FA22F3AB7','301f4e17-6208-4845-8c82-07bf6ea473d0','PENDING',124999.00,'2026-07-18 11:52:29','2026-07-18 11:52:29'),(99,'ORD-956BFE67FA23','301f4e17-6208-4845-8c82-07bf6ea473d0','PENDING',124999.00,'2026-07-18 11:53:35','2026-07-18 11:53:35'),(100,'ORD-68B23413CE9A','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-18 11:53:51','2026-07-18 11:53:55'),(101,'ORD-AEAD45C896A6','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-18 11:59:51','2026-07-18 11:59:53'),(102,'ORD-AC43CD420EA1','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-18 12:07:02','2026-07-18 12:07:06'),(103,'ORD-5CC02AB8F686','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',399995.00,'2026-07-18 12:07:38','2026-07-18 12:07:41'),(104,'ORD-B95AE43555BD','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',79999.00,'2026-07-18 12:27:35','2026-07-18 12:27:38'),(105,'ORD-45AE1F914C56','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',159998.00,'2026-07-19 00:57:59','2026-07-19 00:58:02'),(106,'ORD-E0060CD0C0E7','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',79999.00,'2026-07-19 01:31:43','2026-07-19 01:31:45'),(107,'ORD-A435EC318EC4','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 02:32:53','2026-07-19 02:32:56'),(108,'ORD-7394F65B7865','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 02:39:14','2026-07-19 02:39:17'),(109,'ORD-6F70D20183B5','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',159998.00,'2026-07-19 02:41:42','2026-07-19 02:41:45'),(110,'ORD-0DD2E1357163','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 02:41:54','2026-07-19 02:42:00'),(111,'ORD-B96B7DCB61DF','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 02:47:20','2026-07-19 02:47:27'),(112,'ORD-52E3D879E66C','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 02:48:32','2026-07-19 02:48:35'),(113,'ORD-755DF1D340C5','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',249998.00,'2026-07-19 03:03:07','2026-07-19 03:03:10'),(114,'ORD-90F575DF10C3','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 03:49:39','2026-07-19 04:07:28'),(115,'ORD-6575A9CD0200','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 04:03:12','2026-07-19 04:07:17'),(116,'ORD-0A39208BF974','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 04:03:41','2026-07-19 04:06:48'),(117,'ORD-8EA7D9CFBD97','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 04:06:57','2026-07-19 04:06:59'),(118,'ORD-44800D98FB47','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 04:28:04','2026-07-19 04:28:43'),(119,'ORD-C354CA156E8F','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 04:28:52','2026-07-19 04:28:54'),(120,'ORD-77C863421068','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 04:33:46','2026-07-19 04:33:49'),(121,'ORD-22C19D6B3EE8','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 04:38:52','2026-07-19 04:38:55'),(122,'ORD-A0991621EAE7','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 04:39:48','2026-07-19 04:39:51'),(123,'ORD-96BE40DDCAB1','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 05:04:22','2026-07-19 05:04:25'),(124,'ORD-7E3BEC4E48F9','83a0c8b2-104c-4fba-8460-36ae6050e400','CANCELLED',124999.00,'2026-07-19 05:04:31','2026-07-19 05:04:34'),(125,'ORD-D5107B25B238','83a0c8b2-104c-4fba-8460-36ae6050e400','CANCELLED',124999.00,'2026-07-19 05:05:16','2026-07-19 05:05:46'),(126,'ORD-ADD6F56113CE','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:05:59','2026-07-19 05:06:02'),(127,'ORD-95AEACF71FC3','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:08:56','2026-07-19 05:44:49'),(128,'ORD-D96491F71E3A','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:27:10','2026-07-19 05:44:08'),(129,'ORD-6B8CDA13584A','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:28:05','2026-07-19 05:44:37'),(130,'ORD-AA1E58E1551E','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:28:49','2026-07-19 05:44:25'),(131,'ORD-ABAD3DC4A438','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:31:31','2026-07-19 05:43:20'),(132,'ORD-FEE617F2F0F6','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:34:13','2026-07-19 05:43:28'),(133,'ORD-04BA12D2BBA2','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:35:58','2026-07-19 05:43:13'),(134,'ORD-047B5B901BEC','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:38:56','2026-07-19 05:43:05'),(135,'ORD-3E16EE82C868','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 05:40:18','2026-07-19 05:42:59'),(136,'ORD-BFF8F9DCE360','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',204998.00,'2026-07-19 05:42:38','2026-07-19 05:42:52'),(137,'ORD-97F06CD317B1','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 06:00:05','2026-07-19 06:00:10'),(138,'ORD-099269AD39E1','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',159998.00,'2026-07-19 06:00:19','2026-07-19 06:00:23'),(139,'ORD-AEED13BB9468','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-19 06:00:31','2026-07-19 06:00:42'),(140,'ORD-F84B8EAB50BD','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 07:37:29','2026-07-19 08:34:18'),(141,'ORD-D12EA0F9B4F0','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 08:41:10','2026-07-19 08:41:12'),(142,'ORD-B93AD5647AE9','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 10:57:02','2026-07-19 10:57:05'),(143,'ORD-4ADE760CED9F','83a0c8b2-104c-4fba-8460-36ae6050e400','PAYMENT_PENDING',79999.00,'2026-07-19 11:02:17','2026-07-19 11:02:17'),(144,'ORD-A614B11D9090','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',414994.00,'2026-07-19 11:03:29','2026-07-19 11:04:16'),(145,'ORD-F26FE28CFC22','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',44997.00,'2026-07-19 11:04:45','2026-07-19 11:04:49'),(146,'ORD-E0D8579186E0','83a0c8b2-104c-4fba-8460-36ae6050e400','PAYMENT_PENDING',79999.00,'2026-07-19 12:49:42','2026-07-19 12:49:42'),(147,'ORD-7CAB4C82B0C0','83a0c8b2-104c-4fba-8460-36ae6050e400','PAYMENT_PENDING',79999.00,'2026-07-19 12:49:51','2026-07-19 12:49:51'),(148,'ORD-43630D764EC2','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 12:52:07','2026-07-19 12:54:37'),(149,'ORD-8B579344DAEB','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',79999.00,'2026-07-19 12:54:44','2026-07-19 12:54:50'),(150,'ORD-6C614269D934','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-20 10:31:47','2026-07-20 10:31:50'),(151,'ORD-D5C74BA3C0A8','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',124999.00,'2026-07-20 10:33:00','2026-07-20 10:33:02'),(152,'ORD-1DFD17856B65','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-20 12:51:32','2026-07-20 12:51:35'),(153,'ORD-B8881FDBDBED','1814b14e-1986-4bdf-a03d-c55a182a3861','CONFIRMED',79999.00,'2026-07-21 12:22:43','2026-07-21 12:22:47'),(154,'ORD-11C42C1BDF0B','1814b14e-1986-4bdf-a03d-c55a182a3861','CONFIRMED',29998.00,'2026-07-21 12:28:15','2026-07-21 12:28:18'),(155,'ORD-CD2C3ED6692C','1814b14e-1986-4bdf-a03d-c55a182a3861','CONFIRMED',79999.00,'2026-07-21 12:32:29','2026-07-21 12:32:32'),(156,'ORD-8BE95B073A4B','d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150','CONFIRMED',159998.00,'2026-07-21 12:52:23','2026-07-21 12:52:26'),(157,'ORD-28A14D6F69EF','d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150','CONFIRMED',124999.00,'2026-07-21 12:59:22','2026-07-21 12:59:27'),(158,'ORD-C34C63ECD91E','d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150','CONFIRMED',124999.00,'2026-07-21 13:00:40','2026-07-21 13:00:42'),(159,'ORD-88D7E7001C00','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',79999.00,'2026-07-21 22:34:36','2026-07-21 22:34:39'),(160,'ORD-F6EAF6AE7716','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-21 22:58:48','2026-07-21 22:58:51'),(161,'ORD-B9F89324CF74','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-21 22:59:50','2026-07-21 22:59:56'),(162,'ORD-D992ACB121A2','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-21 23:36:37','2026-07-21 23:36:40'),(163,'ORD-9453E356E05E','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-21 23:44:10','2026-07-21 23:44:13'),(164,'ORD-D76B89C4B595','301f4e17-6208-4845-8c82-07bf6ea473d0','PAYMENT_PENDING',124999.00,'2026-07-21 23:48:19','2026-07-21 23:48:19'),(165,'ORD-9F0FEAC8DE2F','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',204998.00,'2026-07-21 23:57:05','2026-07-21 23:57:08'),(166,'ORD-486FE66AA505','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',79999.00,'2026-07-21 23:59:55','2026-07-22 00:00:09'),(167,'ORD-32E7BFD4611D','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',79999.00,'2026-07-21 23:59:58','2026-07-22 00:00:00'),(168,'ORD-CA607ED8830A','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-22 00:22:23','2026-07-22 00:22:26'),(169,'ORD-0FF886CC8F86','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-22 00:24:52','2026-07-22 00:24:54'),(170,'ORD-C041B5208721','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-22 01:34:24','2026-07-22 01:34:28'),(171,'ORD-DD59764F1B01','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-22 01:48:09','2026-07-22 01:48:12'),(172,'ORD-537A53F4FC78','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-22 02:03:14','2026-07-22 02:03:17'),(173,'ORD-C5C9E6134E84','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-22 02:06:57','2026-07-22 02:06:59'),(174,'ORD-051CA5A2743A','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',204998.00,'2026-07-22 02:36:34','2026-07-22 02:36:37'),(175,'ORD-9FA1D2F1FE01','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-22 05:18:42','2026-07-22 05:18:45'),(176,'ORD-86CB3AF0CA1F','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',12999.00,'2026-07-22 07:54:35','2026-07-22 07:54:38'),(177,'ORD-214511A53F3D','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-22 08:01:44','2026-07-22 08:01:47'),(178,'ORD-7A943742A18C','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',14999.00,'2026-07-22 08:56:13','2026-07-22 08:56:17'),(179,'ORD-55B4C32E36DC','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',79999.00,'2026-07-23 09:48:59','2026-07-23 09:49:02'),(180,'ORD-0E4FDAA2B341','301f4e17-6208-4845-8c82-07bf6ea473d0','CONFIRMED',124999.00,'2026-07-24 12:44:24','2026-07-24 12:44:27'),(181,'ORD-6630781709DB','83a0c8b2-104c-4fba-8460-36ae6050e400','CONFIRMED',204998.00,'2026-07-25 08:18:08','2026-07-25 08:18:11');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processed_events`
--

DROP TABLE IF EXISTS `processed_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `processed_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` varchar(36) NOT NULL,
  `event_type` varchar(100) NOT NULL,
  `processed_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_processed_events_event_id` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=123 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processed_events`
--

LOCK TABLES `processed_events` WRITE;
/*!40000 ALTER TABLE `processed_events` DISABLE KEYS */;
INSERT INTO `processed_events` VALUES (1,'a0d435e5-c825-4cd2-8526-75fd811afcf2','INVENTORY_RESERVED','2026-07-15 15:44:48'),(2,'20ae9488-4bc9-4464-ad4f-afb43a682083','INVENTORY_RESERVED','2026-07-15 15:57:19'),(3,'acc76bb9-3ef3-4cb4-922a-c51638027980','INVENTORY_RESERVED','2026-07-15 15:58:12'),(4,'c20a4d11-a748-499c-b9a9-e708c76eb52b','INVENTORY_RESERVED','2026-07-15 16:54:54'),(5,'8ab37852-4696-4517-9eb2-704c0049427f','INVENTORY_RESERVED','2026-07-15 16:55:04'),(6,'481dc8e7-a906-47b7-a0be-4f39d61e529e','INVENTORY_RESERVED','2026-07-15 16:57:48'),(7,'42311d11-c629-4b70-b46b-626cd0a7c184','INVENTORY_RESERVED','2026-07-15 17:02:49'),(8,'5934fd3d-dc08-4965-8998-84ff2f8b3d82','INVENTORY_RESERVED','2026-07-15 17:18:22'),(9,'877cf4e7-5210-4350-a309-de47e5f52936','PAYMENT_COMPLETED','2026-07-15 17:18:22'),(10,'03040fad-0e1a-4e94-8f63-d3ce80fa048c','INVENTORY_RESERVED','2026-07-15 17:19:58'),(11,'c3998b52-1431-40c5-b589-ffe4dd9d8982','PAYMENT_COMPLETED','2026-07-15 17:19:58'),(12,'b5127a4b-7b0b-4249-bbca-f30d99533b55','INVENTORY_RESERVED','2026-07-16 09:15:10'),(13,'36901565-caec-4b6c-b1a1-48e39dc9ad84','PAYMENT_COMPLETED','2026-07-16 09:15:10'),(14,'35333ebd-285e-4cc6-a7b9-f7d4babbe911','INVENTORY_RESERVED','2026-07-16 09:18:06'),(15,'7e880cbf-2399-4180-a6e4-1a802b721975','INVENTORY_RESERVED','2026-07-16 09:18:42'),(16,'e2392e08-5617-48df-b0e6-54420f6824a7','INVENTORY_RESERVED','2026-07-16 09:20:23'),(17,'47924e82-5667-4506-9c08-63b374311105','INVENTORY_RESERVED','2026-07-16 09:24:03'),(18,'59fef237-329c-4497-87b8-45ea3a5daaed','INVENTORY_RESERVED','2026-07-16 09:27:13'),(19,'3fb6c50e-ba7a-4a0f-91f1-6d49cd46adf4','INVENTORY_RESERVED','2026-07-16 09:29:34'),(20,'9828824d-e741-4b82-bbd7-3426c786ae10','INVENTORY_RESERVED','2026-07-16 09:29:42'),(21,'30f74a85-de00-47cf-a1b5-8013d4091d87','INVENTORY_RESERVED','2026-07-16 09:31:09'),(22,'1b6b6d7f-761a-4bd8-a921-9fa8f7f8e263','INVENTORY_RESERVED','2026-07-16 09:31:29'),(23,'9dd7081d-d9eb-4de8-94c5-77810c78c7ec','INVENTORY_RESERVED','2026-07-16 09:31:42'),(24,'670a8d80-26b1-48d2-a330-eeff00ac18ef','INVENTORY_RESERVED','2026-07-16 09:32:43'),(25,'704c9ce5-1ebe-440e-b59d-3931ed2f662f','INVENTORY_RESERVED','2026-07-16 09:33:09'),(26,'ddd2c65e-37da-4ac1-be31-0f7ea588f191','INVENTORY_RESERVED','2026-07-16 11:56:21'),(27,'acbac271-acea-4332-974c-71d0eeb7b62d','INVENTORY_RESERVED','2026-07-16 18:40:35'),(28,'10cef2f3-bad7-495d-b795-bd22e796564e','INVENTORY_RESERVED','2026-07-17 05:53:55'),(29,'7955f103-9ffb-476a-a321-93ca662ff465','INVENTORY_RESERVED','2026-07-17 05:55:50'),(30,'caff4a27-f944-4ac9-aa7c-cdca297b6045','INVENTORY_RESERVED','2026-07-17 06:02:37'),(31,'0257efad-100e-47b6-806f-6a6a6f5c3e9f','INVENTORY_RESERVED','2026-07-17 06:13:05'),(32,'6e341ec9-68c8-4b21-b60a-198423d495af','INVENTORY_RESERVED','2026-07-17 07:18:53'),(33,'eb1c194d-4fe3-4563-91c2-84dc367d44f7','INVENTORY_RESERVED','2026-07-17 09:49:31'),(34,'9d6195aa-1022-4142-9429-ccacc0103ba8','INVENTORY_RESERVED','2026-07-17 11:48:22'),(35,'2ffaf133-b216-4ebf-af7a-264706ded20b','INVENTORY_RESERVED','2026-07-18 16:29:50'),(36,'2f38cf9d-f196-4e27-bec3-4dcc23577158','INVENTORY_RESERVED','2026-07-18 16:38:43'),(37,'5780a38e-efbb-4fae-b6ab-3b9f7745892b','INVENTORY_RESERVED','2026-07-18 16:44:00'),(38,'ca497e49-710f-4bb4-b94e-6349222f693e','INVENTORY_RESERVED','2026-07-18 16:44:43'),(39,'6cabb7cb-b27a-4c2a-bb86-cbb02e028147','INVENTORY_RESERVED','2026-07-18 16:48:35'),(40,'bf65263e-4bd6-41b6-86bd-57cf228bf643','INVENTORY_RESERVED','2026-07-18 16:48:59'),(41,'c8a3cc37-d21a-4f51-9a98-603596ab344a','INVENTORY_RESERVED','2026-07-18 17:23:52'),(42,'2d4d920c-b1e2-4ba0-9262-c3ff4249549c','INVENTORY_RESERVED','2026-07-18 17:29:51'),(43,'8aaabb1d-3ecb-43c4-b88f-838e7476f90c','INVENTORY_RESERVED','2026-07-18 17:37:03'),(44,'ba56858f-ce29-4f72-b5cf-891da92dc4b0','INVENTORY_RESERVED','2026-07-18 17:37:38'),(45,'6d441183-3bd5-460b-9ee8-8ccdec421108','INVENTORY_RESERVED','2026-07-18 17:57:35'),(46,'12ba77cd-a325-4ea9-9c75-89f7d9d1c66e','INVENTORY_RESERVED','2026-07-19 06:28:00'),(47,'e48d09e6-498d-46b2-991a-6f4e07e0066e','INVENTORY_RESERVED','2026-07-19 07:01:43'),(48,'a78ad87d-75e5-4d6a-b5f5-4d8f81217e83','INVENTORY_RESERVED','2026-07-19 08:02:53'),(49,'f666cdce-c269-4f3e-a157-b0bffe23e652','INVENTORY_RESERVED','2026-07-19 08:09:14'),(50,'5999cbd9-2964-476a-bb87-bae7242ccd24','INVENTORY_RESERVED','2026-07-19 08:11:42'),(51,'1590704e-d5b0-40b2-a9cd-9a5a54a3abfb','INVENTORY_RESERVED','2026-07-19 08:11:54'),(52,'020a492d-c458-4c06-bb74-5c5fb6c46318','INVENTORY_RESERVED','2026-07-19 08:17:20'),(53,'31b2aa45-c5d5-4ec6-80d9-2bd64ff81304','INVENTORY_RESERVED','2026-07-19 08:18:32'),(54,'70851f3b-0cab-492e-a3d6-cf45804f9bf0','INVENTORY_RESERVED','2026-07-19 08:33:07'),(55,'4cd92c1c-9deb-4ab9-977d-6e281b458e61','INVENTORY_RESERVED','2026-07-19 09:19:39'),(56,'df635eb6-6b09-4b7b-bedf-e793f567874c','INVENTORY_RESERVED','2026-07-19 09:33:12'),(57,'32a27627-b289-40c0-ada4-08eef9417e27','INVENTORY_RESERVED','2026-07-19 09:33:41'),(58,'beb67ddd-dd2c-4732-9d9e-2f97f98c6093','INVENTORY_RESERVED','2026-07-19 09:36:57'),(59,'f2ba877d-6cc2-4573-baa0-f9d45bbee399','INVENTORY_RESERVED','2026-07-19 09:58:04'),(60,'0ba8d6fd-d282-4aad-8843-1add2d097c7c','INVENTORY_RESERVED','2026-07-19 09:58:52'),(61,'e7590f36-4032-4fe3-82f2-4cff6a0125b8','INVENTORY_RESERVED','2026-07-19 10:03:46'),(62,'697a4edf-39a1-42d1-8367-3c0e6b124a39','INVENTORY_RESERVED','2026-07-19 10:08:52'),(63,'47d66cd9-8483-45e2-bad2-a1df697b48e4','INVENTORY_RESERVED','2026-07-19 10:09:48'),(64,'cc386246-3fe8-4e0d-ac57-1463e007c079','INVENTORY_RESERVED','2026-07-19 10:34:22'),(65,'65702f02-8a40-4f80-8ee1-5834e623045b','INVENTORY_RESERVED','2026-07-19 10:34:31'),(66,'5547e90d-f0b3-44c4-87b2-8fe9bbaf8bf6','INVENTORY_RESERVED','2026-07-19 10:35:16'),(67,'c6c3fb72-256a-489c-87fe-23d8dd5ed527','INVENTORY_RESERVED','2026-07-19 10:35:59'),(68,'eaf41b4e-134e-4875-9cd5-75748f4fdc53','INVENTORY_RESERVED','2026-07-19 10:38:56'),(69,'e31058ba-1ecd-42fd-9e56-0ddcd76028df','INVENTORY_RESERVED','2026-07-19 10:57:10'),(70,'46acf6c6-92e8-4c13-9f20-afb602cd88c0','INVENTORY_RESERVED','2026-07-19 10:58:05'),(71,'bb70faec-3b58-4ec2-a1ba-c383a1463b8a','INVENTORY_RESERVED','2026-07-19 10:58:49'),(72,'ae3036bd-7b33-4018-a164-258ad36b349e','INVENTORY_RESERVED','2026-07-19 11:01:31'),(73,'5a4ff58b-cb2e-4b3c-9460-c7a97c234be8','INVENTORY_RESERVED','2026-07-19 11:04:13'),(74,'1e90e37b-f23f-4c09-8314-7eda0eacad56','INVENTORY_RESERVED','2026-07-19 11:05:58'),(75,'15873d42-3e57-4a7e-a000-b3cea8a7c2c2','INVENTORY_RESERVED','2026-07-19 11:08:56'),(76,'8a90c5e9-623f-43b8-bff8-8fe4ccd3b52e','INVENTORY_RESERVED','2026-07-19 11:10:18'),(77,'e4c0ecad-8650-45f8-910b-48a679515682','INVENTORY_RESERVED','2026-07-19 11:12:39'),(78,'990791f3-19f1-4181-bc0f-708eabe833f9','INVENTORY_RESERVED','2026-07-19 11:30:05'),(79,'c52188da-6cc4-48b2-95ed-21d96d2553e9','INVENTORY_RESERVED','2026-07-19 11:30:19'),(80,'551f76e9-13b3-4249-bc97-b06cf495a97e','INVENTORY_RESERVED','2026-07-19 11:30:31'),(81,'07d18984-4d7c-4443-b041-328248b7b5fe','INVENTORY_RESERVED','2026-07-19 13:07:29'),(82,'dd24a401-8413-4748-b389-692213e3453a','INVENTORY_RESERVED','2026-07-19 14:11:10'),(83,'3fdae7ec-4387-43a1-89e2-bb9de7ed4ef4','INVENTORY_RESERVED','2026-07-19 16:27:02'),(84,'322f923e-c4c5-4717-91d0-5837bfd29ed1','INVENTORY_RESERVED','2026-07-19 16:32:17'),(85,'98d3eabd-79f7-42ab-b3d0-bb403958844a','INVENTORY_RESERVED','2026-07-19 16:33:29'),(86,'c8fead70-835a-4589-87db-a9f1199ec14e','INVENTORY_RESERVED','2026-07-19 16:34:45'),(87,'480e3e80-fc49-4d00-9233-cbc3ea777d5c','INVENTORY_RESERVED','2026-07-19 18:19:42'),(88,'0784ca02-d555-4ad4-98ff-38cb8362e25c','INVENTORY_RESERVED','2026-07-19 18:19:51'),(89,'c9c519d1-d3ff-4a1a-aa2e-f9f84fb5b3cc','INVENTORY_RESERVED','2026-07-19 18:22:07'),(90,'7f81a9ce-3e08-4069-b2db-14316b726a03','INVENTORY_RESERVED','2026-07-19 18:24:44'),(91,'b215f6bc-be0d-4f6a-ad41-7a7f77fed71c','INVENTORY_RESERVED','2026-07-20 16:01:47'),(92,'cf2adb38-ceac-47be-8d41-f388c0926f22','INVENTORY_RESERVED','2026-07-20 16:03:00'),(93,'f26411cf-d012-44e0-bfc4-038d77559d56','INVENTORY_RESERVED','2026-07-20 18:21:33'),(94,'98894691-a723-4cb5-8e34-b6ea42049994','INVENTORY_RESERVED','2026-07-21 17:52:44'),(95,'17b49377-c1d3-43ac-b1a3-4cee6c0e25b4','INVENTORY_RESERVED','2026-07-21 17:58:15'),(96,'4ffcc312-f9a4-4016-966f-05fa68805cba','INVENTORY_RESERVED','2026-07-21 18:02:29'),(97,'4cc6c180-19d9-4b3f-be71-0721de2d24df','INVENTORY_RESERVED','2026-07-21 18:22:23'),(98,'02355717-de1d-4b65-9db5-45568a9d647c','INVENTORY_RESERVED','2026-07-21 18:29:22'),(99,'f9120298-f54e-4172-9075-9b0a60759935','INVENTORY_RESERVED','2026-07-21 18:30:40'),(100,'371c2895-2ea4-4057-b22d-254cbb879e85','INVENTORY_RESERVED','2026-07-22 04:04:36'),(101,'17579b80-ea3c-44c9-a509-2debd8d0861d','INVENTORY_RESERVED','2026-07-22 04:28:48'),(102,'16c67863-850e-4e03-b059-651f33697e36','INVENTORY_RESERVED','2026-07-22 04:29:50'),(103,'3dcf6ad7-fb03-424b-bc38-03d639d2c970','INVENTORY_RESERVED','2026-07-22 05:06:37'),(104,'5b6f220a-0ee7-4a14-a9aa-9aad08821058','INVENTORY_RESERVED','2026-07-22 05:14:10'),(105,'465d0c46-317a-4f5c-987e-127c137ed72e','INVENTORY_RESERVED','2026-07-22 05:18:19'),(106,'79dcfe42-4adc-4f53-9d6a-2a682bf976cf','INVENTORY_RESERVED','2026-07-22 05:27:05'),(107,'16604c65-c838-4db5-9ca7-a3d9e79d5a69','INVENTORY_RESERVED','2026-07-22 05:29:55'),(108,'7cac5f00-b6d9-4f30-913c-f07b19ecc6d1','INVENTORY_RESERVED','2026-07-22 05:29:58'),(109,'e02ec30a-e3da-436e-b52f-97519ab686d1','INVENTORY_RESERVED','2026-07-22 05:52:23'),(110,'3c62a413-edc2-4a74-9cfc-01bf6e648e0e','INVENTORY_RESERVED','2026-07-22 05:54:52'),(111,'b765588b-584b-401c-89f5-7d2af3167561','INVENTORY_RESERVED','2026-07-22 07:04:25'),(112,'2d051704-e364-499c-acd1-167a79998fe2','INVENTORY_RESERVED','2026-07-22 07:18:09'),(113,'3ac2ccc7-b230-45bf-b834-21d45ca0ff90','INVENTORY_RESERVED','2026-07-22 07:33:14'),(114,'3a800322-3e5a-4c3b-bb8c-cc18bc0a0643','INVENTORY_RESERVED','2026-07-22 07:36:57'),(115,'b9aa42c1-7201-4c86-8dc4-53e5cb428a2c','INVENTORY_RESERVED','2026-07-22 08:06:34'),(116,'7f11192a-d6cc-4b53-b0d8-d23941cf53ca','INVENTORY_RESERVED','2026-07-22 10:48:42'),(117,'6953b8d3-6632-4d92-be85-2f35f39e6d42','INVENTORY_RESERVED','2026-07-22 13:24:35'),(118,'809ea366-02d7-4fb5-a378-82fd5dd43c1c','INVENTORY_RESERVED','2026-07-22 13:31:44'),(119,'1d187d52-f876-4d5d-b6d9-1daf39e99e82','INVENTORY_RESERVED','2026-07-22 14:26:14'),(120,'6241dfc9-0412-4afa-bd35-2c820134f124','INVENTORY_RESERVED','2026-07-23 15:18:59'),(121,'b490a6f0-3112-4e07-9879-549c2fed1a87','INVENTORY_RESERVED','2026-07-24 18:14:25'),(122,'8650d8b9-538d-4aa0-a30d-c05c63c88f37','INVENTORY_RESERVED','2026-07-25 13:48:08');
/*!40000 ALTER TABLE `processed_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'orderdb'
--

--
-- Dumping routines for database 'orderdb'
--

--
-- Current Database: `payment_db`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `payment_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `payment_db`;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','create payment table','SQL','V1__create_payment_table.sql',-658521188,'root','2026-07-11 12:49:51',23,1),(2,'2','insert sample payments','SQL','V2__insert_sample_payments.sql',-1338709424,'root','2026-07-11 12:49:51',1,1),(3,'3','create processed events','SQL','V3__create_processed_events.sql',-794180183,'root','2026-07-15 16:48:39',13,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_number` varchar(50) NOT NULL,
  `order_id` bigint NOT NULL,
  `customer_id` varchar(255) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_method` varchar(30) NOT NULL,
  `status` varchar(30) NOT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_number` (`payment_number`),
  KEY `idx_payments_order_id` (`order_id`),
  KEY `idx_payments_customer_id` (`customer_id`),
  KEY `idx_payments_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=160 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,'PAY-100001',1,'301f4e17-6208-4845-8c82-07bf6ea473d0',999.00,'CARD','FAILED','TXN100001','2026-07-11 18:19:51','2026-07-11 13:02:06'),(2,'PAY-100002',2,'301f4e17-6208-4845-8c82-07bf6ea473d0',2499.00,'UPI','SUCCESS','TXN100002','2026-07-11 18:19:51','2026-07-11 18:19:51'),(3,'PAY-100003',3,'301f4e17-6208-4845-8c82-07bf6ea473d0',499.00,'NET_BANKING','FAILED','TXN100003','2026-07-11 18:19:51','2026-07-11 18:19:51'),(4,'PAY-100004',4,'301f4e17-6208-4845-8c82-07bf6ea473d0',799.00,'CARD','PENDING','TXN100004','2026-07-11 18:19:51','2026-07-11 18:19:51'),(5,'PAY-100005',5,'301f4e17-6208-4845-8c82-07bf6ea473d0',1299.00,'UPI','SUCCESS','TXN100005','2026-07-11 18:19:51','2026-07-11 18:19:51'),(6,'PAY-100006',6,'301f4e17-6208-4845-8c82-07bf6ea473d0',3499.00,'CARD','SUCCESS','TXN100006','2026-07-11 18:19:51','2026-07-11 18:19:51'),(7,'PAY-100007',7,'301f4e17-6208-4845-8c82-07bf6ea473d0',599.00,'WALLET','FAILED','TXN100007','2026-07-11 18:19:51','2026-07-11 18:19:51'),(8,'PAY-100008',8,'301f4e17-6208-4845-8c82-07bf6ea473d0',1599.00,'UPI','SUCCESS','TXN100008','2026-07-11 18:19:51','2026-07-11 18:19:51'),(9,'PAY-100009',9,'301f4e17-6208-4845-8c82-07bf6ea473d0',899.00,'CARD','REFUNDED','TXN100009','2026-07-11 18:19:51','2026-07-11 18:19:51'),(10,'PAY-100010',10,'301f4e17-6208-4845-8c82-07bf6ea473d0',2199.00,'NET_BANKING','SUCCESS','TXN100010','2026-07-11 18:19:51','2026-07-11 18:19:51'),(11,'PAY-1783774848822',1,'301f4e17-6208-4845-8c82-07bf6ea473d0',0.01,'CARD','SUCCESS','747b743e-6d06-4421-aee4-a556c2ae5b7a','2026-07-11 13:00:49','2026-07-11 13:01:40'),(12,'PAY-1783775978908',10,'301f4e17-6208-4845-8c82-07bf6ea473d0',10.00,'CARD','PENDING',NULL,'2026-07-11 13:19:39','2026-07-11 13:19:39'),(13,'PAY-1783778660472',11,'301f4e17-6208-4845-8c82-07bf6ea473d0',74995.00,'CARD','SUCCESS','fdebb20c-77a1-4c13-8933-ec8e68a43d6f','2026-07-11 14:04:20','2026-07-11 14:06:36'),(14,'PAY-1783780260323',12,'301f4e17-6208-4845-8c82-07bf6ea473d0',399995.00,'CARD','SUCCESS','c4c726f6-5b00-47b8-b7bd-d05f62868024','2026-07-11 14:31:00','2026-07-11 14:56:02'),(15,'PAY-1783863445238',13,'564fe970-3e98-4628-8b3f-bd1d668b5595',209990.00,'CARD','SUCCESS','c29da0e0-b5c0-4b12-b1cf-019f176c794d','2026-07-12 13:37:25','2026-07-12 14:13:31'),(16,'PAY-1783864394006',14,'564fe970-3e98-4628-8b3f-bd1d668b5595',184997.00,'CARD','SUCCESS','18168226-454a-4e95-9979-46ab3acfcdd3','2026-07-12 13:53:14','2026-07-12 14:14:07'),(17,'PAY-1783877754322',15,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','82128952-e834-4add-a0c3-4c324caf8c47','2026-07-12 17:35:54','2026-07-12 17:35:58'),(18,'PAY-1783877956711',16,'564fe970-3e98-4628-8b3f-bd1d668b5595',254995.00,'CARD','PENDING',NULL,'2026-07-12 17:39:17','2026-07-12 17:39:17'),(19,'PAY-1783879145314',17,'301f4e17-6208-4845-8c82-07bf6ea473d0',74999.00,'CARD','SUCCESS','beda9772-16ba-450e-9f26-29d69f1abf60','2026-07-12 17:59:05','2026-07-12 17:59:08'),(20,'PAY-1783879361644',18,'301f4e17-6208-4845-8c82-07bf6ea473d0',74999.00,'CARD','SUCCESS','773dc2f3-bc32-47ff-9516-c399fffc89fb','2026-07-12 18:02:42','2026-07-12 18:02:45'),(21,'PAY-1783879721872',19,'301f4e17-6208-4845-8c82-07bf6ea473d0',74999.00,'CARD','SUCCESS','27c431e5-8caf-4eb0-846e-d1e6d313dd17','2026-07-12 18:08:42','2026-07-12 18:08:45'),(22,'PAY-1783880228129',20,'301f4e17-6208-4845-8c82-07bf6ea473d0',79999.00,'CARD','SUCCESS','1b39aabd-8e6f-4431-9d43-26fd850b460c','2026-07-12 18:17:08','2026-07-12 18:17:10'),(23,'PAY-1783922627383',21,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','89ff4266-bdb6-4573-b009-54f5ca54e7f0','2026-07-13 06:03:47','2026-07-13 06:03:50'),(24,'PAY-1783923259367',22,'301f4e17-6208-4845-8c82-07bf6ea473d0',74999.00,'CARD','SUCCESS','3a85559d-f585-4332-8e03-3679e1f579be','2026-07-13 06:14:19','2026-07-13 06:14:22'),(25,'PAY-1783924217252',23,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','d84c20c4-430b-4a9b-9e1d-4b281c250c14','2026-07-13 06:30:17','2026-07-13 06:30:20'),(26,'PAY-1783924483101',24,'108fa683-146b-4a63-ba63-0842c967c495',34999.00,'CARD','SUCCESS','226473a5-6980-4822-9c24-5d33691a42ee','2026-07-13 06:34:43','2026-07-13 06:34:45'),(27,'PAY-1783924622924',25,'108fa683-146b-4a63-ba63-0842c967c495',34999.00,'CARD','SUCCESS','1da3971c-c566-4174-875a-15f88f003f5c','2026-07-13 06:37:03','2026-07-13 06:37:05'),(28,'PAY-1783924886312',26,'108fa683-146b-4a63-ba63-0842c967c495',74999.00,'CARD','SUCCESS','392d2e71-a232-4790-aca4-3fa9a853f1ad','2026-07-13 06:41:26','2026-07-13 06:41:28'),(29,'PAY-1783925360083',27,'108fa683-146b-4a63-ba63-0842c967c495',74999.00,'CARD','SUCCESS','9c6a7292-3283-4b74-8700-3ad8ef21c3ba','2026-07-13 06:49:20','2026-07-13 06:49:23'),(30,'PAY-1783925514487',28,'301f4e17-6208-4845-8c82-07bf6ea473d0',74999.00,'CARD','SUCCESS','8e1bc9c0-0b98-4c14-882d-b7f6a20a1aa0','2026-07-13 06:51:54','2026-07-13 06:51:56'),(31,'PAY-1783925646041',29,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','e5f6040f-d003-4a12-b877-ce2adce28aec','2026-07-13 06:54:06','2026-07-13 06:54:08'),(32,'PAY-1783925791923',30,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','0df964e9-4fc4-4d3a-b54e-57e22e766d94','2026-07-13 06:56:32','2026-07-13 06:56:34'),(33,'PAY-1783927272385',31,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','72c48ada-8e69-4428-83cf-da529e28ad50','2026-07-13 07:21:12','2026-07-13 07:21:15'),(34,'PAY-1783928022897',32,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','8c977168-7cc5-4f39-a715-c3cdf4d58a90','2026-07-13 07:33:43','2026-07-13 07:33:45'),(35,'PAY-1784004026065',33,'301f4e17-6208-4845-8c82-07bf6ea473d0',74999.00,'CARD','SUCCESS','6bcdc0d2-4603-4c02-a813-70a631b8e48d','2026-07-14 04:40:26','2026-07-14 04:40:28'),(36,'PAY-1784046362205',34,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:26:02','2026-07-14 16:26:02'),(37,'PAY-1784046917678',35,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:35:18','2026-07-14 16:35:18'),(38,'PAY-1784048013512',36,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:53:34','2026-07-14 16:53:34'),(39,'PAY-1784048028990',37,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:53:49','2026-07-14 16:53:49'),(40,'PAY-1784048052500',38,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:54:13','2026-07-14 16:54:13'),(41,'PAY-1784048124895',39,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:55:25','2026-07-14 16:55:25'),(42,'PAY-1784048129217',40,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:55:29','2026-07-14 16:55:29'),(43,'PAY-1784048132771',41,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:55:33','2026-07-14 16:55:33'),(44,'PAY-1784048136387',42,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','PENDING',NULL,'2026-07-14 16:55:36','2026-07-14 16:55:36'),(45,'PAY-1784049157409',43,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','SUCCESS','ea7be1ab-1f09-4352-ba8d-6e5f8eea0a99','2026-07-14 17:12:37','2026-07-14 17:12:40'),(46,'PAY-1784049187112',44,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','d1cc590d-8114-407e-b41d-88ece1a371a8','2026-07-14 17:13:07','2026-07-14 17:13:09'),(47,'PAY-1784050256055',45,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','827e540d-5a97-49d7-b313-fb0fcb0bc40e','2026-07-14 17:30:56','2026-07-14 17:30:59'),(48,'PAY-01674CD04A91',59,'108fa683-146b-4a63-ba63-0842c967c495',2.00,'CARD','COMPLETED',NULL,'2026-07-15 17:18:22','2026-07-15 17:18:22'),(49,'PAY-A98F36CDBEB6',60,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','COMPLETED',NULL,'2026-07-15 17:19:58','2026-07-15 17:19:58'),(50,'PAY-52E351D076DB',61,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','COMPLETED',NULL,'2026-07-16 09:15:10','2026-07-16 09:15:10'),(51,'PAY-3D1E2C4F6310',62,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','PENDING',NULL,'2026-07-16 09:18:06','2026-07-16 09:18:06'),(52,'PAY-F18C1349F3B1',63,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','PENDING',NULL,'2026-07-16 09:18:42','2026-07-16 09:18:42'),(53,'PAY-E35C754C7C95',64,'301f4e17-6208-4845-8c82-07bf6ea473d0',123.00,'CARD','PENDING',NULL,'2026-07-16 09:20:23','2026-07-16 09:20:23'),(54,'PAY-5A2DA3391874',65,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','2cb1115a-100e-46b3-ae2a-51ef6438c37d','2026-07-16 09:24:04','2026-07-16 09:24:10'),(55,'PAY-78436561C3E7',66,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','1060aa7c-9b4c-4895-b92d-31f9d38c7bee','2026-07-16 09:27:13','2026-07-16 12:07:39'),(56,'PAY-D10E64FAE04F',67,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','79fde339-cb6b-4a85-8fc6-507d8076d3bc','2026-07-16 09:29:34','2026-07-16 12:07:05'),(57,'PAY-6B2EF53412BD',68,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','728a0f76-f0df-454d-acac-0c7fb5b8c048','2026-07-16 09:29:42','2026-07-16 09:29:46'),(58,'PAY-39CC4FCCE01A',69,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','e8b429ea-e84d-4319-b713-00a2d60cbe80','2026-07-16 09:31:09','2026-07-16 09:31:11'),(59,'PAY-1CEC83D249CE',70,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','ade4e93f-d010-4b8d-ab96-cd0e02e6c79f','2026-07-16 09:31:29','2026-07-16 12:06:28'),(60,'PAY-AAF5D6523053',71,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','a72d42f3-a1b3-4874-b701-77cb92b8aa98','2026-07-16 09:31:42','2026-07-16 09:31:49'),(61,'PAY-809C1EF34582',72,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','8872b6ab-a058-4239-8bf3-6ce770e4e7fb','2026-07-16 09:32:43','2026-07-16 09:32:49'),(62,'PAY-C2640D008CAF',73,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','664fc075-2242-410e-bd72-8e628d4bfac7','2026-07-16 09:33:09','2026-07-16 09:33:11'),(63,'PAY-06BAB2B55CEC',74,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','SUCCESS','2c56d01d-d7dd-4a25-a528-5c57eddc5411','2026-07-16 11:56:21','2026-07-16 11:56:25'),(64,'PAY-170069B38A71',75,'301f4e17-6208-4845-8c82-07bf6ea473d0',2.00,'CARD','SUCCESS','f8d687ee-dad7-430a-9d07-e7980c0d1060','2026-07-16 18:40:35','2026-07-16 18:40:38'),(65,'PAY-203480F2D728',76,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','64c24872-539e-48df-9ea0-77be77172eae','2026-07-17 05:53:55','2026-07-17 05:55:31'),(66,'PAY-4A729AFDC51E',77,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','3b1cdf23-3f88-4906-80ef-1bf28db75da4','2026-07-17 05:55:50','2026-07-17 05:56:07'),(67,'PAY-123151B61A29',78,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','9e63ff11-4c8f-4f9c-a3a9-70a4034cc5f5','2026-07-17 06:02:37','2026-07-17 06:02:40'),(68,'PAY-D1830F059C86',79,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','dca9b8e1-965a-4a9c-8de7-2efdce73d12b','2026-07-17 06:13:05','2026-07-17 06:13:08'),(69,'PAY-79BD044C0DC2',80,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','79007f46-83a0-46f2-b494-e6230d648091','2026-07-17 07:18:53','2026-07-17 07:19:14'),(70,'PAY-CD4D5051E6A7',81,'301f4e17-6208-4845-8c82-07bf6ea473d0',34999.00,'CARD','SUCCESS','6918eda8-0109-41dd-815f-c0dceb8c0932','2026-07-17 09:49:31','2026-07-17 09:49:51'),(71,'PAY-3F0A0C35CCF4',82,'301f4e17-6208-4845-8c82-07bf6ea473d0',123.00,'CARD','SUCCESS','95a8fb0c-5244-48b0-bcf7-b0a1c1a89ad0','2026-07-17 11:48:22','2026-07-17 11:48:25'),(72,'PAY-5A8160F181BE',83,'1814b14e-1986-4bdf-a03d-c55a182a3861',34999.00,'CARD','PENDING',NULL,'2026-07-18 16:29:50','2026-07-18 16:29:50'),(73,'PAY-4ADC0A1A4875',86,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','b260ed93-ec49-413a-b0b4-5dee8b1e29ea','2026-07-18 16:38:44','2026-07-18 16:38:47'),(74,'PAY-DB641C3DC2EF',91,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','709b1279-7f94-40d8-9a53-9851117dc347','2026-07-18 16:44:00','2026-07-18 16:44:05'),(75,'PAY-2B04C9471993',92,'1814b14e-1986-4bdf-a03d-c55a182a3861',12999.00,'CARD','SUCCESS','be6db0d1-d6aa-4654-8d96-cdd129de2537','2026-07-18 16:44:43','2026-07-18 16:46:46'),(76,'PAY-AD9D473C0ED6',93,'1814b14e-1986-4bdf-a03d-c55a182a3861',12999.00,'CARD','SUCCESS','91c2e419-f0ac-474b-85bf-440860942607','2026-07-18 16:48:35','2026-07-18 16:48:38'),(77,'PAY-90D90E3B10BB',94,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','ebe31cb9-c60a-4a01-8911-f7a9fa90bbb1','2026-07-18 16:48:59','2026-07-18 16:49:02'),(78,'PAY-A8B4BC4E2231',100,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','41d45766-6a86-4764-9644-6ab773db9d3d','2026-07-18 17:23:52','2026-07-18 17:23:55'),(79,'PAY-F249A35789B0',101,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','3a68cc86-6c89-4260-8077-df85dd9dd8f2','2026-07-18 17:29:51','2026-07-18 17:29:53'),(80,'PAY-B092B6C59FAC',102,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','a2d24b1b-f7a0-4ff7-8d40-2738a912fb15','2026-07-18 17:37:03','2026-07-18 17:37:06'),(81,'PAY-4D361290D196',103,'301f4e17-6208-4845-8c82-07bf6ea473d0',399995.00,'CARD','SUCCESS','b776a9d7-dfdd-46ab-845f-bdf0bbccd67d','2026-07-18 17:37:38','2026-07-18 17:37:41'),(82,'PAY-88730767B2DB',104,'301f4e17-6208-4845-8c82-07bf6ea473d0',79999.00,'CARD','SUCCESS','07d45d8e-5bb8-40b2-91b1-57f06e380ef9','2026-07-18 17:57:35','2026-07-18 17:57:38'),(83,'PAY-629E6E22C4EA',105,'301f4e17-6208-4845-8c82-07bf6ea473d0',159998.00,'CARD','SUCCESS','62438e63-b886-4995-a264-f0d953e1f46b','2026-07-19 06:28:00','2026-07-19 06:28:02'),(84,'PAY-213672377071',106,'301f4e17-6208-4845-8c82-07bf6ea473d0',79999.00,'CARD','SUCCESS','41eaa1e7-9a1e-4ebb-bc34-1d8ba529b59e','2026-07-19 07:01:43','2026-07-19 07:01:45'),(85,'PAY-453F897FAE28',107,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','cd5a8a2a-4820-44da-8fcb-62d7782cc592','2026-07-19 08:02:54','2026-07-19 08:02:56'),(86,'PAY-88B225E4B138',108,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','2f045832-9d4e-4f1e-b241-cd7e0e19ea19','2026-07-19 08:09:14','2026-07-19 08:09:17'),(87,'PAY-04321A602BAC',109,'83a0c8b2-104c-4fba-8460-36ae6050e400',159998.00,'CARD','SUCCESS','53244fae-68e1-4aa1-805b-60f1d1edbd0c','2026-07-19 08:11:42','2026-07-19 08:11:45'),(88,'PAY-EF2912354947',110,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','42f6f8dc-4282-4579-8d57-993ffc4f32c2','2026-07-19 08:11:54','2026-07-19 08:12:00'),(89,'PAY-E89CD622856B',111,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','9e3f9cbf-56e9-4ee5-86a0-046afbbb29c1','2026-07-19 08:17:20','2026-07-19 08:17:27'),(90,'PAY-F2FE0561EFA6',112,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','b077a55b-c135-41ca-96c0-776430a4a1ec','2026-07-19 08:18:32','2026-07-19 08:18:35'),(91,'PAY-589D96D79526',113,'83a0c8b2-104c-4fba-8460-36ae6050e400',249998.00,'CARD','SUCCESS','bc3bb06e-a7b1-4ac8-a83f-eecb723cea11','2026-07-19 08:33:07','2026-07-19 08:33:10'),(92,'PAY-FD2FD76275A2',114,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','37299a44-cb03-4a88-b506-a2cfe32985a4','2026-07-19 09:19:39','2026-07-19 09:37:28'),(93,'PAY-3DB232D0D3A9',115,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','9221569a-59bc-4666-b1f9-bac175859157','2026-07-19 09:33:12','2026-07-19 09:37:17'),(94,'PAY-CF42FA494FD6',116,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','57895a9e-ec42-4270-a769-cc0f615aa0c2','2026-07-19 09:33:41','2026-07-19 09:36:48'),(95,'PAY-049F496AD606',117,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','220e0da4-f0a8-4a45-8dd4-4c33fe3bdf5f','2026-07-19 09:36:57','2026-07-19 09:36:59'),(96,'PAY-51C1E62E5FB8',118,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','34cc7e6f-ec32-41df-8250-6924b4bdcac0','2026-07-19 09:58:05','2026-07-19 09:58:43'),(97,'PAY-716AC5B0F97D',119,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','ff0f6f8b-f494-4a78-a5ac-b83d44eb9739','2026-07-19 09:58:52','2026-07-19 09:58:54'),(98,'PAY-721B65007738',120,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','a6a4739a-a179-4ac3-a95c-d1df522978c9','2026-07-19 10:03:46','2026-07-19 10:03:49'),(99,'PAY-C6F80B0AFCA4',121,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','17773942-af7e-4f36-aeeb-0b9161b03a79','2026-07-19 10:08:52','2026-07-19 10:08:55'),(100,'PAY-CA0D152FE39A',122,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','61bf0719-bb18-4096-b586-4caeb1548a27','2026-07-19 10:09:48','2026-07-19 10:09:51'),(101,'PAY-A1F1237A2EC6',123,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','e2b028c4-9bac-4a87-8c77-e4a2a3db4df5','2026-07-19 10:34:22','2026-07-19 10:34:25'),(102,'PAY-3BB478E74FBC',124,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','FAILED',NULL,'2026-07-19 10:34:31','2026-07-19 10:34:34'),(103,'PAY-5BE9465B0F5F',125,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','FAILED',NULL,'2026-07-19 10:35:16','2026-07-19 10:35:46'),(104,'PAY-835143856E0F',126,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','15681560-5b4e-470d-8d24-cbd1c5445c7e','2026-07-19 10:35:59','2026-07-19 10:36:02'),(105,'PAY-D260C7A84BFF',127,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','9ef3401c-8478-45b1-897b-240eeea41616','2026-07-19 10:38:56','2026-07-19 11:14:49'),(106,'PAY-09F2A8388D63',128,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','c1f4b23b-93d6-4d45-a2e3-ca90e79e8ff1','2026-07-19 10:57:10','2026-07-19 11:14:08'),(107,'PAY-7078156960AA',129,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','04370cc8-bd5c-4ab7-a369-28be0df14e94','2026-07-19 10:58:05','2026-07-19 11:14:37'),(108,'PAY-AE083630D7A6',130,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','700d1659-a213-4a16-9283-4360a7d46256','2026-07-19 10:58:49','2026-07-19 11:14:25'),(109,'PAY-A24421345177',131,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','62210159-697c-4787-a6bd-3c7e1d5362fb','2026-07-19 11:01:31','2026-07-19 11:13:20'),(110,'PAY-BF48CA65FCBF',132,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','f91b80a3-76af-48bd-8854-eb36ce8e2ae5','2026-07-19 11:04:13','2026-07-19 11:13:28'),(111,'PAY-F04B6EB98CA9',133,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','603b922a-7243-4186-b0bd-c180917a0e71','2026-07-19 11:05:58','2026-07-19 11:13:13'),(112,'PAY-76DCCD476897',134,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','253b03b2-be27-4fb9-bdd4-f5d7bb07ce54','2026-07-19 11:08:56','2026-07-19 11:13:05'),(113,'PAY-0352D0D410DF',135,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','baf5454c-e86b-4ed6-bf50-4652f666739e','2026-07-19 11:10:18','2026-07-19 11:12:59'),(114,'PAY-193FD5C8CEA5',136,'83a0c8b2-104c-4fba-8460-36ae6050e400',204998.00,'CARD','SUCCESS','d3c1e95e-9b23-40b0-8acd-3dbda988eb38','2026-07-19 11:12:39','2026-07-19 11:12:52'),(115,'PAY-664D5B5A2432',137,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','dd3752f1-df92-4672-836e-70b13ab5eb51','2026-07-19 11:30:05','2026-07-19 11:30:10'),(116,'PAY-DCEF9C2C49C9',138,'83a0c8b2-104c-4fba-8460-36ae6050e400',159998.00,'CARD','SUCCESS','58de8976-622f-44c0-bb6b-c1967cb3e113','2026-07-19 11:30:19','2026-07-19 11:30:23'),(117,'PAY-9062FE2244B0',139,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','ca3ea7e1-b119-4c34-b9b7-2fafca7f4dd1','2026-07-19 11:30:31','2026-07-19 11:30:42'),(118,'PAY-C81D434D35B1',140,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','f7cd7796-7ed7-4348-838b-2ea4750c2e17','2026-07-19 13:12:02','2026-07-19 14:04:18'),(119,'PAY-6B0B2A8F2CF6',141,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','6f0344c3-ba06-469b-b7fd-1a972fa69d88','2026-07-19 14:11:10','2026-07-19 14:11:12'),(120,'PAY-50FF2BF96010',142,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','84e6211c-e5df-45cf-ac51-f60bd968b772','2026-07-19 16:27:02','2026-07-19 16:27:05'),(121,'PAY-DCF0B6022A58',143,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','PENDING',NULL,'2026-07-19 16:32:17','2026-07-19 16:32:17'),(122,'PAY-55D8A8B863AD',144,'83a0c8b2-104c-4fba-8460-36ae6050e400',414994.00,'CARD','SUCCESS','e9ee9540-6426-4586-a953-a348c2c5a75f','2026-07-19 16:33:29','2026-07-19 16:34:16'),(123,'PAY-F9B4A528CBF4',145,'83a0c8b2-104c-4fba-8460-36ae6050e400',44997.00,'CARD','SUCCESS','f8a9968b-92d4-4dd8-837f-e56f2eef303f','2026-07-19 16:34:45','2026-07-19 16:34:49'),(124,'PAY-429B91CA4AC5',146,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','PENDING',NULL,'2026-07-19 18:19:42','2026-07-19 18:19:42'),(125,'PAY-7A848ACB539E',147,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','PENDING',NULL,'2026-07-19 18:19:51','2026-07-19 18:19:51'),(126,'PAY-D6D712B8B727',148,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','ed618e88-b6a0-4e9b-86e2-e6c5e58ff460','2026-07-19 18:22:07','2026-07-19 18:24:37'),(127,'PAY-A0F66A7F7EEB',149,'83a0c8b2-104c-4fba-8460-36ae6050e400',79999.00,'CARD','SUCCESS','69691621-6aba-43b6-99ba-744f1ed9866c','2026-07-19 18:24:44','2026-07-19 18:24:50'),(128,'PAY-88CEAD11E270',150,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','e3385909-1dbf-4817-a28d-aeaa6fc0b7ec','2026-07-20 16:01:47','2026-07-20 16:01:50'),(129,'PAY-24504849FE81',151,'83a0c8b2-104c-4fba-8460-36ae6050e400',124999.00,'CARD','SUCCESS','f13d8681-c58e-4d3f-aa0c-4c881b3682d2','2026-07-20 16:03:00','2026-07-20 16:03:02'),(130,'PAY-4371D2DAF46D',152,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','8c019f58-c94c-42d0-a6a6-984384753d47','2026-07-20 18:21:33','2026-07-20 18:21:35'),(131,'PAY-3EA98DFF7D4F',153,'1814b14e-1986-4bdf-a03d-c55a182a3861',79999.00,'CARD','SUCCESS','2b582bab-63aa-4180-b8b9-1ce558f7a54a','2026-07-21 17:52:44','2026-07-21 17:52:47'),(132,'PAY-78407E330B2B',154,'1814b14e-1986-4bdf-a03d-c55a182a3861',29998.00,'CARD','SUCCESS','4751415c-f2d9-4a62-a9bb-da3ae261f9d9','2026-07-21 17:58:15','2026-07-21 17:58:18'),(133,'PAY-B35F8F3BF98C',155,'1814b14e-1986-4bdf-a03d-c55a182a3861',79999.00,'CARD','SUCCESS','953ade37-e81d-4cd5-ba49-890e3aeaa756','2026-07-21 18:02:29','2026-07-21 18:02:32'),(134,'PAY-98EAC28C246A',156,'d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150',159998.00,'CARD','SUCCESS','63848209-ec0b-449b-8b0c-17efd5cb172b','2026-07-21 18:22:24','2026-07-21 18:22:26'),(135,'PAY-76487D020BC1',157,'d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150',124999.00,'CARD','SUCCESS','c0b9e504-dd3d-48fa-8791-865c27eda4fd','2026-07-21 18:29:22','2026-07-21 18:29:27'),(136,'PAY-884178E38288',158,'d0f7b0b0-ce4b-4d4a-8b37-31a543fd4150',124999.00,'CARD','SUCCESS','b468c11e-727e-4017-9d0c-f28fe7722cdc','2026-07-21 18:30:40','2026-07-21 18:30:42'),(137,'PAY-91D859C6D99C',159,'301f4e17-6208-4845-8c82-07bf6ea473d0',79999.00,'CARD','SUCCESS','1f0308d8-89f7-4c9e-9196-f7d463929bb2','2026-07-22 04:04:36','2026-07-22 04:04:39'),(138,'PAY-51016A272265',160,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','a534823c-f7bf-4c97-933c-d8c09ed5e05b','2026-07-22 04:28:48','2026-07-22 04:28:51'),(139,'PAY-1CD614D9235A',161,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','67143d31-a5f6-417e-ad34-22bfc31b5d43','2026-07-22 04:29:50','2026-07-22 04:29:56'),(140,'PAY-9DF77B6F7565',162,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','8d24c087-0cc6-4cd5-a491-c2cb3b0985f8','2026-07-22 05:06:37','2026-07-22 05:06:40'),(141,'PAY-B1C1531A6A6E',163,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','ca171c4f-1772-4f7c-84c9-7a31402a83dc','2026-07-22 05:14:10','2026-07-22 05:14:13'),(142,'PAY-4E1584BA6CEC',164,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','PENDING',NULL,'2026-07-22 05:18:19','2026-07-22 05:18:19'),(143,'PAY-7BCF3BD7EFED',165,'301f4e17-6208-4845-8c82-07bf6ea473d0',204998.00,'CARD','SUCCESS','68852bf0-0003-4221-a0ed-16c2809ca22d','2026-07-22 05:27:05','2026-07-22 05:27:08'),(144,'PAY-26548C47AB69',166,'301f4e17-6208-4845-8c82-07bf6ea473d0',79999.00,'CARD','SUCCESS','e9f2a391-2396-48b0-b07e-6478897c4214','2026-07-22 05:29:55','2026-07-22 05:30:09'),(145,'PAY-B7DD70350423',167,'301f4e17-6208-4845-8c82-07bf6ea473d0',79999.00,'CARD','SUCCESS','114fab3f-906b-4238-a9aa-76d084ab20ca','2026-07-22 05:29:58','2026-07-22 05:30:00'),(146,'PAY-9A6D005BD97E',168,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','d5909c56-1cc4-4036-948a-cb43841e0d61','2026-07-22 05:52:23','2026-07-22 05:52:26'),(147,'PAY-A395DDDA7F7C',169,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','ccdd6add-1275-40c0-90c3-4f8f9a269484','2026-07-22 05:54:52','2026-07-22 05:54:54'),(148,'PAY-3F4FB534626F',170,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','b4a4218f-8986-4713-8ef0-aa8a2223079a','2026-07-22 07:04:25','2026-07-22 07:04:28'),(149,'PAY-0FBF6552B916',171,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','77da8316-d3cf-4341-9b8c-ea7a9baf3799','2026-07-22 07:18:09','2026-07-22 07:18:12'),(150,'PAY-9F68228C75C7',172,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','dfa3e627-076f-4111-ba19-1bdba113d189','2026-07-22 07:33:14','2026-07-22 07:33:17'),(151,'PAY-D2F936B1F051',173,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','159cdc04-6c59-48b3-918f-bb27a327a752','2026-07-22 07:36:57','2026-07-22 07:36:59'),(152,'PAY-B15B7EC04773',174,'301f4e17-6208-4845-8c82-07bf6ea473d0',204998.00,'CARD','SUCCESS','9bd22a5d-7bc3-4d37-a52b-b4e364d10514','2026-07-22 08:06:35','2026-07-22 08:06:37'),(153,'PAY-B8D33D5C0C0F',175,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','e3e098c0-2b82-4f32-9bfd-5e57d39795cb','2026-07-22 10:48:42','2026-07-22 10:48:45'),(154,'PAY-BCEC4EDDA27F',176,'301f4e17-6208-4845-8c82-07bf6ea473d0',12999.00,'CARD','SUCCESS','eff0c189-cbf2-4a07-a5ed-05f6b84eee2c','2026-07-22 13:24:35','2026-07-22 13:24:38'),(155,'PAY-92D880BB49C3',177,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','ea62e8f2-0648-4b44-a30d-ae915f829b5e','2026-07-22 13:31:44','2026-07-22 13:31:47'),(156,'PAY-A6855791D9BB',178,'301f4e17-6208-4845-8c82-07bf6ea473d0',14999.00,'CARD','SUCCESS','533cc6c4-7448-4432-99d2-4e7661090d70','2026-07-22 14:26:14','2026-07-22 14:26:17'),(157,'PAY-DA1D7E47E9D3',179,'301f4e17-6208-4845-8c82-07bf6ea473d0',79999.00,'CARD','SUCCESS','834c9db5-c41a-4d7b-8808-381d0688310d','2026-07-23 15:18:59','2026-07-23 15:19:02'),(158,'PAY-A25035B181F0',180,'301f4e17-6208-4845-8c82-07bf6ea473d0',124999.00,'CARD','SUCCESS','3a34ff06-0cac-4e53-bd8d-f010e196d7cf','2026-07-24 18:14:25','2026-07-24 18:14:27'),(159,'PAY-CCFCC461F1BA',181,'83a0c8b2-104c-4fba-8460-36ae6050e400',204998.00,'CARD','SUCCESS','e90920a2-0027-4aaa-98e3-de27faa0bad2','2026-07-25 13:48:09','2026-07-25 13:48:11');
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `processed_events`
--

DROP TABLE IF EXISTS `processed_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `processed_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `event_id` varchar(36) NOT NULL,
  `event_type` varchar(100) NOT NULL,
  `processed_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_processed_event_id` (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `processed_events`
--

LOCK TABLES `processed_events` WRITE;
/*!40000 ALTER TABLE `processed_events` DISABLE KEYS */;
INSERT INTO `processed_events` VALUES (1,'8d658a26-1a8d-4d1e-9dff-46c4a7e4af59','PAYMENT_REQUESTED','2026-07-15 17:18:22'),(2,'d552be64-b3e7-4fb8-9fc7-fce487f9e851','PAYMENT_REQUESTED','2026-07-15 17:19:58'),(3,'9dee9885-d038-4d44-a031-619417569edd','PAYMENT_REQUESTED','2026-07-16 09:15:10'),(4,'45ec0cbf-8ce5-4bdb-9ba1-19921cd11c1c','PAYMENT_REQUESTED','2026-07-16 09:18:06'),(5,'51f0c56e-b426-441e-a3e8-49beae101a8d','PAYMENT_REQUESTED','2026-07-16 09:18:42'),(6,'390c14f0-4ea8-443e-94b7-80028d414c05','PAYMENT_REQUESTED','2026-07-16 09:20:23'),(7,'254f8ebf-366d-4de4-a745-64cacaad991a','PAYMENT_REQUESTED','2026-07-16 09:24:04'),(8,'f4e3049c-c3d3-4a53-8599-1208b848f0bb','PAYMENT_REQUESTED','2026-07-16 09:27:13'),(9,'c4ed4b64-46fc-4101-a038-95a932dd687e','PAYMENT_REQUESTED','2026-07-16 09:29:34'),(10,'3af4a0d3-c661-4b2a-85d2-13aec7db2666','PAYMENT_REQUESTED','2026-07-16 09:29:42'),(11,'d443916d-0e1c-43f4-86e4-1bc31cb7fa94','PAYMENT_REQUESTED','2026-07-16 09:31:09'),(12,'7858dc47-857b-4ed4-86d4-a82e3e61293f','PAYMENT_REQUESTED','2026-07-16 09:31:29'),(13,'c2fe2066-58f0-4af9-bc4c-d540d723c87f','PAYMENT_REQUESTED','2026-07-16 09:31:42'),(14,'5fdcc9b3-8bc9-4b6d-b9d2-acc9631142c5','PAYMENT_REQUESTED','2026-07-16 09:32:43'),(15,'c4e08a79-ac13-49f2-a67d-9da0d66d3543','PAYMENT_REQUESTED','2026-07-16 09:33:09'),(16,'471d3d9d-a725-4cc6-bbe7-d11020001550','PAYMENT_REQUESTED','2026-07-16 11:56:21'),(17,'09ca54b5-1254-4674-9e0b-896355b6e37b','PAYMENT_REQUESTED','2026-07-16 18:40:35'),(18,'84e1aa43-63da-45b2-81e8-9eadeb032618','PAYMENT_REQUESTED','2026-07-17 05:53:55'),(19,'0b916585-bf31-4383-a9ac-0a83ad94ce1e','PAYMENT_REQUESTED','2026-07-17 05:55:50'),(20,'5c747af4-19eb-448d-ada7-ed83c8c41aa4','PAYMENT_REQUESTED','2026-07-17 06:02:37'),(21,'db053a75-fc77-4aa4-bc9c-bb4fde1d80be','PAYMENT_REQUESTED','2026-07-17 06:13:05'),(22,'c398f8bf-5948-4a8a-a26f-03a9b53ebcb7','PAYMENT_REQUESTED','2026-07-17 07:18:53'),(23,'bf029d27-b693-49f8-9105-4b92d5f2e39c','PAYMENT_REQUESTED','2026-07-17 09:49:31'),(24,'6da2fa83-11de-4425-bc9f-1f303126848b','PAYMENT_REQUESTED','2026-07-17 11:48:22'),(25,'c18cfefc-7d89-4202-95c6-2a9b25a3569d','PAYMENT_REQUESTED','2026-07-18 16:29:50'),(26,'5324dd6a-bb40-42e4-b15f-f6d865c319a2','PAYMENT_REQUESTED','2026-07-18 16:38:44'),(27,'7cfac794-6a12-4336-91ec-3458c0bf6634','PAYMENT_REQUESTED','2026-07-18 16:44:00'),(28,'cb952a36-8cd8-4c82-a1e0-ed5a3f1982d4','PAYMENT_REQUESTED','2026-07-18 16:44:43'),(29,'43f0f24a-0ebe-4364-a57c-cc68344e19f6','PAYMENT_REQUESTED','2026-07-18 16:48:35'),(30,'7a0b1538-ae90-4e0a-939f-24d2ad7cd2d5','PAYMENT_REQUESTED','2026-07-18 16:48:59'),(31,'2829335a-0bd3-44b0-9417-f0cc6eefae36','PAYMENT_REQUESTED','2026-07-18 17:23:52'),(32,'5ba8aaeb-7bf8-45fb-80be-6283d40a2856','PAYMENT_REQUESTED','2026-07-18 17:29:51'),(33,'f77d3240-4658-47a5-a3bd-19bd49c60530','PAYMENT_REQUESTED','2026-07-18 17:37:03'),(34,'a8cb3fa4-7915-415f-9321-3c4024018a78','PAYMENT_REQUESTED','2026-07-18 17:37:38'),(35,'2b18d0e8-c887-45d5-af84-f6ac5d44e65a','PAYMENT_REQUESTED','2026-07-18 17:57:35'),(36,'ae160efe-c406-4f6a-9fee-963e714ae848','PAYMENT_REQUESTED','2026-07-19 06:28:00'),(37,'34ea8153-f688-450b-a227-0200062dc373','PAYMENT_REQUESTED','2026-07-19 07:01:43'),(38,'4db8115a-ec42-4de0-bc12-62feb062b3f8','PAYMENT_REQUESTED','2026-07-19 08:02:54'),(39,'db7e232b-2cc2-4851-b296-0f425f140701','PAYMENT_REQUESTED','2026-07-19 08:09:14'),(40,'9b61ed2c-a74a-4449-ac67-356f378d28e5','PAYMENT_REQUESTED','2026-07-19 08:11:42'),(41,'c779c55b-51fa-4115-b971-b734ee98f8a3','PAYMENT_REQUESTED','2026-07-19 08:11:54'),(42,'0ea2b933-42e4-4419-bbef-d97089f5b4af','PAYMENT_REQUESTED','2026-07-19 08:17:20'),(43,'c5dfc01e-ca2f-42f2-8668-41f9a456d3ee','PAYMENT_REQUESTED','2026-07-19 08:18:32'),(44,'801361f7-0bfb-4a1c-85b5-84dc7c6a1bc5','PAYMENT_REQUESTED','2026-07-19 08:33:07'),(45,'7e55a55d-27aa-43a6-9fdb-cdddb7d8020a','PAYMENT_REQUESTED','2026-07-19 09:19:39'),(46,'a3def1ad-bf07-44f3-a5bd-f700d7475a6f','PAYMENT_REQUESTED','2026-07-19 09:33:12'),(47,'b17a3a42-22b2-4b9d-b94a-e22238b5f20f','PAYMENT_REQUESTED','2026-07-19 09:33:41'),(48,'9b257581-b937-42a8-8c54-2c65aadb0820','PAYMENT_REQUESTED','2026-07-19 09:36:57'),(49,'ddc3426f-a5c3-4ca3-840e-835b35b990cc','PAYMENT_REQUESTED','2026-07-19 09:58:05'),(50,'9218e229-694b-43bb-8eee-c0b09aac6be1','PAYMENT_REQUESTED','2026-07-19 09:58:52'),(51,'9e0203a4-6605-4311-927e-58883e4bd882','PAYMENT_REQUESTED','2026-07-19 10:03:46'),(52,'df3023dc-eba1-4e63-8c50-c0072b9a97f8','PAYMENT_REQUESTED','2026-07-19 10:08:52'),(53,'ee976c07-2113-4a95-af74-2d4927140989','PAYMENT_REQUESTED','2026-07-19 10:09:48'),(54,'1ee60df9-f085-49e7-8f8f-fee5c3958c7c','PAYMENT_REQUESTED','2026-07-19 10:34:22'),(55,'5059d063-ce76-4e35-84e4-3b848b25a82a','PAYMENT_REQUESTED','2026-07-19 10:34:31'),(56,'11432915-93f0-4c9a-b7a8-74c9c833fa31','PAYMENT_REQUESTED','2026-07-19 10:35:16'),(57,'b6e1d00b-a2b6-42e7-bf89-9722b9ed65e2','PAYMENT_REQUESTED','2026-07-19 10:35:59'),(58,'15352eef-c771-4aee-95b3-b1de528b333b','PAYMENT_REQUESTED','2026-07-19 10:38:56'),(59,'6d5fc0f7-d2a7-4100-8e8f-f619a61061d0','PAYMENT_REQUESTED','2026-07-19 10:57:10'),(60,'d5a358bf-8a2c-4819-8a2d-f89861cee8a5','PAYMENT_REQUESTED','2026-07-19 10:58:05'),(61,'e3fbc7cb-d931-415f-bb69-15ec6bf094e5','PAYMENT_REQUESTED','2026-07-19 10:58:49'),(62,'28b19f0d-6f59-4e18-bb74-3a55f38ab6dc','PAYMENT_REQUESTED','2026-07-19 11:01:31'),(63,'17fdf572-ec21-443d-a163-770bf53dd60c','PAYMENT_REQUESTED','2026-07-19 11:04:13'),(64,'3642c0f7-f160-44ae-a098-e05130783210','PAYMENT_REQUESTED','2026-07-19 11:05:58'),(65,'6e7f172e-f291-488f-b274-93ac5cb639cd','PAYMENT_REQUESTED','2026-07-19 11:08:56'),(66,'658bee05-4406-48fa-b23b-49d7d82346e1','PAYMENT_REQUESTED','2026-07-19 11:10:18'),(67,'55ee0da5-1cd4-412c-a2fc-d0b76aa960ba','PAYMENT_REQUESTED','2026-07-19 11:12:39'),(68,'990c70e1-8e16-47fb-baa9-d719da6cb40b','PAYMENT_REQUESTED','2026-07-19 11:30:05'),(69,'4b1ce976-c437-42e8-af7f-4e627fa329b8','PAYMENT_REQUESTED','2026-07-19 11:30:19'),(70,'15b3e8e0-e80d-46e9-8990-19c18aebfe7a','PAYMENT_REQUESTED','2026-07-19 11:30:31'),(71,'b64be2c3-ca4d-4295-93bd-2e5038c5e83c','PAYMENT_REQUESTED','2026-07-19 13:12:02'),(72,'20a4d3a5-c5dd-4653-85c1-a6c4f171acdf','PAYMENT_REQUESTED','2026-07-19 14:11:10'),(73,'5a9648e4-89b2-43ba-840e-c2df0575a07e','PAYMENT_REQUESTED','2026-07-19 16:27:02'),(74,'ff57a510-63e0-4634-9e58-104769aff56e','PAYMENT_REQUESTED','2026-07-19 16:32:17'),(75,'7f1d6554-a67c-4dbf-bb1e-05c309fbfbcf','PAYMENT_REQUESTED','2026-07-19 16:33:29'),(76,'9a4c3d21-987a-4061-800d-fc6dc6516333','PAYMENT_REQUESTED','2026-07-19 16:34:45'),(77,'98b91160-4395-47d6-8986-b7e8b300103f','PAYMENT_REQUESTED','2026-07-19 18:19:42'),(78,'daacae30-3d1d-4c2e-be52-3d20b5787912','PAYMENT_REQUESTED','2026-07-19 18:19:51'),(79,'a54ec413-ac5b-4b9f-81ac-8cde804f186c','PAYMENT_REQUESTED','2026-07-19 18:22:07'),(80,'6c0100bb-ff5d-41a5-9b74-d75b611f8426','PAYMENT_REQUESTED','2026-07-19 18:24:44'),(81,'bfc147e5-8061-40b9-a416-feee973a079e','PAYMENT_REQUESTED','2026-07-20 16:01:47'),(82,'70c13b8d-0000-4850-ba77-e633ee8baeed','PAYMENT_REQUESTED','2026-07-20 16:03:00'),(83,'445e47c0-4b6d-4bed-976e-38f8b61c5593','PAYMENT_REQUESTED','2026-07-20 18:21:33'),(84,'09d2e9b9-6af5-4a10-8522-563fdd0ef1fa','PAYMENT_REQUESTED','2026-07-21 17:52:44'),(85,'f720c72c-4af4-48b2-80d7-d11b2101a453','PAYMENT_REQUESTED','2026-07-21 17:58:15'),(86,'d5a0f011-6629-40bb-afa7-33013da0299c','PAYMENT_REQUESTED','2026-07-21 18:02:29'),(87,'1ba5d8aa-c033-49bc-8e45-d3ec30bdde96','PAYMENT_REQUESTED','2026-07-21 18:22:24'),(88,'d2353b06-11d2-4c04-882b-510d230069b6','PAYMENT_REQUESTED','2026-07-21 18:29:22'),(89,'89b35d83-f069-4788-b7f8-4e95441ea17a','PAYMENT_REQUESTED','2026-07-21 18:30:40'),(90,'49306401-ed1a-4e89-9635-fd0b96038b44','PAYMENT_REQUESTED','2026-07-22 04:04:36'),(91,'cf4bf87d-7e73-4ee1-b40d-4638d44318c8','PAYMENT_REQUESTED','2026-07-22 04:28:48'),(92,'98b6d0f2-e921-4757-8d63-ce4c6d7eff71','PAYMENT_REQUESTED','2026-07-22 04:29:50'),(93,'4fe62f60-ffa2-4700-b2ee-d943c14fafd6','PAYMENT_REQUESTED','2026-07-22 05:06:37'),(94,'8555e917-0c65-4520-9884-6001f04b39b5','PAYMENT_REQUESTED','2026-07-22 05:14:10'),(95,'a165eb12-a703-4c58-8052-7069deb45589','PAYMENT_REQUESTED','2026-07-22 05:18:19'),(96,'07a77925-482d-4a42-9838-64ad3a37d7ab','PAYMENT_REQUESTED','2026-07-22 05:27:05'),(97,'52768797-7d8e-47ea-910b-084ac8db73dc','PAYMENT_REQUESTED','2026-07-22 05:29:55'),(98,'b8637238-4292-4eac-b454-6b164603cab9','PAYMENT_REQUESTED','2026-07-22 05:29:58'),(99,'45449077-85ae-45e7-9e84-98a6244f4f28','PAYMENT_REQUESTED','2026-07-22 05:52:23'),(100,'1dfe2bbf-218e-4ae7-9edd-992cc6bf7172','PAYMENT_REQUESTED','2026-07-22 05:54:52'),(101,'1c654274-caa8-4269-b47e-27bce8e866de','PAYMENT_REQUESTED','2026-07-22 07:04:25'),(102,'8e6b0346-c037-44ba-88f4-b29b12b7259b','PAYMENT_REQUESTED','2026-07-22 07:18:09'),(103,'22d865cf-8067-4514-badc-4dc3a1e5f8a9','PAYMENT_REQUESTED','2026-07-22 07:33:14'),(104,'cd0fcad2-7985-4e60-9ce5-200a16418b3e','PAYMENT_REQUESTED','2026-07-22 07:36:57'),(105,'d26ecb03-1ebf-4e86-bbc0-3fb80e0a4cf9','PAYMENT_REQUESTED','2026-07-22 08:06:35'),(106,'edcb8be2-b1fe-4387-91e2-8e0cfdde4745','PAYMENT_REQUESTED','2026-07-22 10:48:42'),(107,'5549c8fc-585a-41a1-9acb-136564b86559','PAYMENT_REQUESTED','2026-07-22 13:24:35'),(108,'fd0cfa22-941d-49b0-bb97-5acaa7aaac17','PAYMENT_REQUESTED','2026-07-22 13:31:44'),(109,'cd12a8d9-59ff-4bd7-b04c-a9b6d1f72c17','PAYMENT_REQUESTED','2026-07-22 14:26:14'),(110,'c350db32-5e9e-4245-a54d-bf98557a2580','PAYMENT_REQUESTED','2026-07-23 15:18:59'),(111,'3d26f841-38df-4521-9d73-1ee87e1894c2','PAYMENT_REQUESTED','2026-07-24 18:14:25'),(112,'e833a9e2-d6a4-4de6-b43d-74c5fbbddd85','PAYMENT_REQUESTED','2026-07-25 13:48:09');
/*!40000 ALTER TABLE `processed_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'payment_db'
--

--
-- Dumping routines for database 'payment_db'
--
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-26 20:51:09
