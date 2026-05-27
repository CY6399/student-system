package com.edu.controller;

import com.edu.common.Result;
import com.edu.entity.Schedule;
import com.edu.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程排课接口
 * <p>提供排课的增删改查功能，包括按班级查看周课表、排课冲突校验等。</p>
 */
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 新增排课
     * <p>
     * 请求方式：POST<br>
     * 请求体：{"classId":1,"courseName":"语文","teacherName":"王老师",
     *          "dayOfWeek":1,"timeSlot":1,"classroom":"101教室"}
     *
     * @param schedule 排课信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Void> add(@RequestBody Schedule schedule) {
        // === 参数校验 ===
        if (schedule.getClassId() == null || schedule.getClassId() <= 0) {
            return Result.error(400, "班级不能为空");
        }
        if (schedule.getCourseName() == null || schedule.getCourseName().trim().isEmpty()) {
            return Result.error(400, "课程名称不能为空");
        }
        if (schedule.getCourseName().length() > 20) {
            return Result.error(400, "课程名称不能超过20个字符");
        }
        if (schedule.getTeacherName() == null || schedule.getTeacherName().trim().isEmpty()) {
            return Result.error(400, "任课老师不能为空");
        }
        if (schedule.getTeacherName().length() > 20) {
            return Result.error(400, "老师姓名不能超过20个字符");
        }
        if (schedule.getDayOfWeek() == null || schedule.getDayOfWeek() < 1 || schedule.getDayOfWeek() > 5) {
            return Result.error(400, "星期无效，请传入 1（周一）~ 5（周五）");
        }
        if (schedule.getTimeSlot() == null || schedule.getTimeSlot() < 1 || schedule.getTimeSlot() > 4) {
            return Result.error(400, "时间段无效，请传入 1（上午第一节）~ 4（下午第二节）");
        }
        if (schedule.getClassroom() != null && schedule.getClassroom().length() > 50) {
            return Result.error(400, "上课地点不能超过50个字符");
        }

        scheduleService.addSchedule(schedule);
        return Result.success();
    }

    /**
     * 根据班级 ID 查询该班级全部排课
     * <p>
     * 请求方式：GET<br>
     * 请求示例：/api/schedule/list?classId=1
     *
     * @param classId 班级 ID
     * @return 排课列表（含 dayOfWeekText、timeSlotText）
     */
    @GetMapping("/list")
    public Result<List<Schedule>> list(@RequestParam Long classId) {
        if (classId == null || classId <= 0) {
            return Result.error(400, "班级ID无效");
        }

        List<Schedule> list = scheduleService.listSchedules(classId);
        return Result.success(list);
    }

    /**
     * 更新排课信息
     * <p>
     * 请求方式：PUT<br>
     * 请求体：{"courseName":"数学","teacherName":"李老师","dayOfWeek":2,"timeSlot":1,"classroom":"102教室"}
     *
     * @param id       排课 ID（路径参数）
     * @param schedule 排课信息（请求体）
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        // === 参数校验 ===
        if (id == null || id <= 0) {
            return Result.error(400, "排课ID无效");
        }
        if (schedule.getClassId() == null || schedule.getClassId() <= 0) {
            return Result.error(400, "班级不能为空");
        }
        if (schedule.getCourseName() == null || schedule.getCourseName().trim().isEmpty()) {
            return Result.error(400, "课程名称不能为空");
        }
        if (schedule.getCourseName().length() > 20) {
            return Result.error(400, "课程名称不能超过20个字符");
        }
        if (schedule.getTeacherName() == null || schedule.getTeacherName().trim().isEmpty()) {
            return Result.error(400, "任课老师不能为空");
        }
        if (schedule.getTeacherName().length() > 20) {
            return Result.error(400, "老师姓名不能超过20个字符");
        }
        if (schedule.getDayOfWeek() == null || schedule.getDayOfWeek() < 1 || schedule.getDayOfWeek() > 5) {
            return Result.error(400, "星期无效，请传入 1（周一）~ 5（周五）");
        }
        if (schedule.getTimeSlot() == null || schedule.getTimeSlot() < 1 || schedule.getTimeSlot() > 4) {
            return Result.error(400, "时间段无效，请传入 1（上午第一节）~ 4（下午第二节）");
        }
        if (schedule.getClassroom() != null && schedule.getClassroom().length() > 50) {
            return Result.error(400, "上课地点不能超过50个字符");
        }

        // 将路径中的 ID 设置到实体中
        schedule.setId(id);
        scheduleService.updateSchedule(schedule);
        return Result.success();
    }

    /**
     * 逻辑删除排课
     *
     * @param id 排课 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "排课ID无效");
        }

        scheduleService.deleteSchedule(id);
        return Result.success();
    }
}
