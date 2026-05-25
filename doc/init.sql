-- ============================================================
-- 教学数字平台管理系统 —— 数据库初始化脚本
-- 版本: v1.0
-- 说明: 执行此脚本将创建 edu 数据库及全部 8 张业务表，
--       并插入默认管理员账号。
-- ============================================================

-- 创建数据库（如已存在则先删除，开发环境使用）
DROP DATABASE IF EXISTS `edu`;
CREATE DATABASE `edu` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `edu`;

-- ============================================================
-- 1. user —— 用户表（管理员账号）
-- ============================================================
CREATE TABLE `user` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `username`          VARCHAR(50)  NOT NULL                         COMMENT '用户名，登录用',
    `password`          VARCHAR(128) NOT NULL                         COMMENT '密码，BCrypt 加密存储',
    `real_name`         VARCHAR(30)  NOT NULL                         COMMENT '真实姓名',
    `status`            TINYINT      NOT NULL DEFAULT 1               COMMENT '账号状态：1=正常，0=锁定',
    `login_error_count` INT          NOT NULL DEFAULT 0               COMMENT '连续登录失败次数',
    `locked_until`      DATETIME     DEFAULT NULL                     COMMENT '锁定截止时间，NULL 表示未锁定',
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`        TINYINT      NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表（管理员账号）';

-- ============================================================
-- 2. class —— 班级表
-- ============================================================
CREATE TABLE `class` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `class_name`   VARCHAR(30)  NOT NULL                         COMMENT '班级名称，不可重复',
    `teacher_name` VARCHAR(20)  NOT NULL                         COMMENT '负责老师姓名',
    `description`  VARCHAR(200) DEFAULT NULL                     COMMENT '班级描述',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`   TINYINT      NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_name` (`class_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- ============================================================
-- 3. student —— 学生表
-- ============================================================
CREATE TABLE `student` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `name`            VARCHAR(20)  NOT NULL                         COMMENT '学生姓名',
    `student_no`      VARCHAR(30)  NOT NULL                         COMMENT '学号，全局唯一',
    `gender`          TINYINT      NOT NULL                         COMMENT '性别：1=男，2=女',
    `phone`           VARCHAR(11)  DEFAULT NULL                     COMMENT '联系电话，11 位手机号',
    `class_id`        BIGINT       NOT NULL                         COMMENT '所属班级ID，关联 class 表',
    `enrollment_date` DATE         NOT NULL                         COMMENT '入学日期',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT      NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_no` (`student_no`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- ============================================================
-- 4. schedule —— 课程排课表
-- ============================================================
CREATE TABLE `schedule` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `class_id`     BIGINT       NOT NULL                         COMMENT '班级ID，关联 class 表',
    `course_name`  VARCHAR(20)  NOT NULL                         COMMENT '课程名称，如"语文"、"数学"',
    `teacher_name` VARCHAR(20)  NOT NULL                         COMMENT '任课老师姓名',
    `day_of_week`  TINYINT      NOT NULL                         COMMENT '星期：1=周一，2=周二，3=周三，4=周四，5=周五',
    `time_slot`    TINYINT      NOT NULL                         COMMENT '时间段：1=上午第一节，2=上午第二节，3=下午第一节，4=下午第二节',
    `classroom`    VARCHAR(50)  DEFAULT NULL                     COMMENT '上课地点，如"101 教室"',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`   TINYINT      NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_class_day_slot` (`class_id`, `day_of_week`, `time_slot`) COMMENT '同一班级同一时间段只能排一门课',
    KEY `idx_class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程排课表';

-- ============================================================
-- 5. attendance —— 考勤记录表
-- ============================================================
CREATE TABLE `attendance` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `student_id`  BIGINT       NOT NULL                         COMMENT '学生ID，关联 student 表',
    `class_id`    BIGINT       NOT NULL                         COMMENT '班级ID，记录当日所属班级，关联 class 表',
    `date`        DATE         NOT NULL                         COMMENT '考勤日期',
    `status`      TINYINT      NOT NULL DEFAULT 1               COMMENT '考勤状态：1=出勤，2=迟到，3=请假，4=缺勤',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_date` (`student_id`, `date`) COMMENT '同一学生同一天只能有一条考勤记录',
    KEY `idx_class_id` (`class_id`),
    KEY `idx_date` (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤记录表';

-- ============================================================
-- 6. score —— 成绩表
-- ============================================================
CREATE TABLE `score` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `student_id`  BIGINT       NOT NULL                         COMMENT '学生ID，关联 student 表',
    `subject`     VARCHAR(20)  NOT NULL                         COMMENT '科目名称，如"语文"、"数学"',
    `score`       DECIMAL(5,1) NOT NULL                         COMMENT '成绩，范围 0-100，支持一位小数',
    `exam_date`   DATE         NOT NULL                         COMMENT '考试日期，不能晚于当前日期',
    `exam_type`   TINYINT      NOT NULL                         COMMENT '考试类型：1=期中考试，2=期末考试，3=月考，4=单元测试，5=其他',
    `remark`      VARCHAR(100) DEFAULT NULL                     COMMENT '备注，最多 100 字',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT      NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_student_subject_date_type` (`student_id`, `subject`, `exam_date`, `exam_type`) COMMENT '防止同一学生同一科目同一考试重复录入',
    KEY `idx_student_id` (`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩表';

-- ============================================================
-- 7. homework —— 作业表
-- ============================================================
CREATE TABLE `homework` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `class_id`    BIGINT        NOT NULL                         COMMENT '班级ID，关联 class 表',
    `title`       VARCHAR(50)   NOT NULL                         COMMENT '作业标题，2-50 个字符',
    `content`     VARCHAR(1000) NOT NULL                         COMMENT '作业内容，10-1000 个字符',
    `deadline`    DATETIME      NOT NULL                         COMMENT '提交截止时间，不能早于当前时间',
    `publisher`   VARCHAR(20)   NOT NULL                         COMMENT '发布人姓名',
    `status`      TINYINT       NOT NULL DEFAULT 1               COMMENT '作业状态：1=进行中，2=已关闭',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT       NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_class_id` (`class_id`),
    KEY `idx_deadline` (`deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业表';

-- ============================================================
-- 8. homework_submission —— 作业提交表
-- ============================================================
CREATE TABLE `homework_submission` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT          COMMENT '主键ID',
    `homework_id` BIGINT        NOT NULL                         COMMENT '作业ID，关联 homework 表',
    `student_id`  BIGINT        NOT NULL                         COMMENT '学生ID，关联 student 表',
    `content`     VARCHAR(2000) DEFAULT NULL                     COMMENT '提交内容',
    `submit_time` DATETIME      DEFAULT NULL                     COMMENT '提交时间，提交时自动记录',
    `status`      TINYINT       NOT NULL DEFAULT 1               COMMENT '提交状态：1=未提交，2=已提交，3=已批改',
    `score`       DECIMAL(5,1)  DEFAULT NULL                     COMMENT '评分，范围 0-100',
    `comment`     VARCHAR(200)  DEFAULT NULL                     COMMENT '老师评语，最多 200 字',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT       NOT NULL DEFAULT 0               COMMENT '逻辑删除：0=正常，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_homework_student` (`homework_id`, `student_id`) COMMENT '同一作业同一学生只有一条提交记录',
    KEY `idx_homework_id` (`homework_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';

-- ============================================================
-- 初始数据：插入默认管理员账号
-- 用户名：admin
-- 密码：123456（BCrypt 加密）
-- ============================================================
INSERT INTO `user` (`username`, `password`, `real_name`, `status`)
VALUES ('admin', '$2a$10$NPmGukv0VpKnF/zD0ZBSIevOBLhVM0kkAyMolVC6NErAzH1XMM0xm', '系统管理员', 1);
