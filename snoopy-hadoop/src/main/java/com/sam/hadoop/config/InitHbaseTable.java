package com.sam.hadoop.config;

import com.sam.hadoop.model.HTable;
import com.sam.hadoop.model.HTableHolder;
import com.sam.hadoop.service.HBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@AutoConfigureAfter(HadoopConfig.class)
@ConditionalOnBean(HBaseService.class)
public class InitHbaseTable implements ApplicationRunner, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        HBaseService service = applicationContext.getBean(HBaseService.class);
        List<HTable> tables = HTableHolder.getTables();
        try {
            for (HTable hTable : tables) {
                String namespace = hTable.getNamespace();
                String tableName = hTable.getTableName();
                Set<String> families = hTable.getFamilies();
                service.createNamespace(namespace);
                service.createTable(tableName, families);
                log.info("Create HTable {}", hTable);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
