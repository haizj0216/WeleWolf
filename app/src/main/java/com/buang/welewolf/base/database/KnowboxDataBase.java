package com.buang.welewolf.base.database;

import android.content.Context;

import com.buang.welewolf.base.database.tables.AnswerTable;
import com.buang.welewolf.base.database.tables.ClassInfoTable;
import com.buang.welewolf.base.database.tables.CorrectFailureTable;
import com.buang.welewolf.base.database.tables.QuestionTable;
import com.buang.welewolf.base.database.tables.UserTable;
import com.hyena.framework.database.BaseDataBaseHelper;
import com.hyena.framework.database.DataBaseHelper;
import com.hyena.framework.utils.BaseApp;

/**
 * 知识Box默认数据库
 *
 * @author yangzc
 * @version 1.0
 * @createTime 2014年11月29日 下午6:17:55
 */
public class KnowboxDataBase extends BaseDataBaseHelper {

    public static final String DATABASE_NAME = "knowbox.db";
    public static final int DATABASE_VERSION = 1;
    Context mContext;
    DataBaseHelper mDataBaseHelper;

    public KnowboxDataBase() {
        super(BaseApp.getAppContext(), DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public void initTablesImpl(DataBaseHelper db) {
        mDataBaseHelper = db;
        addTable(AnswerTable.class, new AnswerTable(db));//答案内容表
//        addTable(ClassInfoTable.class, new ClassInfoTable(db));//班级信息表
        addTable(CorrectFailureTable.class, new ClassInfoTable(db));//上传失败表
        addTable(QuestionTable.class, new QuestionTable(db));//题目信息表
        addTable(UserTable.class, new UserTable(db));//用户表
    }
}
