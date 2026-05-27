package com.edu.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 成绩实体
 * <p>对应数据库表 `score`</p>
 */
public class Score {

    /** 主键ID */
    private Long id;

    /** 学生ID，关联 student 表 */
    private Long studentId;

    /** 学生姓名（非数据库字段，由 LEFT JOIN student 表填充） */
    private String studentName;

    /** 学号（非数据库字段，由 LEFT JOIN student 表填充） */
    private String studentNo;

    /** 科目名称 */
    private String subject;

    /** 成绩，范围 0-100，支持一位小数 */
    private BigDecimal score;

    /** 考试日期 */
    private LocalDate examDate;

    /** 考试类型：1=期中考试，2=期末考试，3=月考，4=单元测试，5=其他 */
    private Integer examType;

    /** 备注 */
    private String remark;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    /** 考试类型中文文本（非数据库字段，由 examType 转换） */
    public String getExamTypeText() {
        if (examType == null) return null;
        switch (examType) {
            case 1: return "期中考试";
            case 2: return "期末考试";
            case 3: return "月考";
            case 4: return "单元测试";
            case 5: return "其他";
            default: return "未知";
        }
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
