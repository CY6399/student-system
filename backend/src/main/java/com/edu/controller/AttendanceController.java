package com.edu.controller;

import com.edu.common.Result;
import com.edu.entity.Attendance;
import com.edu.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 考勤打卡接口
 * <p>提供考勤记录的查询、批量保存、单条修改、考勤统计等功能。</p>
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * 查询某班级某日期的考勤列表
     * <p>
     * 请求方式：GET<br>
     * 请求示例：/api/attendance/list?classId=1&date=2026-05-27
     *
     * @param classId 班级 ID
     * @param date    考勤日期
     * @return 考勤列表（尚无记录的学生默认出勤，id 为 null）
     */
    @GetMapping("/list")
    public Result<List<Attendance>> list(@RequestParam Long classId,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        // 参数校验
        if (classId == null || classId <= 0) {
            return Result.error(400, "班级ID无效");
        }
        if (date == null) {
            return Result.error(400, "日期不能为空");
        }

        List<Attendance> list = attendanceService.listAttendance(classId, date);
        return Result.success(list);
    }

    /**
     * 批量保存考勤记录
     * <p>
     * 请求方式：POST<br>
     * 请求体示例：
     * <pre>
     * [
     *   {"studentId":1, "classId":1, "date":"2026-05-27", "status":1},
     *   {"studentId":2, "classId":1, "date":"2026-05-27", "status":2}
     * ]
     * </pre>
     * 使用 INSERT ON DUPLICATE KEY UPDATE，重复保存幂等不报错。
     *
     * @param list 考勤记录列表
     * @return 操作结果
     */
    @PostMapping("/batch-save")
    public Result<Void> batchSave(@RequestBody List<Attendance> list) {
        // 参数校验
        if (list == null || list.isEmpty()) {
            return Result.error(400, "考勤记录不能为空");
        }
        for (int i = 0; i < list.size(); i++) {
            Attendance a = list.get(i);
            if (a.getStudentId() == null || a.getStudentId() <= 0) {
                return Result.error(400, "第" + (i + 1) + "条记录的学生ID无效");
            }
            if (a.getClassId() == null || a.getClassId() <= 0) {
                return Result.error(400, "第" + (i + 1) + "条记录的班级ID无效");
            }
            if (a.getDate() == null) {
                return Result.error(400, "第" + (i + 1) + "条记录的日期不能为空");
            }
            if (a.getStatus() == null || a.getStatus() < 1 || a.getStatus() > 4) {
                return Result.error(400, "第" + (i + 1) + "条记录的考勤状态无效（1=出勤 2=迟到 3=请假 4=缺勤）");
            }
        }

        attendanceService.batchSave(list);
        return Result.success();
    }

    /**
     * 单条修改考勤状态
     * <p>
     * 请求方式：PUT<br>
     * 请求体：{"status": 2}
     *
     * @param id         考勤记录 ID（路径参数）
     * @param attendance 考勤信息（请求体，仅需 status）
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Attendance attendance) {
        // 参数校验
        if (id == null || id <= 0) {
            return Result.error(400, "考勤记录ID无效");
        }
        if (attendance.getStatus() == null || attendance.getStatus() < 1 || attendance.getStatus() > 4) {
            return Result.error(400, "考勤状态无效（1=出勤 2=迟到 3=请假 4=缺勤）");
        }

        attendanceService.updateStatus(id, attendance.getStatus());
        return Result.success();
    }

    /**
     * 获取某班级某日期的考勤统计
     * <p>
     * 请求方式：GET<br>
     * 请求示例：/api/attendance/stats?classId=1&date=2026-05-27
     *
     * @param classId 班级 ID
     * @param date    考勤日期
     * @return 统计结果：totalCount / presentCount / lateCount / leaveCount / absentCount / attendanceRate
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats(@RequestParam Long classId,
                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        // 参数校验
        if (classId == null || classId <= 0) {
            return Result.error(400, "班级ID无效");
        }
        if (date == null) {
            return Result.error(400, "日期不能为空");
        }

        Map<String, Object> stats = attendanceService.getStats(classId, date);
        return Result.success(stats);
    }
}
