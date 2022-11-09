package com.tduck.cloud.project.entity.struct;

import lombok.Data;

import java.util.List;

@Data
public class MatrixscaleExpandStuct {
    private Table table;
    public MaxTipData maxTipData;
    public String showIcon;


    public static class Table {
        public List<Option> rows;
        public Integer level;
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
