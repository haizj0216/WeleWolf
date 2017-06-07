package com.buang.welewolf.base.services.upload;

import java.io.File;
import java.util.UUID;

/**
 * Created by weilei on 17/6/7.
 */

public class UploadTask {

    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_RECORDER = 2;
    private String taskId = "";
    private int type = 1;
    public String filePath;
    public byte[] buf;

    public UploadTask(int type, String filePath) {
        this.filePath = filePath;
        this.taskId = UUID.randomUUID().toString();
    }

    public UploadTask(int type, byte[] buf) {
        this.buf = buf;
        this.taskId = UUID.randomUUID().toString();
    }

    public String getTaskId() {
        return this.taskId;
    }

    public int getType() {
        return this.type;
    }

    public boolean isEmpty() {
        return (this.filePath == null || !(new File(this.filePath)).exists()) && this.buf == null;
    }
}
