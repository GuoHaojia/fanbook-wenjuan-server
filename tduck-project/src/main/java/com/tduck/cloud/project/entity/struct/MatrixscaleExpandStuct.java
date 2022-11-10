package com.tduck.cloud.project.entity.struct;

import lombok.Data;

import java.util.List;

@Data
public class MatrixscaleExpandStuct {
    private Table table;
    private MaxTipData maxTipData;
    private String showIcon;
    private String maxTip;
    private Integer level;

    public static class Table {
        public List<Option> rows;
    }

    public static class Option {
        public String label;
        public Integer id;
    }

    public static class MaxTipData {
        public String max;
        public String min;
    }


}
