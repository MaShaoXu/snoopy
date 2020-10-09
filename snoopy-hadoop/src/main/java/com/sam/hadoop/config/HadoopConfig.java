package com.sam.hadoop.config;

import com.sam.hadoop.service.HBaseDataService;
import com.sam.hadoop.service.HBaseService;
import com.sam.hadoop.service.HdfsSercice;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.log4j.BasicConfigurator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

@org.springframework.context.annotation.Configuration
@EnableConfigurationProperties({HadoopProperties.class})
public class HadoopConfig {

    @Value("spring.profiles.active")
    private String active;

    private static final String HBASE_QUORUM = "hbase.zookeeper.quorum";
    private static final String HBASE_PORT = "hbase.zookeeper.property.clientPort";
    private static final String HDFS_PATH = "fs.defaultFS";

    @Bean
    public Configuration configuration(HadoopProperties hbaseProperties) {
        //设置临时的hadoop环境变量，程序会去\bin目录下找winutils.exe工具
        String hadoop_home = System.getenv("HADOOP_HOME");
        String username = System.getenv("USERNAME");
        System.setProperty("hadoop.home.dir", hadoop_home);
        System.setProperty("HADOOP_USER_NAME", username);
        BasicConfigurator.configure();
        Configuration conf = HBaseConfiguration.create();
        conf.set(HBASE_QUORUM, hbaseProperties.getZkQuorum());
        conf.set(HBASE_PORT, hbaseProperties.getZkClientPort());
        conf.set(HDFS_PATH, hbaseProperties.getHdfsPath());
        return conf;
    }

    @Bean
    public HBaseService hBaseService(Configuration configuration) throws IOException {
        return new HBaseService(configuration);
    }

    @Bean
    public HBaseDataService hBaseDataService(HBaseService hBaseService) {
        return new HBaseDataService(active, hBaseService);
    }

    @Bean
    public HdfsSercice hdfsSercice(Configuration configuration) throws IOException {
        return new HdfsSercice(configuration);
    }

}
