/**
 * Copyright (C) 2014 The KnowBoxTeacher2.0 Project
 */
package com.knowbox.teacher.base.database.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.database.BaseTable;
import com.knowbox.teacher.base.bean.OnlineQuestionListInfo;
import com.knowbox.teacher.base.database.bean.QuestionItem;

import java.util.ArrayList;
import java.util.List;

public class QuestionTable extends BaseTable<QuestionItem> {

	public static final String TABLE_NAME = "question";

	public static final String COLUMN_QUESTIONTYPE = "questiontype";
	public static final String COLUMN_QUESTIONID = "questionid";
	public static final String COLUMN_INDEX = "questionIndex";
	public static final String COLUMN_HOMEWORKID = "homeworkid";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_RIGHTRATE = "rightrate";
	public static final String COLUMN_PARENTQUESTIONID = "parentid";
	public static final String COLUMN_ISCOLLECT = "isCollect";
	public static final String COLUMN_RIGHTANSWER = "rightAnser";
	public static final String COLUMN_EXPLAINANSWER = "explainAnswer";
	public static final String COLUMN_KOWNLEDGE = "kownledge";
	public static final String COLUMN_SECTION = "section";
	public static final String COLUMN_CONTENT_SOURCE = "content_source";
	public static final String COLUMN_ISOUT = "isOut";
	public static final String COLUMN_DIFFICULTY = "mDifficulty";
	public static final String COLUMN_HOT = "mHot";
	public static final String COLUMN_SOURCEID = "sourceid";
	public static final String COLUMN_SOURCETYPE = "sourceType";
	public static final String COLUMN_FROMTYPE = "fromType";
	public static final String COLUMN_ISSPECIAL = "isSpecial";
	public static final String COLUMN_WELLCHOSEN = "wellChosen";
	public static final String COLUMN_CATEGORY = "mCategory";
	public static final String COLUMN_TAG = "tag";
	public static final String COLUMN_GRADE = "grade";

	public QuestionTable(SQLiteOpenHelper sqlHelper) {
		super(TABLE_NAME, sqlHelper);
	}

	@Override
	public String getCreateSql() {
		String sql = "CREATE TABLE IF NOT EXISTS " +
				QuestionTable.TABLE_NAME + "(" + QuestionTable._ID
				+ " integer primary key ," + QuestionTable.COLUMN_QUESTIONTYPE
				+ " integer," + QuestionTable.COLUMN_QUESTIONID + " varchar unique,"
				+ QuestionTable.COLUMN_HOMEWORKID + " varchar,"
				+ QuestionTable.COLUMN_INDEX + " varchar,"
				+ QuestionTable.COLUMN_CONTENT + " varchar,"
				+ QuestionTable.COLUMN_RIGHTRATE + " real,"
				+ QuestionTable.COLUMN_ISCOLLECT + " integer,"
				+ QuestionTable.COLUMN_EXPLAINANSWER + " varchar,"
				+ QuestionTable.COLUMN_KOWNLEDGE + " varchar,"
				+ QuestionTable.COLUMN_RIGHTANSWER + " varchar,"
				+ QuestionTable.COLUMN_SECTION + " varchar,"
				+ QuestionTable.COLUMN_CONTENT_SOURCE + " varchar,"
				+ QuestionTable.COLUMN_SOURCEID + " varchar,"
				+ QuestionTable.COLUMN_CATEGORY + " varchar,"
				+ QuestionTable.COLUMN_TAG + " varchar,"
				+ QuestionTable.COLUMN_GRADE + " varchar,"
				+ QuestionTable.COLUMN_DIFFICULTY + " integer,"
				+ QuestionTable.COLUMN_ISOUT + " integer,"
				+ QuestionTable.COLUMN_SOURCETYPE + " integer,"
				+ QuestionTable.COLUMN_FROMTYPE + " integer,"
				+ QuestionTable.COLUMN_ISSPECIAL + " integer,"
				+ QuestionTable.COLUMN_WELLCHOSEN + " integer,"
				+ QuestionTable.COLUMN_HOT + " real,"
				+ QuestionTable.COLUMN_PARENTQUESTIONID + " varchar" + ")";
		return sql;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
	}

	@Override
	public QuestionItem getItemFromCursor(Cursor cursor) {
		QuestionItem item = new QuestionItem();
		item.mQuestionType = getValue(cursor, COLUMN_QUESTIONTYPE,
				Integer.class);
		item.mQuestionId = getValue(cursor, COLUMN_QUESTIONID, String.class);
		item.mQuestionIndex = getValue(cursor, COLUMN_INDEX, String.class);
		item.mHomeworkId = getValue(cursor, COLUMN_HOMEWORKID, String.class);
		item.mContent = getValue(cursor, COLUMN_CONTENT, String.class);
		item.mRightRate = getValue(cursor, COLUMN_RIGHTRATE, Float.class);
		item.mParentQuestionId = getValue(cursor, COLUMN_PARENTQUESTIONID,
				String.class);
		item.mIsCollect = getValue(cursor, COLUMN_ISCOLLECT, Integer.class) == 1 ? true:false;
		item.mAnswerExplain = getValue(cursor, COLUMN_EXPLAINANSWER, String.class);
		item.mRightAnswer = getValue(cursor, COLUMN_RIGHTANSWER, String.class);
		String knowledgeName = getValue(cursor, COLUMN_KOWNLEDGE, String.class);
		if (knowledgeName != null) {
			String knows[] = knowledgeName.split("!&");
			if (knows != null) {
				item.mKnowledgeName = new ArrayList<String>();
				for (String string : knows) {
					if(!TextUtils.isEmpty(string))
						item.mKnowledgeName.add(string);
				}
			}
		}
		item.mSectionName = getValue(cursor, COLUMN_SECTION, String.class);
		item.mContentSource = getValue(cursor, COLUMN_CONTENT_SOURCE, String.class);
		item.mIsOut = getValue(cursor, COLUMN_ISOUT, Integer.class) == 1 ? true : false;
		item.mDifficulty = getValue(cursor, COLUMN_DIFFICULTY, Integer.class);
		item.mHot = getValue(cursor, COLUMN_HOT, Long.class);
		item.mSourceId = getValue(cursor, COLUMN_SOURCEID, String.class);
		item.mQuestionSourceType = getValue(cursor, COLUMN_SOURCETYPE, Integer.class);
		item.mFromType = getValue(cursor, COLUMN_FROMTYPE, Integer.class);
		item.isSpecial = getValue(cursor, COLUMN_ISSPECIAL, Integer.class) == 1;
		item.wellChosen = getValue(cursor, COLUMN_WELLCHOSEN, Integer.class) == 1;
		item.mCategory = getValue(cursor, COLUMN_CATEGORY, String.class);
		item.mTag = getValue(cursor, COLUMN_TAG, String.class);
		item.mChineseGrade = getValue(cursor, COLUMN_GRADE, String.class);
		return item;
	}

	@Override
	public ContentValues getContentValues(QuestionItem item) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_QUESTIONTYPE, item.mQuestionType);
		values.put(COLUMN_QUESTIONID, item.mQuestionId);
		values.put(COLUMN_INDEX, item.mQuestionIndex);
		values.put(COLUMN_HOMEWORKID, item.mHomeworkId);
		values.put(COLUMN_CONTENT, item.mContent);
		values.put(COLUMN_RIGHTRATE, item.mRightRate);
		values.put(COLUMN_PARENTQUESTIONID, item.mParentQuestionId);
		values.put(COLUMN_ISCOLLECT, item.mIsCollect ? 1 : 0);
		values.put(COLUMN_EXPLAINANSWER, item.mAnswerExplain);
		StringBuffer buf = new StringBuffer();
		if(item.mKnowledgeName != null){
			for (String string : item.mKnowledgeName) {
				buf.append(string).append("!&");
			}
		}
		values.put(COLUMN_KOWNLEDGE, buf.toString());
		values.put(COLUMN_RIGHTANSWER, item.mRightAnswer);
		values.put(COLUMN_SECTION, item.mSectionName);
		values.put(COLUMN_CONTENT_SOURCE, item.mContentSource);
		values.put(COLUMN_HOT, item.mHot);
		values.put(COLUMN_DIFFICULTY, item.mDifficulty);
		values.put(COLUMN_ISOUT, item.mIsOut ? 1 : 0);
		values.put(COLUMN_SOURCEID, item.mSourceId);
		values.put(COLUMN_FROMTYPE, item.mFromType);
		values.put(COLUMN_SOURCETYPE, item.mQuestionSourceType);
		values.put(COLUMN_ISSPECIAL, item.isSpecial ? 1 : 0);
		values.put(COLUMN_WELLCHOSEN, item.wellChosen ? 1 : 0);
		values.put(COLUMN_CATEGORY, item.mCategory);
		values.put(COLUMN_TAG, item.mTag);
		values.put(COLUMN_GRADE, item.mChineseGrade);
		return values;
	}
	
	/**
	 * 插入数据库
	 * @author weilei
	 *
	 * @param item
	 */
	public void insertQuestion(QuestionItem item) {
		insert(item);
	}
	
	public void insertOrupdateQuestion(QuestionItem item) {
		String where = COLUMN_QUESTIONID + " = ?";
		String whereargs[] = new String[] {item.mQuestionId};
		List<QuestionItem> questions = queryByCase(where, whereargs, null);
		if(questions != null && questions.size() > 0) {
			updateByCase(item, where, whereargs);
		} else {
			insert(item);
		}
		
	}
	
	public void updateCollectQuestion(List<QuestionItem> mItems) {
	}
	
	public boolean isCollect(QuestionItem item){
		List<QuestionItem> questions = queryByCase(COLUMN_QUESTIONID + " = ?",
				new String[] {item.mQuestionId}, null);
		if(questions.isEmpty()) {
			return false;
		}
		QuestionItem queryItem = questions.get(0);
		
		return queryItem.mIsCollect;
	}
	
	/**
	 * 检索已收藏的题目列表
	 * @author weilei
	 *
	 * @return
	 */
	public List<QuestionItem> getQuestionCollected() {
		List<QuestionItem> questions = queryByCase(COLUMN_ISCOLLECT + " = ?",
				new String[] {String.valueOf(true)}, null);
		return questions;
	
	}
	
	/**
	 * 保存题目
	 * @param homeworkId
	 * @param questions
	 */
	public void insertQuestions(String homeworkId, List<QuestionItem> questions) {
		if(questions == null){
			return;
		}
		SQLiteDatabase db = getDatabase();
		try {
			if(querySingleByCase(COLUMN_HOMEWORKID + " = ?", new String[]{homeworkId}, null) != null)
				return;
//			deleteByCase(COLUMN_HOMEWORKID + "=?", new String[]{homeworkId});
			
			db.beginTransaction();
			//删除以前的数据
			for(int i = 0; i< questions.size(); i++){
				QuestionItem question = questions.get(i);
				if (question != null) {
					question.mParentQuestionId = "root";
					db.insert(TABLE_NAME, null, getContentValues(question));
					if (question.mSubQuestions != null) {
						for (int j = 0; j < question.mSubQuestions.size(); j++) {
							QuestionItem subQuestion = question.mSubQuestions
									.get(j);
							subQuestion.mParentQuestionId = question.mQuestionId;
							db.insert(TABLE_NAME, null,
									getContentValues(subQuestion));
						}
					}
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			LogUtil.e(getTableName(), e);
			
		} finally{
			try {
				db.endTransaction();
			} catch (Exception e2) {
			}
		}
		
	}

	/**
	 * 通过作业获得所有题目
	 * @param homeworkId
	 * @return
	 */
	public List<QuestionItem> getQuestionByHomeworkId(String homeworkId) {
		List<QuestionItem> questions = queryByCase(COLUMN_HOMEWORKID + " = ? and " + COLUMN_PARENTQUESTIONID + "= ?",
				new String[] {homeworkId, "root"}, null);
		if(questions != null){
			for(int i=0; i< questions.size(); i++){
				QuestionItem question = questions.get(i);
				switch (question.mQuestionType) {
				case OnlineQuestionListInfo.TYPE_COMPREHENISON:
				case OnlineQuestionListInfo.TYPE_CLOZE:
				case OnlineQuestionListInfo.TYPE_FILLIN:
				case OnlineQuestionListInfo.TYPE_RATIONAL:
				{
					List<QuestionItem> subQuestion = queryByCase(COLUMN_PARENTQUESTIONID + " = ?",
							new String[] { question.mQuestionId }, null);
					question.mSubQuestions = subQuestion;
				}
				default:
					break;
				}
			}
		}
		return questions;
	}

	public void insertQuestions(List<QuestionItem> questions) {
		if (questions == null) {
			return;
		}
		try {
			//删除以前的数据
			for (int i = 0; i < questions.size(); i++) {
				QuestionItem question = questions.get(i);
				if (question != null) {
					insertOrupdateQuestion(question);
				}
			}
		} catch (Exception e) {
			LogUtil.e(getTableName(), e);
		} finally {
		}
	}

	public void deleteQuestions(List<QuestionItem> questions) {
		if (questions == null) {
			return;
		}
		try {
			//删除以前的数据
			for (int i = 0; i < questions.size(); i++) {
				QuestionItem question = questions.get(i);
				if (question != null) {
					deleteQuestion(question);
				}
			}
		} catch (Exception e) {
			LogUtil.e(getTableName(), e);
		} finally {
		}
	}

	public void deleteQuestion(QuestionItem item) {
		String where = COLUMN_QUESTIONID + " = ?";
		String whereargs[] = new String[]{item.mQuestionId};
		deleteByCase(where, whereargs);
	}



	public void clearQuestions() {
		deleteByCase(null, null);
	}
}
