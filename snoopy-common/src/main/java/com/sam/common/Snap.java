package com.sam.common;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Snap {

    private static Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * 对屏幕进行拍照
     **/
    public static void snapshot(String filename, String format) {
        try {
            Robot robot = new Robot();
            //拷贝屏幕到一个BufferedImage对象screenshot
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(0, 0, (int) d.getWidth(), (int) d.getHeight()));
            ImageIO.write(screenshot, format, new File(filename + "." + format));
            System.out.println("..Finished");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        snapshot("C:\\Users\\Sam\\Desktop\\日志\\java\\bootstrap栅格", "png");
    }

}
