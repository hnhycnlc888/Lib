package yzkf.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
public class ImageTool {

	/**
	 * 生成JPEG图片缩略图
	 * @param filename 原图文件路径
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param outFilename	缩略图的保存路径
	 * @throws IOException
	 */
	public static void createJPEGThumbnail(String filename, int maxWidth, int maxHeight,
				 String outFilename) throws IOException{
        createJPEGThumbnail(filename, maxWidth, maxHeight, 
        		true, outFilename);        
	}
	/**
	 * 生成JPEG图片缩略图
	 * @param filename 原图文件路径
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param keepRatio 是否保持比例缩放，默认：true
	 * @param outFilename	缩略图的保存路径
	 * @throws IOException
	 */
	public static void createJPEGThumbnail(String filename, int maxWidth, int maxHeight,
				boolean keepRatio, String outFilename) throws IOException{
        createJPEGThumbnail(filename, maxWidth, maxHeight, 
        		keepRatio, 100, outFilename);        
	}
	/**
	 * 生成JPEG图片缩略图
	 * @param filename 原图文件路径
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param keepRatio 是否保持比例缩放，默认：true
	 * @param quality	缩略图的质量，1-100的整数，默认： 100
	 * @param outFilename	缩略图的保存路径
	 * @throws IOException
	 */
	public static void createJPEGThumbnail(String filename, int maxWidth, int maxHeight,
				boolean keepRatio, int quality, String outFilename) throws IOException{
        createJPEGThumbnail(new File(filename), maxWidth, maxHeight, 
        		keepRatio, quality, outFilename);        
	}
	/**
	 * 生成JPEG图片缩略图
	 * @param file 原图文件
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param keepRatio 是否保持比例缩放，默认：true
	 * @param quality	缩略图的质量，1-100的整数，默认： 100
	 * @param outFilename	缩略图的保存路径
	 * @throws IOException
	 */
	public static void createJPEGThumbnail(File file, int maxWidth, int maxHeight,
			boolean keepRatio, int quality, String outFilename) throws IOException{
		 BufferedImage image = ImageIO.read(file);
        //如果按比例缩放，则根据原图比例计算缩略图的的长宽
        if(keepRatio){	        
	        int imageWidth = image.getWidth(null); 
	        int imageHeight = image.getHeight(null); 
	        double thumbRatio = (double)maxWidth / (double)maxHeight; 
	        double imageRatio = (double)imageWidth / (double)imageHeight; 
	        if (thumbRatio < imageRatio) { 
	            maxHeight = (int)(maxWidth / imageRatio); 
	        } else { 
	            maxWidth = (int)(maxHeight * imageRatio); 
	        } 
        }
   
        //从原图中绘制缩略图
        BufferedImage thumbImage = new BufferedImage(maxWidth, maxHeight, 
		BufferedImage.TYPE_INT_RGB); 
        Graphics2D graphics2D = thumbImage.createGraphics(); 
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
		RenderingHints.VALUE_INTERPOLATION_BILINEAR); 
        graphics2D.drawImage(image, 0, 0, maxWidth, maxHeight, null); 
        graphics2D.dispose();
        
        // 保存缩略图
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFilename)); 
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out); 
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage); 
        quality = Math.max(0, Math.min(quality, 100)); 
        param.setQuality((float)quality / 100.0f, false); 
        encoder.setJPEGEncodeParam(param); 
        encoder.encode(thumbImage); 
        out.close(); 
	}
	/**
	 * 生成缩略图(支持jpeg和png)
	 * @param filename 原图文件路径
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param outFilename 缩略图的保存路径
	 * @throws IOException
	 */
	public static void createThumbnail(String filename, int maxWidth, int maxHeight,
			 String outFilename) throws IOException {
		createThumbnail(filename, maxWidth, maxHeight, true, outFilename);
	}
	/**
	 * 生成缩略图(支持jpeg和png)
	 * @param filename 原图文件路径
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param keepRatio 是否保持比例缩放，默认：true
	 * @param outFilename 缩略图的保存路径
	 * @throws IOException
	 */
	public static void createThumbnail(String filename, int maxWidth, int maxHeight,
			 boolean keepRatio,String outFilename) throws IOException {
		createThumbnail(new File(filename), maxWidth, maxHeight, keepRatio, outFilename);
	}
	 /**
	 * 生成缩略图(支持jpeg和png)
	 * @param file 原图文件
	 * @param maxWidth 最大宽度
	 * @param maxHeight 最大高度
	 * @param keepRatio 是否保持比例缩放，默认：true
	 * @param outFilename 缩略图的保存路径
	 * @throws IOException
	 */
	 public static void createThumbnail(File file, int maxWidth, int maxHeight,
			 boolean keepRatio,String outFilename) throws IOException {
		BufferedImage image = ImageIO.read(file);
		String imgType = "JPEG";
		if (file.getName().toLowerCase().endsWith(".png")) {
			imgType = "PNG";
		}
		int type = image.getType();
		
		if(keepRatio){
			int imageWidth = image.getWidth(null); 
	        int imageHeight = image.getHeight(null); 
			double thumbRatio = (double)maxWidth / (double)maxHeight;         
	        double imageRatio = (double)imageWidth / (double)imageHeight; 
	        if (thumbRatio < imageRatio) { 
	            maxHeight = (int)(maxWidth / imageRatio); 
	        } else { 
	            maxWidth = (int)(maxHeight * imageRatio); 
	        }
		}
        
        BufferedImage thumbImage = null;
		 if (type == BufferedImage.TYPE_CUSTOM) { //handmade
			 ColorModel cm = image.getColorModel();
			 WritableRaster raster = cm.createCompatibleWritableRaster(maxWidth, maxHeight);
			 boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			 thumbImage = new BufferedImage(cm, raster, alphaPremultiplied, null);
		 } else{
			 thumbImage = new BufferedImage(maxWidth, maxHeight, type);
		 }
		 Graphics2D graphics2D = thumbImage.createGraphics();
		 //smoother than exlax:
		 graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		 graphics2D.drawImage(image, 0, 0, maxWidth, maxHeight, null); 
		 graphics2D.dispose();
		 
		 ImageIO.write(thumbImage, imgType, new File(outFilename));
	 }

}
