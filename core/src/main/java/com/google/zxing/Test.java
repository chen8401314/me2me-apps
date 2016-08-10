package com.google.zxing;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/6/24
 * Time :12:59
 */
public class Test {


    public static void main(String[] args) {
        try {

            String content = "http://www.51nick.com";
            String path = "D:/";
            String filePath = "D://";
            String fileName = "zxing.png";

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            String format = "png";// 图像类型
            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 400, 400,hints);
            Path path1 = FileSystems.getDefault().getPath(filePath, fileName);
            Path p = FileSystems.getFileSystem(URI.create("http://cdn.me-to-me.com")).getPath("FpXdLCD5Nhos0NbWPaLHcegzAiMe");
            p = Paths.get("http://cdn.me-to-me.com/FpXdLCD5Nhos0NbWPaLHcegzAiMe");
            MatrixToImageWriter.writeToPath(bitMatrix, format, p);
            System.out.println("输出成功.");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
