package com.tduck.cloud.project.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
public class DxCountVo {
    private String titie;

    private List<Option> options;

    private Object num;

    @Data
    @AllArgsConstructor
    public static class Option {
        public String key;

        public Object value;

        public String percent;
    }

}
