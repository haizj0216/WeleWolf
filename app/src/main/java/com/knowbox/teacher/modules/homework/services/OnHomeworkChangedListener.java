package com.knowbox.teacher.modules.homework.services;

/**
 * Created by weilei on 16/2/2.
 */
public interface OnHomeworkChangedListener {
    public void onHomeworkReset(String homeworkID);

    public void onHomeworkDelete(String homeworkID);

    public void onHomeworkCorrect(String homeworkID, boolean isCorrect);
}
