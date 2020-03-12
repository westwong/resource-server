package com.k2.resourceserver.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import lombok.extern.slf4j.Slf4j;

/**
 * @author West
 * @date create in 2019/9/4
 */
@Slf4j
@Component
public class PathUtil {

    private static File ROOT_PATH;

    private void initFilePath() {

        if (!ROOT_PATH.exists()) {
            try {
                Files.createDirectories(ROOT_PATH.getCanonicalFile().toPath());
            } catch (IOException e) {
                log.error("初始化上传文件临时保存路径失败");
                e.printStackTrace();
            }
        }
    }

    @Value("${upload.file.path.win}")
    public void setRootPathWin(String path) {

        if (ROOT_PATH != null || !System.getProperty("os.name").toLowerCase().contains("win")) {
            return;
        }
        log.info("当前为Windows环境, 初始化上传文件路径到{}", path);
        ROOT_PATH = new File(path);
        initFilePath();
    }

    @Value("${upload.file.path.linux}")
    public void setRootPathLinux(String path) {
        if (ROOT_PATH != null || !System.getProperty("os.name").toLowerCase().contains("linux")) {
            return;
        }
        log.info("当前为Linux环境, 初始化上传文件路径到{}", path);
        ROOT_PATH = new File(path);
        initFilePath();
    }

    public static File getDirectory() {
        return ROOT_PATH;
    }

    public static String getPath() {
        return ROOT_PATH.getAbsolutePath();
    }
}
