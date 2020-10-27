package com.sam.common;

import java.io.*;
import java.nio.charset.Charset;

public class ProcessUtils {

    private static final Charset charset = Charset.forName("GBK");

    public static void process(String path, String[] command) throws Exception{
        process(path, charset, command);
    }

    public static void process(String path, String charset, String[] command) throws Exception {
        process(path, Charset.forName(charset), command);
    }

    public static void process(String path, Charset charset, String[] command) throws Exception {
        Process p = new ProcessBuilder(command)
                .directory(new File(path))
                .start();
        InputStream ins = p.getInputStream();
        InputStream ers = p.getErrorStream();
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(ins, charset));
        BufferedReader errReader = new BufferedReader(new InputStreamReader(ers, charset));
        String line;
        while ((line = inputReader.readLine()) != null) {
            System.out.println(line);
        }
        while ((line = errReader.readLine()) != null) {
            System.err.println(line);
        }
        p.waitFor();
    }

    public static void main(String[] args) {
        try {
            String property = System.getProperty("file.encoding");
            System.out.println(property);
            System.out.println(Charset.defaultCharset());
            process("E:\\xiangmu", new String[]{"cmd", "/c", "echo 你好"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
