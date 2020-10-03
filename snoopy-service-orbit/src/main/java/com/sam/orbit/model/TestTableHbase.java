package com.sam.orbit.model;

import com.sam.hadoop.annotation.HbaseColumn;
import com.sam.hadoop.annotation.HbaseTable;

@HbaseTable
public class TestTableHbase {

    @HbaseColumn
    private String name;

    @HbaseColumn
    private Integer age;

}
