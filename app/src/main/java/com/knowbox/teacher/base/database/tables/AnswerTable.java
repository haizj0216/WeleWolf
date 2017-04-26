/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.base.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.BaseTable;
import com.knowbox.teacher.base.database.bean.AnswerItem;

import java.util.List;

public class AnswerTable extends BaseTable<AnswerItem> {

	public static final String TABLE_NAME = "answer";
	
	public static final String COLUMN_ANSWERID = "answerid";
	public static final String COLUMN_USERID = "userid";
	public static final String COLUMN_STUDENTID = "studentid";
	public static final String COLUMN_STUDENTNAME = "studentname";
	public static final String COLUMN_HEADPHOTO = "headphoto";
	public static final String COLUMN_SPENDTIME = "spendtime";
	public static final String COLUMN_ISCORRECT = "iscorrect";
	public static final String COLUMN_ISCOMMIT = "iscommit";
	public static final String COLUMN_ADDTIME = "addtime";
	public static final String COLUMN_PICTURE = "picture";
	public static final String COLUMN_ANSWER = "answer";
	public static final String COLUMN_SERVERID = "serverid";
	public static final String COLUMN_CORRECTPIC = "correctpic";
	public static final String COLUMN_CORRECTSTATE = "correctstate";
	public static final String COLUMN_ISPRAISE = "ispraise";
	public static final String COLUMN_ISSUGGEST = "issuggest";
	public static final String COLUMN_ANSWERTXT = "answertxt";
	public static final String COLUMN_ISRIGHT = "isright";
	
	public static final String COLUMN_HOMEWORKID = "homeworkid";
	public static final String COLUMN_QUESTIONID = "questionid";
	public static final String COLUMN_ANSWERTYPE = "answertype";
	
	public static final String COLUMN_ISCACHED = "iscached";
	public static final String COLUMN_UPLOADSTATE = "uploadmark";
	
	public static final String COLUMN_QUESTIONTRUNK = "question";
	public static final String COLUMN_ANSWEREXPAIN = "answerexpain";
	
	public AnswerTable(SQLiteOpenHelper sqlHelper) {
		super(TABLE_NAME, sqlHelper);
	}

	@Override
	public String getCreateSql() {
		String sql = "CREATE TABLE IF NOT EXISTS " +
				AnswerTable.TABLE_NAME + "(" + AnswerTable._ID
				+ " integer primary key ," + AnswerTable.COLUMN_ANSWERID
				+ " varchar," + AnswerTable.COLUMN_USERID + " varchar,"
				+ AnswerTable.COLUMN_STUDENTID + " varchar,"
				+ AnswerTable.COLUMN_STUDENTNAME + " varchar,"
				+ AnswerTable.COLUMN_HEADPHOTO + " varchar,"
				+ AnswerTable.COLUMN_SPENDTIME + " varchar,"
				+ AnswerTable.COLUMN_ISCORRECT + " varchar,"
				+ AnswerTable.COLUMN_ISCOMMIT + " varchar,"
				+ AnswerTable.COLUMN_ADDTIME + " varchar,"
				+ AnswerTable.COLUMN_PICTURE + " varchar,"
				+ AnswerTable.COLUMN_ANSWER + " varchar,"
				+ AnswerTable.COLUMN_SERVERID + " varchar,"
				+ AnswerTable.COLUMN_CORRECTPIC + " varchar,"
				+ AnswerTable.COLUMN_CORRECTSTATE + " varchar,"
				+ AnswerTable.COLUMN_ISPRAISE + " varchar,"
				+ AnswerTable.COLUMN_ISSUGGEST + " varchar,"
				+ AnswerTable.COLUMN_ANSWERTXT + " varchar,"
				+ AnswerTable.COLUMN_ISRIGHT + " varchar,"
				+ AnswerTable.COLUMN_HOMEWORKID + " varchar,"
				+ AnswerTable.COLUMN_QUESTIONID + " varchar,"
				+ AnswerTable.COLUMN_ISCACHED + " varchar,"
				+ AnswerTable.COLUMN_UPLOADSTATE + " varchar,"
				+ AnswerTable.COLUMN_ANSWERTYPE + " varchar,"
				+ AnswerTable.COLUMN_QUESTIONTRUNK + " varchar,"
				+ AnswerTable.COLUMN_ANSWEREXPAIN + " varchar" + ")";
		return sql;
	}

	@Override
	public AnswerItem getItemFromCursor(Cursor cursor) {
		AnswerItem item = new AnswerItem();
		item.answerId = getValue(cursor, COLUMN_ANSWERID, String.class);
		item.userId = getValue(cursor, COLUMN_USERID, String.class);
		item.studentId = getValue(cursor, COLUMN_STUDENTID, String.class);
		item.studenetName = getValue(cursor, COLUMN_STUDENTNAME, String.class);
		item.headPhoto = getValue(cursor, COLUMN_HEADPHOTO, String.class);
//		item.spendTime = getValue(cursor, COLUMN_SPENDTIME, String.class);
//		item.isCorrect = getValue(cursor, COLUMN_ISCORRECT, String.class);
//		item.isCommit = getValue(cursor, COLUMN_ISCOMMIT, String.class);
//		item.addTime = getValue(cursor, COLUMN_ADDTIME, String.class);
		item.answers = getValue(cursor, COLUMN_PICTURE, String.class);
//		item.answer = getValue(cursor, COLUMN_ANSWER, String.class);
//		item.serverId = getValue(cursor, COLUMN_SERVERID, String.class);
		item.correctAnswers = getValue(cursor, COLUMN_CORRECTPIC, String.class);
		item.correctScore = getValue(cursor, COLUMN_CORRECTSTATE, String.class);
		item.isPraise = getValue(cursor, COLUMN_ISPRAISE, String.class);
		item.isSuggest = getValue(cursor, COLUMN_ISSUGGEST, String.class);
		item.answerTxt = getValue(cursor, COLUMN_ANSWERTXT, String.class);
		item.isRight = "Y".equalsIgnoreCase(getValue(cursor, COLUMN_ISRIGHT, String.class));
		item.mHomeworkId = getValue(cursor, COLUMN_HOMEWORKID, String.class);
		item.mQuestionId = getValue(cursor, COLUMN_QUESTIONID, String.class);
		item.answerType = getValue(cursor, COLUMN_ANSWERTYPE, String.class);
		item.mIsCached = "Y".equalsIgnoreCase(getValue(cursor, COLUMN_ISCACHED, String.class));
		item.uploadeState = getValue(cursor, COLUMN_UPLOADSTATE, String.class);
		item.question = getValue(cursor, COLUMN_QUESTIONTRUNK, String.class);
		item.answerExplain = getValue(cursor, COLUMN_ANSWEREXPAIN, String.class);
		return item;
	}

	@Override
	public ContentValues getContentValues(AnswerItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ANSWERID, item.answerId);
		values.put(COLUMN_USERID, item.userId);
		values.put(COLUMN_STUDENTID, item.studentId);
		values.put(COLUMN_STUDENTNAME, item.studenetName);
		values.put(COLUMN_HEADPHOTO, item.headPhoto);
//		values.put(COLUMN_SPENDTIME, item.spendTime);
//		values.put(COLUMN_ISCORRECT, item.isCorrect);
//		values.put(COLUMN_ISCOMMIT, item.isCommit);
//		values.put(COLUMN_ADDTIME, item.addTime);
		values.put(COLUMN_PICTURE, item.answers);
//		values.put(COLUMN_ANSWER, item.answer);
//		values.put(COLUMN_SERVERID, item.serverId);
		values.put(COLUMN_CORRECTPIC, item.correctAnswers);
		values.put(COLUMN_CORRECTSTATE, item.correctScore);
		values.put(COLUMN_ISPRAISE, item.isPraise);
		values.put(COLUMN_ISSUGGEST, item.isSuggest);
		values.put(COLUMN_ANSWERTXT, item.answerTxt);
		values.put(COLUMN_ISRIGHT, item.isRight ? "Y" : "N");
		values.put(COLUMN_HOMEWORKID, item.mHomeworkId);
		values.put(COLUMN_QUESTIONID, item.mQuestionId);
		values.put(COLUMN_ANSWERTYPE, item.answerType);
		values.put(COLUMN_ISCACHED, item.mIsCached ? "Y" : "N");
		values.put(COLUMN_UPLOADSTATE, item.uploadeState);
		values.put(COLUMN_QUESTIONTRUNK, item.question);
		values.put(COLUMN_ANSWEREXPAIN, item.answerExplain);
		return values;
	}

	public AnswerItem queryOrInsert(String answerId, AnswerItem item){
		AnswerItem localAnswer = querySingleByCase(AnswerTable.COLUMN_ANSWERID + "= ?", 
				new String[]{answerId}, null);
		if(localAnswer == null){
			insert(item);
			return item;
		}
		return localAnswer;
	}
	
	public AnswerItem insertOrUpdate(String answerId, String studentId,
			AnswerItem item) {
		String whereCause = AnswerTable.COLUMN_ANSWERID + " = ? AND "
				+ AnswerTable.COLUMN_STUDENTID + " = ?";
		String[] whereArgs = new String[] { answerId, studentId };
		AnswerItem localAnswer = querySingleByCase(whereCause, whereArgs, null);
		if (localAnswer == null) {
			insert(item);
		} else {
			updateByCase(item, whereCause, whereArgs);
		}
		return item;
	}
	
	public int updateCacheState(String answerId, boolean isCached){
		try {
			SQLiteDatabase db = getDatabase();
			if(db == null)
				return -1;
			
			ContentValues values = new ContentValues();
			values.put(COLUMN_ISCACHED, isCached ? "Y" : "N");
			int cnt = db.update(getTableName(), values, COLUMN_ANSWERID + "= ?", new String[]{answerId});
			if(cnt > 0)
				notifyChange(getNotifyCorrectUri());
			return cnt;
		} catch (Exception e) {
			LogUtil.e(getTableName(), e);
		}
		return -1;
	}
	
	public int updateUploadState(AnswerItem answerItem){
		try {
			SQLiteDatabase db = getDatabase();
			if(db == null)
				return -1;
			ContentValues values = new ContentValues();
//			values.put(COLUMN_ISCORRECT, answerItem.isCorrect);
			values.put(COLUMN_CORRECTSTATE, answerItem.correctScore);
			values.put(COLUMN_UPLOADSTATE, answerItem.uploadeState);
			values.put(COLUMN_CORRECTPIC, answerItem.correctAnswers);
			int cnt = db.update(getTableName(), values, COLUMN_ANSWERID + "= ?", new String[]{answerItem.answerId});
			if(cnt > 0)
				notifyChange(getNotifyUploadStateUri());
			return cnt;
		} catch (Exception e) {
			LogUtil.e(getTableName(), e);
		}
		return -1;
	}
	
	public int getAnswerCorrectedCnt(String homeworkId, String questionId){
		String where = COLUMN_HOMEWORKID + " = ? And " + COLUMN_QUESTIONID
				+ " = ? And " + COLUMN_CORRECTSTATE + " not in (-1,-2)";
		List<AnswerItem> items = queryByCase(where, new String[]{homeworkId, questionId}, null);
		if(items == null)
			return 0;
		return items.size();
	}
	
	public int getAnswerCorrectedCnt(String homeworkId){
		String where = COLUMN_HOMEWORKID + " = ?";
		List<AnswerItem> items = queryByCase(where, new String[]{homeworkId}, null);
		if(items == null)
			return 0;
		return items.size();
	}
	
	public static Uri getNotifyUploadStateUri(){
		Uri uri = Uri.parse(UserTable.SCHEMA + "com.knowbox.wb.provider.providers.update_" + AnswerTable.TABLE_NAME
				+ "/uploadstate");
		return uri;
	}
	
	public Uri getNotifyCorrectUri(){
		Uri uri = Uri.parse(UserTable.SCHEMA + "com.knowbox.wb.provider.providers.update_" + getTableName()
				+ "/correcturi");
		return uri;
	}
	
	public Uri getNotifyRawPicUri(){
		Uri uri = Uri.parse(UserTable.SCHEMA + "com.knowbox.wb.provider.providers.update_" + getTableName()
				+ "/rawpic");
		return uri;
	}
}
