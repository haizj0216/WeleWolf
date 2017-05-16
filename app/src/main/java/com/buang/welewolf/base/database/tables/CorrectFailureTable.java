/**
 * Copyright (C) 2014 The knowbox_Teacher Project
 */
package com.buang.welewolf.base.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.hyena.framework.database.BaseTable;
import com.buang.welewolf.base.database.bean.CorrectFailureItem;

import java.util.List;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年12月1日 下午4:00:55 批改试卷上传失败表
 */
public class CorrectFailureTable extends BaseTable<CorrectFailureItem> {

	/** 批改试卷上传失败表 */
	public static final String TABLE_NAME = "CORRECT_FAILUER_TABLE";

	public static final String ANSWERID = "ANSWERID";
	public static final String FAILURECONTENT = "FAILURECONTENT";

	public CorrectFailureTable(SQLiteOpenHelper sqlHelper) {
		super(TABLE_NAME, sqlHelper);
	}

	@Override
	public String getCreateSql() {
		String sql = "CREATE TABLE IF NOT EXISTS "
				+ CorrectFailureTable.TABLE_NAME + "("
				+ CorrectFailureTable._ID + " integer primary key ,"
				+ CorrectFailureTable.ANSWERID + " varchar,"
				+ CorrectFailureTable.FAILURECONTENT + " varchar" + ")";
		return sql;
	}

	@Override
	public CorrectFailureItem getItemFromCursor(Cursor cursor) {
		String ansId = cursor.getString(cursor
				.getColumnIndexOrThrow(CorrectFailureTable.ANSWERID));

		String failureContent = cursor.getString(cursor
				.getColumnIndexOrThrow(CorrectFailureTable.FAILURECONTENT));

		CorrectFailureItem item = new CorrectFailureItem();
		item.answerId = ansId;
		item.failureContent = failureContent;
		return item;
	}

	@Override
	public ContentValues getContentValues(CorrectFailureItem item) {
		ContentValues values = new ContentValues();
		values.put(CorrectFailureTable.ANSWERID, item.answerId);
		values.put(CorrectFailureTable.FAILURECONTENT, item.failureContent);
		return values;
	}

	/**
	 * 更新
	 * 
	 * @param answerId
	 * @param uploadHomeworkJson
	 */
	public void insertUploadFailuer(String answerId, String uploadHomeworkJson) {
		List<CorrectFailureItem> items = this.queryByCase(
				CorrectFailureTable.ANSWERID + " = ? ",
				new String[] { answerId }, null);
		if (items == null || items.isEmpty()) {
			insertUploadFailuerAns(answerId, uploadHomeworkJson);
		} else {
			updateUploadFailuerAns(answerId, uploadHomeworkJson);
		}
	}

	/**
	 * 更新
	 * 
	 * @param answerId
	 * @param uploadHomeworkJson
	 */
	private void updateUploadFailuerAns(String answerId,
			String uploadHomeworkJson) {
		String whereClause = CorrectFailureTable.ANSWERID + " = ?  ";
		String[] whereArgs = new String[] { answerId };

		CorrectFailureItem item = new CorrectFailureItem();
		item.answerId = answerId;
		item.failureContent = uploadHomeworkJson;
		this.updateByCase(item, whereClause, whereArgs);
	}

	/**
	 * 添加
	 * 
	 * @param answerId
	 * @param uploadHomeworkJson
	 */
	private void insertUploadFailuerAns(String answerId,
			String uploadHomeworkJson) {
		CorrectFailureItem item = new CorrectFailureItem();
		item.answerId = answerId;
		item.failureContent = uploadHomeworkJson;
		this.insert(item);
	}

	/**
	 * 删除
	 * 
	 * @param answerId
	 */
	public void deleteUploadFailure(String answerId) {
		this.deleteByCase(CorrectFailureTable.ANSWERID + " = ?",
				new String[] { answerId });
	}

	/**
	 * 查询所有
	 * 
	 * @return
	 */
	public List<CorrectFailureItem> findUploadFailuersContent() {
		return this.queryAll();
	}
}
