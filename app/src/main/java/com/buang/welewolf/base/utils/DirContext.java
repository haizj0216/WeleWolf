/**
 * Copyright (C) 2014 The plugin_music Project
 */
package com.buang.welewolf.base.utils;

import com.hyena.framework.utils.BaseApp;

import java.io.File;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月12日 下午7:07:34
 * 
 */
public class DirContext {

	/**
	 * 获得引用根路径
	 * @return
	 */
	public static File getRootDir(){
		File rootDir = new File(android.os.Environment.getExternalStorageDirectory(), "welewolf");
		return rootDir;
	}

	public static File getFileDir() {
		return getDir(getRootDir(), "files");
	}

	/**
	 * 获得图片缓存路径
	 * @return
	 */
	public static File getImageCacheDir(){
		return getDir(getRootDir(), "images");
	}
	
	/**
	 * 聊天缓存路径
	 * @return
	 */
	public static File getChatCacheDir(){
		return getDir(getRootDir(), "chat");
	}
	
	/**
	 * 聊天接收到的图片存储路径
	 * @return
	 */
	public static File getChatImageCacheDir(){
		return getDir(getChatCacheDir(), "images");
	}
	
	/**
	 * 获得音频缓存路径
	 * @return
	 */
	public static File getAudioCacheDir(){
		return getDir(getRootDir(), "audio");
	}

	/**
	 * 获得模板缓存路径
	 * @return
     */
	public static File getTemplateFile() {
		File file = new File(BaseApp.getAppContext().getCacheDir() + "/template");
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	
	/**
	 * 获得特定目录下的文件夹
	 * @param parent
	 * @param dirName
	 * @return
	 */
	public static File getDir(File parent, String dirName){
		File file = new File(parent, dirName);
		if(!file.exists() || !file.isDirectory())
			file.mkdirs();
		return file;
	}
}
