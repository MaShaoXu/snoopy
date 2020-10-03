package com.sam.hadoop.service;

import com.sam.hadoop.model.HCell;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HBaseService {

    private Connection connection;
    private HBaseAdmin admin;

    public HBaseService(Configuration conf) throws IOException {
        connection = ConnectionFactory.createConnection(conf);
        admin = (HBaseAdmin) connection.getAdmin();
    }

    public void createNamespace(String namespace) throws IOException {
        if (!namespaceExists(namespace)) {
            admin.createNamespace(NamespaceDescriptor.create(namespace).build());
        }
    }

    public void deleteNamespace(String namespace) throws IOException {
        if (namespaceExists(namespace)) {
            Pattern compile = Pattern.compile(namespace + ":.*");
            admin.disableTables(compile);
            admin.deleteTables(compile);
            admin.deleteNamespace(namespace);
        }
    }

    public void modifyNamespace(NamespaceDescriptor descriptor) throws IOException {
        String namespace = descriptor.getName();
        if (namespaceExists(namespace)) {
            admin.modifyNamespace(descriptor);
        }
    }

    public List<NamespaceDescriptor> listNamespaceDescriptors() throws IOException {
        NamespaceDescriptor[] namespaceDescriptors = admin.listNamespaceDescriptors();
        return Arrays.asList(namespaceDescriptors);
    }

    public NamespaceDescriptor getNamespaceDescriptor(String namespace) throws IOException {
        return admin.getNamespaceDescriptor(namespace);
    }

    public boolean namespaceExists(String namespace) throws IOException {
        List<NamespaceDescriptor> namespaceDescriptors = listNamespaceDescriptors();
        if (namespaceDescriptors.size() < 1) {
            return false;
        }
        for (NamespaceDescriptor namespaceDescriptor : namespaceDescriptors) {
            String name = namespaceDescriptor.getName();
            if (StringUtils.isNotEmpty(name) && name.equals(namespace)) {
                return true;
            }
        }
        return false;
    }


    public void createTable(String tableName, Set<String> families) throws IOException {
        List<ColumnFamilyDescriptor> familyDescriptors = families.stream()
                .map(s -> ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(s)).build())
                .collect(Collectors.toList());
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        builder.setColumnFamilies(familyDescriptors);
        TableDescriptor build = builder.build();
        deleteTable(tableName);
        admin.createTable(build);
    }

    public void deleteTable(String tableName) throws IOException {
        if (admin.tableExists(TableName.valueOf(tableName))) {
            admin.disableTable(TableName.valueOf(tableName));
            admin.deleteTable(TableName.valueOf(tableName));
        }
    }

    public void modifyTable(TableDescriptor descriptor) throws IOException {
        TableName tableName = descriptor.getTableName();
        if (admin.tableExists(tableName)) {
            admin.modifyTable(descriptor);
        }
    }

    public List<TableDescriptor> listTableDescriptors() throws IOException {
        return admin.listTableDescriptors();
    }

    public TableDescriptor getTableDescriptors(String tableName) throws IOException {
        return admin.getTableDescriptor(TableName.valueOf(tableName));
    }

    public List<TableName> listTableNames() throws IOException {
        TableName[] tableNames = admin.listTableNames();
        return Arrays.asList(tableNames);
    }

    private Table getTable(String tableName) throws IOException {
        return connection.getTable(TableName.valueOf(tableName));
    }


    public void put(String tableName, HCell cell) throws IOException {
        Put put = getPut(cell);
        puts(tableName, Collections.singletonList(put));
    }

    public void put(String tableName, List<HCell> cells) throws IOException {
        List<Put> puts = cells.stream().map(this::getPut).collect(Collectors.toList());
        puts(tableName, puts);
    }

    public void puts(String tableName, List<Put> puts) throws IOException {
        try (Table table = getTable(tableName)) {
            table.put(puts);
        }
    }

    public Put getPut(HCell cell) {
        String rowKey = cell.getRowKey();
        String family = cell.getFamily();
        String qualifier = cell.getQualifier();
        byte[] valBytes = cell.getValue();
        Put put = new Put(Bytes.toBytes(rowKey));
        put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), valBytes);
        return put;
    }

    public void delete(String tableName, HCell cell) throws IOException {
        Delete delete = getDelete(cell);
        deletes(tableName, Collections.singletonList(delete));
    }

    public void delete(String tableName, List<HCell> cells) throws IOException {
        List<Delete> deletes = cells.stream().map(this::getDelete).collect(Collectors.toList());
        deletes(tableName, deletes);
    }

    public void deletes(String tableName, List<Delete> deletes) throws IOException {
        try (Table table = getTable(tableName)) {
            table.delete(deletes);
        }
    }

    public Delete getDelete(HCell cell) {
        String rowKey = cell.getRowKey();
        String family = cell.getFamily();
        String qualifier = cell.getQualifier();
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        if (StringUtils.isNotEmpty(family) && StringUtils.isNotEmpty(qualifier)) {
            delete.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        } else if (StringUtils.isNotEmpty(family)) {
            delete.addFamily(Bytes.toBytes(family));
        }
        return delete;
    }

    public List<HCell> getScanner(String tableName) throws IOException {
        return getScanner(tableName, new Scan());
    }

    public List<HCell> getScanner(String tableName, String family) throws IOException {
        List<HCell> result = new ArrayList<>();
        try (Table table = getTable(tableName); ResultScanner rs = table.getScanner(Bytes.toBytes(family))) {
            for (Result r : rs) {
                List<HCell> cells = listCells(r);
                result.addAll(cells);
            }
        }
        return result;
    }

    public List<HCell> getScanner(String tableName, String family, String qualifier) throws IOException {
        List<HCell> result = new ArrayList<>();
        try (Table table = getTable(tableName);
             ResultScanner rs = table.getScanner(Bytes.toBytes(family), Bytes.toBytes(qualifier))) {
            for (Result r : rs) {
                List<HCell> cells = listCells(r);
                result.addAll(cells);
            }
        }
        return result;
    }

    public List<HCell> getScanner(String tableName, Scan scan) throws IOException {
        List<HCell> result = new ArrayList<>();
        try (Table table = getTable(tableName); ResultScanner rs = table.getScanner(scan)) {
            for (Result r : rs) {
                List<HCell> cells = listCells(r);
                result.addAll(cells);
            }
        }
        return result;
    }

    public List<HCell> get(String tableName, HCell cell) throws Exception {
        Get get = getGet(cell);
        return gets(tableName, Collections.singletonList(get));
    }

    public List<HCell> get(String tableName, List<HCell> cells) throws Exception {
        List<Get> gets = cells.stream().map(this::getGet).collect(Collectors.toList());
        return gets(tableName, gets);
    }

    public List<HCell> gets(String tableName, List<Get> gets) throws Exception {
        List<HCell> result = new ArrayList<>();
        try (Table table = getTable(tableName)) {
            Result[] results = table.get(gets);
            for (Result r : results) {
                List<HCell> cells = listCells(r);
                result.addAll(cells);
            }
        }
        return result;
    }

    public Get getGet(HCell cell) {
        String rowKey = cell.getRowKey();
        String family = cell.getFamily();
        String qualifier = cell.getQualifier();
        Get get = new Get(Bytes.toBytes(rowKey));
        if (StringUtils.isNotEmpty(family) && StringUtils.isNotEmpty(qualifier)) {
            get.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        } else if (StringUtils.isNotEmpty(family)) {
            get.addFamily(Bytes.toBytes(family));
        }
        return get;
    }

    private List<HCell> listCells(Result result) {
        List<HCell> cells = new ArrayList<>();
        for (Cell cell : result.listCells()) {
            String rowKey = Bytes.toString(CellUtil.cloneRow(cell));
            String family = Bytes.toString(CellUtil.cloneFamily(cell));
            String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
            byte[] value = CellUtil.cloneValue(cell);
            byte type = cell.getType().getCode();
            long timestamp = cell.getTimestamp();
            HCell hCell = new HCell(rowKey, family, qualifier, value);
            hCell.setType(type);
            hCell.setTimestamp(timestamp);
            cells.add(hCell);
        }
        return cells;
    }

    public boolean exists(String tableName, HCell cell) throws IOException {
        try (Table table = getTable(tableName)) {
            Get get = getGet(cell);
            return exists(tableName, get);
        }
    }

    public boolean exists(String tableName, Get get) throws IOException {
        try (Table table = getTable(tableName)) {
            return table.exists(get);
        }
    }

}
