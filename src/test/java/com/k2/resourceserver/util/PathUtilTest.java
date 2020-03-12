package com.k2.resourceserver.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

/**
 * @author West
 * @date create in 2019/9/4
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PathUtilTest {

    @Test
    public void test01() {
        String path = PathUtil.getPath();
        File directory = PathUtil.getDirectory();
        System.out.println(path);
        System.out.println(directory + "/sss");
    }
}