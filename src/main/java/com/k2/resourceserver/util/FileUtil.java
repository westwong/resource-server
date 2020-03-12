package com.k2.resourceserver.util;

import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 扩展FileUtil
 *
 * @author west
 * @since 2017年4月25日
 */

public class FileUtil {

    private static final String JPG = ".jpg";
    private static final String PNG = ".png";
    private static final String MP4 = ".mp4";
    private static final long DEFAULT_MAX_AGE = 3600 * 24 * 30;

    /**
     * 获取扩展名
     *
     * @param fileName 文件名称
     */
    public static String getExtensionName(String fileName) {
        if (null == fileName) {
            return null;
        }
        if (!fileName.contains(".")) {
            return null;
        }
        // 获取图片的扩展名
        return  fileName
                .substring(fileName.lastIndexOf("."));
    }

    /**
     * 输出文件
     */
    public static void outputFile(File file, HttpServletResponse response) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(response.getOutputStream());
            while ((bytesRead = bis.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
        } finally {
            if (bis != null){
                bis.close();
            }
            if (bos != null){
                bos.close();
            }
        }
    }

    /**
     *
     */
    public static void downloadFile(File file, HttpServletResponse response, HttpServletRequest request) throws IOException {
        String contentType = response.getContentType();
        if (contentType == null || contentType.trim().equals("")) {
            response.setContentType("APPLICATION/OCTET-STREAM; charset=UTF-8");
        }
        String fileName = file.getName();
        // ie浏览器
        if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
            fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.name());
            // 非ie浏览器
        } else {
            fileName = new String(fileName.getBytes(StandardCharsets.UTF_8.name()), "ISO8859-1");
        }
        response.setContentLengthLong(file.length());
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.setHeader("cache-control", "max-age=" + DEFAULT_MAX_AGE);
        response.setHeader("etag", getMD5(file));
        // 断点下载？
        String range = request.getHeader("Range");
        if (range == null) {
            outputFile(file, response);
            // 断点下载
        } else {
            long[] pos = getPosition(range);
            breakpointOutputFile(file, pos, response);
        }
    }

    /**
     * 获取 起始位置
     *
     * @param rangeBytes bytes=40-100
     * @return 如果出错默认为0
     */
    private static long[] getPosition(String rangeBytes) {
        long start = 0L;
        long end = 0L;
        try {
            if (rangeBytes.endsWith("-")) {
                // bytes=270000-
                start = Long.parseLong(rangeBytes.substring(0, rangeBytes.indexOf("-")));
            } else {
                // bytes=270000-320000
                String temp1 = rangeBytes.substring(0, rangeBytes.indexOf("-"));
                String temp2 = rangeBytes.substring(rangeBytes.indexOf("-") + 1);
                start = Long.parseLong(temp1);
                end = Long.parseLong(temp2);
            }
        } catch (NumberFormatException e) {

        }

        return new long[]{start, end};
    }

    private static void breakpointOutputFile(File file, long[] positions, HttpServletResponse response) throws IOException {
        long fileSize = file.length();
        long start = positions[0];
        long end = positions[1] == 0 ? fileSize - 1 : positions[1];

        response.setHeader("Content-Range", new StringBuffer("bytes ").append(start + "").append("-")
                .append((end) + "").append("/").append(fileSize + "").toString());
        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        //读取长度
        long contentLength = end - start + 1;
        response.setContentLengthLong(contentLength);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        int bytesRead;
        int byteSize = 8192;
        byte[] buffer = new byte[byteSize];
        long readLength = 0L;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.skip(start);
            bos = new BufferedOutputStream(response.getOutputStream());
            while (readLength <= contentLength - byteSize) {
                bytesRead = bis.read(buffer);
                readLength += bytesRead;
                bos.write(buffer, 0, bytesRead);
            }
            if (readLength <= contentLength){
                bytesRead = bis.read(buffer,0, (int) (contentLength - readLength));
                bos.write(buffer,0,bytesRead);
            }
            bos.flush();
        } finally {
            if (bis != null){
                bis.close();
            }
            if (bos != null){
                bos.close();
            }
        }
    }

    /**
     *
     */
    public static void downloadImage(File file, HttpServletResponse response, HttpServletRequest request) throws IOException {

        String fileName = file.getName();
        String extensionName = getExtensionName(fileName);
        if (JPG.equalsIgnoreCase(extensionName)) {
            response.setContentType("image/jpeg");
        } else if (PNG.equalsIgnoreCase(extensionName)) {
            response.setContentType("image/png");
        }

        downloadFile(file, response, request);
    }

    public static void downloadVideo(File file, HttpServletResponse response, HttpServletRequest request) throws IOException {

        String fileName = file.getName();
        String extensionName = getExtensionName(fileName);
        if (MP4.equalsIgnoreCase(extensionName)) {
            response.setContentType("video/mp4");
        }
        response.setHeader("Accept-Ranges", "bytes");
        downloadFile(file, response, request);
    }


    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static String getMD5(InputStream inputStream) {
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
