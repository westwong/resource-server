package com.k2.resourceserver.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author  West
 * @date  create in 2017/12/11
 *
 **/
public interface FileService {

    /**
     *  保存图片
     * @param path 保存路径
     * @param file 文件
     * @return 文件名
     */
    String saveImage(String path, MultipartFile file);
    /**
     *  保存视频
     * @param paths 保存路径 第一个为video,第二个为图片
     * @param file 文件
     * @param getImage 是否获取图片
     * @return 文件名
     */
    String[] saveVideo(String[] paths, MultipartFile file ,boolean getImage);


    /**
     *  下载文件
     * @param response 推流
     * @param request
     * @param filePath
     */
    void downloadFile(HttpServletResponse response, HttpServletRequest request, String filePath);
    /**
     *  下载文件
     * @param response 推流
     * @param request
     * @param filePath
     */
    void downloadImage(HttpServletResponse response, HttpServletRequest request, String filePath);
    /**
     *  下载文件
     * @param response 推流
     * @param request
     * @param filePath
     */
    void downloadVideo(HttpServletResponse response, HttpServletRequest request, String filePath);
}