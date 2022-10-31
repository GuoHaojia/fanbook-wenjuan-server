package com.tduck.cloud.project.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JzCountVo {

    private String lable;

    private List<DxCountVo> table;
}
