package com.buang.welewolf.base.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImageCompressor {
	private static final int MAX_SIZE = 100;
	private static final int MAX_WIDTH = 640;
	private static final int MAX_HEIGHT = 960;

	public static byte[] compressImage(String path, int[] bitmapSize) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds设为true
		newOpts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		int inSampleSize = 1;
		if (w > MAX_WIDTH || h > MAX_HEIGHT) {
			final int heightRatio = Math.round((float) h / (float) MAX_HEIGHT);
			final int widthRatio = Math.round((float) w / (float) MAX_WIDTH);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		newOpts.inSampleSize = inSampleSize;// 设置缩放比例
		newOpts.inPreferredConfig = Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts);
		bitmapSize[0] = newOpts.outWidth;
		bitmapSize[1] = newOpts.outHeight;
		try {
			return compressImage2(ImageUtil.setOrientation(bitmap, path));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] compressImage2(Bitmap bitmap)
			throws Exception {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int options = 90;
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
			int size = baos.toByteArray().length / 1024;
			while (size > MAX_SIZE && options > 0) {
				baos.reset();// 重置baos即清空baos
				options -= 10;// 每次都减少10
				// 这里压缩options%，把压缩后的数据存放到baos中
				bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
				size = baos.toByteArray().length / 1024;
			}
			bitmap.recycle();
			return baos.toByteArray();
		} catch (Exception e) {
			throw e;
		}
	}

}
