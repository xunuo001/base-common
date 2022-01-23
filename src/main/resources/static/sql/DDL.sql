INSERT INTO base.sys_authority (authority_id,authority_name,authority_remark,create_time,update_time,authority_content) VALUES
	 ('1','ROLE_USER','普通用户','2019-09-10 10:08:58','2019-09-10 10:08:58',''),
	 ('2','ROLE_SA','超级管理员','2019-09-10 10:08:58','2019-09-10 10:08:58','/sys/**,/logging'),
	 ('3','ROLE_ADMIN','管理员','2019-09-10 10:08:58','2022-01-23 21:03:56','/sys/**,/vcoin/cost,/vcoin/incr/increase,/vcoin/cost/page');

INSERT INTO base.sys_menu (menu_id,menu_name,menu_path,menu_parent_id,sort_weight,create_time,update_time) VALUES
	 ('35cb950cebb04bb18bb1d8b742a02005','图片测试','/image','',1,'2019-09-11 18:05:21','2019-09-11 18:05:21'),
	 ('35cb950cebb04bb18bb1d8b742a02xaa','权限管理','/sys/sysAuthority/authority','35cb950cebb04bb18bb1d8b742a02xxx',1,'2019-09-10 10:08:58','2019-09-10 10:08:58'),
	 ('35cb950cebb04bb18bb1d8b742a02xcc','菜单管理','/sys/sysMenu/menu','35cb950cebb04bb18bb1d8b742a02xxx',2,'2019-09-10 10:08:58','2019-09-10 10:08:58'),
	 ('35cb950cebb04bb18bb1d8b742a02xxx','系统管理','/sys','',0,'2019-09-10 10:08:58','2019-09-10 10:08:58'),
	 ('35cb950cebb04bb18bb1d8b742a02xzz','用户管理','/sys/sysUser/user','35cb950cebb04bb18bb1d8b742a02xxx',3,'2019-09-10 10:08:58','2019-09-10 10:08:58'),
	 ('366ea3e9f1584b92b85c841496e6e103','添加虚拟币','/vcoin/incr/index','78fb15a19e894777afbc239d574da423',2,'2020-10-30 17:02:15','2022-01-23 14:05:49'),
	 ('74315e162f524a4d88aa931f02416f26','实时监控','/monitor','35cb950cebb04bb18bb1d8b742a02xxx',4,'2020-06-10 15:07:07','2020-06-10 15:07:07'),
	 ('78fb15a19e894777afbc239d574da423','虚拟币','/vcoin','',2,'2020-10-16 13:04:41','2020-10-16 13:04:41'),
	 ('914aa22c78af4327822061f3eada4067','实时日志','/logging','35cb950cebb04bb18bb1d8b742a02xxx',5,'2019-09-11 11:19:52','2019-09-11 11:19:52'),
	 ('bcf17dc0ce304f0ba02d64ce21ddb4f9','系统设置','/sys/sysSetting/setting','35cb950cebb04bb18bb1d8b742a02xxx',0,'2019-09-17 10:46:11','2019-09-17 10:46:11');
INSERT INTO base.sys_menu (menu_id,menu_name,menu_path,menu_parent_id,sort_weight,create_time,update_time) VALUES
	 ('c6568158891142eb9688d9b29b8f818f','消费记录','/vcoin/cost/index','78fb15a19e894777afbc239d574da423',1,'2020-10-30 17:02:15','2022-01-23 14:05:43'),
	 ('d6d8b301316b43f2a9fb33caf1286376','新增测试','/aa','35cb950cebb04bb18bb1d8b742a02xxx',6,'2020-10-30 17:02:15','2020-10-30 17:02:15');

	 INSERT INTO base.sys_user_menu (user_menu_id,user_id,menu_id,create_time,update_time) VALUES
	 ('1184ccd6770543b0a067f0bbde362eda','4e008a88939f45aa9d73f5e473e35e3e','366ea3e9f1584b92b85c841496e6e103','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('1337996a0aba460bbf0b82db9a1da207','1','35cb950cebb04bb18bb1d8b742a02xxx','2020-06-10 15:07:23','2020-06-10 15:07:23'),
	 ('13fb3fd5b6d448aa86c33c8adb52df97','2','35cb950cebb04bb18bb1d8b742a02xxx','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('1a2593ad2f6b4008bc7b6f9e4a70805a','2','366ea3e9f1584b92b85c841496e6e103','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('1d073061c633499e91a2238f6b5b7bd9','4e008a88939f45aa9d73f5e473e35e3e','74315e162f524a4d88aa931f02416f26','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('27c0c77a87e04c4fb05cdda5463a0247','2','35cb950cebb04bb18bb1d8b742a02xaa','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('3294667e4dde4aa2b88bb01e57708caa','2','35cb950cebb04bb18bb1d8b742a02xzz','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('39d6a157972e400fabcd753d3766efc9','1','35cb950cebb04bb18bb1d8b742a02xaa','2020-06-10 15:07:23','2020-06-10 15:07:23'),
	 ('3b0a160e8c624b2f86918afcd5107704','1','35cb950cebb04bb18bb1d8b742a02xcc','2020-06-10 15:07:23','2020-06-10 15:07:23'),
	 ('4fa28cc876f24786a29470dc242d0446','4e008a88939f45aa9d73f5e473e35e3e','c6568158891142eb9688d9b29b8f818f','2022-01-23 20:14:11','2022-01-23 20:14:11');
INSERT INTO base.sys_user_menu (user_menu_id,user_id,menu_id,create_time,update_time) VALUES
	 ('565eeee34dfc4c919ea37b08bf3bc24f','4e008a88939f45aa9d73f5e473e35e3e','35cb950cebb04bb18bb1d8b742a02005','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('602314e8e5ba43c79bec3c8aff88ac84','4e008a88939f45aa9d73f5e473e35e3e','d6d8b301316b43f2a9fb33caf1286376','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('6117b88110164ea49072fc71b07fd110','4e008a88939f45aa9d73f5e473e35e3e','35cb950cebb04bb18bb1d8b742a02xaa','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('67016e6279174e80a5d40ca2823e6066','4e008a88939f45aa9d73f5e473e35e3e','914aa22c78af4327822061f3eada4067','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('6afadafdc36c426182853761bf68d870','1','74315e162f524a4d88aa931f02416f26','2020-06-10 15:07:23','2020-06-10 15:07:23'),
	 ('6ed01b78c45642fbb7491da0a66b3fd3','2','35cb950cebb04bb18bb1d8b742a02xcc','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('7697d6744ce54b30a5d44d7f33d780e9','4e008a88939f45aa9d73f5e473e35e3e','78fb15a19e894777afbc239d574da423','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('81f4999dde514e0ea43acfc70bfd35a8','1','914aa22c78af4327822061f3eada4067','2020-06-10 15:07:23','2020-06-10 15:07:23'),
	 ('89a9e50e6af5416bb7ffe8686ccaf895','2','35cb950cebb04bb18bb1d8b742a02005','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('9142240c412d4e4eae78b66874f5a6cf','4e008a88939f45aa9d73f5e473e35e3e','35cb950cebb04bb18bb1d8b742a02xzz','2022-01-23 20:14:11','2022-01-23 20:14:11');
INSERT INTO base.sys_user_menu (user_menu_id,user_id,menu_id,create_time,update_time) VALUES
	 ('a29460c8eb454cc694077108b33aa258','4e008a88939f45aa9d73f5e473e35e3e','bcf17dc0ce304f0ba02d64ce21ddb4f9','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('bc83b870176e43cf8fd20323b07eb732','2','78fb15a19e894777afbc239d574da423','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('c05095c42bed43bf9eb5484b8f9a00a1','2','c6568158891142eb9688d9b29b8f818f','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('cbf9edf24fb944f3a70caa70e006760e','4e008a88939f45aa9d73f5e473e35e3e','35cb950cebb04bb18bb1d8b742a02xxx','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('cdf8f786c658437ba77eb7d7fdd6b9cb','1','35cb950cebb04bb18bb1d8b742a02xzz','2020-06-10 15:07:23','2020-06-10 15:07:23'),
	 ('d646090ba4114c85b0a2fc2c9082a188','1','bcf17dc0ce304f0ba02d64ce21ddb4f9','2020-06-10 15:07:23','2020-06-10 15:07:23'),
	 ('f908b31dba0241aab7f0f0a12ccffddd','4e008a88939f45aa9d73f5e473e35e3e','35cb950cebb04bb18bb1d8b742a02xcc','2022-01-23 20:14:11','2022-01-23 20:14:11');

	 INSERT INTO base.sys_shortcut_menu (shortcut_menu_id,shortcut_menu_name,shortcut_menu_path,user_id,shortcut_menu_parent_id,sort_weight,create_time,update_time) VALUES
	 ('104370a3fa7948bab156afd4a5f2a730','个性化菜单','','1','',0,'2019-09-12 18:35:13','2019-09-12 18:35:13'),
	 ('bd36d4507327434eb57b67b21343246f','腾讯云','https://cloud.tencent.com/','1','104370a3fa7948bab156afd4a5f2a730',0,'2020-10-15 18:42:39','2020-12-31 14:44:18'),
	 ('c309dafafe964a9d8de021b0da193bad','啊啊啊','/aaa','1','db234c8f4fad4b0c9a4522e872c0f588',0,'2020-10-16 10:20:20','2020-10-16 10:20:20'),
	 ('cf78ced9ce7b480c85812540d1936145','百度','https://www.baidu.com','1','104370a3fa7948bab156afd4a5f2a730',2,'2019-09-12 18:35:39','2020-12-31 14:44:27'),
	 ('cf78ced9ce7b480c85fd2540d1936145','阿里云','https://www.aliyun.com/','1','104370a3fa7948bab156afd4a5f2a730',1,'2019-09-12 18:35:39','2020-12-31 14:44:23'),
	 ('db234c8f4fad4b0c9a4522e872c0f588','菜单2','','1','',1,'2020-10-16 10:20:02','2020-10-16 10:20:02');

	 INSERT INTO base.sys_user (user_id,login_name,user_name,phone,company,location,email,password,valid,limited_ip,expired_time,last_change_pwd_time,last_login_time,limit_multi_login,create_time,update_time,coin_num) VALUES
	 ('1','sa','超级管理员','13512530160','','','','E10ADC3949BA59ABBE56E057F20F883E','Y','',NULL,'2019-09-17 12:00:36','2022-01-23 16:03:43','Y','2019-07-19 16:36:03','2019-09-17 12:00:36','0'),
	 ('2','admin','管理员','13512530161','','','','E10ADC3949BA59ABBE56E057F20F883E','Y','',NULL,'2019-09-17 12:00:36','2022-01-23 21:14:37','N','2019-07-19 16:36:03','2022-01-23 20:13:26','23'),
	 ('4e008a88939f45aa9d73f5e473e35e3e','test','xn','123213','','','','E10ADC3949BA59ABBE56E057F20F883E','Y','',NULL,'2022-01-23 20:13:50','2022-01-23 21:04:11','N','2022-01-23 20:13:50','2022-01-23 20:14:11','1');

	 INSERT INTO base.sys_user_authority (user_authority_id,user_id,authority_id,create_time,update_time) VALUES
	 ('5564d82041684de38d8e77848dbed504','2','1','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('9ca34956ceae4af0a74f4931344e9d1b','1','1','2019-09-17 12:00:37','2019-09-17 12:00:37'),
	 ('ab318a626bb24d9ea56ddef6692109f6','4e008a88939f45aa9d73f5e473e35e3e','1','2022-01-23 20:14:11','2022-01-23 20:14:11'),
	 ('b34f7092406c46189fee2690d9f6e493','1','2','2019-09-17 12:00:37','2019-09-17 12:00:37'),
	 ('dfd2f996c9f14e7484b6131ec82cb97c','2','3','2022-01-23 20:13:27','2022-01-23 20:13:27'),
	 ('f6514b57d1524afd8dfa7cb2c3ca6a11','1','3','2019-09-17 12:00:37','2019-09-17 12:00:37');

	 INSERT INTO base.sys_setting (id,sys_name,sys_logo,sys_bottom_text,sys_notice_text,create_time,update_time,user_init_password,sys_color,sys_api_encrypt,sys_open_api_limiter_encrypt) VALUES
	 ('1','Base Admin','https://avatar.gitee.com/uploads/0/5137900_huanzi-qch.png!avatar100?1562729811','© 2019 - 2020  XXX系统','<h1 style="white-space: normal; text-align: center;"><span style="color: rgb(255, 0, 0);">通知</span></h1><p style="white-space: normal;"><span style="color: rgb(255, 0, 0);">1、不得在公共场合吸烟；</span></p><p style="white-space: normal;"><span style="color: rgb(255, 0, 0);">2、xxxxxxx；</span></p><p><br/></p>','2019-09-17 10:15:38','2019-09-17 10:15:40','123456','rgba(54, 123, 183,  0.73)','Y','N');