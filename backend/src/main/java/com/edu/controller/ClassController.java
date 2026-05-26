package com.edu.controller;

import com.edu.common.Result;
import com.edu.entity.ClassEntity;
import com.edu.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 班级管理接口
 * <p>提供班级的增删改查功能。</p>
 */
@RestController
@RequestMapping("/api/class")
public class ClassController {

    @Autowired
    private ClassService classService;

    /**
     * 新增班级
     * <p>
     * 请求方式：POST<br>
     * 请求体：{"className": "一年级一班", "teacherName": "张三", "description": "实验班"}
     *
     * @param classEntity 班级信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> add(@RequestBody ClassEntity classEntity) {
        // 参数校验
        if (classEntity.getClassName() == null || classEntity.getClassName().trim().isEmpty()) {
            return Result.error(400, "班级名称不能为空");
        }
        if (classEntity.getClassName().length() > 30) {
            return Result.error(400, "班级名称不能超过30个字符");
        }
        if (classEntity.getTeacherName() == null || classEntity.getTeacherName().trim().isEmpty()) {
            return Result.error(400, "负责老师不能为空");
        }
        if (classEntity.getTeacherName().length() > 20) {
            return Result.error(400, "老师姓名不能超过20个字符");
        }
        if (classEntity.getDescription() != null && classEntity.getDescription().length() > 200) {
            return Result.error(400, "班级描述不能超过200个字符");
        }

        classService.addClass(classEntity);
        return Result.success();
    }

    /**
     * 根据 ID 查询班级
     *
     * @param id 班级 ID
     * @return 班级信息
     */
    @GetMapping("/{id}")
    public Result<ClassEntity> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "班级ID无效");
        }

        ClassEntity classEntity = classService.getClassById(id);
        return Result.success(classEntity);
    }

    /**
     * 分页查询班级列表（可按班级名称模糊搜索）
     *
     * @param page      页码（默认 1）
     * @param pageSize  每页条数（默认 10）
     * @param className 班级名称（可选，模糊匹配）
     * @return 分页数据
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String className) {
        // 参数校验
        if (page < 1) {
            return Result.error(400, "页码不能小于1");
        }
        if (pageSize < 1 || pageSize > 100) {
            return Result.error(400, "每页条数范围为 1-100");
        }

        Map<String, Object> result = classService.listClasses(page, pageSize, className);
        return Result.success(result);
    }

    /**
     * 更新班级信息
     * <p>
     * 请求方式：PUT<br>
     * 请求体：{"className": "一年级一班", "teacherName": "李四", "description": "updated"}
     *
     * @param id          班级 ID（路径参数）
     * @param classEntity 班级信息（请求体）
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClassEntity classEntity) {
        // 参数校验
        if (id == null || id <= 0) {
            return Result.error(400, "班级ID无效");
        }
        if (classEntity.getClassName() == null || classEntity.getClassName().trim().isEmpty()) {
            return Result.error(400, "班级名称不能为空");
        }
        if (classEntity.getClassName().length() > 30) {
            return Result.error(400, "班级名称不能超过30个字符");
        }
        if (classEntity.getTeacherName() == null || classEntity.getTeacherName().trim().isEmpty()) {
            return Result.error(400, "负责老师不能为空");
        }
        if (classEntity.getTeacherName().length() > 20) {
            return Result.error(400, "老师姓名不能超过20个字符");
        }
        if (classEntity.getDescription() != null && classEntity.getDescription().length() > 200) {
            return Result.error(400, "班级描述不能超过200个字符");
        }

        // 将路径中的 ID 设置到实体中
        classEntity.setId(id);
        classService.updateClass(classEntity);
        return Result.success();
    }

    /**
     * 逻辑删除班级
     *
     * @param id 班级 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "班级ID无效");
        }

        classService.deleteClass(id);
        return Result.success();
    }
}
