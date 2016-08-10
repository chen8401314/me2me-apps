package com.me2me.core;


import com.google.common.collect.Maps;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.beans.factory.annotation.Value;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
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


    /**
     * 设置二维码的格式参数
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

    public static byte[] encode(String contents) {
        MultiFormatWriter formatWriter = new MultiFormatWriter();
        try {
        // 按照指定的宽度，高度和附加参数对字符串进行编码
            BitMatrix bitMatrix = formatWriter.encode(contents, BarcodeFormat.QR_CODE, width, height, getDecodeHintType());
            int w = bitMatrix.getWidth();
            int h = bitMatrix.getHeight();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix,"png",os);
            return os.toByteArray();
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return null;
    }
}

