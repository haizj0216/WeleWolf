/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.knowbox.teacher.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Video;
import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.security.MessageDigest;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月13日 下午12:02:00
 * 
 */
public class FileUtils {

	private static final double MIN_FREE_SPACE = 10;

	public static final String SYSTEM_SEPARATOR = System
			.getProperty("file.separator");
	/**
	 * KB
	 */
	public static final long ONE_KB = 1024;

	/**
	 * MB
	 */
	public static final long ONE_MB = ONE_KB * ONE_KB;

	/**
	 * GB
	 */
	public static final long ONE_GB = ONE_KB * ONE_MB;

	private static final int BUF_SIZE = 1024;
	private static final String TAG = "FileUtils";

	/**
	 * SDCARD是否存在
	 */
	public static boolean isSdcardAvailable() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_SHARED)
				|| status.equals(Environment.MEDIA_UNMOUNTED)) {
			return false;
		} else if (status.equals(Environment.MEDIA_REMOVED)) {
			return false;
		} else
			return true;
	}

	public static boolean isSdcardWritable() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)
				|| status.equals(Environment.MEDIA_CHECKING)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSdcardMounted() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)
				|| status.equals(Environment.MEDIA_CHECKING)
				|| status.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * SDCARD的绝对路径。
	 */
	public static String getExternalStoragePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 是否mount外置存储
	 * 
	 * @return
	 */
	public static boolean isMount() {
		String status = Environment.getExternalStorageState();
		return status.equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * SD卡剩余空间的百分比
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static double freePercentage() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		long availableBlocks = stat.getAvailableBlocks();
		long totalBlocks = stat.getBlockCount();
		return (double) availableBlocks / (double) totalBlocks * 100;
	}

	/**
	 * 判断SD卡访问状态,是否已经映射并且有足够的剩余空间
	 * 
	 * @return
	 */
	public static String assertSdCard() {
		if (!android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return "Your SD card is not mounted. You'll need it for caching thumbs.";
		}
		return null;
	}

	public static String checkFreeSpaceSdCard() {
		if (freePercentage() < MIN_FREE_SPACE) {
			return "You need to have more than " + MIN_FREE_SPACE
					+ "% of free space on your SD card.";
		}
		return null;
	}

	/**
	 * 得到SD卡全部存储空间
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long totalSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	/**
	 * 检查SD卡剩余空间
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static long freeSpace() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
				.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static String writeToFile(Context context, InputStream is,
			String filename) {
		if (is == null || context == null || TextUtils.isEmpty(filename))
			return null;
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		touchFile(context, filename);
		LogUtil.d(TAG, "writeToFile : " + filename);
		try {
			in = new BufferedInputStream(is);
			out = new BufferedOutputStream(new FileOutputStream(getFile(
					context, filename)));
			byte[] buffer = new byte[BUF_SIZE];
			int l;
			while ((l = in.read(buffer)) != -1) {
				// LogUtil.d("writeToFile", "length : " + l);
				out.write(buffer, 0, l);
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
				is.close();
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return context.getFilesDir() + File.separator + filename;
	}

	public static String writeToFile(Context context, byte[] bytes,
			String filename) {
		return writeToFile(context, bytes, filename, false);
	}

	public static String writeToFile(Context context, byte[] bytes,
			String filename, boolean isAppend) {
		if (bytes == null || context == null || TextUtils.isEmpty(filename))
			return null;
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(getFile(
					context, filename), isAppend));
			out.write(bytes);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			LogUtil.e(TAG, "IOException : " + ioe.getMessage());
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return context.getFilesDir() + File.separator + filename;
	}

	/**
	 * write a string To a File
	 * 
	 * @param context
	 * @param file
	 * @param string
	 * @param isAppend
	 * @return
	 */
	public static boolean writeStringToFile(File file, String string,
			boolean isAppend) {
		boolean isWriteOk = false;

		if (null == file || null == string) {
			return isWriteOk;
		}

		FileWriter fw = null;
		try {
			fw = new FileWriter(file, isAppend);

			fw.write(string, 0, string.length());
			fw.flush();
			isWriteOk = true;
		} catch (Exception e) {
			isWriteOk = false;
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					isWriteOk = false;
					e.printStackTrace();
				}
			}
		}
		return isWriteOk;
	}

	/**
	 * 根据文件URI判断是否为媒体文件
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isMediaUri(String uri) {
		if (uri.startsWith(Audio.Media.INTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Audio.Media.EXTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Video.Media.INTERNAL_CONTENT_URI.toString())
				|| uri.startsWith(Video.Media.EXTERNAL_CONTENT_URI.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public static void copyfile(File src, File dec) {
		try {
			if (src == null || dec == null) {
				return;
			}

			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dec);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void clearFile(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return;
		File file = getFile(context, filename);
		LogUtil.d(TAG, "clearFile path : " + file.getAbsolutePath());
		File dir = file.getParentFile();
		if (!dir.exists()) {
			LogUtil.d(TAG, "dir not exists");
			dir.mkdirs();
		}
		if (file.exists())
			file.delete();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void touchFile(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return;
		File file = getFile(context, filename);
		LogUtil.d(TAG, "touchFile path : " + file.getAbsolutePath());
		File dir = file.getParentFile();
		if (!dir.exists()) {
			LogUtil.d(TAG, "dir not exists");
			dir.mkdirs();
		}
		if (file.exists())
			return;
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File getFile(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return null;
		return new File(context.getFilesDir().getAbsoluteFile() + filename);

	}

	public static String getFileName(String path) {
		if (path == null) {
			return null;
		}
		String retStr = "";
		if (path.indexOf(SYSTEM_SEPARATOR) > 0) {
			retStr = path.substring(path.lastIndexOf(SYSTEM_SEPARATOR) + 1);
		} else {
			retStr = path;
		}
		return retStr;
	}

	public static String getFileNameNoPostfix(String path) {
		if (path == null) {
			return null;
		}
		return path.substring(path.lastIndexOf(File.pathSeparator) + 1);
	}

	/**
	 * 根据文件URI得到文件扩展名
	 * 
	 * @param uri
	 * @return
	 */
	public static String getExtension(String uri) {
		if (uri == null) {
			return null;
		}

		int dot = uri.lastIndexOf(".");
		if (dot >= 0) {
			return uri.substring(dot);
		} else {
			// No extension.
			return "";
		}
	}

	/**
	 * 判断是否为本地文件
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isLocal(String uri) {
		if (uri != null && !uri.startsWith("http://")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断文件是否为视频文件
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isVideo(String filename) {
		String mimeType = getMimeType(filename);
		if (mimeType != null && mimeType.startsWith("video/")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断文件是否为音频文件
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isAudio(String filename) {
		String mimeType = getMimeType(filename);
		if (mimeType != null && mimeType.startsWith("audio/")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据文件名得到文件的mimetype 简单判断,考虑改为xml文件配置关联
	 * 
	 * @param filename
	 * @return
	 */
	public static String getMimeType(String filename) {
		String mimeType = null;

		if (filename == null) {
			return mimeType;
		}
		if (filename.endsWith(".3gp")) {
			mimeType = "video/3gpp";
		} else if (filename.endsWith(".mid")) {
			mimeType = "audio/mid";
		} else if (filename.endsWith(".mp3")) {
			mimeType = "audio/mpeg";
		} else if (filename.endsWith(".xml")) {
			mimeType = "text/xml";
		} else {
			mimeType = "";
		}
		return mimeType;
	}

	/**
	 * 将文件大小的long值转换为可读的文字
	 * 
	 * @param size
	 * @return 10KB或10MB或1GB
	 */
	public static String byteCountToDisplaySize(long size) {
		String displaySize;

		if (size / ONE_GB > 0) {
			displaySize = String.valueOf(size / ONE_GB) + " GB";
		} else if (size / ONE_MB > 0) {
			displaySize = String.valueOf(size / ONE_MB) + " MB";
		} else if (size / ONE_KB > 0) {
			displaySize = String.valueOf(size / ONE_KB) + " KB";
		} else {
			displaySize = String.valueOf(size) + " bytes";
		}
		return displaySize;
	}

	/**
	 * 将文件大小的long值转换为可读的文字
	 * 
	 * @param size
	 * @param scale
	 *            保留几位小数
	 * @return 10KB或10MB或1GB
	 */
	public static String byteCountToDisplaySize(long size, int scale) {
		String displaySize;
		if (size / ONE_GB > 0) {
			float d = (float) size / ONE_GB;
			displaySize = getOffsetDecimal(d, scale) + " GB";
		} else if (size / ONE_MB > 0) {
			float d = (float) size / ONE_MB;
			displaySize = getOffsetDecimal(d, scale) + " MB";
		} else if (size / ONE_KB > 0) {
			float d = (float) size / ONE_KB;
			displaySize = getOffsetDecimal(d, scale) + " KB";
		} else {
//			displaySize = String.valueOf(size) + " bytes";
			displaySize = "0.00KB";
		}
		return displaySize;
	}

	public static String getOffsetDecimal(float ft, int scale) {
		int roundingMode = 4;// 表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
		BigDecimal bd = new BigDecimal((double) ft);
		bd = bd.setScale(scale, roundingMode);
		ft = bd.floatValue();
		return "" + ft;
	}

	public static boolean isDirectory(File file) {
		return file.exists() && file.isDirectory();
	}

	public static boolean isFile(File file) {
		return file.exists() && file.isFile();
	}

	// 检测目录，没有重建
	public static void checkDir(String dirPath) {
		File file = new File(dirPath);
		createNewDirectory(file);
	}

	public static boolean createNewDirectory(File file) {
		if (file.exists() && file.isDirectory()) {
			return false;
		}
		return file.mkdirs();
	}

	public static boolean deleteFile(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return true;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			return true;
		}
		boolean flag = false;
		if (file.isFile()) {
			flag = file.delete();
		}
		return flag;
	}

	public static void delDirectory(String directoryPath) {
		try {
			delAllFile(directoryPath); // 删除完里面所有内容
			String filePath = directoryPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delDirectory(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public static boolean delAllFileWithoutDir(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFileWithoutDir(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				flag = true;
			}
		}
		return flag;
	}

	// -------------------- 获得文件的md5等hash值---------------------//
	public final static String HASH_TYPE_MD5 = "MD5";
	public final static String HASH_TYPE_SHA1 = "SHA1";
	public final static String HASH_TYPE_SHA1_256 = "SHA-256";
	public final static String HASH_TYPE_SHA1_384 = "SHA-384";
	public final static String HASH_TYPE_SHA1_512 = "SHA-512";
	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String getHash(String fileName, String hashType)
			throws Exception {
		InputStream fis;
		fis = new FileInputStream(fileName);
		byte[] buffer = new byte[1024];
		MessageDigest md5 = MessageDigest.getInstance(hashType);
		int numRead = 0;
		while ((numRead = fis.read(buffer)) > 0) {
			md5.update(buffer, 0, numRead);
		}
		fis.close();
		return toHexString(md5.digest());
	}

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String loadString(Context context, String filename) {
		if (context == null || TextUtils.isEmpty(filename))
			return null;
		File file = getFile(context, filename);
		return loadString(file);
	}

	/**
	 * read file to a string
	 * 
	 * @param context
	 * @param file
	 * @return
	 */
	public static String loadString(File file) {
		if (null == file || !file.exists()) {
			return "";
		}
		FileInputStream fis = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			fis = new FileInputStream(file);
			int restSize = fis.available();
			int bufSize = restSize > BUF_SIZE ? BUF_SIZE : restSize;
			byte[] buf = new byte[bufSize];
			while (fis.read(buf) != -1) {
				baos.write(buf);
				restSize -= bufSize;

				if (restSize <= 0)
					break;
				if (restSize < bufSize)
					bufSize = restSize;
				buf = new byte[bufSize];
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return baos.toString();
	}

	public static long getFolderSize(File folder)
			throws IllegalArgumentException {
		// Validate
		if (folder == null || !folder.isDirectory())
			throw new IllegalArgumentException("Invalid   folder ");
		String list[] = folder.list();
		if (list == null || list.length < 1)
			return 0;

		// Get size
		File object = null;
		long folderSize = 0;
		for (int i = 0; i < list.length; i++) {
			object = new File(folder, list[i]);
			if (object.isDirectory())
				folderSize += getFolderSize(object);
			else if (object.isFile())
				folderSize += object.length();
		}
		return folderSize;
	}

	/**
	 * 检查文件是否存在于某目录下, 3.3.0
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public static String checkFile(String filePathName) {
		// 添加入口参数检查
		if (filePathName == null) {
			return null;
		}
		// 在图片存储目录里检查
		File file = new File(filePathName);
		if (file.exists() || file.isFile()) {
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 获取文件编码
	 * @param sourceFile
	 * @return
	 */
	public static String getFileCharset(String sourceFile) {
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		BufferedInputStream bis = null;
		try {
			boolean checked = false;
			bis = new BufferedInputStream(new FileInputStream(sourceFile));
			bis.mark(3);
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				return charset; // 文件编码为 ANSI
			} else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE"; // 文件编码为 Unicode
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE"; // 文件编码为 Unicode big endian
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8"; // 文件编码为 UTF-8
				checked = true;
			}
			bis.reset();
			if (!checked) {
				while ((read = bis.read()) != -1) {
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)
							// (0x80
							// - 0xBF),也可能在GB编码内
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bis != null)
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return charset;
	}
	
	/**
	 * 读文件
	 * @param file
	 * @param charset
	 * @return
	 */
	public static String readFile2String(File file, String charset) {
		if (null == file)
			return "";
		FileInputStream is = null;
		ByteArrayOutputStream os = null;
		try {
			is = new FileInputStream(file);
			os = new ByteArrayOutputStream();
			copyStream(is, os);
			return new String(os.toByteArray(), charset);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 流拷贝
	 * @param is
	 * @param os
	 * @throws IOException
	 */
	public static void copyStream(InputStream is, OutputStream os) throws IOException{
		byte buffer[] = new byte[1024];
		int len = -1;
		while((len = is.read(buffer, 0, 1024)) != -1){
			os.write(buffer, 0, len);
		}
	}
	
	/**
	 * 拷贝流到文件中
	 * @param is
	 * @param desc
	 * @throws IOException
	 */
	public static void copyStream2File(InputStream is, File desc) throws IOException{
		OutputStream os = null;
		try {
			os = new FileOutputStream(desc);
			copyStream(is, os);
		}finally{
			if(os != null)
				os.close();
		}
	}
	
	/**
	 * 拷贝文件
	 * @param src
	 * @param desc
	 * @throws IOException
	 */
	public static void copyFile(File src, File desc) throws IOException{
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(src);
			os = new FileOutputStream(desc);
			copyStream(is, os);
		}finally{
			if(is != null)
				is.close();
			if(os != null)
				os.close();
		}
	}

	/**
	 * 输入流中数据转换成数据
	 * @param is
	 * @return
	 */
	public static byte[] getBytes(InputStream is) throws IOException{
		ByteArrayOutputStream os = null;
		try {
			os = new ByteArrayOutputStream();
			copyStream(is, os);
		} finally {
			os.close();
		}
		return os.toByteArray();
	}
	
	/**
	 * 读取文件
	 * @param file
	 * @return
	 */
	public static byte[] getBytes(File file) {
		FileInputStream fis = null;
		ByteArrayOutputStream baos = null;
		try {
			fis = new FileInputStream(file);
			baos = new ByteArrayOutputStream();
			copyStream(fis, baos);
			return baos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis != null){
				try {
					fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 保存图片
	 * @param bitmap
	 * @param dest
	 */
	public static void saveBitmap(Bitmap bitmap, File dest) {
		ByteArrayInputStream bais = null;
		try {
			//TODO:
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bStream);
			byte[] bytes = bStream.toByteArray();
			bais = new ByteArrayInputStream(bytes);
			copyStream2File(bais, dest);
			DebugUtils.debugTxt("saveBitmap : " + dest.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(bais != null){
				try {
					bais.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean createEmptyFile(String path, long size) throws IOException{
	    File file = new File(path);
	    File parent = file.getParentFile();
	    parent.mkdirs();	    
    	RandomAccessFile raf = null;    	   	
		raf = new RandomAccessFile(file, "rw");
		raf.setLength(size);
		raf.close();		
    	return true;
    }
}
