CREATE TABLE `searchdata_admin` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `product_id` char(64) DEFAULT NULL,
  `product_name` text,
  `brand_name` text,
  `url` text,
  `thumb_url` text,
  `thumb_big_url` text CHARACTER SET utf8mb4,
  `org_price` int(11) DEFAULT NULL,
  `sale_price` int(11) DEFAULT NULL,
  `sale_per` float DEFAULT NULL,
  `cp` text,
  `keyword` text,
  `status` char(11) DEFAULT '',
  `seed_url` text,
  `date` text,
  `cate1` varchar(4) DEFAULT '',
  `cate2` varchar(4) DEFAULT '',
  `cate3` varchar(4) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=450806 DEFAULT CHARSET=utf8;


CREATE TABLE `crawl_data` (
`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
`url` text,
`date` text,
`savepath` text,
`cp` text,
`keyword` text,
`md5` text,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12736 DEFAULT CHARSET=utf8;


CREATE TABLE `search` (
`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
`product_id` char(64) DEFAULT NULL,
`product_name` text,
`brand_name` text,
`url` text,
`thumb_url` text,
`org_price` int(11) DEFAULT NULL,
`sale_price` int(11) DEFAULT NULL,
`sale_per` float DEFAULT NULL,
`cp` text,
`keyword` text,
`status` char(11) DEFAULT NULL,
`seed_url` text,
`date` text,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=467279 DEFAULT CHARSET=utf8;


CREATE TABLE `seed` (
`id` int(11) unsigned NOT NULL AUTO_INCREMENT,
`keyword` text,
`url` text,
`cp` text,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1551 DEFAULT CHARSET=utf8;


# user 생성
## remote에서 mysql 접속 가능한 user 생성
mysql> create user 'erpy'@'%' identified by 'erpy000';
mysql> grant all privileges on *.* to 'erpy'@'%';
mysql> grant all privileges on search_db.* to 'erpy'@'%';
mysql> FLUSH PRIVILEGES;


# database 생성
mysql> create database social default character set utf8 collate utf8_general_ci;

# mysql 한글 설정
# my.cnf 파일에 아래 설정 등록

[mysql]
default-character-set = utf8

[client]
default-character-set = utf8

[mysqld]
character-set-client-handshake=FALSE
init_connect="SET collation_connection = utf8_general_ci"
init_connect="SET NAMES utf8"
character-set-server = utf8
collation-server = utf8_general_ci

[mysqldump]
default-character-set = utf8