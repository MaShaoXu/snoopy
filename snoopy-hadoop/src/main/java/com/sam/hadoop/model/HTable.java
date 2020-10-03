package com.sam.hadoop.model;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HTable {
    private String namespace;
    private String tableName;
    private Set<String> families;

    public HTable(String namespace, String tableName) {
        this.namespace = namespace;
        this.tableName = tableName;
    }
}
