package com.me2me.common.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {

	private static final Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	
	@SuppressWarnings("restriction")
	public static String getImageBase64String(BufferedImage image){
		BufferedOutputStream bos = null;
        if(image != null){
            try {
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	ImageIO.write(image, "png", baos);  //经测试转换的图片是格式这里就什么格式，否则会失真  
                byte[] bytes = baos.toByteArray();  
      
                return  new sun.misc.BASE64Encoder().encodeBuffer(bytes).trim();
            } catch (Exception e) {
            	logger.error("图片转换失败", e);
            }finally{
                if(bos!=null){//关闭输出流
                    try {
                        bos.close();
                    } catch (IOException e) {
                    	logger.error("图片关流失败", e);
                    }
                }
            }
        }
        return "";
	}
}
