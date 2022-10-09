package com.tduck.cloud.account.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tduck.cloud.account.entity.RoleEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/9 16:18
 * <p>
 * mark
 */

public interface RoleService  extends IService<RoleEntity> {
    List<RoleEntity> queryList();
}
