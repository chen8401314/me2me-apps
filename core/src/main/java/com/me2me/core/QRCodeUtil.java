package com.me2me.core;


import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

/**
 * 上海拙心网络科技有限公司出品
 * Author: 代宝磊
 * Date: 2016/7/1
 * Time :14:46
 */
public class QRCodeUtil {

    private static final int height = 400;

    private static final int width = 400;

    private static final String content = "http://www.51nick.com";

    /**
     * 设置二维码的格式参数
     *
     * @return
     */
    public static Map<EncodeHintType, Object> getDecodeHintType() {
        // 用于设置QR二维码参数
        Map<EncodeHintType, Object> hints = Maps.newConcurrentMap();
        // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
        return hints;
    }
    /**
     * 给二维码图片添加Logo
     * @param qrPic
     * @param logoStream
     */
    public static byte[] addLogo_QRCode(BufferedImage qrPic, byte[] logoStream, LogoConfig logoConfig) {
        try{
            //读取二维码图片，并构建绘图对象
            BufferedImage image = qrPic;
            Graphics2D g = image.createGraphics();
            //读取Logo图片
            InputStream in = new ByteArrayInputStream(logoStream);
            BufferedImage logo = ImageIO.read(in);
            //设置logo的大小,本人设置为二维码图片的20%,因为过大会盖掉二维码
            int widthLogo = logo.getWidth(null) > image.getWidth() * 2 / 10 ? (image.getWidth() * 2 / 10) : logo.getWidth(null), heightLogo = logo.getHeight(null) > image.getHeight() * 2 / 10 ? (image.getHeight() * 2 / 10) : logo.getWidth(null);
             //logo放在中心
            int x = (image.getWidth() - widthLogo) / 2;
            int y = (image.getHeight() - heightLogo) / 2;
            //开始绘制图片
            //InputStream waterin = new ByteArrayInputStream("MeToMe米汤".getBytes());
            //BufferedImage water = ImageIO.read(waterin);
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
            g.setStroke(new BasicStroke(logoConfig.getBorder()));
            g.setColor(logoConfig.getBorderColor());
            g.drawRect(x, y, widthLogo, heightLogo);
            g.dispose();
            logo.flush();
            image.flush();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image,"png",os);
            return os.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            BufferedImage image = qrPic;
            Graphics2D g = image.createGraphics();

            g.setStroke(new BasicStroke(logoConfig.getBorder()));
            g.setColor(logoConfig.getBorderColor());
            g.dispose();
            image.flush();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                ImageIO.write(image,"png",os);
                return os.toByteArray();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        }
    }

    public static BufferedImage getQR_CODEBufferedImage(String content, BarcodeFormat barcodeFormat, int width, int height, Map<EncodeHintType, Object> hints) {
        MultiFormatWriter multiFormatWriter = null;
        BitMatrix bm = null;
        BufferedImage image = null;
        try {
            multiFormatWriter = new MultiFormatWriter();
            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            bm = multiFormatWriter.encode(content, barcodeFormat, width, height, hints);
            int w = bm.getWidth();
            int h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? 0xFFFFFFFF : 0xFF000000);
                }
            }
        }
        catch (WriterException e) {
            e.printStackTrace();
        }
        return image;
    }


    public static BufferedImage getQR_CODEBufferedImage(){
        return getQR_CODEBufferedImage(content, BarcodeFormat.QR_CODE, width, height, getDecodeHintType());
    }

    public static byte[] addLogo_QRCode(BufferedImage qrPic, byte[] logoStream){
        return addLogo_QRCode(qrPic,logoStream, new LogoConfig());
    }


    public static byte[] getQR_CODEByte(){
        BufferedImage bufferedImage = getQR_CODEBufferedImage(content, BarcodeFormat.QR_CODE, width, height, getDecodeHintType());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage,"png",os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return os.toByteArray();
    }


    public static class LogoConfig {
        // logo默认边框宽度
        public static final int DEFAULT_BORDER = 2;
        // logo大小默认为照片的1/5
        public static final int DEFAULT_LOGOPART = 5;
        private final int border = DEFAULT_BORDER;
        private final Color borderColor;
        private final int logoPart;

        public LogoConfig() {
            this( Color.WHITE, 5);
        }

        public LogoConfig(Color borderColor, int logoPart) {
            this.borderColor = borderColor;
            this.logoPart = logoPart;
        }

        public Color getBorderColor() {
            return borderColor;
        }

        public int getBorder() {
            return border;
        }

        public int getLogoPart() {
            return logoPart;
        }
    }
    public static void main(String[] args) throws WriterException{
        BufferedImage image = getQR_CODEBufferedImage(content,BarcodeFormat.QR_CODE, width, height, getDecodeHintType());
        try {
            ImageIO.write(image,"a.png",new FileOutputStream(new File("D://aaaaaa.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

