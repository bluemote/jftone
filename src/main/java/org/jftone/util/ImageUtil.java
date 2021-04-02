package org.jftone.util;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class ImageUtil {

	/**
	 * 根据尺寸图片居中裁剪
	 * @param src
	 * @param dest
	 * @param w
	 * @param h
	 * @throws IOException
	 */
	public static void cutCenterImage(String src, String dest, int w, int h)
			throws IOException {
		String ext = src.substring(src.lastIndexOf(".") + 1);
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(ext);
		ImageReader reader = (ImageReader) iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		Rectangle rect = new Rectangle((reader.getWidth(imageIndex) - w) / 2,
				(reader.getHeight(imageIndex) - h) / 2, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, ext, new File(dest));

	}

	/**
	 * 图片裁剪二分之一
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void cutHalfImage(String src, String dest) throws IOException {
		String ext = src.substring(src.lastIndexOf(".") + 1);
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(ext);
		ImageReader reader = iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		int imageIndex = 0;
		int width = reader.getWidth(imageIndex) / 2;
		int height = reader.getHeight(imageIndex) / 2;
		Rectangle rect = new Rectangle(width / 2, height / 2, width, height);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, ext, new File(dest));
	}

	/**
	 * 根据指定坐标及宽高裁剪图片
	 * @param src
	 * @param dest
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @throws IOException
	 */
	public static void cutImage(String src, String dest, int x, int y, int w,
			int h) throws IOException {
		String ext = src.substring(src.lastIndexOf(".") + 1);
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(ext);
		ImageReader reader = iterator.next();
		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		ImageIO.write(bi, ext, new File(dest));

	}

	/**
	 * 根据指定宽高缩放图片
	 * @param src
	 * @param dest
	 * @param w
	 * @param h
	 * @throws IOException 
	 * @throws Exception
	 */
	public static void zoomImage(String src, String dest, int w, int h) throws IOException  {
		double wr = 0, hr = 0;
		File srcFile = new File(src);
		File destFile = new File(dest);
		BufferedImage bufImg = ImageIO.read(srcFile);
		Image Itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		wr = w * 1.0 / bufImg.getWidth();
		hr = h * 1.0 / bufImg.getHeight();
		AffineTransformOp ato = new AffineTransformOp(
				AffineTransform.getScaleInstance(wr, hr), null);
		Itemp = ato.filter(bufImg, null);
		ImageIO.write((BufferedImage) Itemp, dest.substring(dest.lastIndexOf(".") + 1), destFile);
	}

	/**
	 * 根据宽度缩放图片
	 * @param src
	 * @param dest
	 * @param w
	 * @throws Exception
	 */
	public static void zoomImageByWidth(String src, String dest, int w)
			throws Exception {
		File srcFile = new File(src);
		BufferedImage bufImg = ImageIO.read(srcFile);
		zoomImage(src, dest, w, (w * bufImg.getHeight()) /bufImg.getWidth());
	}
	
	/**
	 * 根据高度缩放图片
	 * @param src
	 * @param dest
	 * @param h
	 * @throws Exception
	 */
	public static void zoomImageByHeight(String src, String dest, int h)
			throws Exception {
		File srcFile = new File(src);
		BufferedImage bufImg = ImageIO.read(srcFile);
		zoomImage(src, dest, (h * bufImg.getWidth())/bufImg.getHeight(), h);
	}
}
