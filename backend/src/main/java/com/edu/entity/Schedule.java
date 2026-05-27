package com.edu.entity;

import java.time.LocalDateTime;

/**
 * 课程排课实体
 * <p>对应数据库表 `schedule`</p>
 */
public class Schedule {

    /** 主键ID */
    private Long id;

    /** 班级ID，关联 class 表 */
    private Long classId;

    /** 班级名称（非数据库字段，由联表查询填充） */
    private String className;

    /** 课程名称 */
    private String courseName;

    /** 任课老师姓名 */
    private String teacherName;

    /** 星期：1=周一，2=周二，3=周三，4=周四，5=周五 */
    private Integer dayOfWeek;

    /** 时间段：1=上午第一节，2=上午第二节，3=下午第一节，4=下午第二节 */
    private Integer timeSlot;

    /** 上课地点 */
    private String classroom;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0=正常，1=已删除 */
    private Integer isDeleted;

    // ========== Getter / Setter ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /** 星期中文文本（非数据库字段，由 dayOfWeek 转换） */
    public String getDayOfWeekText() {
        if (dayOfWeek == null) return null;
        switch (dayOfWeek) {
            case 1: return "周一";
            case 2: return "周二";
            case 3: return "周三";
            case 4: return "周四";
            case 5: return "周五";
            default: return "未知";
        }
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    /** 时间段中文文本（非数据库字段，由 timeSlot 转换） */
    public String getTimeSlotText() {
        if (timeSlot == null) return null;
        switch (timeSlot) {
            case 1: return "上午第一节";
            case 2: return "上午第二节";
            case 3: return "下午第一节";
            case 4: return "下午第二节";
            default: return "未知";
        }
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
