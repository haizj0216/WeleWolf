package com.knowbox.teacher.modules.utils;

import android.app.Activity;
import android.content.Context;

import com.knowbox.teacher.base.bean.OnlineTeachingMaterialInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by weilei on 15/12/30.
 */
public class MaterialUtils {

    private final String FILENAME = "material.out";
    private static MaterialUtils mInstance;
    private Activity mActivity;
    private OnlineTeachingMaterialInfo.ChooseItem mChooseItem;

    public static MaterialUtils getInstance(Activity activity) {
        if (mInstance == null) {
            mInstance = new MaterialUtils(activity);
        }
        return mInstance;
    }

    public OnlineTeachingMaterialInfo.ChooseItem getChooseItem() {
        if (mChooseItem == null) {
            mChooseItem = readObject();
        }
        return mChooseItem;
    }

    public MaterialUtils(Activity activity) {
        mActivity = activity;
    }

    public void writeObject(OnlineTeachingMaterialInfo.ChooseItem td) {
        try {
            FileOutputStream stream = mActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(stream);
            oos.writeObject(td);//td is an Instance of TableData;
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mChooseItem = td;
    }

    private OnlineTeachingMaterialInfo.ChooseItem readObject() {
        OnlineTeachingMaterialInfo.ChooseItem item = null;
        FileInputStream fis = null;   //获得输入流
        try {
            fis = mActivity.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            item = (OnlineTeachingMaterialInfo.ChooseItem) ois.readObject();
            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return item;
    }


}
