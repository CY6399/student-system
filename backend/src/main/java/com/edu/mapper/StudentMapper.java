package com.edu.mapper;

import com.edu.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 学生 Mapper 接口
 * <p>提供学生信息的数据库增删改查操作</p>
 */
@Mapper
public interface StudentMapper {

    /** 新增学生 */
    void insert(Student student);

    /** 根据 ID 查询学生 */
    Student findById(@Param("id") Long id);

    /**
     * 分页查询学生列表（支持按姓名模糊搜索 + 班级筛选）
     * <p>通过 LEFT JOIN class 表返回班级名称 className</p>
     */
    List<Student> findByPage(@Param("offset") int offset, @Param("pageSize") int pageSize,
                             @Param("name") String name, @Param("classId") Long classId);

    /**
     * 查询学生总数（支持按姓名模糊搜索 + 班级筛选）
     */
    Long count(@Param("name") String name, @Param("classId") Long classId);

    /** 根据学号查询（用于校验学号唯一性） */
    Student findByStudentNo(@Param("studentNo") String studentNo);

    /** 更新学生信息 */
    void update(Student student);

    /** 逻辑删除学生 */
    void deleteById(@Param("id") Long id);
}
