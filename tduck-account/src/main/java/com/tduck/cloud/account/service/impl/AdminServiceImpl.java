package com.tduck.cloud.account.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tduck.cloud.account.entity.AdminEntity;
import com.tduck.cloud.account.entity.RoleEntity;
import com.tduck.cloud.account.mapper.AdminMapper;
import com.tduck.cloud.account.mapper.RoleMapper;
import com.tduck.cloud.account.mapper.UserMapper;
import com.tduck.cloud.account.service.AdminService;
import com.tduck.cloud.account.vo.AdminRoleVo;
import com.tduck.cloud.account.vo.AdminVo;
import com.tduck.cloud.account.vo.UserRoleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/11 11:31
 * <p>
 * mark
 */


@Slf4j
@Service("adminService")
@Component
public class AdminServiceImpl extends ServiceImpl<AdminMapper, AdminEntity> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public List<AdminEntity> queryList(){
        return this.adminMapper.queryList();
    }

    @Override
    public List<AdminEntity> adminListByRole(AdminVo adminVo){
        List<AdminEntity> list = this.adminMapper.getUserByRole(adminVo);
        return list;
    }

    @Override
    public Long updateUserBelong(AdminRoleVo adminRoleVo) {
        return this.adminMapper.updateUserBelong(adminRoleVo);
    }
}
