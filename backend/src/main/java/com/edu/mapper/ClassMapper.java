package com.edu.mapper;

import com.edu.entity.ClassEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级 Mapper 接口
 * <p>提供班级信息的数据库增删改查操作</p>
 */
@Mapper
public interface ClassMapper {

    /** 新增班级 */
    void insert(ClassEntity classEntity);

    /** 根据 ID 查询班级 */
    ClassEntity findById(@Param("id") Long id);

    /** 分页查询班级列表（支持按班级名称模糊搜索） */
    List<ClassEntity> findByPage(@Param("offset") int offset, @Param("pageSize") int pageSize,
                                 @Param("className") String className);

    /** 查询班级总数（支持按班级名称模糊搜索） */
    Long count(@Param("className") String className);

    /** 根据班级名称查询（用于校验唯一性） */
    ClassEntity findByClassName(@Param("className") String className);

    /** 更新班级信息 */
    void update(ClassEntity classEntity);

    /** 逻辑删除班级 */
    void deleteById(@Param("id") Long id);
}
