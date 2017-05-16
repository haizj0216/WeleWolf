/**
 * Copyright (C) 2014 The KnowBoxTeacher-android Project
 */
package com.buang.welewolf.base.database.bean;

import com.buang.welewolf.base.bean.OnlineRecorderItemInfo;
import com.buang.welewolf.base.bean.OnlineAnswersCommon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 答案信息
 * @author yangzc
 *
 */
public class AnswerItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2260500785765015025L;

	public static final String TYPE_ANSWER_PICTURE = "type_picture";
	public static final String TYPE_ANSWER_TRANSLATION = "type_translation";
	public static final String TYPE_ANSWER_COMPOSITION = "type_composition";//作文

	public static final int UPLOAD_STATE_SUCCESS = 1;
	public static final int UPLOAD_STATE_FAIL = 2;

	public static final int WORK_DOWNLOAD_NULL = 0;
	public static final int WORK_DOWNLOAD_ING = 1;
	public static final int WORK_DOWNLOAD_DONE = 2;
	
	//0  错  1 半对  2 全对  -1 未批改，-2 未提交
	public static final String SCORE_CORRECT_WRONG = "0";
	public static final String SCORE_CORRECT_RIGHT_PART = "1";
	public static final String SCORE_CORRECT_RIGHT = "2";
	
	public static final String SCORE_CORRECT_UNCORRECT = "-1";
	public static final String SCORE_CORRECT_UNCOMMIT = "-2";

	public static final String SCORE_CORRECT_RATE_0 = "0";
	public static final String SCORE_CORRECT_RATE_20 = "20";
	public static final String SCORE_CORRECT_RATE_50 = "50";
	public static final String SCORE_CORRECT_RATE_80 = "80";
	public static final String SCORE_CORRECT_RATE_100 = "100";
	
	public static final String FLAG_NO_PRAISE = "0";
	public static final String FLAG_IS_PRAISED = "1";
	public static final String FLAG_NO_RECOMMEND = "0";
	public static final String FLAG_IS_RECOMMEND = "1";
	
	//通用答案数据
	public String answerId;
	public String userId;
	public String studentId;
	public String studenetName;
	public String headPhoto;
	public String isPraise;//赞
	public String isSuggest;//推荐
	public String answers;//提交图片
	public String correctAnswers;//批改图片

	//图片类型特定数据
	public String correctScore;
	public String correctRate;

	//英语特定数据
	public String answerTxt;//答案数据
	public boolean isRight;//是否正确

	// just for fill_in_with_pic
	public List<FillinWithPicAnswerItem> items;

	//客户端扩展数据
	public String mHomeworkId;
	public String mQuestionId;
	public String answerType;
	public boolean mIsCached;
	public String uploadeState;//是否已经上传

	public int mDownloadState = WORK_DOWNLOAD_NULL;
	public float mDlProgress;

	public String question;
	public String answerExplain;
	
	public int mPsize;	//好标签数量
	public int mNsize;	//差标签数量
	public ArrayList<OnlineRecorderItemInfo> mAnswerAudios;

	public int mDisplayType = 0;

	public static final int DISPLAY_TYPE_ORIGINAL = 1;
	public static final int DISPLAY_TYPE_SUMMARY = 2;
	public OnlineAnswersCommon mOnlineAnswerCommon;

	public int mQuestionIndex;
	public int mQuestionCount;

	@Override
	public boolean equals(Object o) {
		return answerId.equals(((AnswerItem)o).answerId) && studentId.equals(((AnswerItem)o).studentId);
	}

	/**
	 * 是否提交
	 * @return
	 */
	public boolean isCommitted() {
//		if (TextUtils.isEmpty(isCommit)) {
//			return true;
//		}
		return !SCORE_CORRECT_UNCOMMIT.equalsIgnoreCase(correctScore);
	}

	/**
	 * 是否批注过
	 * @return
	 */
	public boolean isCorrected() {
//		if ("Y".equalsIgnoreCase(isCorrect)) {
//			return true;
//		}
//		return false;
		return !SCORE_CORRECT_UNCORRECT.equalsIgnoreCase(correctScore);
	}

	public static class FillinWithPicAnswerItem implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 122913742055127383L;
		public String answerId;
		public String correctScore;
		public String questionNo;
	}
}
