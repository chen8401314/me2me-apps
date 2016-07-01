package com.me2me.core;

import com.google.common.collect.Maps;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/1
 * Time :14:46
 */
public class QRCodeUtil {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    /**
     *  最终调用该方法生成二维码图片
     * @param url 要生成二维码的url
     * @param imgPath 二维码生成的绝对路径
     * @param logoPath 二维码中间logo绝对地址
     * @throws Exception
     */
    public static void get2CodeImage(String url,String imgPath,String logoPath) throws Exception{
        String format = "png";
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, 300, 300, hints);
        File qrcodeFile = new File(imgPath);
        QRCodeUtil.writeToFile(bitMatrix, format, qrcodeFile, logoPath);
    }

    /**
     *
     * @param matrix 二维码矩阵相关
     * @param format 二维码图片格式
     * @param file 二维码图片文件
     * @param logoPath logo路径
     * @throws IOException
     */
    public static void writeToFile(BitMatrix matrix,String format,File file,String logoPath) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        Graphics2D gs = image.createGraphics();

        //载入logo
        Image img = ImageIO.read(new File(logoPath));
        gs.drawImage(img, 125, 125, null);
        gs.dispose();
        img.flush();
        if(!ImageIO.write(image, format, file)){
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix){
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }


    public static void main(String[] args) {
        try {
            get2CodeImage("www.baidu.com","d:\\test\\logotest\\logocode.jpg","d:\\test\\logotest\\logo.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

