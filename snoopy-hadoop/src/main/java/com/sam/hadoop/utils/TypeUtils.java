package com.sam.hadoop.utils;

import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TypeUtils {

    public static byte[] getBytes(Object o){
        if (o.getClass().equals(float.class)) {
            float f =  (float) o;
            return Bytes.toBytes(f);
        } else if (o.getClass().equals(Float.class)) {
            Float f =  (Float) o;
            return Bytes.toBytes(f);
        }else if (o.getClass().equals(int.class)) {
            int i =  (int) o;
            return Bytes.toBytes(i);
        }else if (o.getClass().equals(Integer.class)) {
            Integer i =  (Integer) o;
            return Bytes.toBytes(i);
        }else if (o.getClass().equals(double.class)) {
            double d =  (double) o;
            return Bytes.toBytes(d);
        }else if (o.getClass().equals(Double.class)) {
            Double d =  (Double) o;
            return Bytes.toBytes(d);
        }else if (o.getClass().equals(long.class)) {
            long l =  (long) o;
            return Bytes.toBytes(l);
        }else if (o.getClass().equals(Long.class)) {
            Long l =  (Long) o;
            return Bytes.toBytes(l);
        }else if (o.getClass().equals(boolean.class)) {
            boolean b =  (boolean) o;
            return Bytes.toBytes(b);
        }else if (o.getClass().equals(Boolean.class)) {
            Boolean b =  (Boolean) o;
            return Bytes.toBytes(b);
        }else if (o.getClass().equals(short.class)) {
            short s =  (short) o;
            return Bytes.toBytes(s);
        }else if (o.getClass().equals(Short.class)) {
            Short s =  (Short) o;
            return Bytes.toBytes(s);
        }else if (o.getClass().equals(String.class)) {
            String s =  (String) o;
            return Bytes.toBytes(s);
        }else if (o.getClass().equals(BigDecimal.class)) {
            BigDecimal bd =  (BigDecimal) o;
            return Bytes.toBytes(bd);
        }else if (o.getClass().equals(ByteBuffer.class)) {
            ByteBuffer bb =  (ByteBuffer) o;
            return Bytes.toBytes(bb);
        }else {
            throw new IllegalArgumentException(o.getClass().toString());
        }
    }

    public static <T> Object getType(byte[] bytes, Class<T> tClass){
        if (tClass.equals(float.class)) {
            return Bytes.toFloat(bytes);
        } else if (tClass.equals(Float.class)) {
            return Bytes.toFloat(bytes);
        }else if (tClass.equals(int.class)) {
            return Bytes.toInt(bytes);
        }else if (tClass.equals(Integer.class)) {
            return Bytes.toInt(bytes);
        }else if (tClass.equals(double.class)) {
            return Bytes.toDouble(bytes);
        }else if (tClass.equals(Double.class)) {
            return Bytes.toDouble(bytes);
        }else if (tClass.equals(long.class)) {
            return Bytes.toLong(bytes);
        }else if (tClass.equals(Long.class)) {
            return Bytes.toLong(bytes);
        }else if (tClass.equals(boolean.class)) {
            return Bytes.toBoolean(bytes);
        }else if (tClass.equals(Boolean.class)) {
            return Bytes.toBoolean(bytes);
        }else if (tClass.equals(short.class)) {
            return Bytes.toShort(bytes);
        }else if (tClass.equals(Short.class)) {
            return Bytes.toShort(bytes);
        }else if (tClass.equals(String.class)) {
            return Bytes.toString(bytes);
        }else if (tClass.equals(BigDecimal.class)) {
            return Bytes.toBigDecimal(bytes);
        }else if (tClass.equals(ByteBuffer.class)) {
            return ByteBuffer.wrap(bytes);
        }else {
            throw new IllegalArgumentException(tClass.toString());
        }
    }

    public static void main(String[] args) {
        int i = 100;
        byte[] bytes = getBytes(i);
        System.out.println(Arrays.toString(bytes));
        Object type = getType(bytes, int.class);
        System.out.println(type);

        Integer i1 = 100;
        byte[] bytes1 = getBytes(i1);
        System.out.println(Arrays.toString(bytes1));
        Object type1 = getType(bytes1, Integer.class);
        System.out.println(type1);

        Boolean b = false;
        byte[] bytes2 = getBytes(b);
        System.out.println(Arrays.toString(bytes2));
        Object type2 = getType(bytes2, Boolean.class);
        System.out.println(type2);

        String s = "wfwef";
        byte[] bytes3 = getBytes(s);
        System.out.println(Arrays.toString(bytes3));
        Object type3 = getType(bytes3, String.class);
        System.out.println(type3);
    }

}
