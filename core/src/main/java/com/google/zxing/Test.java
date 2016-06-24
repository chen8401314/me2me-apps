package com.google.zxing;

import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.nio.file.Path;
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

            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 400, 400,hints);
            File file1 = new File(path,"me2me.jpg");
            Path path1 = null;
            MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file1);
            MatrixToImageWriter.writeToPath(bitMatrix,content,path1,new MatrixToImageConfig());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
