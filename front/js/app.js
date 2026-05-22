(function() {
    'use strict';

    /* ===== Token 工具 ===== */
    window.getToken = function() {
        return localStorage.getItem('edu_token');
    };
    window.setToken = function(token) {
        localStorage.setItem('edu_token', token);
    };
    window.clearToken = function() {
        localStorage.removeItem('edu_token');
        localStorage.removeItem('edu_user');
    };
    window.checkLogin = function() {
        return !!window.getToken();
    };
    window.logout = function() {
        window.clearToken();
        window.location.href = 'login.html';
    };
    window.getCurrentUser = function() {
        var raw = localStorage.getItem('edu_user');
        return raw ? JSON.parse(raw) : null;
    };
    window.setCurrentUser = function(user) {
        localStorage.setItem('edu_user', JSON.stringify(user));
    };

    /* ===== Mock 数据 ===== */
    var idSeq = { class: 9, student: 321, schedule: 200, attendance: 1000,
                  score: 400, homework: 12, submission: 2000 };

    function nextId(k) { return idSeq[k]++; }

    var clsList = [
        { id:1,  className:'高三一班', teacherName:'王老师', description:'理科重点班',       createTime:'2024-09-01 08:00:00' },
        { id:2,  className:'高三二班', teacherName:'李老师', description:'理科普通班',       createTime:'2024-09-01 08:00:00' },
        { id:3,  className:'高三三班', teacherName:'张老师', description:'文科重点班',       createTime:'2024-09-01 08:00:00' },
        { id:4,  className:'高三四班', teacherName:'刘老师', description:'文科普通班',       createTime:'2024-09-01 08:00:00' },
        { id:5,  className:'高三五班', teacherName:'陈老师', description:'艺术特长班',       createTime:'2024-09-01 08:00:00' },
        { id:6,  className:'高三六班', teacherName:'杨老师', description:'体育特长班',       createTime:'2024-09-01 08:00:00' },
        { id:7,  className:'高三七班', teacherName:'赵老师', description:'理科实验班',       createTime:'2024-09-01 08:00:00' },
        { id:8,  className:'高三八班', teacherName:'周老师', description:'',                 createTime:'2024-09-01 08:00:00' }
    ];

    var names = '李王张刘陈杨赵黄周吴徐孙马朱胡郭何林高罗'.split('');
    var givens = '华明强伟芳娜敏静丽洋勇军杰磊涛艳超波霞文辉鑫峰浩然宇轩晨阳宁'.split('');

    var stuList = [];
    var noCounter = 20240001;
    clsList.forEach(function(cls) {
        var cnt = 32 + Math.floor(Math.random() * 18); // 32-49
        for (var i = 0; i < cnt; i++) {
            var nm = (names[i % names.length] || '张') + (givens[i % givens.length] || '华');
            stuList.push({
                id: nextId('student'), name: nm,
                studentNo: '' + (noCounter++),
                gender: i % 2 === 0 ? 1 : 2,
                phone: '138' + (10000000 + Math.floor(Math.random() * 90000000)),
                classId: cls.id,
                enrollmentDate: '2024-0' + (8 + Math.floor(Math.random() * 2)) + '-' +
                    ('0' + (20 + Math.floor(Math.random() * 10))).slice(-2)
            });
        }
    });

    var dayText = ['周一','周二','周三','周四','周五'];
    var slotText = ['上午第一节','上午第二节','下午第一节','下午第二节'];
    var coursePool = ['语文','数学','英语','物理','化学','生物','历史','地理','政治','体育','美术','音乐'];
    var tPool = ['王老师','李老师','张老师','刘老师','陈老师','杨老师','赵老师','周老师','孙老师','马老师'];

    var schList = [];
    clsList.forEach(function(cls) {
        var used = {};
        for (var i = 0; i < 16; i++) {
            var d = Math.floor(Math.random() * 5) + 1;
            var s = Math.floor(Math.random() * 4) + 1;
            var key = d + '-' + s;
            if (used[key]) continue;
            used[key] = true;
            schList.push({
                id: nextId('schedule'), classId: cls.id,
                courseName: coursePool[Math.floor(Math.random() * coursePool.length)],
                teacherName: tPool[Math.floor(Math.random() * tPool.length)],
                dayOfWeek: d, timeSlot: s,
                dayOfWeekText: dayText[d-1], timeSlotText: slotText[s-1],
                classroom: '10' + (Math.floor(Math.random() * 9) + 1) + '教室'
            });
        }
    });

    var attList = [];
    function dateStr(off) {
        var d = new Date(); d.setDate(d.getDate() - off);
        var m = d.getMonth() + 1, day = d.getDate();
        return d.getFullYear() + '-' + (m < 10 ? '0' + m : m) + '-' + (day < 10 ? '0' + day : day);
    }
    // 预生成最近 10 天的部分考勤（每个学生每天约70%概率有记录）
    stuList.forEach(function(stu) {
        for (var o = 0; o < 10; o++) {
            if (Math.random() < 0.3) continue;
            var r = Math.random(), st;
            if (r < 0.75) st = 1;       // 出勤
            else if (r < 0.85) st = 2;   // 迟到
            else if (r < 0.93) st = 3;   // 请假
            else st = 4;                  // 缺勤
            attList.push({ id: nextId('attendance'), studentId: stu.id, classId: stu.classId,
                           date: dateStr(o), status: st });
        }
    });

    var subjects = ['语文','数学','英语','物理','化学'];
    var examTypeText = ['','期中考试','期末考试','月考','单元测试','其他'];
    var scList = [];
    stuList.slice(0, 180).forEach(function(stu) {
        var n = 2 + Math.floor(Math.random() * 3);
        for (var i = 0; i < n; i++) {
            scList.push({
                id: nextId('score'), studentId: stu.id, studentName: stu.name,
                studentNo: stu.studentNo,
                subject: subjects[Math.floor(Math.random() * subjects.length)],
                score: parseFloat((35 + Math.random() * 65).toFixed(1)),
                examDate: dateStr(3 + Math.floor(Math.random() * 7)),
                examType: Math.floor(Math.random() * 5) + 1,
                remark: Math.random() > 0.7 ? '表现良好' : null
            });
        }
    });

    var hwList = [], subList = [];
    var hwTitles = ['第五章练习题','课后习题集','阅读笔记','实验报告','单元测试卷','复习提纲','作文练习'];
    clsList.slice(0, 4).forEach(function(cls) {
        var hwId = nextId('homework');
        var sub = Math.floor(Math.random() * clsList.length + 10);
        hwList.push({
            id: hwId, classId: cls.id, className: cls.className,
            title: hwTitles[Math.floor(Math.random() * hwTitles.length)],
            content: '请完成本次作业，截止时间前提交。',
            deadline: dateStr(-5 - Math.floor(Math.random() * 10)) + ' 18:00:00',
            publisher: cls.teacherName, status: 1, statusText: '进行中',
            submitCount: sub, totalCount: clsList.length + 30,
            createTime: dateStr(5 + Math.floor(Math.random() * 5)) + ' 10:30:00'
        });
        clsList.forEach(function(sc, idx) {
            var st = idx < sub ? (idx < Math.floor(sub * 0.5) ? 3 : 2) : 1;
            subList.push({
                id: nextId('submission'), homeworkId: hwId,
                studentId: 0, studentName: sc.className + '学生' + (idx+1),
                studentNo: '2024' + (1000 + idx),
                status: st, statusText: ['','未提交','已提交','已批改'][st],
                content: st >= 2 ? '已完成并提交' : null,
                submitTime: st >= 2 ? dateStr(2) + ' 14:30:00' : null,
                score: st === 3 ? parseFloat((60 + Math.random() * 40).toFixed(1)) : null,
                comment: st === 3 ? '认真完成' : null
            });
        });
    });

    /* ===== 辅助函数 ===== */
    function wait(ms) { return new Promise(function(r) { setTimeout(r, ms || 200 + Math.random() * 200); }); }
    function ok(d, m) { return { code: 200, message: m || 'success', data: d }; }
    function err(c, m) { return { code: c || 400, message: m || '请求失败', data: null }; }
    function page(list, p, ps) {
        p = p || 1; ps = ps || 10;
        return { total: list.length, list: list.slice((p-1) * ps, p * ps) };
    }
    function cp(o) { return JSON.parse(JSON.stringify(o)); }
    function notDel(a) { return a.filter(function(x) { return !x.isDeleted; }); }

    /* ===== window.API ===== */
    window.API = {

        login: function(p) {
            return wait(400).then(function() {
                if (!p.username) return err(400, '请输入用户名');
                if (!p.password) return err(400, '请输入密码');
                if (p.username === 'admin' && p.password === '123456') {
                    var t = 'mock-jwt-' + Date.now();
                    var u = { id: 1, username: 'admin', realName: '管理员' };
                    window.setToken(t); window.setCurrentUser(u);
                    return ok({ token: t, userId: 1, username: 'admin', realName: '管理员' });
                }
                return err(400, '用户名或密码错误');
            });
        },

        logout: function() { return wait(100).then(function() { window.clearToken(); return ok(null); }); },

        user: {
            info: function() {
                return wait(100).then(function() {
                    if (!window.checkLogin()) return err(401, '未登录或会话已过期');
                    return ok(window.getCurrentUser());
                });
            }
        },

        dashboard: {
            stats: function() {
                return wait(300).then(function() {
                    var sc = notDel(stuList);
                    var today = dateStr(0);
                    var records = attList.filter(function(a) { return a.date === today; });
                    var pre = records.filter(function(a) { return a.status === 1 || a.status === 2; }).length;
                    var rate = sc.length ? parseFloat((pre / sc.length * 100).toFixed(1)) : 0;
                    return ok({
                        totalStudent: sc.length, totalClass: notDel(clsList).length,
                        todayAttendanceRate: rate,
                        pendingHomeworkCount: subList.filter(function(s) { return s.status === 2; }).length
                    });
                });
            },
            classStudentDistribution: function() {
                return wait(300).then(function() {
                    return ok(notDel(clsList).map(function(c) {
                        return { className: c.className, studentCount: stuList.filter(function(s) {
                            return s.classId === c.id && !s.isDeleted;
                        }).length };
                    }).sort(function(a, b) { return b.studentCount - a.studentCount; }));
                });
            },
            attendanceTrend: function() {
                return wait(300).then(function() {
                    var total = notDel(stuList).length;
                    var trend = [];
                    for (var i = 6; i >= 0; i--) {
                        var dt = dateStr(i);
                        var recs = attList.filter(function(a) { return a.date === dt; });
                        var p = recs.filter(function(a) { return a.status === 1 || a.status === 2; }).length;
                        trend.push({ date: dt, attendanceRate: total ? parseFloat((p / total * 100).toFixed(1)) : 0 });
                    }
                    return ok(trend);
                });
            }
        },

        class: {
            list: function(p) {
                return wait(300).then(function() {
                    var lst = cp(notDel(clsList));
                    if (p.className) lst = lst.filter(function(c) { return c.className.indexOf(p.className) !== -1; });
                    lst.forEach(function(c) {
                        c.studentCount = stuList.filter(function(s) { return s.classId === c.id && !s.isDeleted; }).length;
                    });
                    return ok(page(lst, p.page, p.pageSize));
                });
            },
            all: function() {
                return wait(200).then(function() {
                    return ok(notDel(clsList).map(function(c) { return { id: c.id, className: c.className }; }));
                });
            },
            getById: function(id) {
                return wait(200).then(function() {
                    var c = clsList.find(function(c) { return c.id === Number(id) && !c.isDeleted; });
                    if (!c) return err(400, '班级不存在');
                    var r = cp(c);
                    r.studentCount = stuList.filter(function(s) { return s.classId === r.id && !s.isDeleted; }).length;
                    return ok(r);
                });
            },
            create: function(p) {
                return wait(300).then(function() {
                    if (!p.className) return err(400, '请输入班级名称');
                    if (clsList.some(function(c) { return c.className === p.className && !c.isDeleted; }))
                        return err(400, '该班级名称已存在，请更换');
                    var c = { id: nextId('class'), className: p.className, teacherName: p.teacherName || '',
                              description: p.description || '', createTime: dateStr(0) + ' ' + new Date().toTimeString().slice(0,8) };
                    clsList.push(c);
                    return ok({ id: c.id });
                });
            },
            update: function(id, p) {
                return wait(300).then(function() {
                    var idx = clsList.findIndex(function(c) { return c.id === Number(id) && !c.isDeleted; });
                    if (idx === -1) return err(400, '班级不存在');
                    if (clsList.some(function(c) { return c.className === p.className && c.id !== Number(id) && !c.isDeleted; }))
                        return err(400, '该班级名称已存在，请更换');
                    clsList[idx].className = p.className;
                    clsList[idx].teacherName = p.teacherName;
                    clsList[idx].description = p.description || '';
                    return ok(null);
                });
            },
            delete: function(id) {
                return wait(300).then(function() {
                    var c = clsList.find(function(c) { return c.id === Number(id) && !c.isDeleted; });
                    if (!c) return err(400, '班级不存在');
                    var cnt = stuList.filter(function(s) { return s.classId === Number(id) && !s.isDeleted; }).length;
                    if (cnt > 0) return err(400, '该班级下还有 ' + cnt + ' 名学生，请先将学生转出后再删除');
                    c.isDeleted = true;
                    return ok(null);
                });
            }
        },

        student: {
            list: function(p) {
                return wait(300).then(function() {
                    var lst = cp(notDel(stuList));
                    if (p.name) lst = lst.filter(function(s) { return s.name.indexOf(p.name) !== -1; });
                    if (p.classId) lst = lst.filter(function(s) { return s.classId === Number(p.classId); });
                    lst.forEach(function(s) {
                        var c = clsList.find(function(c) { return c.id === s.classId; });
                        s.className = c ? c.className : '已删除';
                        s.genderText = s.gender === 1 ? '男' : '女';
                    });
                    return ok(page(lst, p.page, p.pageSize));
                });
            },
            getById: function(id) {
                return wait(200).then(function() {
                    var s = stuList.find(function(s) { return s.id === Number(id) && !s.isDeleted; });
                    if (!s) return err(400, '学生不存在');
                    var r = cp(s);
                    var c = clsList.find(function(c) { return c.id === r.classId; });
                    r.className = c ? c.className : '已删除';
                    r.genderText = r.gender === 1 ? '男' : '女';
                    return ok(r);
                });
            },
            create: function(p) {
                return wait(300).then(function() {
                    if (!p.name) return err(400, '请输入学生姓名');
                    if (!p.studentNo) return err(400, '请输入学号');
                    if (stuList.some(function(s) { return s.studentNo === p.studentNo && !s.isDeleted; }))
                        return err(400, '该学号已被使用，请更换');
                    if (!clsList.find(function(c) { return c.id === Number(p.classId) && !c.isDeleted; }))
                        return err(400, '所选班级不存在');
                    var s = { id: nextId('student'), name: p.name, studentNo: p.studentNo,
                              gender: p.gender || 1, phone: p.phone || null, classId: Number(p.classId),
                              enrollmentDate: p.enrollmentDate, isDeleted: false };
                    stuList.push(s);
                    return ok({ id: s.id });
                });
            },
            update: function(id, p) {
                return wait(300).then(function() {
                    var idx = stuList.findIndex(function(s) { return s.id === Number(id) && !s.isDeleted; });
                    if (idx === -1) return err(400, '学生不存在');
                    if (stuList.some(function(s) { return s.studentNo === p.studentNo && s.id !== Number(id) && !s.isDeleted; }))
                        return err(400, '该学号已被使用，请更换');
                    stuList[idx].name = p.name; stuList[idx].studentNo = p.studentNo;
                    stuList[idx].gender = p.gender; stuList[idx].phone = p.phone || null;
                    stuList[idx].classId = Number(p.classId); stuList[idx].enrollmentDate = p.enrollmentDate;
                    return ok(null);
                });
            },
            delete: function(id) {
                return wait(300).then(function() {
                    var s = stuList.find(function(s) { return s.id === Number(id) && !s.isDeleted; });
                    if (!s) return err(400, '学生不存在');
                    s.isDeleted = true;
                    return ok(null);
                });
            }
        },

        schedule: {
            list: function(p) {
                return wait(300).then(function() {
                    if (!p.classId) return err(400, '班级ID不能为空');
                    return ok(cp(schList.filter(function(s) { return s.classId === Number(p.classId) && !s.isDeleted; })));
                });
            },
            create: function(p) {
                return wait(300).then(function() {
                    if (!p.courseName) return err(400, '请输入课程名称');
                    if (schList.find(function(s) {
                        return s.classId === Number(p.classId) && s.dayOfWeek === Number(p.dayOfWeek) &&
                               s.timeSlot === Number(p.timeSlot) && !s.isDeleted;
                    })) return err(400, '该时间段已有课程，请选择其他时间段');
                    var s = { id: nextId('schedule'), classId: Number(p.classId), courseName: p.courseName,
                              teacherName: p.teacherName || '', dayOfWeek: Number(p.dayOfWeek),
                              timeSlot: Number(p.timeSlot), dayOfWeekText: dayText[Number(p.dayOfWeek)-1],
                              timeSlotText: slotText[Number(p.timeSlot)-1], classroom: p.classroom || null };
                    schList.push(s);
                    return ok({ id: s.id });
                });
            },
            update: function(id, p) {
                return wait(300).then(function() {
                    var idx = schList.findIndex(function(s) { return s.id === Number(id); });
                    if (idx === -1) return err(400, '排课不存在');
                    var conflict = schList.find(function(s) {
                        return s.classId === schList[idx].classId && s.dayOfWeek === Number(p.dayOfWeek) &&
                               s.timeSlot === Number(p.timeSlot) && s.id !== Number(id) && !s.isDeleted;
                    });
                    if (conflict) return err(400, '该时间段已有课程，请选择其他时间段');
                    schList[idx].courseName = p.courseName; schList[idx].teacherName = p.teacherName;
                    schList[idx].dayOfWeek = Number(p.dayOfWeek); schList[idx].timeSlot = Number(p.timeSlot);
                    schList[idx].dayOfWeekText = dayText[Number(p.dayOfWeek)-1];
                    schList[idx].timeSlotText = slotText[Number(p.timeSlot)-1];
                    schList[idx].classroom = p.classroom || null;
                    return ok(null);
                });
            },
            delete: function(id) {
                return wait(300).then(function() {
                    var s = schList.find(function(s) { return s.id === Number(id); });
                    if (!s) return err(400, '排课不存在');
                    s.isDeleted = true;
                    return ok(null);
                });
            }
        },

        attendance: {
            list: function(p) {
                return wait(300).then(function() {
                    if (!p.classId) return err(400, '请选择班级');
                    if (!p.date) return err(400, '请选择日期');
                    var classId = Number(p.classId), date = p.date;
                    var exist = attList.filter(function(a) { return a.classId === classId && a.date === date; });
                    if (exist.length > 0) {
                        return ok(exist.map(function(a) {
                            var stu = stuList.find(function(s) { return s.id === a.studentId; });
                            return { id: a.id, studentId: a.studentId, studentName: stu ? stu.name : '已删除',
                                     studentNo: stu ? stu.studentNo : '', status: a.status,
                                     statusText: ['','出勤','迟到','请假','缺勤'][a.status] };
                        }));
                    }
                    return ok(stuList.filter(function(s) { return s.classId === classId && !s.isDeleted; }).map(function(s) {
                        return { id: null, studentId: s.id, studentName: s.name, studentNo: s.studentNo,
                                 status: 1, statusText: '出勤' };
                    }));
                });
            },
            batchSave: function(p) {
                return wait(400).then(function() {
                    if (!p.records || !p.records.length) return err(400, '考勤记录不能为空');
                    p.records.forEach(function(r) {
                        var ex = attList.find(function(a) { return a.studentId === Number(r.studentId) && a.date === p.date; });
                        if (ex) { ex.status = Number(r.status); }
                        else { attList.push({ id: nextId('attendance'), studentId: Number(r.studentId),
                                              classId: Number(p.classId), date: p.date, status: Number(r.status) }); }
                    });
                    return ok(null);
                });
            },
            update: function(id, p) {
                return wait(300).then(function() {
                    var a = attList.find(function(a) { return a.id === Number(id); });
                    if (!a) return err(400, '考勤记录不存在');
                    a.status = Number(p.status);
                    return ok(null);
                });
            },
            stats: function(p) {
                return wait(200).then(function() {
                    var classId = Number(p.classId), date = p.date;
                    var total = stuList.filter(function(s) { return s.classId === classId && !s.isDeleted; }).length;
                    var recs = attList.filter(function(a) { return a.classId === classId && a.date === date; });
                    var pc = 0, lc = 0, lv = 0, ab = 0;
                    recs.forEach(function(a) {
                        if (a.status === 1) pc++; else if (a.status === 2) lc++;
                        else if (a.status === 3) lv++; else ab++;
                    });
                    pc += total - recs.length; // 未记录的默认为出勤
                    return ok({ totalCount: total, presentCount: pc, lateCount: lc, leaveCount: lv,
                                absentCount: ab,
                                attendanceRate: total ? parseFloat(((pc + lc) / total * 100).toFixed(1)) : 0 });
                });
            }
        },

        score: {
            list: function(p) {
                return wait(300).then(function() {
                    var classId = Number(p.classId), subject = p.subject,
                        examDate = p.examDate, examType = Number(p.examType);
                    var cStuIds = stuList.filter(function(s) { return s.classId === classId && !s.isDeleted; }).map(function(s) { return s.id; });
                    var lst = scList.filter(function(sc) {
                        return cStuIds.indexOf(sc.studentId) !== -1 && sc.subject === subject &&
                               sc.examDate === examDate && sc.examType === examType;
                    });
                    var ss = lst.map(function(s) { return Number(s.score); });
                    var avg = ss.length ? parseFloat((ss.reduce(function(a,b){return a+b;},0)/ss.length).toFixed(1)) : 0;
                    var max = ss.length ? Math.max.apply(null, ss) : 0;
                    var min = ss.length ? Math.min.apply(null, ss) : 0;
                    var pass = ss.filter(function(s) { return s >= 60; }).length;
                    return ok({ examInfo: { subject: subject, examDate: examDate, examType: examType,
                                            examTypeText: examTypeText[examType] },
                                stats: { avgScore: avg, maxScore: max, minScore: min,
                                         passRate: ss.length ? parseFloat((pass/ss.length*100).toFixed(1)) : 0 },
                                list: cp(lst) });
                });
            },
            studentScores: function(sid, p) { return wait(300).then(function() {
                var lst = scList.filter(function(sc) { return sc.studentId === Number(sid); });
                var r = page(lst, p.page, p.pageSize);
                r.list.forEach(function(s) { s.examTypeText = examTypeText[s.examType] || '其他'; });
                return ok(r);
            });},
            batchSave: function(p) { return wait(400).then(function() {
                if (!p.records || !p.records.length) return err(400, '成绩记录不能为空');
                var dup = false;
                p.records.forEach(function(r) {
                    if (scList.find(function(s) { return s.studentId === Number(r.studentId) && s.subject === p.subject &&
                                                   s.examDate === p.examDate && s.examType === Number(p.examType); }))
                    { dup = true; return; }
                    var stu = stuList.find(function(s) { return s.id === Number(r.studentId); });
                    scList.push({ id: nextId('score'), studentId: Number(r.studentId),
                                  studentName: stu ? stu.name : '已删除', studentNo: stu ? stu.studentNo : '',
                                  subject: p.subject, score: Number(r.score), examDate: p.examDate,
                                  examType: Number(p.examType), remark: r.remark || null });
                });
                if (dup) return err(400, '该学生此科目的成绩已存在，是否覆盖');
                return ok(null);
            });},
            update: function(id, p) { return wait(300).then(function() {
                var sc = scList.find(function(s) { return s.id === Number(id); });
                if (!sc) return err(400, '成绩记录不存在');
                if (p.score < 0 || p.score > 100) return err(400, '成绩应在 0-100 之间');
                sc.score = Number(p.score); sc.remark = p.remark || null;
                return ok(null);
            });},
            stats: function(p) { return wait(200).then(function() {
                var cStuIds = stuList.filter(function(s) { return s.classId === Number(p.classId) && !s.isDeleted; }).map(function(s) { return s.id; });
                var ss = scList.filter(function(sc) {
                    return cStuIds.indexOf(sc.studentId) !== -1 && sc.subject === p.subject &&
                           sc.examDate === p.examDate && sc.examType === Number(p.examType);
                }).map(function(s) { return Number(s.score); });
                var avg = ss.length ? parseFloat((ss.reduce(function(a,b){return a+b;},0)/ss.length).toFixed(1)) : 0;
                return ok({ avgScore: avg, maxScore: ss.length ? Math.max.apply(null,ss) : 0,
                            minScore: ss.length ? Math.min.apply(null,ss) : 0,
                            passRate: ss.length ? parseFloat((ss.filter(function(s){return s>=60;}).length/ss.length*100).toFixed(1)) : 0,
                            totalCount: ss.length });
            });}
        },

        homework: {
            list: function(p) { return wait(300).then(function() {
                return ok(page(hwList.filter(function(h) { return h.classId === Number(p.classId) && !h.isDeleted; }), p.page, p.pageSize));
            });},
            create: function(p) { return wait(300).then(function() {
                if (!p.title) return err(400, '请输入作业标题');
                if (p.deadline && p.deadline < new Date().toISOString().replace('T',' ').substring(0,19))
                    return err(400, '截止时间不能早于当前时间');
                var hwId = nextId('homework');
                var cls = clsList.find(function(c) { return c.id === Number(p.classId); });
                var hw = { id: hwId, classId: Number(p.classId), className: cls ? cls.className : '',
                           title: p.title, content: p.content || '', deadline: p.deadline,
                           publisher: p.publisher || '管理员', status: 1, statusText: '进行中',
                           submitCount: 0, totalCount: 0, createTime: dateStr(0) + ' ' + new Date().toTimeString().slice(0,8) };
                hwList.push(hw);
                var clsStus = stuList.filter(function(s) { return s.classId === Number(p.classId) && !s.isDeleted; });
                hw.totalCount = clsStus.length;
                clsStus.forEach(function(stu) {
                    subList.push({ id: nextId('submission'), homeworkId: hwId, studentId: stu.id,
                                   studentName: stu.name, studentNo: stu.studentNo,
                                   status: 1, statusText: '未提交', content: null, submitTime: null,
                                   score: null, comment: null });
                });
                return ok({ id: hwId });
            });},
            update: function(id, p) { return wait(300).then(function() {
                var hw = hwList.find(function(h) { return h.id === Number(id); });
                if (!hw) return err(400, '作业不存在');
                if (subList.some(function(s) { return s.homeworkId === hw.id && s.status >= 2; }))
                    return err(400, '已有学生提交，无法编辑');
                hw.title = p.title; hw.content = p.content; hw.deadline = p.deadline;
                return ok(null);
            });},
            delete: function(id) { return wait(300).then(function() {
                var hw = hwList.find(function(h) { return h.id === Number(id); });
                if (!hw) return err(400, '作业不存在');
                if (subList.some(function(s) { return s.homeworkId === hw.id && s.status >= 2; }))
                    return err(400, '已有学生提交，无法删除，是否改为关闭该作业？');
                hw.isDeleted = true;
                return ok(null);
            });},
            submissions: function(id, p) { return wait(300).then(function() {
                return ok(page(subList.filter(function(s) { return s.homeworkId === Number(id); }), p.page, p.pageSize));
            });},
            gradeSubmission: function(id, p) { return wait(300).then(function() {
                var sub = subList.find(function(s) { return s.id === Number(id); });
                if (!sub) return err(400, '提交记录不存在');
                if (p.score < 0 || p.score > 100) return err(400, '评分应在 0-100 之间');
                sub.score = Number(p.score); sub.comment = p.comment || null;
                sub.status = 3; sub.statusText = '已批改';
                return ok(null);
            });}
        }

    };

})();
