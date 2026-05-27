package com.edu.controller;

import com.edu.common.Result;
import com.edu.entity.Score;
import com.edu.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 成绩管理接口
 * <p>提供成绩的批量录入、按班级查询、按学生查询、单条修改、逻辑删除等功能。</p>
 */
@RestController
@RequestMapping("/api/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    /**
     * 批量录入成绩
     * <p>
     * 请求方式：POST<br>
     * 请求体示例：
     * <pre>
     * [
     *   {"studentId":1, "subject":"语文", "score":90.5, "examDate":"2026-05-27", "examType":1},
     *   {"studentId":2, "subject":"语文", "score":85.0, "examDate":"2026-05-27", "examType":1}
     * ]
     * </pre>
     *
     * @param list 成绩记录列表
     * @return 操作结果
     */
    @PostMapping("/batch-save")
    public Result<Void> batchSave(@RequestBody List<Score> list) {
        // 参数校验
        if (list == null || list.isEmpty()) {
            return Result.error(400, "成绩记录不能为空");
        }
        for (int i = 0; i < list.size(); i++) {
            Score s = list.get(i);
            if (s.getStudentId() == null || s.getStudentId() <= 0) {
                return Result.error(400, "第" + (i + 1) + "条记录的学生ID无效");
            }
            if (s.getSubject() == null || s.getSubject().trim().isEmpty()) {
                return Result.error(400, "第" + (i + 1) + "条记录的科目不能为空");
            }
            if (s.getSubject().length() > 20) {
                return Result.error(400, "第" + (i + 1) + "条记录的科目名称不能超过20个字符");
            }
            if (s.getScore() == null) {
                return Result.error(400, "第" + (i + 1) + "条记录的成绩不能为空");
            }
            if (s.getScore().compareTo(BigDecimal.ZERO) < 0
                    || s.getScore().compareTo(new BigDecimal("100")) > 0) {
                return Result.error(400, "第" + (i + 1) + "条记录的成绩应在 0-100 之间");
            }
            if (s.getExamDate() == null) {
                return Result.error(400, "第" + (i + 1) + "条记录的考试日期不能为空");
            }
            if (s.getExamType() == null || s.getExamType() < 1 || s.getExamType() > 5) {
                return Result.error(400, "第" + (i + 1) + "条记录的考试类型无效（1=期中 2=期末 3=月考 4=单元测试 5=其他）");
            }
            if (s.getRemark() != null && s.getRemark().length() > 100) {
                return Result.error(400, "第" + (i + 1) + "条记录的备注不能超过100个字符");
            }
        }

        scoreService.batchSave(list);
        return Result.success();
    }

    /**
     * 按班级 + 科目 + 考试日期 + 类型 查询成绩列表及统计
     * <p>
     * 请求方式：GET<br>
     * 请求示例：/api/score/list?classId=1&subject=语文&examDate=2026-05-27&examType=1
     *
     * @param classId  班级 ID
     * @param subject  科目名称
     * @param examDate 考试日期
     * @param examType 考试类型
     * @return 成绩列表 + 统计信息（avgScore / maxScore / minScore / passRate / totalCount）
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> listByClassAndExam(@RequestParam Long classId,
                                                          @RequestParam String subject,
                                                          @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate examDate,
                                                          @RequestParam Integer examType) {
        // 参数校验
        if (classId == null || classId <= 0) {
            return Result.error(400, "班级ID无效");
        }
        if (subject == null || subject.trim().isEmpty()) {
            return Result.error(400, "科目不能为空");
        }
        if (subject.length() > 20) {
            return Result.error(400, "科目名称不能超过20个字符");
        }
        if (examDate == null) {
            return Result.error(400, "考试日期不能为空");
        }
        if (examType == null || examType < 1 || examType > 5) {
            return Result.error(400, "考试类型无效（1=期中 2=期末 3=月考 4=单元测试 5=其他）");
        }

        Map<String, Object> result = scoreService.listByClassAndExam(classId, subject, examDate, examType);
        return Result.success(result);
    }

    /**
     * 按学生分页查询成绩记录
     * <p>
     * 请求方式：GET<br>
     * 请求示例：/api/score/student/1?page=1&pageSize=10
     *
     * @param studentId 学生 ID
     * @param page      页码（默认 1）
     * @param pageSize  每页条数（默认 10）
     * @return 分页数据
     */
    @GetMapping("/student/{studentId}")
    public Result<Map<String, Object>> listByStudent(@PathVariable Long studentId,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int pageSize) {
        // 参数校验
        if (studentId == null || studentId <= 0) {
            return Result.error(400, "学生ID无效");
        }
        if (page < 1) {
            return Result.error(400, "页码不能小于1");
        }
        if (pageSize < 1 || pageSize > 100) {
            return Result.error(400, "每页条数范围为 1-100");
        }

        Map<String, Object> result = scoreService.listByStudent(studentId, page, pageSize);
        return Result.success(result);
    }

    /**
     * 更新成绩（分数和备注）
     * <p>
     * 请求方式：PUT<br>
     * 请求体：{"score": 95.0, "remark": "成绩优秀"}
     *
     * @param id    成绩 ID（路径参数）
     * @param score 成绩信息（请求体）
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Score score) {
        // 参数校验
        if (id == null || id <= 0) {
            return Result.error(400, "成绩ID无效");
        }
        if (score.getScore() == null) {
            return Result.error(400, "成绩不能为空");
        }
        if (score.getScore().compareTo(BigDecimal.ZERO) < 0
                || score.getScore().compareTo(new BigDecimal("100")) > 0) {
            return Result.error(400, "成绩应在 0-100 之间");
        }
        if (score.getRemark() != null && score.getRemark().length() > 100) {
            return Result.error(400, "备注不能超过100个字符");
        }

        score.setId(id);
        scoreService.updateScore(score);
        return Result.success();
    }

    /**
     * 逻辑删除成绩
     *
     * @param id 成绩 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return Result.error(400, "成绩ID无效");
        }

        scoreService.deleteScore(id);
        return Result.success();
    }
}
