package com.buang.welewolf.base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.hyena.framework.clientlog.LogUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

public class ImageUtil {
	private static final String TAG = "ImageUtilsa";

	private static final int MAX_SIZE = 400;// 最大尺寸400

	/**
	 * Convert drawable to Bitmap
	 *
	 * @param drawable
	 * @return bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null)
			return null;

		Bitmap bitmap = null;
		try {
			bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
		} catch (OutOfMemoryError e) {
			Log.d(TAG, "Exception : " + e.getMessage());
		}
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * Resize bitmap in original scale
	 *
	 * @param bitmap
	 * @param aMaxWidth
	 * @param aMaxHeight
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int aMaxWidth,
			int aMaxHeight) {

		int originWidth = bitmap.getWidth();
		int originHeight = bitmap.getHeight();

		// no need to resize
		if (originWidth < aMaxWidth && originHeight < aMaxHeight) {
			return bitmap;
		}

		int newWidth = originWidth;
		int newHeight = originHeight;

		if (originWidth > aMaxWidth) {
			newWidth = aMaxWidth;

			double i = originWidth * 1.0 / aMaxWidth;
			newHeight = (int) Math.floor(originHeight / i);

			bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight,
					true);
		}

		if (newHeight > aMaxHeight) {
			newHeight = aMaxHeight;

			int startPointY = (int) ((originHeight - aMaxHeight) / 2.0);
			try {
				bitmap = Bitmap.createBitmap(bitmap, 0, startPointY, newWidth,
						newHeight);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return bitmap;
	}


    public static Bitmap rotate(Bitmap bm, final int orientationDegree) {
        Bitmap bm1 = null;
        try {
            bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bm1 == null) {
            return null;
        }
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);


        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);
        return bm1;
    }

	/**
	 * 增加圆角
	 *
	 * @param bitmap
	 * @param width
	 * @param height
	 * @param radius
	 * @return
	 */
	public static Bitmap round(Bitmap bitmap, int width, int height,
			int radius, boolean recycleSource) {
		if (width == 0 || height == 0 || radius <= 0 || bitmap == null)
			return bitmap;

		Bitmap ret = null;
		try {
			ret = Bitmap.createBitmap(width, height, Config.RGB_565);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		if (ret == null)
			return null;

		Canvas canvas = new Canvas(ret);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, width, height);
		RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xff424242);
		canvas.drawRoundRect(rectF, radius, radius, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		if (recycleSource)
			ImageUtil.clear(bitmap);
		return ret;
	}

	/**
	 * 增加圆角
	 *
	 * @param bitmap
	 * @param radius
	 * @return
	 */
	public static Bitmap round(Bitmap bitmap, int radius, boolean recycleSource) {
		if (radius <= 0 || bitmap == null)
			return bitmap;
		return round(bitmap, bitmap.getWidth(), bitmap.getHeight(), radius,
				recycleSource);
	}

	/**
	 * Get rounded bitmap image
	 *
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getResizedBitmap(Bitmap bitmap, int width, int height) {
		if (width == 0 || height == 0) {
			return null;
		}
		Bitmap resizedBitmap = null;
		try {
			resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height,
					true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		if (resizedBitmap == null)
			return null;
		else {
			ImageUtil.clear(bitmap);
			return resizedBitmap;
		}
	}

	/**
	 * 裁剪图片，默认先缩放
	 *
	 * @param bitmapPath
	 *            原图片路径
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap crop(String bitmapPath, int width, int height) {
		Bitmap bitmap = createBitmap(bitmapPath);
		bitmap = scale(bitmap, width, height, ScaleType.CENTER_CROP, true);
		return crop(bitmap, width, height, true);
	}

	/**
	 * 裁剪图片，默认先缩放
	 *
	 * @param bitmap
	 *            原图
	 * @param width
	 * @param height
	 * @param recycleSource
	 *            是否回收原图
	 * @return
	 */
	public static Bitmap scaleAndCrop(Bitmap bitmap, int width, int height,
			boolean recycleSource) {
		LogUtil.d(TAG, "scaleAndCrop()...");
		bitmap = scale(bitmap, width, height, ScaleType.CENTER_CROP,
				recycleSource);
		return crop(bitmap, width, height, true);
	}

	/**
	 * 剪裁图片 思路：取原图与目标大小的交叉部分
	 *
	 * @param sourceBitmap
	 *            原图
	 * @param targetWidth
	 *            剪裁到的宽度
	 * @param targetHeight
	 *            剪裁到的高度
	 * @param recycleSource
	 *            是否回收原图
	 * @return
	 */
	private static Bitmap crop(Bitmap sourceBitmap, int targetWidth,
			int targetHeight, boolean recycleSource) {
		LogUtil.d(TAG, "crop()...");
		if (sourceBitmap == null)
			return null;

		Bitmap croppedBitmap = null;

		// 获取原图缩放之后与目标图的交叉区域
		int xDiff = Math.max(0, sourceBitmap.getWidth() - targetWidth);
		int yDiff = Math.max(0, sourceBitmap.getHeight() - targetHeight);
		int x = xDiff / 2;
		int y = yDiff / 2;

		try {
			// 根据交叉区域进行剪裁
			croppedBitmap = Bitmap.createBitmap(sourceBitmap, x, y,
					targetWidth, targetHeight);
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError",
					"OutOfMemoryError in BitmapUtil.crop() : " + e.getMessage());
		}

		if (recycleSource && sourceBitmap != croppedBitmap)
			clear(sourceBitmap);
		return croppedBitmap;
	}

	/**
	 * 等比缩放图片
	 *
	 * @param sourceBitmap
	 *            原图
	 * @param targetWidth
	 *            目标宽度
	 * @param targetHeight
	 *            目标高度
	 * @param scaleType
	 *            缩放类型同ImageView.ScaleType，但只用到CENTER_CROP和 CENTER_INSIDE
	 * @param recycleSource
	 *            是否回收原图
	 *
	 * @return
	 */
	public static Bitmap scale(Bitmap sourceBitmap, float targetWidth,
			float targetHeight, ScaleType scaleType, boolean recycleSource) {
		LogUtil.d(TAG, "scale()...");
		if (sourceBitmap != null)
			LogUtil.d(TAG,
					"sourceBitmap.isRecycled() : " + sourceBitmap.isRecycled());
		if (sourceBitmap == null || sourceBitmap.isRecycled())
			return null;

		Bitmap scaledBitmap = null;

		float scale;

		float sourceWidth = sourceBitmap.getWidth();
		float sourceHeight = sourceBitmap.getHeight();

		float sourceRatio = sourceWidth / sourceHeight;
		float targetRatio = targetWidth / targetHeight;

		// 计算缩放比例，比较(原图宽/高比)和(目标图的宽/高比)，若前者大用高度比例，否则用宽度比例
		if (scaleType == ScaleType.CENTER_CROP)
			scale = sourceRatio > targetRatio ? targetHeight / sourceHeight
					: targetWidth / sourceWidth;
		else
			scale = sourceRatio < targetRatio ? targetHeight / sourceHeight
					: targetWidth / sourceWidth;

		// 不需要缩放，直接返回
		if (scale == 1.0F)
			return sourceBitmap;

		// 创建缩放矩阵
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);

		// LogUtil.d(TAG, "sourceWidth : " + sourceWidth + " , sourceHeight : "
		// + sourceHeight);
		try {
			// 将原图缩放
			scaledBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
					sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix,
					true);

			// LogUtil.d(TAG, "scaled: width : " + scaledBitmap.getWidth());
			// LogUtil.d(TAG, "scaled: height : " + scaledBitmap.getHeight());
		} catch (IllegalArgumentException e) {
			LogUtil.e(
					"IllegalArgumentException",
					"IllegalArgumentException in BitmapUtil.scale(): "
							+ e.getMessage());
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError",
					"OutOfMemoryError in BitmapUtil.scale(): " + e.getMessage());
		}

		if (recycleSource && sourceBitmap != scaledBitmap)
			clear(sourceBitmap);
		return scaledBitmap;
	}

	public static InputStream Bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}


	public static Options getBitmapOptions(String path, int maxSize) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = (options.outWidth > options.outHeight ? options.outWidth
				: options.outHeight)
				/ (maxSize < 1 ? MAX_SIZE : maxSize);
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return options;
	}

	public static Options getBitmapOptions(InputStream is, int maxSize) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);
		options.inSampleSize = (options.outWidth > options.outHeight ? options.outWidth
				: options.outHeight)
				/ maxSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return options;
	}

	public static Options getBitmapOptions(Resources res, int resId, int maxSize) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		options.inSampleSize = (options.outWidth > options.outHeight ? options.outWidth
				: options.outHeight)
				/ maxSize;
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return options;
	}

	public static Options getBitmapOptions(Context context, Uri uri, int maxSize) {
		if (context == null || uri == null)
			return null;
		Options options = new Options();

		FileDescriptor fd = getFileDescriptor(context, uri);

		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fd, null, options);

		options.inSampleSize = (options.outWidth > options.outHeight ? options.outWidth
				: options.outHeight)
				/ maxSize;

		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		return options;
	}

	public static FileDescriptor getFileDescriptor(Context context, Uri uri) {
		if (context == null || uri == null)
			return null;
		ContentResolver res = context.getContentResolver();

		ParcelFileDescriptor parcelFileDescriptor = null;
		FileDescriptor fd = null;
		try {
			parcelFileDescriptor = res.openFileDescriptor(uri, "r");
			fd = parcelFileDescriptor.getFileDescriptor();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (parcelFileDescriptor != null)
					parcelFileDescriptor.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fd;
	}

	public static Bitmap createBitmap(String path) {
		return createBitmap(path, MAX_SIZE);
	}

	public static Bitmap createBitmap(String path, int maxSize) {
		if (TextUtils.isEmpty(path))
			return null;
		Bitmap bitmap = null;
		try {
			Options options = getBitmapOptions(path, maxSize);
			bitmap = BitmapFactory.decodeFile(path, options);
			return setOrientation(bitmap, path);
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError", "OOM in BitmapUtil.createBitmap : "
					+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap createBitmap(int width, int height, Config config) {
		if (width <= 0 || height <= 0)
			return null;
		Bitmap bitmap = null;
		try {
			bitmap = Bitmap.createBitmap(width, height, config);
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError", "OOM in BitmapUtil.createBitmap : "
					+ e.getMessage());
		}
		return bitmap;
	}

	public static Bitmap createBitmap(InputStream is) {
		if (is == null)
			return null;
		Bitmap bitmap = null;
		try {
			Options options = getBitmapOptions(is, MAX_SIZE);
			bitmap = BitmapFactory.decodeStream(is, null, options);
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError", "OOM in BitmapUtil.createBitmap : "
					+ e.getMessage());
		}
		return bitmap;
	}

	public static Bitmap createBitmap(InputStream is, Rect outPadding,
			Options opts) {
		if (is == null)
			return null;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(is, outPadding, opts);
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError", "OOM in BitmapUtil.createBitmap : "
					+ e.getMessage());
		}
		return bitmap;
	}

	public static Bitmap createBitmap(Context context, int resId) {
		if (context == null)
			return null;
		Bitmap bitmap = null;
		try {
			Options options = getBitmapOptions(context.getResources(), resId,
					MAX_SIZE);
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					resId, options);
		} catch (OutOfMemoryError e) {
			LogUtil.e("OutOfMemoryError", "OOM in BitmapUtil.createBitmap : "
					+ e.getMessage());
		}
		return bitmap;
	}

	/**
	 * 从Uri中获取一张bitmap
	 *
	 * @param context
	 * @param uri
	 * @param maxSize
	 * @return
	 */
	public static Bitmap createBitmap(Context context, Uri uri, int maxSize) {
		if (context == null || uri == null)
			return null;
		FileDescriptor fd = getFileDescriptor(context, uri);
		Options options = getBitmapOptions(context, uri, maxSize);
		if (fd == null || options == null)
			return null;
		Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
		if (bitmap != null
				&& (bitmap.getWidth() > options.outWidth || bitmap.getHeight() > options.outHeight)) {
			Bitmap tmp = Bitmap.createScaledBitmap(bitmap, options.outWidth,
					options.outHeight, true);
			if (tmp != null && tmp != bitmap)
				ImageUtil.clear(bitmap);
			if (tmp != null)
				bitmap = tmp;
		}
		return bitmap;
	}

	/**
	 * 根据参数修整图片
	 *
	 * @param bitmap
	 * @param width
	 * @param height
	 * @param radius
	 * @param needCrop
	 * @param needScale
	 * @param recycleSource
	 *            是否回收原图
	 * @return
	 */
	public static Bitmap revise(Bitmap bitmap, int width, int height,
			int radius, boolean needCrop, boolean needScale,
			boolean recycleSource) {
		if (bitmap == null)
			return null;
		LogUtil.d(TAG, "revise to width : " + width + " height : " + height);
		if (needCrop && needScale || (radius > 0 && (width > 0 || height > 0))) {
			bitmap = scaleAndCrop(bitmap, width, height, recycleSource);
		} else if (needScale) {
			bitmap = scale(bitmap, width, height,
					ImageView.ScaleType.CENTER_INSIDE, recycleSource);
		} else if (needCrop)
			bitmap = crop(bitmap, width, height, recycleSource);
		if (radius > 0)
			bitmap = ImageUtil.round(bitmap, width, height, radius,
					recycleSource);
		return bitmap;
	}

	/**
	 * 将bitmap写入文件
	 *
	 * @param bitmap
	 * @param path
	 * @param quality
	 * @param recycleSource
	 * @return
	 */
	public static String writeToFile(Bitmap bitmap, String path, int quality,
			boolean recycleSource) {
		LogUtil.d(TAG, "writeToFile : " + path);
		if (bitmap == null)
			return null;

		try {
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();
			File f = new File(path);

			if (f.exists())
				f.delete();

			if (f.createNewFile()) {
				FileOutputStream fos = new FileOutputStream(f);
				bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (recycleSource)
			clear(bitmap);

		return path;
	}

	/**
	 * 释放bitmap
	 *
	 * @param bitmap
	 */
	public static void clear(Bitmap bitmap) {
		if (bitmap != null)
			bitmap.recycle();
	}

	/**
	 * 刷新ImageView上的图片，并将原来的图片回收
	 *
	 * @param iv
	 * @param imgTagId
	 * @param bitmap
	 * @param defaultBitmap
	 */
	public static void refresh(ImageView iv, int imgTagId, Bitmap bitmap,
			Bitmap defaultBitmap) {
		if (iv == null)
			return;
		Bitmap oldBitmap = (Bitmap) iv.getTag(imgTagId);
		if (oldBitmap != bitmap)
			clear(oldBitmap);
		iv.setImageBitmap(bitmap == null ? defaultBitmap : bitmap);
		iv.setTag(imgTagId, bitmap);
	}

	public static int refresh(ImageView iv, int imgTagId, Bitmap bitmap,
			int resId) {
		if (iv == null)
			return 0;
		int hashCode = 0;
		Bitmap oldBitmap = (Bitmap) iv.getTag(imgTagId);
		if (oldBitmap != null && oldBitmap != bitmap) {
			hashCode = oldBitmap.hashCode();
			clear(oldBitmap);
		}
		if (bitmap == null)
			iv.setImageResource(resId);
		else
			iv.setImageBitmap(bitmap);
		iv.setTag(imgTagId, bitmap);
		return hashCode;
	}

	public static Bitmap createBitmap(Context context, String filename) {
		return createBitmap(filename, MAX_SIZE);
	}

	public static Bitmap createBitmap(Context context, String filename,
			int maxSize) {
		if (context == null || TextUtils.isEmpty(filename))
			return null;
		filename = context.getFilesDir() + File.separator + filename;
		return createBitmap(filename, maxSize);
	}

	public static Bitmap createBitmapFromAsset(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return null;
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = context.getAssets().open(filename);
			bitmap = ImageUtil.createBitmap(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public static Bitmap createBitmapFromFile(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return null;
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			is = context.openFileInput(filename);
			bitmap = ImageUtil.createBitmap(is);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 对Bitmap.createBitmap的原始封装，只是加了内存溢出判断
	 *
	 * @param src
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param m
	 * @param filter
	 * @return
	 */
	public static Bitmap createBitmap(Bitmap src, int x, int y, int width,
			int height, Matrix m, boolean filter) {
		if (src == null)
			return null;
		Bitmap bitmap = src;
		try {
			// 将原图缩放
			bitmap = Bitmap.createBitmap(src, x, y, width, height, m, filter);
		} catch (IllegalArgumentException e) {
			LogUtil.e(
					"IllegalArgumentException",
					"IllegalArgumentException in BitmapUtil.rotate(): "
							+ e.getMessage());
		} catch (OutOfMemoryError e) {
			LogUtil.e(
					"OutOfMemoryError",
					"OutOfMemoryError in BitmapUtil.rotate(): "
							+ e.getMessage());
		}
		return bitmap;
	}

	/**
	 * 将Bitmap转化成int数组
	 *
	 * @param bitmap
	 * @return
	 */
	public static int[] getIntArray(Bitmap bitmap) {
		if (bitmap == null)
			return null;
		int pix[] = null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		try {
			pix = new int[w * h];
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			LogUtil.e(TAG, "method getIntArray w : " + w);
			LogUtil.e(TAG, "method getIntArray h : " + h);
			LogUtil.e(
					TAG,
					"OutOfMemoryError in method getIntArray : "
							+ e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pix;
	}

	public static byte[] toBytes(Bitmap bitmap) {
		if (bitmap == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static String getImageNameByTime() {
		Calendar calendar = Calendar.getInstance();
		return "IMG_" + String.valueOf(calendar.get(Calendar.YEAR))
				+ String.valueOf(calendar.get(Calendar.MONTH))
				+ String.valueOf(calendar.get(Calendar.DATE)) + "_"
				+ String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))
				+ String.valueOf(calendar.get(Calendar.MINUTE))
				+ String.valueOf(calendar.get(Calendar.SECOND)) + ".jpg";
	}

	/**
	 * 从url获取该图片的文件名
	 *
	 * @param url
	 * @param postfix
	 * @return
	 */
	public static String getImageNameFromUrl(String url, String postfix) {
		return getImageNameFromUrl(url, postfix, null);
	}

	/**
	 * 从url获取当前图片的文件名，如果url以ignoreTag开头则直接返回该url；如果ignoreTag为空，则不会判断ignoreTag
	 *
	 * @param url
	 * @param postfix
	 * @param ignoreTag
	 * @return
	 */
	public static String getImageNameFromUrl(String url, String postfix,
			String ignoreTag) {
		if (TextUtils.isEmpty(url)
				|| ((!TextUtils.isEmpty(ignoreTag)) && url.startsWith(ignoreTag)))
			return url;
		int lastIndex = url.lastIndexOf(postfix);
		if (lastIndex < 0)
			lastIndex = url.length() - 1;
		int beginIndex = url.lastIndexOf("/") + 1;
		int slashIndex = url.lastIndexOf("%2F") + 3;
		int finalSlashIndex = url.lastIndexOf("%252F") + 5;
		beginIndex = Math
				.max(Math.max(beginIndex, slashIndex), finalSlashIndex);

		if (beginIndex >= lastIndex) {
			return null;
		}
		return url.substring(beginIndex, lastIndex);
	}

	public static Bitmap copy(Bitmap bitmap) {
		if (bitmap == null || bitmap.isRecycled())
			return null;
		return bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
	}

	/**
	 * 图片操作回调
	 *
	 * @author wangzengyang 2013-4-11
	 *
	 */
	public interface ImageListener {
		/**
		 * 剪裁回调(UI线程中)
		 *
		 * @param bitmap
		 */
		void onRevise(Bitmap bitmap);
	}

	/**
	 * 在Bitmap上做装饰
	 *
	 * @param bitmap
	 *            原始图片
	 * @param dots
	 *            装饰用的点
	 * @return 装饰后的图片对象
	 */
	public static Bitmap decorate(Bitmap bitmap, int[] dots) {
		if (bitmap == null || dots == null)
			return null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		try {
			if (dots.length != w * h)
				return null;
			if (dots.length > 0) {
				bitmap.setPixels(dots, 0, w, 0, 0, w, h);
				return bitmap;
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			LogUtil.e(TAG, "method decorate w : " + w);
			LogUtil.e(TAG, "method decorate h : " + h);
			LogUtil.e(TAG,
					"OutOfMemoryError in method decorate : " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将图片更新到MediaStore
	 *
	 * @param resolver
	 * @param title
	 * @param location
	 * @param orientation
	 * @param jpeg
	 *            图片二进制数据
	 * @param path
	 *            图片路径
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Uri saveToMediaStore(ContentResolver resolver, String title,
			Location location, int orientation, byte[] jpeg, String path) {
		ContentValues values = new ContentValues(9);
		values.put(ImageColumns.TITLE, title);
		values.put(ImageColumns.DISPLAY_NAME, title + ".jpg");
		Date d = new Date();
		values.put(ImageColumns.DATE_TAKEN, d.getDate());
		values.put(ImageColumns.MIME_TYPE, "image/jpeg");
		values.put(ImageColumns.ORIENTATION, orientation);
		values.put(ImageColumns.DATA, path);
		values.put(ImageColumns.SIZE, jpeg.length);

		if (location != null) {
			values.put(ImageColumns.LATITUDE, location.getLatitude());
			values.put(ImageColumns.LONGITUDE, location.getLongitude());
		}

		Uri uri = resolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
		if (uri == null) {
			LogUtil.e(TAG, "Failed to write MediaStore");
			return null;
		}
		return uri;
	}

	public static BitmapDrawable decodeWithOOMHandling(Resources res,
			String imagePath) {
		BitmapDrawable result = null;

		if (TextUtils.isEmpty(imagePath))
			return result;

		try {
			result = new BitmapDrawable(res, imagePath);
		} catch (OutOfMemoryError e) {
			LogUtil.e(TAG, e.getMessage(), e);
			System.gc();
			// TODO: handle exception
			LogUtil.d(TAG, "++++decodeWithOOMHandling,OutOfMemoryError");
			// Wait some time while GC is working
			SystemClock.sleep(1000);
			System.gc();
		}
		return result;
	}

	/**
	   *
	   * */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 150 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 根据URI获取图片物理路径
	 *
	 * */
	@SuppressWarnings("deprecation")
	protected static String getAbsoluteImagePath(Uri uri, Activity activity) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 滤镜效果
	 * @param bitmap
	 * @param maskBitmap
	 * @return
	 */
	public static Bitmap maskDrawable(Bitmap bitmap, Bitmap maskBitmap){
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFF000000);
		new NinePatch(maskBitmap, maskBitmap.getNinePatchChunk(), null).draw(
				canvas, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, 0, 0, paint);
		return output;
	}

	/**
	 * 处理成圆角图片
	 *
	 * @param bitmap
	 * @param roundPX
	 * @return
	 */
	public static Bitmap getRCB(Bitmap bitmap, float roundPX) {
		Bitmap dstbmp = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(dstbmp);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		paint.setFilterBitmap(true);
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return dstbmp;
	}

	/**
	 * 从文件读取图片并调整尺寸
	 * @param file
	 * @param MAX_SIZE
	 * @param defaultWidth
	 * @param defaultHeight
	 * @return
	 */
	public static byte[] getBitmapBytes(File file, final int MAX_SIZE,
			final int defaultWidth, final int defaultHeight) {
		long fileSize = file.length() / 1024;
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = null;
		Bitmap srcBitmap = null;

		if (fileSize >= MAX_SIZE) {
			int sqr = (int) Math
					.ceil(Math.sqrt(((double) fileSize / MAX_SIZE)));
			options.inSampleSize = sqr;
			try {
				srcBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
			} catch (OutOfMemoryError e) {
				LogUtil.e("OutOfMemoryError",
						"OOM in BitmapUtil.filterFileImage : " + e.getMessage());
				return null;
			}
			if (srcBitmap == null)
				return null;

			bitmap = setOrientation(resizeBitmap(srcBitmap, defaultWidth,
					defaultHeight), file.getAbsolutePath());

			// 重新生成上传的图片文件
			ByteArrayOutputStream baos = null;
			try {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
				byte[] content = baos.toByteArray();
				if (content != null && content.length > 0) {
					file.delete();
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(content);
					if (fos != null)
						fos.close();
				}
				return content;
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally{
				if(baos != null){
					try {
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			srcBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (srcBitmap == null)
				return null;
			bitmap = setOrientation(resizeBitmap(srcBitmap, defaultWidth,
					defaultHeight), file.getAbsolutePath());
		}
		ByteArrayOutputStream baos = null;
		baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
		byte[]  result = baos.toByteArray();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}
		if (srcBitmap != null) {
			srcBitmap.recycle();
			srcBitmap = null;
		}
		return result;
	}

	/**
	 * Stack Blur v1.0 from
	 * http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
	 * Java Author: Mario Klingemann
	 * <p/>
	 * I called it Stack Blur because this describes best how this
	 * filter works internally: it creates a kind of moving stack
	 * of colors whilst scanning through the image. Thereby it
	 * just has to add one new block of color to the right side
	 * of the stack and remove the leftmost color. The remaining
	 * colors on the topmost layer of the stack are either added on
	 * or reduced by one, depending on if they are on the right or
	 * on the left side of the stack.
	 * <p/>
	 * Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>
	 */
	public static Bitmap fastblur(Bitmap sentBitmap, int radius) {

		if (sentBitmap == null || sentBitmap.isRecycled()) {
			return null;
		}

//    	LogUtil.d(TAG, "fastblur, original size: " + sentBitmap.getByteCount());

		Bitmap bitmap = sentBitmap.copy(Bitmap.Config.RGB_565, true);

		if (bitmap == null || bitmap.isRecycled()) {
			return null;
		}
//        LogUtil.d(TAG, "fastblur, copy size: " + bitmap.getByteCount());

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}

	public static Bitmap setOrientation(Bitmap originBitmap, String imagePath) {
		ExifInfo exifInfo = defineExifOrientation(imagePath);
		Matrix m = new Matrix();
		// Flip bitmap if need
		if (exifInfo.flipHorizontal) {
			m.postScale(-1, 1);
		}
		// Rotate bitmap if need
		if (exifInfo.rotation != 0) {
			m.postRotate(exifInfo.rotation);
		}

		Bitmap finalBitmap = Bitmap.createBitmap(originBitmap, 0, 0, originBitmap.getWidth(), originBitmap
				.getHeight(), m, true);
		if (finalBitmap != originBitmap) {
			originBitmap.recycle();
		}
		return finalBitmap;
	}

	private static ExifInfo defineExifOrientation(String imagePath) {
		int rotation = 0;
		boolean flip = false;
		try {
			ExifInterface exif = new ExifInterface(imagePath);
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (exifOrientation) {
				case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
					flip = true;
				case ExifInterface.ORIENTATION_NORMAL:
					rotation = 0;
					break;
				case ExifInterface.ORIENTATION_TRANSVERSE:
					flip = true;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotation = 90;
					break;
				case ExifInterface.ORIENTATION_FLIP_VERTICAL:
					flip = true;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotation = 180;
					break;
				case ExifInterface.ORIENTATION_TRANSPOSE:
					flip = true;
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotation = 270;
					break;
			}
		} catch (IOException e) {
			LogUtil.w("Can't read EXIF tags from file [%s]", imagePath);
		}
		return new ExifInfo(rotation, flip);
	}

	private static class ExifInfo {

		public int rotation;
		public boolean flipHorizontal;

		public ExifInfo(int rotation, boolean flipHorizontal) {
			this.rotation = rotation;
			this.flipHorizontal = flipHorizontal;
		}
	}
}
