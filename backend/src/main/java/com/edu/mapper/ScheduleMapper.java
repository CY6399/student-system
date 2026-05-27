package com.edu.mapper;

import com.edu.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 课程排课 Mapper 接口
 * <p>提供排课信息的数据库增删改查操作</p>
 */
@Mapper
public interface ScheduleMapper {

    /** 新增排课 */
    void insert(Schedule schedule);

    /** 根据 ID 查询排课 */
    Schedule findById(@Param("id") Long id);

    /**
     * 根据班级 ID 查询该班级全部排课
     * <p>通过 LEFT JOIN class 表返回班级名称 className</p>
     */
    List<Schedule> findByClassId(@Param("classId") Long classId);

    /**
     * 统计同一班级同一天同一时间段的排课数量
     * <p>用于新增/编辑时校验排课冲突</p>
     *
     * @param excludeId 编辑时排除自身 ID，新增时传 null
     */
    int countByClassDaySlot(@Param("classId") Long classId,
                            @Param("dayOfWeek") Integer dayOfWeek,
                            @Param("timeSlot") Integer timeSlot,
                            @Param("excludeId") Long excludeId);

    /** 更新排课信息 */
    void update(Schedule schedule);

    /** 逻辑删除排课 */
    void deleteById(@Param("id") Long id);
}
