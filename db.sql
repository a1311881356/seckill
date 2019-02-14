create database seckill CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';
drop database seckill;
SHOW TABLES;

create table seckill_user(
	id bigint(20) not null comment '用户id，手机号码',
	nickname varchar(225) not null,
	password varchar(32) default null comment 'MD5(MD5(pass明文+固定salt)+salt)',
	salt varchar(10) default null,
	head varchar(128) default null comment '头像，云存储的ID',
	register_date datetime default null comment '注册时间',
	last_login_date datetime default null comment '上次登陆时间',
	login_count int(11) default '0' comment '登陆次数',
	primary key(id)
) engine=innoDB default charset=utf8mb4;
delete from seckill_user where id=13142388075
insert into seckill_user value(13142388075,'lbw',
'79ab8966070eab9ab3c37efa2450f010','de45hrda4',null,now(),now(),0);
select count(*) from seckill_user;
select 1

CREATE TABLE goods (
	id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商 品ID',
	goods_name varchar(16) DEFAULT NULL COMMENT '商品名称备',
	goods_title varchar(64) DEFAULT NULL COMMENT '商品标题',
	goods_img varchar(64) DEFAULT NULL COMMENT '商品图片',
	goods_detail longtext COMMENT '商品的详情介绍',
	goods_price decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
	goods_stock int(11) DEFAULT '0' COMMENT '商品库存,-1表示没有限制',
	PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
insert into goods values(1,'iphoneX','Apple iPhone X(A1865)
 64GB 银色 移动联通电信4G手机','/img/iphonex.png','详情...',8765,100);
insert into goods values(2,'小米Max','小米Max
 64GB 银色 移动联通电信4G大屏手机/平板','/img/xiaomimax.png','详情...',3212,110);
select * from goods;


CREATE TABLE seckill_goods(
	id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
	goods_id bigint(20) DEFAULT NULL COMMENT '商品Id',
	miaosha_price decimal(10,2) DEFAULT '0.00' COMMENT '秒杀价',
	stock_count int(11) DEFAULT NULL COMMENT '库存数量',
	start_date datetime DEFAULT NULL COMMENT '秒杀开始时间',
	end_date datetime DEFAULT NULL COMMENT '秒杀结束时间',
	PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
drop table seckill_goods;
insert into seckill_goods values(1,1,0.01,4,'2019-1-22 15:18:00','2019-2-20 15:18:00'),(2,2,0.01,9,'2019-2-1 15:18:00','2019-2-23 15:18:00');
select * from seckill_goods;
update seckill_goods set end_date='2019-2-20 14:53:00';


CREATE TABLE order_info(
	id bigint(20) NOT NULL AUTO_INCREMENT,
	user_id bigint(20) DEFAULT NULL COMMENT '用户ID',
	goods_id bigint(20) DEFAULT NULL COMMENT '商品ID',
	delivery_addr_id bigint(20) DEFAULT NULL COMMENT '收货地址ID',
	goods_name varchar(16) DEFAULT NULL COMMENT '冗余的商品名称',
	goods_count int(11) DEFAULT '0' COMMENT '商品数量',
	goods_price decimal(10,2) DEFAULT '0.00' COMMENT '商品单价',
	order_channel tinyint(4) DEFAULT '0' COMMENT '1pc,2android,3ios',
	status tinyint(4) DEFAULT '0' COMMENT '订单状态：0:新建未支付,1:已支付,2:已发货,3:已收货,4:已退货,5:已完成', 
	create_date datetime DEFAULT NULL COMMENT '订单的创建时间',
	pay_date datetime COMMENT '支付时间',
PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;
select * from order_info where user_id=13142388075;

CREATE TABLE seckill_order (
	id bigint(20) NOT NULL AUTO_INCREMENT,
	user_id bigint(20) DEFAULT NULL COMMENT '用户ID',
	order_id bigint(20) DEFAULT NULL COMMENT '订单ID',
	goods_id bigint(20) DEFAULT NULL COMMENT '商品ID',
	PRIMARY KEY (id),
	
)ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
防止同一个人秒杀由于同时发多个请求而秒杀到多个>>>>>>>>>>>>create unique index index_userId_goodsId on seckill_order(user_id,goods_id);
drop table seckill_order;
select * from seckill_order where user_id=13142388075;

select * from order_info where user_id=13142388075;
select * from seckill_order where user_id=13142388075;
#删除秒杀结果
delete from seckill_order;
delete from order_info;
update seckill_goods set stock_count=100 where id=1;
