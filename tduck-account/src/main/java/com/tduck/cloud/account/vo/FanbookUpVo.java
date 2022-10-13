package com.tduck.cloud.account.vo;

import com.tduck.cloud.account.entity.MemberInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/13 15:05
 * <p>
 * mark
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FanbookUpVo {
    List<MemberInfo> members;
    Long roleid;
}
