/**
 *
 */
package com.globalegrow.dy.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ***********************************************************
 *
 * @类名 ：FileUtil.java
 * @DESCRIPTION :
 * @AUTHOR : wangzhongfu
 * @DATE : 2016/10/1
 * ***********************************************************
 */
public class FileUtil {

    protected static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取文件夹下所有的文件
     *
     * @param obj
     * @return
     */
    public static List<File> getListFiles(Object obj) {
        File directory = null;
        if (obj instanceof File) {
            directory = (File) obj;
        } else {
            directory = new File(obj.toString());
        }
        List<File> files = new ArrayList<File>();
        if (directory.isFile()) {
            files.add(directory);
            return files;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                files.addAll(getListFiles(fileOne));
            }
        }
        return files;
    }

    /**
     * 获取文件编码格式
     *
     * @param filePpath
     * @return
     * @throws IOException
     */
    public static String getFileEncod(String filePpath) throws IOException {
        try(BufferedInputStream bin = new BufferedInputStream(new FileInputStream(filePpath))){
            int p = (bin.read() << 8) + bin.read();
            String code;
            switch (p) {
                case 0xefbb:
                    code = "UTF-8";
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                default:
                    code = "GBK";
            }
            return code;
        }
    }

    /**
     * 指定编码格式读取文件
     *
     * @param filePath:文件绝对路径
     * @param encoding:文件编码
     * @author wang zhong fu
     */
    public static String readFileContent(String filePath, String encoding) throws IOException {
        File wordFile = new File(filePath);
        Long filelength = wordFile.length();
        byte[] filecontent = new byte[filelength.intValue()];
        try (FileInputStream in = new FileInputStream(wordFile)) {
            in.read(filecontent);
            String wordsContent = new String(filecontent, encoding);
            return wordsContent;
        }
    }


    public static String getCurrentTimePath() {
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy/MM/dd");
        return simpleFormatter.format(new Date());
    }

    /**
     * 保存文件并返回相对路径
     * @param file
     * @param basePath
     * @return
     */
    public static Map<String,String> saveFileToDisk(MultipartFile file, String basePath) throws IOException {
        Map<String, String> map = new HashMap<>();
        String fileName = getRandomFileName(file.getOriginalFilename());
        String relationPath = getCurrentTimePath() ;
        String absolutePath = absolutePath(basePath, relationPath);
        log.info("upload file path [{}]",absolutePath);
        String absoluteFile = absolutePath + "/" + fileName;

        saveToDisk(file.getInputStream(), absoluteFile);

        map.put("imgUrl", "/"+relationPath + "/" + fileName);
        map.put("fileName", fileName);
        return map;
    }

    /**
     * 从指定 url 下载图片到本地
     * @param url
     * @param basePath
     * @return
     */
    public static Map<String, String> downloadImageToDask(String url, String basePath) throws IOException {
        Map<String, String> map = new HashMap<>();
        String fileName = getRandomFileName(url);
        String relationPath = getCurrentTimePath() ;
        String absolutePath = absolutePath(basePath, relationPath);

        String absoluteFile = absolutePath + "/" + fileName;

        HttpClient httpclient = HttpClients.custom().build();
        //DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet(url);

        httpget
                .setHeader(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
        httpget
                .setHeader("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        HttpResponse resp = httpclient.execute(httpget);
        if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
            HttpEntity entity = resp.getEntity();

            InputStream in = entity.getContent();

            saveToDisk(in,absoluteFile);

        }
        map.put("imgUrl","/"+relationPath + "/" + fileName);
        map.put("fileName", fileName);
        return map;
    }



    private static String getRandomFileName(String input) {
        return UUID.randomUUID().toString().replaceAll("-","") + input.substring(input.lastIndexOf("."));
    }

    /**
     * 将文件流保存到磁盘
     * @param inputStream
     * @param file
     * @throws IOException
     */
    public static void saveToDisk(InputStream inputStream, String file) throws IOException {
        try (BufferedOutputStream stream = new BufferedOutputStream(
                new FileOutputStream(new File(file)))) {
            FileCopyUtils.copy(inputStream, stream);
        }
    }

    /**
     * 获取绝对文件路径，如果文件夹不存在，则创建文件夹
     * @param basePath
     * @param relationPath
     * @return
     */
    private static String absolutePath(String basePath, String relationPath) {
        String absolutePath = basePath + "/" + relationPath;
        if(!new File(absolutePath).exists()){
            new File(absolutePath).mkdirs();
        }
        return absolutePath;
    }

}
