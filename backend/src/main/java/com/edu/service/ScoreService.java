package com.edu.service;

import com.edu.entity.Score;
import com.edu.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 成绩业务逻辑
 * <p>处理成绩的批量录入、按班级查询、按学生查询、统计等业务逻辑。</p>
 */
@Service
public class ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    /**
     * 批量录入成绩
     * <p>
     * 逐条校验分数范围（0-100）和重复性（同一学生+科目+考试日期+类型），
     * 校验通过后逐条插入。
     *
     * @param list 成绩记录列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchSave(List<Score> list) {
        for (Score score : list) {
            // 校验分数范围 0-100
            if (score.getScore().compareTo(BigDecimal.ZERO) < 0
                    || score.getScore().compareTo(new BigDecimal("100")) > 0) {
                throw new RuntimeException("成绩应在 0-100 之间");
            }

            // 校验重复录入：同一学生 + 同一科目 + 同一考试日期 + 同一考试类型
            Score exist = scoreMapper.findByUniqueKey(
                    score.getStudentId(), score.getSubject(),
                    score.getExamDate(), score.getExamType());
            if (exist != null) {
                throw new RuntimeException("该学生此科目的成绩已存在");
            }

            scoreMapper.insert(score);
        }
    }

    /**
     * 根据 ID 获取成绩（含学生信息）
     *
     * @param id 成绩 ID
     * @return 成绩实体
     * @throws RuntimeException 成绩不存在
     */
    public Score getScoreById(Long id) {
        Score score = scoreMapper.findById(id);
        if (score == null) {
            throw new RuntimeException("成绩记录不存在");
        }
        return score;
    }

    /**
     * 按班级 + 科目 + 考试日期 + 类型 查询成绩列表及统计信息
     * <p>
     * 统计信息包含：平均分(avgScore)、最高分(maxScore)、
     * 最低分(minScore)、及格率(passRate，≥60分为及格)。
     *
     * @return 包含 list（成绩列表）和 stats（统计信息）的 Map
     */
    public Map<String, Object> listByClassAndExam(Long classId, String subject,
                                                   LocalDate examDate, Integer examType) {
        List<Score> list = scoreMapper.findByClassAndExam(classId, subject, examDate, examType);

        // === 计算统计信息 ===
        Map<String, Object> stats = new HashMap<>();

        if (list.isEmpty()) {
            stats.put("avgScore", null);
            stats.put("maxScore", null);
            stats.put("minScore", null);
            stats.put("passRate", null);
            stats.put("totalCount", 0);
        } else {
            BigDecimal sum = BigDecimal.ZERO;
            BigDecimal max = list.get(0).getScore();
            BigDecimal min = list.get(0).getScore();
            long passCount = 0;

            for (Score s : list) {
                BigDecimal sc = s.getScore();
                sum = sum.add(sc);
                if (sc.compareTo(max) > 0) max = sc;
                if (sc.compareTo(min) < 0) min = sc;
                if (sc.compareTo(new BigDecimal("60")) >= 0) passCount++;
            }

            BigDecimal avg = sum.divide(BigDecimal.valueOf(list.size()), 1, RoundingMode.HALF_UP);
            double passRate = (double) passCount / list.size() * 100;
            double roundedPassRate = Math.round(passRate * 100.0) / 100.0;

            stats.put("avgScore", avg);
            stats.put("maxScore", max);
            stats.put("minScore", min);
            stats.put("passRate", roundedPassRate);
            stats.put("totalCount", list.size());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("stats", stats);
        return result;
    }

    /**
     * 按学生分页查询成绩
     *
     * @param studentId 学生 ID
     * @param page      页码（从 1 开始）
     * @param pageSize  每页条数
     * @return 包含 list、total、page、pageSize 的分页数据
     */
    public Map<String, Object> listByStudent(Long studentId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;

        List<Score> list = scoreMapper.findByStudentId(studentId, offset, pageSize);
        long total = scoreMapper.countByStudentId(studentId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    /**
     * 更新成绩
     * <p>只允许修改分数和备注，考试信息不允许修改。</p>
     *
     * @param score 成绩信息（必须包含 ID）
     * @throws RuntimeException 成绩不存在 或 分数超出范围
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateScore(Score score) {
        // 检查成绩是否存在
        Score existing = scoreMapper.findById(score.getId());
        if (existing == null) {
            throw new RuntimeException("成绩记录不存在");
        }

        // 校验分数范围
        if (score.getScore().compareTo(BigDecimal.ZERO) < 0
                || score.getScore().compareTo(new BigDecimal("100")) > 0) {
            throw new RuntimeException("成绩应在 0-100 之间");
        }

        scoreMapper.update(score);
    }

    /**
     * 逻辑删除成绩
     *
     * @param id 成绩 ID
     * @throws RuntimeException 成绩不存在
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteScore(Long id) {
        Score existing = scoreMapper.findById(id);
        if (existing == null) {
            throw new RuntimeException("成绩记录不存在");
        }

        scoreMapper.deleteById(id);
    }
}
