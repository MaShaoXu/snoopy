package com.sam.orbit.controller;

import com.sam.hadoop.service.HBaseService;
import com.sam.hadoop.service.HdfsSercice;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.hbase.TableName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class OrbitController {

    @Autowired
    private HBaseService hBaseService;

    @Autowired
    private HdfsSercice hdfsSercice;

    @GetMapping("/hello")
    public String hello(String name) {
        return "hello " + name;
    }

    @GetMapping("/test1")
    public List<FileStatus> hello1() {
        try {
            return hdfsSercice.listStatus("/hbase");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/list")
    public List<TableName> list() {
        try {
            return hBaseService.listTableNames();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
