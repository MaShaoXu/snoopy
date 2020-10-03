package com.sam.hadoop.model;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HCell {

    private String rowKey;
    private String family;
    private String qualifier;
    private byte[] value;
    private byte type;
    private long timestamp;

    public HCell(String rowKey, String family, String qualifier, byte[] value) {
        this.rowKey = rowKey;
        this.family = family;
        this.qualifier = qualifier;
        this.value = value;
    }

}
