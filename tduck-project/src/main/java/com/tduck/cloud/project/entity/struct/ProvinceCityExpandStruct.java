package com.tduck.cloud.project.entity.struct;

import lombok.Data;

import java.util.List;

/**
 * @author : smalljop
 * @description : 级联选择
 * @create : 2020-11-19 15:13
 **/
@Data
public class ProvinceCityExpandStruct {
    /**
     * 地址类型
     */
    public Integer provinceRadio;
    /**
     * 地址
     */
    public String address;

}
