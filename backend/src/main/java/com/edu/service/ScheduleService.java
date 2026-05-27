package com.edu.service;

import com.edu.entity.ClassEntity;
import com.edu.entity.Schedule;
import com.edu.mapper.ClassMapper;
import com.edu.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 课程排课业务逻辑
 * <p>处理排课的增删改查、同一班级同一时间段冲突校验等业务逻辑。</p>
 */
@Service
public class ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    private ClassMapper classMapper;

    /**
     * 新增排课
     *
     * @param schedule 排课信息
     * @throws RuntimeException 班级不存在 或 该时间段已有课程
     */
    @Transactional(rollbackFor = Exception.class)
    public void addSchedule(Schedule schedule) {
        // 校验班级是否存在
        ClassEntity classEntity = classMapper.findById(schedule.getClassId());
        if (classEntity == null) {
            throw new RuntimeException("班级不存在");
        }

        // 校验排课冲突：同一班级 + 同一天 + 同一时间段不能重复
        int count = scheduleMapper.countByClassDaySlot(
                schedule.getClassId(), schedule.getDayOfWeek(),
                schedule.getTimeSlot(), null);
        if (count > 0) {
            throw new RuntimeException("该时间段已有课程，请选择其他时间段");
        }

        scheduleMapper.insert(schedule);
    }

    /**
     * 根据 ID 获取排课信息（含班级名称）
     *
     * @param id 排课 ID
     * @return 排课实体（含 className）
     * @throws RuntimeException 排课不存在
     */
    public Schedule getScheduleById(Long id) {
        Schedule schedule = scheduleMapper.findById(id);
        if (schedule == null) {
            throw new RuntimeException("排课不存在");
        }
        return schedule;
    }

    /**
     * 根据班级 ID 查询该班级全部排课
     *
     * @param classId 班级 ID
     * @return 排课列表（按星期和时间段升序排列）
     */
    public List<Schedule> listSchedules(Long classId) {
        return scheduleMapper.findByClassId(classId);
    }

    /**
     * 更新排课信息
     *
     * @param schedule 排课信息（必须包含 ID）
     * @throws RuntimeException 排课不存在、班级不存在 或 该时间段已有课程
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSchedule(Schedule schedule) {
        // 检查排课是否存在
        Schedule existing = scheduleMapper.findById(schedule.getId());
        if (existing == null) {
            throw new RuntimeException("排课不存在");
        }

        // 校验班级是否存在
        ClassEntity classEntity = classMapper.findById(schedule.getClassId());
        if (classEntity == null) {
            throw new RuntimeException("班级不存在");
        }

        // 校验排课冲突（排除自身）
        int count = scheduleMapper.countByClassDaySlot(
                schedule.getClassId(), schedule.getDayOfWeek(),
                schedule.getTimeSlot(), schedule.getId());
        if (count > 0) {
            throw new RuntimeException("该时间段已有课程，请选择其他时间段");
        }

        scheduleMapper.update(schedule);
    }

    /**
     * 逻辑删除排课
     *
     * @param id 排课 ID
     * @throws RuntimeException 排课不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteSchedule(Long id) {
        // 检查排课是否存在
        Schedule existing = scheduleMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("排课不存在");
        }

        scheduleMapper.deleteById(id);
    }
}
