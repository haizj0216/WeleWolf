package com.buang.welewolf.modules.message.bean;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;

/**
 * Created by weilei on 17/5/22.
 */
@MessageTag(value = "app:customize", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class CustomizeMessage extends MessageContent {
    public String content;

    //给消息赋值。
    public CustomizeMessage(Parcel in) {
        //这里可继续增加你消息的属性
    }

    public CustomizeMessage(byte[] data) {
        String jsonStr = null;
        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);

            if (jsonObj.has("content"))
                content = jsonObj.optString("content");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", "这是一条消息内容");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<CustomizeMessage> CREATOR = new Creator<CustomizeMessage>() {

        @Override
        public CustomizeMessage createFromParcel(Parcel source) {
            return new CustomizeMessage(source);
        }

        @Override
        public CustomizeMessage[] newArray(int size) {
            return new CustomizeMessage[size];
        }
    };
}
