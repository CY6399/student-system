package com.edu.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 考勤记录实体
 * <p>对应数据库表 `attendance`</p>
 */
public class Attendance {

    /** 考勤记录主键ID（无考勤记录时为 null） */
    private Long id;

    /** 学生ID，关联 student 表 */
    private Long studentId;

    /** 学生姓名（非数据库字段，由 LEFT JOIN student 表填充） */
    private String studentName;

    /** 学号（非数据库字段，由 LEFT JOIN student 表填充） */
    private String studentNo;

    /** 班级ID，记录当日所属班级，关联 class 表 */
    private Long classId;

    /** 考勤日期 */
    private LocalDate date;

    /** 考勤状态：1=出勤，2=迟到，3=请假，4=缺勤（无记录时为 null） */
    private Integer status;

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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
