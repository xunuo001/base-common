/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50528
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 50528
 File Encoding         : 65001

 Date: 17/09/2019 15:41:04
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- persistent_logins表，用户实现记住我功能
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins`
(
    `series`    varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'id',
    `username`  varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '登陆账号',
    `token`     varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'cookie令牌',
    `last_used` timestamp                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT = 'persistent_logins表，用户实现记住我功能' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for sys_authority
-- ----------------------------
DROP TABLE IF EXISTS `sys_authority`;
CREATE TABLE `sys_authority`
(
    `authority_id`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限id',
    `authority_name`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限名称，ROLE_开头，全大写',
    `authority_remark`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限描述',
    `create_time`       datetime                                                NOT NULL COMMENT '创建时间',
    `update_time`       datetime                                                NOT NULL COMMENT '修改时间',
    `authority_content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限内容，可访问的url，多个时用,隔开',
    PRIMARY KEY (`authority_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统权限表' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `menu_id`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单id',
    `menu_name`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
    `menu_path`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单路径',
    `menu_parent_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级id',
    `sort_weight`    int(2) NULL DEFAULT NULL COMMENT '同级排序权重：0-10',
    `create_time`    datetime                                                NOT NULL COMMENT '创建时间',
    `update_time`    datetime                                                NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统菜单表' ROW_FORMAT = Compact;



-- ----------------------------
-- Table structure for sys_setting
-- ----------------------------
DROP TABLE IF EXISTS `sys_setting`;
CREATE TABLE `sys_setting`
(
    `id`                           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表id',
    `sys_name`                     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统名称',
    `sys_logo`                     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统logo图标',
    `sys_bottom_text`              varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统底部信息',
    `sys_notice_text`              longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '系统公告',
    `create_time`                  datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`                  datetime NULL DEFAULT NULL COMMENT '修改时间',
    `user_init_password`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户管理：初始、重置密码',
    `sys_color`                    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '系统颜色',
    `sys_api_encrypt`              char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'API加密 Y/N',
    `sys_open_api_limiter_encrypt` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'OpenAPI限流 Y/N',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统设置表' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for sys_shortcut_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_shortcut_menu`;
CREATE TABLE `sys_shortcut_menu`
(
    `shortcut_menu_id`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户快捷菜单id',
    `shortcut_menu_name`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户快捷菜单名称',
    `shortcut_menu_path`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户快捷菜单路径',
    `user_id`                 varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
    `shortcut_menu_parent_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级id',
    `sort_weight`             int(2) NULL DEFAULT NULL COMMENT '同级排序权重：0-10',
    `create_time`             datetime                                                NOT NULL COMMENT '创建时间',
    `update_time`             datetime                                                NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`shortcut_menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户快捷菜单表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`              varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
    `login_name`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名',
    `user_name`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名称',
    `phone`                varchar(11) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '手机号',
    `company`              varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '公司',
    `location`             varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '地点',
    `email`                varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '邮箱',
    `password`             varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录密码',
    `valid`                char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NOT NULL COMMENT '软删除标识，Y/N',
    `limited_ip`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '限制允许登录的IP集合',
    `expired_time`         datetime NULL DEFAULT NULL COMMENT '账号失效时间，超过时间将不能登录系统',
    `last_change_pwd_time` datetime                                                NOT NULL COMMENT '最近修改密码时间，超出时间间隔，提示用户修改密码',
    `last_login_time`      datetime                                                NOT NULL COMMENT '最近登录时间，最后活跃时间',
    `limit_multi_login`    char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NOT NULL COMMENT '是否允许账号同一个时刻多人在线，Y/N',
    `create_time`          datetime                                                NOT NULL COMMENT '创建时间',
    `update_time`          datetime                                                NOT NULL COMMENT '修改时间',
    `coin_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'coin',
    PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统用户表' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for sys_user_authority
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_authority`;
CREATE TABLE `sys_user_authority`
(
    `user_authority_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户权限表id',
    `user_id`           varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
    `authority_id`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '权限id',
    `create_time`       datetime                                                NOT NULL COMMENT '创建时间',
    `update_time`       datetime                                                NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`user_authority_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户权限表' ROW_FORMAT = Compact;


-- ----------------------------
-- Table structure for sys_user_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_menu`;
CREATE TABLE `sys_user_menu`
(
    `user_menu_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户菜单表id',
    `user_id`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
    `menu_id`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单id',
    `create_time`  datetime                                                NOT NULL COMMENT '创建时间',
    `update_time`  datetime                                                NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`user_menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户菜单表' ROW_FORMAT = Compact;



-- ----------------------------
DROP TABLE IF EXISTS `vcoin_incr_history`;
CREATE TABLE `vcoin_incr_history`
(
    `id`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表id',
    `user_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
    `coin_num`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单id',
    `create_time` datetime   NOT NULL COMMENT '创建时间',
    `type`     varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '消费类型',
    `operation_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '虚拟币表' ROW_FORMAT = Compact;

DROP TABLE IF EXISTS `vcoin_cost_history`;
CREATE TABLE `vcoin_cost_history`
(
    `id`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表id',
    `user_name`     varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
    `coin_num` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'coin',
    `create_time`     datetime                                                NOT NULL COMMENT '消费时间',
    `type`     varchar(25) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '消费类型',
    `operation_name`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '操作人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '消费历史表' ROW_FORMAT = Compact;

DROP TABLE IF EXISTS `black_list`;
CREATE TABLE `black_list`
(
    `ip`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '黑名单ip',
    PRIMARY KEY (`ip`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '黑名单表' ROW_FORMAT = Compact;


