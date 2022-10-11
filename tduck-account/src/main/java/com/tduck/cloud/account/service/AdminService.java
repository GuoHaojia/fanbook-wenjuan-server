package com.tduck.cloud.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.entity.RoleEntity;
import com.tduck.cloud.account.vo.AdminRoleVo;
import com.tduck.cloud.account.vo.AdminVo;
import com.tduck.cloud.account.vo.UserRoleVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 10:29
 * <p>
 * mark
 */

@Service
public interface AdminService extends IService<AdminEntity> {
    List<AdminEntity> queryList();
    List<AdminEntity> adminListByRole(AdminVo adminVo);
    Long updateUserBelong(AdminRoleVo userRoleVo);
}
