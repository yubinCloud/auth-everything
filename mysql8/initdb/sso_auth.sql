/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80018
 Source Host           : localhost:3306
 Source Schema         : sso_auth

 Target Server Type    : MySQL
 Target Server Version : 80018
 File Encoding         : 65001

 Date: 24/10/2023 15:09:40
*/
CREATE DATABASE IF NOT EXISTS sso_auth;

USE sso_auth;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `role_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '角色 ID',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `permission_list` json NOT NULL COMMENT '该角色所拥有的的权限',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, 'super-admin', '[\"avue:84\", \"avue:88\"]');
INSERT INTO `role` VALUES (2, 'normal', '[]');
INSERT INTO `role` VALUES (3, 'visitor', '[]');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码（经 md5 哈希）',
  `screen_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `role_list` json NOT NULL COMMENT '角色列表',
  `permission_list` json NOT NULL,
  `note` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `create_time` bigint(20) NULL DEFAULT NULL COMMENT '用户创建时间的 Unix 时间戳，是从1970年1月1日开始的秒数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (5, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'admin', '[1]', '[\"i:nacos\", \"avue:vs:10\"]', '初始化的管理员', 1691140213);
INSERT INTO `user` VALUES (12, 'yubin', '0c9c88b6ddd4079135cc369c75b1704a', 'yubin', '[1]', '[]', '测试人员', 1691140213);
INSERT INTO `user` VALUES (35, 'normal', '0c9c88b6ddd4079135cc369c75b1704a', 'normal', '[2]', '[]', NULL, 1693363062);
INSERT INTO `user` VALUES (39, 'test1017', '6e162994857b0a3bce163455bf02239c', 'test1017', '[2]', '[\"avue:vs:10\"]', '', 1697531558);

SET FOREIGN_KEY_CHECKS = 1;
