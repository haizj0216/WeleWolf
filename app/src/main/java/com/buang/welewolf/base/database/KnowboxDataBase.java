package com.buang.welewolf.base.database;

import android.content.Context;

import com.buang.welewolf.base.database.tables.CorrectFailureTable;
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
        addTable(UserTable.class, new UserTable(db));//用户表
    }
}
