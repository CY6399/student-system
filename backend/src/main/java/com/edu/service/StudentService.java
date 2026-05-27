package com.edu.service;

import com.edu.entity.ClassEntity;
import com.edu.entity.Student;
import com.edu.mapper.ClassMapper;
import com.edu.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 学生业务逻辑
 * <p>处理学生的增删改查、学号唯一性校验、班级存在性校验等业务逻辑。</p>
 */
@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ClassMapper classMapper;

    /**
     * 新增学生
     *
     * @param student 学生信息
     * @throws RuntimeException 班级不存在 或 学号已被使用
     */
    @Transactional(rollbackFor = Exception.class)
    public void addStudent(Student student) {
        // 校验班级是否存在
        ClassEntity classEntity = classMapper.findById(student.getClassId());
        if (classEntity == null) {
            throw new RuntimeException("班级不存在");
        }

        // 校验学号唯一性
        Student exist = studentMapper.findByStudentNo(student.getStudentNo());
        if (exist != null) {
            throw new RuntimeException("该学号已被使用");
        }

        studentMapper.insert(student);
    }

    /**
     * 根据 ID 获取学生信息（含班级名称）
     *
     * @param id 学生 ID
     * @return 学生实体（含 className）
     * @throws RuntimeException 学生不存在
     */
    public Student getStudentById(Long id) {
        Student student = studentMapper.findById(id);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        return student;
    }

    /**
     * 分页查询学生列表（支持姓名模糊搜索 + 班级筛选）
     *
     * @param page     页码（从 1 开始）
     * @param pageSize 每页条数
     * @param name     学生姓名（可选，模糊匹配）
     * @param classId  班级 ID（可选，精确筛选）
     * @return 包含 list、total、page、pageSize 的分页数据
     */
    public Map<String, Object> listStudents(int page, int pageSize, String name, Long classId) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询列表和总数
        List<Student> list = studentMapper.findByPage(offset, pageSize, name, classId);
        long total = studentMapper.count(name, classId);

        // 组装分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 更新学生信息（允许修改学号）
     *
     * <p>学号唯一性校验逻辑：</p>
     * <ul>
     *   <li>若学号未变化（查询结果中的 ID 与当前 ID 相同）→ 允许保存</li>
     *   <li>若学号已被其他学生使用 → 抛出异常 "该学号已被使用"</li>
     * </ul>
     *
     * @param student 学生信息（必须包含 ID）
     * @throws RuntimeException 学生不存在、班级不存在 或 学号已被使用
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStudent(Student student) {
        // 检查学生是否存在
        Student existing = studentMapper.findById(student.getId());
        if (existing == null) {
            throw new RuntimeException("学生不存在");
        }

        // 检查修改后的班级是否存在
        ClassEntity classEntity = classMapper.findById(student.getClassId());
        if (classEntity == null) {
            throw new RuntimeException("班级不存在");
        }

        // 校验学号唯一性（排除自身）
        Student sameNo = studentMapper.findByStudentNo(student.getStudentNo());
        if (sameNo != null && !sameNo.getId().equals(student.getId())) {
            throw new RuntimeException("该学号已被使用");
        }

        studentMapper.update(student);
    }

    /**
     * 逻辑删除学生
     *
     * @param id 学生 ID
     * @throws RuntimeException 学生不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteStudent(Long id) {
        // 检查学生是否存在
        Student existing = studentMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("学生不存在");
        }

        studentMapper.deleteById(id);
    }
}
