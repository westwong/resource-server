package com.k2.resourceserver.util;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * @author West
 * @date create in 2019/12/11
 */
public class VideoUtil {
    /**
     * 从视频中获取缩略图
     *
     * @param path  图片存放地址
     * @param video 视频文件
     */
    public static File getImageFromVideo(String path, File video) {

        File targetFile = new File(path + ".jpg");
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        try {
            if (video.exists()) {
                FFmpegFrameGrabber ff = new FFmpegFrameGrabber(video);
                ff.start();
                int ftp = ff.getLengthInFrames();
                int flag = 0;
                Frame frame = null;
                while (flag <= ftp) {
                    //获取帧
                    frame = ff.grabImage();
                    //过滤前3帧，避免出现全黑图片
                    if ((flag > 3) && (frame != null)) {
                        break;
                    }
                    flag++;
                }
                ImageIO.write(FrameToBufferedImage(frame), "jpg", targetFile);
                ff.close();
                ff.stop();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("获取缩略图失败");
        }
        return targetFile;
    }

    private static RenderedImage FrameToBufferedImage(Frame frame) {
        //创建BufferedImage对象
        Java2DFrameConverter converter = new Java2DFrameConverter();
        BufferedImage bufferedImage = converter.getBufferedImage(frame);
        return bufferedImage;
    }
}
