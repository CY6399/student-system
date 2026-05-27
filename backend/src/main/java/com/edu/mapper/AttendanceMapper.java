package com.edu.mapper;

import com.edu.entity.Attendance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 考勤 Mapper 接口
 * <p>提供考勤记录的查询、批量保存、单条修改等数据库操作。</p>
 */
@Mapper
public interface AttendanceMapper {

    /**
     * 查询某班级某日期的考勤列表
     * <p>通过 LEFT JOIN student 表一次查出所有学生及其考勤状态。
     * 无考勤记录的学生，id 和 status 为 null，由 Service 层设默认值。</p>
     */
    List<Attendance> findByClassIdAndDate(@Param("classId") Long classId, @Param("date") LocalDate date);

    /** 根据 ID 查询考勤记录（用于单条修改前校验） */
    Attendance findById(@Param("id") Long id);

    /**
     * 批量保存考勤记录
     * <p>使用 MySQL INSERT ON DUPLICATE KEY UPDATE 语法：
     * 无记录时新增，有记录时更新状态，保证幂等。</p>
     */
    void batchInsertOrUpdate(@Param("list") List<Attendance> list);

    /** 单条修改考勤状态 */
    void updateById(Attendance attendance);

    /** 统计某班级在册学生总数（用于 stats 接口计算应到人数） */
    Long countStudentsByClassId(@Param("classId") Long classId);
}
