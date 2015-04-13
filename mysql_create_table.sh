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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=467279 DEFAULT CHARSET=utf8;

CREATE TABLE `seed` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `keyword` text,
  `url` text,
  `cp` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1551 DEFAULT CHARSET=utf8;