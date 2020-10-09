package com.sam.hadoop.service;

import com.sam.hadoop.annotation.HbaseColumn;
import com.sam.hadoop.annotation.HbaseTable;
import com.sam.hadoop.model.HBaseModel;
import com.sam.hadoop.model.HCell;
import com.sam.hadoop.utils.TypeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HBaseDataService {

    private String active;
    private HBaseService hBaseService;

    public HBaseDataService(String active, HBaseService hBaseService) {
        this.active = active;
        this.hBaseService = hBaseService;
    }

    public void put(HBaseModel model) throws IllegalAccessException, IOException {
        String tableName = getTableName(model);
        List<HCell> cells = trans(model);
        hBaseService.put(tableName, cells);
    }

    private String getTableName(HBaseModel model){
        Class<? extends HBaseModel> aClass = model.getClass();
        HbaseTable hbaseTable = aClass.getAnnotation(HbaseTable.class);
        if (hbaseTable == null) {
            return null;
        }
        String namespace = hbaseTable.namespace();
        String tableName = hbaseTable.tableName();
        if (StringUtils.isEmpty(tableName)) {
            tableName = aClass.getSimpleName();
        }
        String fullTableName;
        if (StringUtils.isNotEmpty(namespace)) {
            fullTableName = namespace + ":" + tableName;
        } else if (StringUtils.isNotEmpty(active)) {
            fullTableName = active + ":" + tableName;
        } else {
            fullTableName = "PUB:" + tableName;
        }
        return fullTableName;
    }

    private List<HCell> trans(HBaseModel model) throws IllegalAccessException {
        Class<? extends HBaseModel> aClass = model.getClass();
        String rowKey = model.getRowKey();
        HbaseTable hbaseTable = aClass.getAnnotation(HbaseTable.class);
        String tableName = hbaseTable.tableName();
        Field[] declaredFields = aClass.getDeclaredFields();
        List<HCell> cells = new ArrayList<>();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(HbaseColumn.class)) {
                String name = field.getName();
                byte[] bytes = TypeUtils.getBytes(field.get(model));
                HbaseColumn hbaseColumn = field.getAnnotation(HbaseColumn.class);

                String family = hbaseColumn.family();
                if (StringUtils.isNotEmpty(family)) {
                    HCell hCell1 = new HCell(rowKey, family, name, bytes);
                    cells.add(hCell1);
                } else {
                    HCell hCell1 = new HCell(rowKey, tableName, name, bytes);
                    cells.add(hCell1);
                }
            }
        }
        return cells;
    }

}
