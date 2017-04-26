package com.knowbox.teacher.modules.homework.services;

/**
 * Created by weilei on 16/2/2.
 */
public class HomeworkServiceImp implements HomeworkService {
    private HomeworkServiceObserver mObserver = new HomeworkServiceObserver();

    @Override
    public void resetHomework(String homeworkID) {
        if (mObserver != null) {
            mObserver.notifyHomeworkReset(homeworkID);
        }
    }

    @Override
    public void deleteHomework(String homeworkID) {
        if (mObserver != null) {
            mObserver.notifyHomeworkDelete(homeworkID);
        }
    }

    @Override
    public void correctHomework(String homeworkID, boolean mNeedCorrect) {
        if (mObserver != null) {
            mObserver.notifyHomeworkCorrect(homeworkID, mNeedCorrect);
        }
    }

    @Override
    public HomeworkServiceObserver getObserver() {
        return mObserver;
    }
}
