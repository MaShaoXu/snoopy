package com.sam.hadoop.service;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HdfsSercice {

    private FileSystem fs;

    public HdfsSercice(Configuration conf) throws IOException {
        fs = FileSystem.get(conf);
    }

    public List<FileStatus> listStatus(String pathString) throws IOException {
        FileStatus[] fileStatuses = fs.listStatus(new Path(pathString), path -> {
            if (path.toUri().getPath().startsWith("/hbase")) {
                return false;
            }
            return true;
        });
        return Arrays.asList(fileStatuses);
    }

    public void upload(String srcPath, String dstPath) throws IOException {
        fs.copyFromLocalFile(new Path(srcPath), new Path(dstPath));
    }

    public void download(String srcPath, String dstPath) throws IOException {
        fs.copyToLocalFile(new Path(srcPath), new Path(dstPath));
    }

    public boolean exists(String pathString) throws IOException {
        return fs.exists(new Path(pathString));
    }

    public FileStatus getFileStatus(String pathString) throws IOException {
        return fs.getFileStatus(new Path(pathString));
    }

    public FSDataInputStream open(String pathString) throws IOException {
        return fs.open(new Path(pathString));
    }

    public boolean mkdirs(String path) throws IOException {
        return fs.mkdirs(new Path(path));
    }

    public FSDataOutputStream create(String pathString, boolean overwrite) throws IOException {
        return fs.create(new Path(pathString), overwrite);
    }

    public boolean createNewFile(String pathString) throws IOException {
        return fs.createNewFile(new Path(pathString));
    }

    public boolean delete(String pathString, boolean recursive) throws IOException {
        return fs.delete(new Path(pathString), recursive);
    }

    public boolean deleteOnExit(String pathString) throws IOException {
        return fs.deleteOnExit(new Path(pathString));
    }

    public boolean rename(String srcPath, String dstPath) throws IOException {
        return fs.rename(new Path(srcPath), new Path(dstPath));
    }

    public FSDataOutputStream append(String pathString) throws IOException {
        return fs.append(new Path(pathString));
    }

    public boolean truncate(String pathString, long newLength) throws IOException {
        return fs.truncate(new Path(pathString), newLength);
    }

    public void concat(String trg, String[] psrcs) throws IOException {
        List<Path> paths = Arrays.stream(psrcs).map(Path::new).collect(Collectors.toList());
        fs.concat(new Path(trg), paths.toArray(new Path[]{}));
    }

}
