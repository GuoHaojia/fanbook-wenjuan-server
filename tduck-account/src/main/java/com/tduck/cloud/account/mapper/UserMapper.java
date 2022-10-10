package com.tduck.cloud.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tduck.cloud.account.entity.UserEntity;
import com.tduck.cloud.account.vo.RoleVo;
import com.tduck.cloud.account.vo.UserRoleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户(AcUser)表数据库访问层
 *
 * @author smalljop
 * @since 2020-11-10 18:10:41
 */
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    List<UserRoleVo> getUserByRole(RoleVo roleVo);

    Long updateUserBelong(UserRoleVo roleVo);
}