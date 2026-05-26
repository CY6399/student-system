package com.edu.service;

import com.edu.entity.ClassEntity;
import com.edu.mapper.ClassMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级业务逻辑
 * <p>处理班级的增删改查、名称唯一性校验等业务逻辑。</p>
 */
@Service
public class ClassService {

    @Autowired
    private ClassMapper classMapper;

    /**
     * 新增班级
     *
     * @param classEntity 班级信息
     * @throws RuntimeException 班级名称已存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void addClass(ClassEntity classEntity) {
        // 校验班级名称唯一性
        ClassEntity exist = classMapper.findByClassName(classEntity.getClassName());
        if (exist != null) {
            throw new RuntimeException("班级名称已存在");
        }

        classMapper.insert(classEntity);
    }

    /**
     * 根据 ID 获取班级信息
     *
     * @param id 班级 ID
     * @return 班级实体
     * @throws RuntimeException 班级不存在
     */
    public ClassEntity getClassById(Long id) {
        ClassEntity classEntity = classMapper.findById(id);
        if (classEntity == null) {
            throw new RuntimeException("班级不存在");
        }
        return classEntity;
    }

    /**
     * 分页查询班级列表（可按班级名称模糊搜索）
     *
     * @param page      页码（从 1 开始）
     * @param pageSize  每页条数
     * @param className 班级名称（可选，模糊匹配）
     * @return 包含 list、total、page、pageSize 的分页数据
     */
    public Map<String, Object> listClasses(int page, int pageSize, String className) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询列表和总数
        List<ClassEntity> list = classMapper.findByPage(offset, pageSize, className);
        long total = classMapper.count(className);

        // 组装分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 更新班级信息
     *
     * @param classEntity 班级信息（必须包含 ID）
     * @throws RuntimeException 班级不存在或班级名称已存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateClass(ClassEntity classEntity) {
        // 检查班级是否存在
        ClassEntity existing = classMapper.findById(classEntity.getId());
        if (existing == null) {
            throw new RuntimeException("班级不存在");
        }

        // 校验班级名称唯一性（排除自身）
        ClassEntity sameName = classMapper.findByClassName(classEntity.getClassName());
        if (sameName != null && !sameName.getId().equals(classEntity.getId())) {
            throw new RuntimeException("班级名称已存在");
        }

        classMapper.update(classEntity);
    }

    /**
     * 逻辑删除班级
     *
     * @param id 班级 ID
     * @throws RuntimeException 班级不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteClass(Long id) {
        // 检查班级是否存在
        ClassEntity existing = classMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("班级不存在");
        }

        classMapper.deleteById(id);
    }
}
