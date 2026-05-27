package com.edu.service;

import com.edu.entity.Attendance;
import com.edu.mapper.AttendanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 考勤业务逻辑
 * <p>处理考勤记录的查询、批量保存、单条修改、统计等业务逻辑。</p>
 */
@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    /**
     * 查询某班级某日期的考勤列表
     * <p>
     * 尚无考勤记录的学生自动设为默认出勤（status=1），
     * 保证前端首次打开页面时全员绿色标签。
     *
     * @param classId 班级 ID
     * @param date    考勤日期
     * @return 考勤列表（含学生姓名、学号）
     */
    public List<Attendance> listAttendance(Long classId, LocalDate date) {
        List<Attendance> list = attendanceMapper.findByClassIdAndDate(classId, date);

        // 无考勤记录的学生，status 为 null，自动设为默认出勤
        for (Attendance a : list) {
            if (a.getStatus() == null) {
                a.setStatus(1);
            }
        }
        return list;
    }

    /**
     * 批量保存考勤记录
     * <p>
     * 使用 INSERT ON DUPLICATE KEY UPDATE 保证幂等：
     * 同一学生同一天有记录时自动更新状态，无记录时新增。
     * 老师重复保存或修改状态后重新保存，不会报错。
     *
     * @param list 考勤记录列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<Attendance> list) {
        attendanceMapper.batchInsertOrUpdate(list);
    }

    /**
     * 单条修改考勤状态
     *
     * @param id     考勤记录 ID
     * @param status 新状态：1=出勤，2=迟到，3=请假，4=缺勤
     * @throws RuntimeException 考勤记录不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        Attendance existing = attendanceMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("考勤记录不存在");
        }

        Attendance update = new Attendance();
        update.setId(id);
        update.setStatus(status);
        attendanceMapper.updateById(update);
    }

    /**
     * 获取某班级某日期的考勤统计
     * <p>
     * 应到人数为该班级在册学生总数。各状态计数从考勤列表实时计算，
     * 无考勤记录的学生视为出勤。
     *
     * @param classId 班级 ID
     * @param date    考勤日期
     * @return 统计结果：totalCount / presentCount / lateCount / leaveCount / absentCount / attendanceRate
     */
    public Map<String, Object> getStats(Long classId, LocalDate date) {
        // 应到人数 = 班级在册学生总数
        long totalCount = attendanceMapper.countStudentsByClassId(classId);

        // 查询考勤列表（含默认出勤填充）
        List<Attendance> list = listAttendance(classId, date);

        // 按状态分类计数
        long presentCount = 0, lateCount = 0, leaveCount = 0, absentCount = 0;
        for (Attendance a : list) {
            switch (a.getStatus()) {
                case 1: presentCount++; break;
                case 2: lateCount++;   break;
                case 3: leaveCount++;  break;
                case 4: absentCount++; break;
                default: presentCount++; break;
            }
        }

        // 出勤率 = 出勤人数 / 应到人数，保留两位小数
        double rate = totalCount > 0 ? (double) presentCount / totalCount * 100 : 0;
        double attendanceRate = Math.round(rate * 100.0) / 100.0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalCount", totalCount);
        result.put("presentCount", presentCount);
        result.put("lateCount", lateCount);
        result.put("leaveCount", leaveCount);
        result.put("absentCount", absentCount);
        result.put("attendanceRate", attendanceRate);
        return result;
    }
}
