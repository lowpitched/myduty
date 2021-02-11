package com.sinya.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Hdfs;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * hdfs client
 */
public class HdfsClientDemo {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        Configuration configuration = new Configuration();
        configuration.set("dfs.replication","1");
        FileSystem fileSystem = FileSystem.get(new URI("hdfs://192.168.1.12:9001"),configuration,"pi");
        //fileSystem.mkdirs(new Path("/project"));
        if(!fileSystem.exists(new Path("/a/b/"))){
            fileSystem.create(new Path("/a/b/"));
            System.out.println("create dir /a/b/");
        }
        if(fileSystem.exists(new Path("/a/b/"))){
            fileSystem.delete(new Path("/a/b/"),true);
            System.out.println("delete dir /a/b/");
        }
        if(fileSystem.exists(new Path("/project"))){
            fileSystem.delete(new Path("/project"),true);
            System.out.println("delete dir /project");
        }
    }

}
