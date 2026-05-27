package com.edu.mapper;

import com.edu.entity.Score;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 成绩 Mapper 接口
 * <p>提供成绩信息的数据库增删改查操作。</p>
 */
@Mapper
public interface ScoreMapper {

    /** 新增成绩记录 */
    void insert(Score score);

    /** 根据 ID 查询成绩 */
    Score findById(@Param("id") Long id);

    /**
     * 按班级 + 科目 + 考试日期 + 考试类型 查询成绩列表
     * <p>通过 LEFT JOIN student 表返回学生姓名和学号。</p>
     */
    List<Score> findByClassAndExam(@Param("classId") Long classId,
                                   @Param("subject") String subject,
                                   @Param("examDate") LocalDate examDate,
                                   @Param("examType") Integer examType);

    /**
     * 按学生分页查询成绩列表
     * <p>查询该学生的所有科目成绩记录。</p>
     */
    List<Score> findByStudentId(@Param("studentId") Long studentId,
                                @Param("offset") int offset,
                                @Param("pageSize") int pageSize);

    /** 按学生统计成绩总数（用于分页） */
    Long countByStudentId(@Param("studentId") Long studentId);

    /**
     * 根据唯一键查询（用于防重复校验）
     * <p>唯一约束：student_id + subject + exam_date + exam_type</p>
     */
    Score findByUniqueKey(@Param("studentId") Long studentId,
                          @Param("subject") String subject,
                          @Param("examDate") LocalDate examDate,
                          @Param("examType") Integer examType);

    /** 更新成绩 */
    void update(Score score);

    /** 逻辑删除成绩 */
    void deleteById(@Param("id") Long id);
}
