package com.knowbox.teacher.modules.homework.services;

/**
 * Created by weilei on 16/2/2.
 */
public interface HomeworkService {

    public static final String SERVICE_NAME = "com.knowbox.wb_homeworkservice";

    public void resetHomework(String homeworkID);

    public void deleteHomework(String homeworkID);

    public void correctHomework(String homeworkID, boolean mNeedCorrect);

    public HomeworkServiceObserver getObserver();

}
