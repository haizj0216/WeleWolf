package com.buang.welewolf.base.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.buang.welewolf.base.database.bean.ClassInfoItem;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.BaseTable;

import java.util.List;

/**
 * 获得班级信息表
 * @author yangzc
 *
 */
public class ClassInfoTable extends BaseTable<ClassInfoItem> {

	/**
	 * 表名称
	 */
	public static final String TABLE_NAME = "HOME_CLASS_TABLE";

	/**
	 * 字段：学科、班级、班级id、班级代码、课程id、学生数量、****状态、是否更新
	 */
	public static final String CLASSID = "classid";
	public static final String CLASSNAME = "classname";
	public static final String CLASSCODE = "classcode";
	public static final String SUBJECT = "subject";
	public static final String COURSEID = "courseid";
	public static final String STUDENTNUM = "studentNum";
	public static final String STATE = "state";
	public static final String GROUPID = "groupId";
	public static final String GRADE = "grade";
	public static final String CREATETIME = "createTime";
	public static final String HEADPHOTO = "headPhoto";

	public ClassInfoTable(SQLiteOpenHelper sqlHelper) {
		super(TABLE_NAME, sqlHelper);
	}

	@Override
	public String getCreateSql() {
		String sql = "CREATE TABLE IF NOT EXISTS " +
				ClassInfoTable.TABLE_NAME + "(" + ClassInfoTable._ID
				+ " integer primary key ," + ClassInfoTable.CLASSID
				+ " varchar," + ClassInfoTable.CLASSCODE + " varchar,"
				+ ClassInfoTable.SUBJECT + " varchar,"
				+ ClassInfoTable.COURSEID + " varchar,"
				+ ClassInfoTable.STUDENTNUM + " integer,"
				+ ClassInfoTable.STATE + " integer,"
				+ ClassInfoTable.GROUPID + " varchar,"
				+ ClassInfoTable.GRADE + " varchar,"
				+ ClassInfoTable.CREATETIME + " varchar,"
				+ ClassInfoTable.HEADPHOTO + " varchar,"
				+ ClassInfoTable.CLASSNAME + " varchar" + ")";
		return sql;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public ClassInfoItem getItemFromCursor(Cursor cursor) {
		ClassInfoItem classInfoItem = new ClassInfoItem();

		classInfoItem.classId = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.CLASSID));
		classInfoItem.className = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.CLASSNAME));
		classInfoItem.classCode = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.CLASSCODE));
		classInfoItem.subject = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.SUBJECT));
		classInfoItem.courseId = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.COURSEID));
		classInfoItem.studentNum = cursor.getInt(cursor
				.getColumnIndexOrThrow(ClassInfoTable.STUDENTNUM));
		classInfoItem.state = cursor.getInt(cursor
				.getColumnIndexOrThrow(ClassInfoTable.STATE));
		classInfoItem.groupId = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.GROUPID));
		classInfoItem.grade = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.GRADE));
		classInfoItem.createTime = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.CREATETIME));
		classInfoItem.mHeadPhoto = cursor.getString(cursor
				.getColumnIndexOrThrow(ClassInfoTable.HEADPHOTO));
		return classInfoItem;
	}

	@Override
	public ContentValues getContentValues(ClassInfoItem item) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ClassInfoTable.CLASSID, item.classId);
		contentValues.put(ClassInfoTable.CLASSNAME, item.className);
		contentValues.put(ClassInfoTable.CLASSCODE, item.classCode);
		contentValues.put(ClassInfoTable.SUBJECT, item.subject);
		contentValues.put(ClassInfoTable.COURSEID, item.courseId);
		contentValues.put(ClassInfoTable.STUDENTNUM, item.studentNum);
		contentValues.put(ClassInfoTable.STATE, item.state);
		contentValues.put(ClassInfoTable.GROUPID, item.groupId);
		contentValues.put(ClassInfoTable.GRADE, item.grade);
		contentValues.put(ClassInfoTable.CREATETIME, item.createTime);
		contentValues.put(ClassInfoTable.HEADPHOTO, item.mHeadPhoto);
		return contentValues;
	}

	/**
	 * 插入一个新班级
	 * 
	 * @param info
	 * @return
	 */
	public boolean insertNewClassName(ClassInfoItem info) {
		if (info == null) {
			return false;
		}
		boolean result = this.insert(info) == -1 ? false : true;
		notifyDataChange();
		return result;
	}

	/**
	 * 批量插入班级
	 * 
	 * @param infos
	 */
	public void insertTransactClassInfos(List<ClassInfoItem> infos) {
		if (infos == null) {
			return;
		}
		// 先清空原有表，在添加来自网络的数据
		deleteByCase(null, null);
		for (int i = 0; i < infos.size(); i++) {
			ClassInfoItem info = infos.get(i);
			if(info != null)
				insert(info);
		}
	}
	
	/**
	 * 删除一个班级
	 * 
	 * @param info
	 */
	public void deleteItemClassInfo(ClassInfoItem info) {
		if (info == null) {
			return;
		}
		deleteByCase(ClassInfoTable.CLASSID + " = ? ",
				new String[] { info.classId });

		notifyDataChange();
	}

	/**
	 * 批量删除班级
	 * @param items
	 */
	public void deleteAll(List<ClassInfoItem> items) {
		if (null == items) {
			return;
		}
		try {
			for (int i = 0; i < items.size(); i++) {
				ClassInfoItem classInfoItem = items.get(i);
				if (null != classInfoItem)
					deleteItemClassInfo(classInfoItem);
			}
		}catch (Exception e){
			LogUtil.e(getTableName(), e);
		}
	}

	/**
	 * 查询所有班级信息
	 * 
	 * @param homeworkClass
	 * @return
	 */
	public List<ClassInfoItem> findAllClassInfos(String homeworkClass) {
		return queryAll();
	}

	/**
	 * 根据GroupId查询班级信息
	 * @param groupId
	 * @return
	 */
	public ClassInfoItem findClassInfoByGroupId(String groupId){
		return querySingleByCase(ClassInfoTable.GROUPID + " = ? ", new String[]{groupId}, null);
	}

	/**
	 * 根据GroupId查询班级信息
	 * @param groupId
	 * @return
	 */
	public ClassInfoItem findClassInfoByClassId(String groupId){
		return querySingleByCase(ClassInfoTable.CLASSID + " = ? ", new String[]{groupId}, null);
	}

	/**
	 * 更新班级信息
	 * @param item
	 * @param key
	 * @param value
	 */
	public void updateClassInfo(ClassInfoItem item, String key, String value) {
		String where = key + " = ?";
		String [] wheres = new String[]{value};
		updateByCase(item, where, wheres);
	}

	public void updateClassInfo(ClassInfoItem item) {
		if (null == item) return;
		updateByCase(item, ClassInfoTable.CLASSID + " = ?", new String[]{item.classId});
	}
}
