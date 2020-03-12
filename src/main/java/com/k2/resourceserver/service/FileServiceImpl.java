package com.k2.resourceserver.service;

import com.k2.resourceserver.util.FileUtil;
import com.k2.resourceserver.util.VideoUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author West
 * @date create in 2017/12/11
 **/
@Service("fileService")
public class FileServiceImpl implements FileService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final long MAX_IMAGE_SIZE = 1024L * 1024 * 5;
    private static final long MAX_VIDEO_SIZE = 1024L * 1024 * 300;

    @Override
    public String saveImage(String path, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("请上传小于5M的文件！");
        }
        String _fileName = file.getOriginalFilename();
        String extensionName = FileUtil.getExtensionName(_fileName);
        if (null == extensionName) {
            throw new IllegalArgumentException("图片错误");
        }
        //判断文件类型
        String allowTYpe = ".jpg,.png";
        if (allowTYpe.indexOf(extensionName.toLowerCase()) < 0) {
            throw new IllegalArgumentException("对不起,只能上传jpg,png格式的图片！");
        }

        File newFile = new File(path + extensionName);
        //创建文件路径
        if (newFile.getParent() != null && !new File(newFile.getParent()).exists()) {
            new File(newFile.getParent()).mkdirs();
        }
        logger.debug(newFile.toString());
        try {
            if (!newFile.exists()) {
                file.transferTo(newFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("文件上传失败");
        }
        logger.info("文件{}上传成功。", file.getOriginalFilename());
        return newFile.getName();
    }

    @Override
    public String[] saveVideo(String[] paths, MultipartFile file, boolean getImage) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空");
        }
        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new IllegalArgumentException("请上传小于300M的文件！");
        }
        String _fileName = file.getOriginalFilename();
        String extensionName = FileUtil.getExtensionName(_fileName);
        if (null == extensionName) {
            throw new IllegalArgumentException("文件错误");
        }
        //判断文件类型
        String allowTYpe = ".mp4";
        if (allowTYpe.indexOf(extensionName.toLowerCase()) < 0) {
            throw new IllegalArgumentException("对不起,只能上传mp4格式的图片！");
        }

        File newFile = new File(paths[0] + extensionName);
        //创建文件路径
        if (newFile.getParent() != null && !new File(newFile.getParent()).exists()) {
            new File(newFile.getParent()).mkdirs();
        }
        logger.debug(newFile.toString());
        String imageName = "";
        try {
            if (!newFile.exists()) {
                file.transferTo(newFile);
            }
            if (getImage) {
                imageName = VideoUtil.getImageFromVideo(paths[1], newFile).getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("文件上传失败");
        }
        logger.info("文件{}上传成功。", file.getOriginalFilename());
        return new String[]{newFile.getName(), imageName};
    }

    /**
     * 附件下载
     */
    @Override
    public void downloadFile(HttpServletResponse response, HttpServletRequest request, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("文件丢失。");
        }
        try {
            FileUtil.downloadImage(file, response, request);
            logger.info(request.getRemoteAddr() + "下载了文件{}", filePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("文件下载失败");
        }
    }

    @Override
    public void downloadImage(HttpServletResponse response, HttpServletRequest request, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("图片丢失。");
        }
        try {
            FileUtil.downloadImage(file, response, request);
            logger.info(request.getRemoteAddr() + "下载了图片{}", filePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("图片下载失败");
        }
    }

    @Override
    public void downloadVideo(HttpServletResponse response, HttpServletRequest request, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IllegalArgumentException("视频丢失。");
        }
        try {
            FileUtil.downloadVideo(file, response, request);
            logger.info(request.getRemoteAddr() + "下载了视频{}", filePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("视频下载失败");
        }
    }
}