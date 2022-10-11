package com.tduck.cloud.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.entity.RoleEntity;
import com.tduck.cloud.account.vo.AdminRoleVo;
import com.tduck.cloud.account.vo.AdminVo;
import com.tduck.cloud.account.vo.UserRoleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 11:33
 * <p>
 * mark
 */
@Mapper
public interface AdminMapper  extends BaseMapper<AdminEntity> {

    List<AdminEntity> queryList();
    List<AdminEntity> getUserByRole(AdminVo adminVo);
    Long updateUserBelong(AdminRoleVo adminRoleVo);
}
