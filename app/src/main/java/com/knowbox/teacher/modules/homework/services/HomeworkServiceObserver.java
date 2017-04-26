package com.knowbox.teacher.modules.homework.services;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weilei on 16/2/2.
 */
public class HomeworkServiceObserver {

    private List<OnHomeworkChangedListener> homeworkChangedListeners = new ArrayList<OnHomeworkChangedListener>();

    public void addHomeworkChangedListener(OnHomeworkChangedListener listener) {
        if (homeworkChangedListeners != null && !homeworkChangedListeners.contains(listener)) {
            homeworkChangedListeners.add(listener);
        }
    }

    public void removeHomeworkChangedListener(OnHomeworkChangedListener listener) {
        if (homeworkChangedListeners != null && homeworkChangedListeners.contains(listener)) {
            homeworkChangedListeners.remove(listener);
        }
    }

    public void notifyHomeworkReset(String homeworkId) {
        if (homeworkChangedListeners != null) {
            for (int i = 0; i < homeworkChangedListeners.size(); i++) {
                homeworkChangedListeners.get(i).onHomeworkReset(homeworkId);
            }
        }
    }

    public void notifyHomeworkCorrect(String homeworkId, boolean isCorrect) {
        if (homeworkChangedListeners != null) {
            for (int i = 0; i < homeworkChangedListeners.size(); i++) {
                homeworkChangedListeners.get(i).onHomeworkCorrect(homeworkId, isCorrect);
            }
        }
    }

    public void notifyHomeworkDelete(String homeworkId) {
        if (homeworkChangedListeners != null) {
            for (int i = 0; i < homeworkChangedListeners.size(); i++) {
                homeworkChangedListeners.get(i).onHomeworkDelete(homeworkId);
            }
        }
    }

}
