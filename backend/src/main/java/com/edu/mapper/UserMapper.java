package com.edu.mapper;

import com.edu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 * <p>提供用户信息的数据库查询与更新操作</p>
 */
@Mapper
public interface UserMapper {

    /** 根据用户名查询用户（用于登录校验） */
    User findByUsername(@Param("username") String username);

    /** 根据 ID 查询用户（用于获取用户信息） */
    User findById(@Param("id") Long id);

    /** 更新登录失败次数和锁定截止时间 */
    void updateLoginError(User user);

    /** 登录成功，重置错误次数和锁定时间 */
    void resetLoginError(@Param("id") Long id);
}
