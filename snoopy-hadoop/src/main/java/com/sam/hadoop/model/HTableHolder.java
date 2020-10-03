package com.sam.hadoop.model;


import java.util.ArrayList;
import java.util.List;

public class HTableHolder {

    private final static List<HTable> tables = new ArrayList<>();

    public static void add(HTable table) {
        tables.add(table);
    }

    public static List<HTable> getTables() {
        return tables;
    }

}
