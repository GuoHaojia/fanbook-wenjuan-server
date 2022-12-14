package com.tduck.cloud.project.entity.struct;

import lombok.Data;

import java.util.List;

@Data
public class MatrixselectExpandStuct {
    private Table table;

    /**
     * 多选
     */
    private Boolean multiple;

    public static class Table {
        public List<Option> rows;
        public List<Option> columns;
    }

    public static class Option {
        public String label;
        public Integer id;
    }

}
