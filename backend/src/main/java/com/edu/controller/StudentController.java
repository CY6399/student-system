package com.edu.controller;

import com.edu.common.Result;
import com.edu.entity.Student;
import com.edu.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学生管理接口
 * <p>提供学生的增删改查功能，包括分页、模糊搜索、学号唯一性校验等。</p>
 */
@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 新增学生
     * <p>
     * 请求方式：POST<br>
     * 请求体：{"name":"张三","studentNo":"2024001","gender":1,"phone":"13800138000",
     *          "classId":1,"enrollmentDate":"2024-09-01"}
     *
     * @param student 学生信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> add(@RequestBody Student student) {
        // === 参数校验 ===
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return Result.error(400, "学生姓名不能为空");
        }
        if (student.getName().length() > 20) {
            return Result.error(400, "学生姓名不能超过20个字符");
        }
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            return Result.error(400, "学号不能为空");
        }
        if (student.getStudentNo().length() > 30) {
            return Result.error(400, "学号不能超过30个字符");
        }
        if (student.getGender() == null) {
            return Result.error(400, "性别不能为空");
        }
        if (student.getGender() != 1 && student.getGender() != 2) {
            return Result.error(400, "性别无效，请传入 1（男）或 2（女）");
        }
        if (student.getPhone() != null && student.getPhone().length() != 11) {
            return Result.error(400, "联系电话必须为11位");
        }
        if (student.getClassId() == null) {
            return Result.error(400, "所属班级不能为空");
        }
        if (student.getEnrollmentDate() == null) {
            return Result.error(400, "入学日期不能为空");
        }

        studentService.addStudent(student);
        return Result.success();
    }

    /**
     * 根据 ID 查询学生信息（含班级名称）
     *
     * @param id 学生 ID
     * @return 学生信息
     */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "学生ID无效");
        }

        Student student = studentService.getStudentById(id);
        return Result.success(student);
    }

    /**
     * 分页查询学生列表（支持姓名模糊搜索 + 班级筛选）
     * <p>
     * 请求方式：GET<br>
     * 请求示例：/api/student/list?page=1&pageSize=10&name=张&classId=1
     *
     * @param page     页码（默认 1）
     * @param pageSize 每页条数（默认 10）
     * @param name     学生姓名（可选，模糊匹配）
     * @param classId  班级 ID（可选，精确筛选）
     * @return 分页数据
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) Long classId) {
        // 参数校验
        if (page < 1) {
            return Result.error(400, "页码不能小于1");
        }
        if (pageSize < 1 || pageSize > 100) {
            return Result.error(400, "每页条数范围为 1-100");
        }

        Map<String, Object> result = studentService.listStudents(page, pageSize, name, classId);
        return Result.success(result);
    }

    /**
     * 更新学生信息（允许修改学号，含学号唯一性校验）
     * <p>
     * 请求方式：PUT<br>
     * 请求体：{"name":"张三","studentNo":"2024001","gender":1,"phone":"13800138000",
     *          "classId":1,"enrollmentDate":"2024-09-01"}
     *
     * @param id      学生 ID（路径参数）
     * @param student 学生信息（请求体）
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Student student) {
        // === 参数校验 ===
        if (id == null || id <= 0) {
            return Result.error(400, "学生ID无效");
        }
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return Result.error(400, "学生姓名不能为空");
        }
        if (student.getName().length() > 20) {
            return Result.error(400, "学生姓名不能超过20个字符");
        }
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty()) {
            return Result.error(400, "学号不能为空");
        }
        if (student.getStudentNo().length() > 30) {
            return Result.error(400, "学号不能超过30个字符");
        }
        if (student.getGender() == null) {
            return Result.error(400, "性别不能为空");
        }
        if (student.getGender() != 1 && student.getGender() != 2) {
            return Result.error(400, "性别无效，请传入 1（男）或 2（女）");
        }
        if (student.getPhone() != null && student.getPhone().length() != 11) {
            return Result.error(400, "联系电话必须为11位");
        }
        if (student.getClassId() == null) {
            return Result.error(400, "所属班级不能为空");
        }
        if (student.getEnrollmentDate() == null) {
            return Result.error(400, "入学日期不能为空");
        }

        // 将路径中的 ID 设置到实体中
        student.setId(id);
        studentService.updateStudent(student);
        return Result.success();
    }

    /**
     * 逻辑删除学生
     *
     * @param id 学生 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "学生ID无效");
        }

        studentService.deleteStudent(id);
        return Result.success();
    }
}
