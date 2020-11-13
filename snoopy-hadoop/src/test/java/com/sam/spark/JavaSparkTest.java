package com.sam.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Test;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class JavaSparkTest implements Serializable {

    // local、yarn-client、yarn-standalone等
    // 应用名
    private static SparkConf conf = new SparkConf().setMaster("local").setAppName("Spark Test");
    private static JavaSparkContext jsc = new JavaSparkContext(conf);

    @After
    public void destroy() {
        jsc.stop();
        jsc.close();
    }

    @Test
    public void sparkPi() {
        int n = 200000;

        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            integers.add(i);
        }

        JavaRDD<Integer> dataSet = jsc.parallelize(integers);

        Integer count = dataSet.map((Function<Integer, Integer>) integer -> {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;
            return (x * x + y * y < 1) ? 1 : 0;
        }).reduce((Function2<Integer, Integer, Integer>) (integer, integer2) -> integer + integer2);

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Pi is roughly " + 4.0 * count / n);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    public static void main(String[] args) {
        //从HDFS文件转化 sc.textFile("hdfs://")
        //从本地文件转化 sc.textFile("file:/")
        String classFilePath = JavaSparkTest.class.getResource("/blsmy.txt").getPath();
        //读入数据
        JavaRDD<String> lines = jsc.textFile(classFilePath);
        //分词
        JavaRDD<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            public Iterator<String> call(String line) throws Exception {
                return Arrays.asList(line.split(" ")).iterator();
            }
        });

        //每个单词记一次数
        JavaPairRDD<String, Integer> wordOne = words.mapToPair(new PairFunction<String, String, Integer>() {
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String, Integer>(word, 1);
            }
        });

        //执行reduceByKey的操作
        JavaPairRDD<String, Integer> count = wordOne.reduceByKey(new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer i1, Integer i2) throws Exception {
                return i1 + i2;
            }
        });

        //执行计算，执行action操作: 把结果打印在屏幕上
        //count、collect、save等
        List<Tuple2<String, Integer>> result = count.collect();
        for (Tuple2<String, Integer> tuple : result) {
            System.out.println(tuple._1 + "\t" + tuple._2);
        }
        System.out.println("RDD count " + count.count());
        count.saveAsTextFile("C:\\Users\\Administrator\\Desktop\\orbit\\test.txt");
    }

    @Test
    public void sparkSQL() {
        String classFilePath = JavaSparkTest.class.getResource("/people.txt").getPath();
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
        Dataset<Row> df = spark.read().json(classFilePath);
        // 显示表的内容 (前20条)
        df.show(2);
        // 打印节点 (tree 结构)
//        df.printSchema();
//        //选择属性显示 并对属性做简单操作
//        df.select(df.col("name"), df.col("age").plus(1)).show();
//        //简单的过滤
//        df.filter(df.col("age").gt(21)).show();
//        //分组统计
        df.groupBy("age").count().show();


        df.createOrReplaceTempView("peopleTmp");
//        Dataset<Row> teenagers = spark.sql("select name,age from peopleTmp where age > 13 and age <=19");
//        teenagers.toJavaRDD().map(row -> "Name: " + row.getString(0)).collect().forEach(System.out::println);
//        //parquet file
//        teenagers.write().mode(SaveMode.Overwrite).parquet("people.parquet");
//        //对parquet文件做些简单的操作
//        System.out.println("=== Data source: Parquet File ===");
//
//        Dataset<Row> parquet = spark.read().parquet("people.parquet");
//        parquet.show();
//        parquet.createOrReplaceTempView("parquetPeople");
//        Dataset<Row> teenagers2 = spark.sql("select name from parquetPeople where age > 13 and age <= 19");
//        teenagers2.show();
    }


}
