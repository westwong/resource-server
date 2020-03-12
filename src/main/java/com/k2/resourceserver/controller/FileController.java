package com.k2.resourceserver.controller;

import com.k2.resourceserver.service.FileService;
import com.k2.resourceserver.util.FileUtil;
import com.k2.resourceserver.util.PathUtil;
import com.k2.resourceserver.util.RespBuilder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件控制器
 *
 * @author west
 * @since 日期
 */
@RestController
@RequestMapping("/static")
public class FileController {
    @Autowired
    private FileService fileService;


    public static final String IMAGE_DIR = "/image/";
    public static final String VIDEO_DIR = "/video/";

    private static final String SIGN = "32ImGhMspCzKakkF2efA7GW6BhrvAAbWest";

    @PostMapping("/image/upload")
    public Map imageUpload(@RequestParam(value = "file", required = false) MultipartFile file
            , String sign) throws IOException {
        if (!SIGN.equals(sign)) {
            return RespBuilder.errorJsonStr("没有权限");
        }
        String md5 = FileUtil.getMD5(file.getInputStream());
        //存在项目下方便直接访问
        //备份文件目录
        String savePath = PathUtil.getPath() + IMAGE_DIR + md5;

        try {
            String fileName = fileService.saveImage(savePath, file);
            //备份文件
            return RespBuilder.kv2Json(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return RespBuilder.errorJsonStr(e);
        }
    }
    @PostMapping("/video/upload")
    public Map videoUpload(@RequestParam(value = "file", required = false) MultipartFile file
            , String sign , boolean getImage) throws IOException {
        if (!SIGN.equals(sign)) {
            return RespBuilder.errorJsonStr("没有权限");
        }
        String md5 = FileUtil.getMD5(file.getInputStream());
        //存在项目下方便直接访问
        //备份文件目录
        String videoPath = PathUtil.getPath() + VIDEO_DIR + md5;
        String imagePath = PathUtil.getPath() + IMAGE_DIR + md5;

        try {
            String[] fileName = fileService.saveVideo(new String[]{videoPath,imagePath}, file, getImage);
            //备份文件
            return RespBuilder.kv2Json("videoName",fileName[0],"imageName",fileName[1]);
        } catch (Exception e) {
            e.printStackTrace();
            return RespBuilder.errorJsonStr(e);
        }
    }

    @GetMapping("/image/download")
    public void downloadImage(HttpServletResponse response, HttpServletRequest request) {
        String fileName = request.getParameter("fileName");
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名为空");
        }
        try {
            fileService.downloadImage(response, request, PathUtil.getPath() + IMAGE_DIR + fileName);
        } catch (Exception e) {
            throw new IllegalArgumentException("该文件不存在");
        }
    }
    @GetMapping("/image/download/{fileName:.+}")
    public void downloadImage(@PathVariable String fileName, HttpServletResponse response, HttpServletRequest request) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名为空");
        }
        try {
            fileService.downloadImage(response, request, PathUtil.getPath() + IMAGE_DIR + fileName);
        } catch (Exception e) {
            throw new IllegalArgumentException("该文件不存在");
        }
    }

    @GetMapping("/video/download")
    public void downloadVideo(HttpServletResponse response, HttpServletRequest request) {
        String fileName = request.getParameter("fileName");
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名为空");
        }
        try {
            fileService.downloadVideo(response, request, PathUtil.getPath() + VIDEO_DIR + fileName);
        } catch (Exception e) {
            throw new IllegalArgumentException("该文件不存在");
        }
    }



}
	