/**
 * Copyright (C) 2014 The knowbox_Teacher Project
 */
package com.knowbox.teacher.base.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hyena.framework.database.BaseTable;
import com.knowbox.teacher.base.database.bean.UserItem;

import java.util.List;

/**
 * @author yangzc
 * @version 1.0
 * @createTime 2014年12月1日 下午12:01:12 用户表
 */
public class UserTable extends BaseTable<UserItem> {

	public static final String USERID = "USERID";
	public static final String LOGINNAME = "LOGINNAME";
	public static final String USERNAME = "USERNAME";
	public static final String SUBJECT = "SUBJECT";
	public static final String SUBJECTCODE = "SUBJECTCODE";
	public static final String PASSWORD = "PASSWORD";
	public static final String SCHOOL = "SCHOOL";
	public static final String SCHOOLNAME = "SCHOOLNAME";
	public static final String TOKEN = "TOKEN";
	public static final String HEADPHOTO = "HEADPHOTO";
	public static final String BIRTHDAY = "BIRTHDAY";
	public static final String SEX = "SEX";
	public static final String GRADE = "GRADE";
	public static final String AUTHSTATUS = "AUTHSTATUS";
	public static final String REALNAME = "REALNAME";
	public static final String DOCUMNUMBER = "DOCUMNUMBER";
	public static final String AUTHIMAGE = "AUTHIMAGE";
	public static final String AUTHERROR = "AUTHERROR";
	public static final String IDCARD = "IDCARD";
	public static final String CERTTIME = "CERTTIME";
	public static final String EDITIONID = "EDITIONID";
	public static final String EDITIONNAME = "EDITIONNAME";
	public static final String BOOKID = "BOOKID";
	public static final String BOOKNAME = "BOOKNAME";

	/** 增加用户表 */
	public static final String TABLE_NAME = "HOME_USER_TABLE";

	/**
	 * @param sqlHelper
	 */
	public UserTable(SQLiteOpenHelper sqlHelper) {
		super(TABLE_NAME, sqlHelper);
	}

	@Override
	public String getCreateSql() {
		String sql = "CREATE TABLE IF NOT EXISTS " + UserTable.TABLE_NAME + "("
				+ UserTable._ID + " integer primary key ," + UserTable.USERID
				+ " varchar," + UserTable.LOGINNAME + " varchar,"
				+ UserTable.SUBJECT + " varchar," + UserTable.SUBJECTCODE
				+ " varchar," + UserTable.TOKEN + " varchar,"
				+ UserTable.USERNAME + " varchar," + UserTable.SCHOOL
				+ " varchar," + UserTable.SCHOOLNAME + " varchar," + UserTable.HEADPHOTO + " varchar,"
				+ UserTable.PASSWORD + " varchar," + UserTable.BIRTHDAY
				+ " varchar," + UserTable.GRADE + " varchar," + UserTable.SEX
				+ " varchar," + UserTable.AUTHERROR + " varchar," + UserTable.AUTHIMAGE
				+ " varchar," + UserTable.AUTHSTATUS + " varchar," + UserTable.DOCUMNUMBER
				+ " varchar," + UserTable.REALNAME + " varchar," + UserTable.IDCARD
				+ " varchar," + UserTable.CERTTIME + " varchar," + UserTable.EDITIONID
				+ " varchar," + UserTable.EDITIONNAME + " varchar," + UserTable.BOOKID
				+ " varchar," + UserTable.BOOKNAME
				+ " integer)";
		return sql;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public UserItem getItemFromCursor(Cursor cursor) {
		String userId = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.USERID));
		String username = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.USERNAME));
		String loginname = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.LOGINNAME));
		String subject = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.SUBJECT));
		String subjectCode = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.SUBJECTCODE));
		String school = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.SCHOOL));
		String token = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.TOKEN));
		String hPhoto = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.HEADPHOTO));
		String birthday = cursor.getString(cursor
				.getColumnIndex(UserTable.BIRTHDAY));
		String sex = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.SEX));
		String grade = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.GRADE));
		String authError = cursor.getString(cursor
				.getColumnIndex(UserTable.AUTHERROR));
		String authImage = cursor.getString(cursor
				.getColumnIndex(UserTable.AUTHIMAGE));
		String authStatus = cursor.getString(cursor
				.getColumnIndex(UserTable.AUTHSTATUS));
		String documCode = cursor.getString(cursor
				.getColumnIndex(UserTable.DOCUMNUMBER));
		String readName = cursor.getString(cursor
				.getColumnIndex(UserTable.REALNAME));
		String idCard = cursor.getString(cursor
				.getColumnIndex(UserTable.IDCARD));
		String certTime = cursor.getString(cursor
				.getColumnIndex(UserTable.CERTTIME));
		String jiaoCaiId = cursor.getString(cursor
				.getColumnIndex(UserTable.BOOKID));
		String teachingName = cursor.getString(cursor
				.getColumnIndex(UserTable.EDITIONNAME));
		String teachingID = cursor.getString(cursor
				.getColumnIndex(UserTable.EDITIONID));
		String jiaoCaiName = cursor.getString(cursor
				.getColumnIndex(UserTable.BOOKNAME));
		String schoolName = cursor.getString(cursor
				.getColumnIndexOrThrow(UserTable.SCHOOLNAME));

		UserItem user = new UserItem();
		user.userId = userId;
		user.loginName = loginname;
		user.subjectCode = subjectCode;
		user.userName = username;
		user.school = school;
		user.token = token;
		user.headPhoto = hPhoto;
		user.birthday = birthday;
		user.sex = sex;
		user.gradePart = grade;
		user.subject = subject;
		user.authImage = authImage;
		user.authError = authError;
		user.authenticationStatus = authStatus;
		user.realName = readName;
		user.documentNumber = documCode;
		user.idCard = idCard;
		user.mCertTime = certTime;
		user.mEditionId = teachingID;
		user.mEditionName = teachingName;
		user.mBookName = jiaoCaiName;
		user.mBookId = jiaoCaiId;
		user.schoolName = schoolName;
		return user;
	}

	@Override
	public ContentValues getContentValues(UserItem item) {
		ContentValues values = new ContentValues();
		values.put(UserTable.USERID, item.userId);
		values.put(UserTable.LOGINNAME, item.loginName);
		values.put(UserTable.USERNAME, item.userName);
		values.put(UserTable.SUBJECTCODE, item.subjectCode);
		values.put(UserTable.SCHOOL, item.school);
		values.put(UserTable.PASSWORD, item.password);
		values.put(UserTable.TOKEN, item.token);
		values.put(UserTable.HEADPHOTO, item.headPhoto);
		values.put(UserTable.BIRTHDAY, item.birthday);
		values.put(UserTable.SEX, item.sex);
		values.put(UserTable.GRADE, item.gradePart);
		values.put(UserTable.SUBJECT, item.subject);
		values.put(UserTable.AUTHERROR, item.authError);
		values.put(UserTable.AUTHSTATUS, item.authenticationStatus);
		values.put(UserTable.AUTHIMAGE, item.authImage);
		values.put(UserTable.REALNAME, item.realName);
		values.put(UserTable.DOCUMNUMBER, item.documentNumber);
		values.put(UserTable.IDCARD, item.idCard);
		values.put(UserTable.CERTTIME, item.mCertTime);
		values.put(UserTable.BOOKID, item.mBookId);
		values.put(UserTable.BOOKNAME, item.mBookName);
		values.put(UserTable.EDITIONID, item.mEditionId);
		values.put(UserTable.EDITIONNAME, item.mEditionName);
		values.put(UserTable.SCHOOLNAME,item.schoolName);
		return values;
	}

	/**
	 * 添加用户
	 * 
	 * @param user
	 */
	public void addUserInfo(UserItem user) {
		if (user != null) {
			deleteByCase(null, null);
			insert(user);
		}
	}

	/**
	 * 更新用户
	 * 
	 * @param user
	 */
	public void updateCurrentUserInfo(UserItem user) {
		if (user != null) {
			updateByCase(user, UserTable.USERID + " = ? ",
					new String[] { user.userId });
			notifyDataChange();
		}
	}

	/**
	 * 查询当前用户
	 * 
	 * @param token
	 * @return
	 */
	public UserItem findCurrentUser(String token) {
		if (token != null) {
			List<UserItem> userList = queryByCase("token = ?",
					new String[] { token }, null);
			if (userList != null && userList.size() > 0) {
				return userList.get(0);
			}
		}
		return null;
	}
}
