package com.tduck.cloud.account.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author Cool
 * @version 1.0
 * @email 861917988@qq.com
 * @data 2022/10/19 15:24
 * <p>
 * mark
 */
@Data
@Builder
public class Sign {
    private String appKey;
    private String nonce;
    private String timestamp;
    private String signature;
}
